/**
 * PortOne SDK 연동 템플릿
 * PortOne 결제 및 빌링키 발급 템플릿 함수 제공
 */

/**
 * PortOne SDK 초기화
 * PortOne 함수 사용 전에 호출 필요
 */
async function initPortOne() {
    const config = await getConfig();

    // PortOne SDK 로드 확인
    if (typeof PortOne === 'undefined') {
        console.error('PortOne SDK가 로드되지 않았습니다. HTML에 스크립트를 포함하세요.');
        throw new Error('PortOne SDK not loaded');
    }

    return config.portone;
}

/**
 * PortOne 결제창 열기 (Mode A - 클라이언트 SDK)
 * @param {Object} paymentData - 결제 요청 데이터
 * @returns {Promise<Object>} 결제 결과
 */
async function openPortOnePayment(paymentData) {
    try {
        const portoneConfig = await initPortOne();

        // 일반결제: KG 이니시스 채널 키 사용 (고정)
        const channelKeys = portoneConfig.channelKeys || {};
        const channelKey = channelKeys['kg-inicis'];

        if (!channelKey) {
            throw new Error('kg-inicis 채널키가 설정되지 않았습니다.');
        }

        // 1단계: 서버에 결제 시작 요청 (PENDING 상태로 DB 저장)
        console.log('1단계: 서버에 결제 시작 요청...');
        const createPaymentResult = await makeApiRequest('create-payment', {
            pathParams: { orderId: paymentData.orderId },
            body: {
                // 백엔드 DTO: PaymentTryRequest.payment_price
                payment_price: paymentData.backendPaymentPrice ?? paymentData.totalAmount
            }
        });

        // 서버 응답 검증
        if (!validateApiResponse('create-payment', createPaymentResult)) {
            throw new Error('결제 시작 요청 응답 형식이 올바르지 않습니다.');
        }

        if (!createPaymentResult.success) {
            throw new Error('결제 시작 요청 실패');
        }

        const createPaymentData = createPaymentResult.data ?? {};

        // 서버에서 생성한 paymentsId(PortOne paymentId)를 내려주지 않는 경우가 있어
        // 여기서는 최대한 호환되게 파생합니다.
        // - 권장: 백엔드가 PaymentTryResponse에 paymentsId(또는 paymentId)를 포함해 프론트에 내려주게 수정
        const serverPaymentId =
            createPaymentData.paymentId ||
            createPaymentData.paymentsId ||
            // PaymentTryResponse.id가 PortOne에서 사용하는 paymentId
            (createPaymentData.id != null ? String(createPaymentData.id) : `payment_${Date.now()}`);

        if (!createPaymentData.paymentId && !createPaymentData.paymentsId) {
            console.warn(
                '서버 응답에 PortOne paymentId가 없어 임시 paymentId를 사용합니다. ' +
                '결제창은 열릴 수 있으나, 웹훅/상태 연동은 백엔드 수정이 필요할 수 있습니다.'
            );
        }

        console.log('서버/임시 결제 ID:', serverPaymentId);

        // 고객 정보는 /api/auth/me (get-current-user) 응답을 기준으로 PortOne 포맷에 맞춰 구성
        // paymentData.customer가 이미 있으면 그걸 우선 사용하되, 비어있으면 보강
        let customer = paymentData.customer;
        const hasRequiredCustomerFields =
            customer &&
            customer.customerId &&
            customer.fullName &&
            customer.phoneNumber &&
            customer.email;

        if (!hasRequiredCustomerFields) {
            const currentUser = await makeApiRequest('get-current-user', {});
            const userData = currentUser?.data ?? currentUser ?? {};

            customer = {
                customerId: userData.customerUid,
                fullName: userData.name,
                phoneNumber: userData.phone || '01012345678',
                email: userData.email
            };
        }

        // 2단계: PortOne 결제창 열기
        console.log('2단계: PortOne 결제창 열기...');
        const paymentRequest = {
            storeId: portoneConfig.storeId,
            channelKey: channelKey,
            paymentId: serverPaymentId,  // 서버에서 받은 ID 사용
            orderName: paymentData.orderName || '상품 주문',
            totalAmount: paymentData.totalAmount,
            currency: paymentData.currency || 'KRW',
            payMethod: paymentData.payMethod || 'CARD',
            customer: customer,
            redirectUrl: window.location.href,
            noticeUrls: paymentData.noticeUrls || []
        };

        console.log('PortOne 결제창 열기:', paymentRequest);

        // 요청 패널에 표시
        updateRequestDisplay(paymentRequest);
        updateEndpointDisplay('SDK', 'PortOne.requestPayment()');

        // PortOne SDK 호출
        const response = await PortOne.requestPayment(paymentRequest);

        console.log('결제 응답:', response);

        // 결과 표시
        if (response.code != null) {
            // 결제 실패 또는 취소
            displayError({
                code: response.code,
                message: response.message
            });
            throw new Error(response.message);
        } else {
            // 결제 성공
            displaySuccess({
                paymentId: response.paymentId,
                txId: response.txId,
                message: '결제창 완료. 서버에서 검증하세요.'
            });
            return response;
        }
    } catch (error) {
        console.error('결제 오류:', error);
        displayError({
            error: error.message,
            stack: error.stack
        });
        throw error;
    }
}

/**
 * PortOne 결제창 열기 - 포인트 포함 (Mode A - 클라이언트 SDK)
 * @param {Object} paymentData - 결제 요청 데이터 (pointsToUse 포함)
 * @returns {Promise<Object>} 결제 결과
 */
async function openPortOnePaymentWithPoints(paymentData) {
    try {
        const portoneConfig = await initPortOne();

        // 일반결제: KG 이니시스 채널 키 사용 (고정)
        const channelKeys = portoneConfig.channelKeys || {};
        const channelKey = channelKeys['kg-inicis'];

        if (!channelKey) {
            throw new Error('kg-inicis 채널키가 설정되지 않았습니다.');
        }

        // 포인트 검증 (UI/요청 계산용; 백엔드 DTO에는 pointsToUse가 없으므로 서버에선 제외)
        const pointsToUse = paymentData.pointsToUse || 0;
        if (pointsToUse < 0) throw new Error('포인트는 0 이상이어야 합니다.');

        // 1단계: 서버에 결제 시작 요청 (PENDING 상태로 DB 저장)
        // 백엔드 DTO: PaymentTryRequest.payment_price 만 받음
        console.log('1단계: 서버에 결제 시작 요청 (포인트 포함)...');
        const createPaymentResult = await makeApiRequest('create-payment', {
            pathParams: { orderId: paymentData.orderId },
            body: {
                payment_price: paymentData.backendPaymentPrice ?? paymentData.totalAmount
            }
        });

        // 서버 응답 검증
        if (!validateApiResponse('create-payment', createPaymentResult)) {
            throw new Error('결제 시작 요청 응답 형식이 올바르지 않습니다.');
        }

        if (!createPaymentResult.success) {
            throw new Error('결제 시작 요청 실패');
        }

        const createPaymentData = createPaymentResult.data ?? {};

        const serverPaymentId =
            createPaymentData.paymentId ||
            createPaymentData.paymentsId ||
            // PaymentTryResponse.id가 PortOne에서 사용하는 paymentId
            (createPaymentData.id != null ? String(createPaymentData.id) : `payment_${Date.now()}`);

        if (!createPaymentData.paymentId && !createPaymentData.paymentsId) {
            console.warn(
                '서버 응답에 PortOne paymentId가 없어 임시 paymentId를 사용합니다. ' +
                '결제창은 열릴 수 있으나, 웹훅/상태 연동은 백엔드 수정이 필요할 수 있습니다.'
            );
        }

        console.log('서버/임시 결제 ID:', serverPaymentId);

        // 고객 정보는 /api/auth/me (get-current-user) 응답을 기준으로 PortOne 포맷에 맞춰 구성
        // paymentData.customer가 이미 있으면 그걸 우선 사용하되, 비어있으면 보강
        let customer = paymentData.customer;
        const hasRequiredCustomerFields =
            customer &&
            customer.customerId &&
            customer.fullName &&
            customer.phoneNumber &&
            customer.email;

        if (!hasRequiredCustomerFields) {
            const currentUser = await makeApiRequest('get-current-user', {});
            const userData = currentUser?.data ?? currentUser ?? {};

            customer = {
                customerId: userData.customerUid,
                fullName: userData.name,
                phoneNumber: userData.phone || '01012345678',
                email: userData.email
            };
        }

        // 포인트 차감 후 최종 금액 계산
        const finalAmount = Math.max(0, paymentData.totalAmount - pointsToUse);
        console.log(`포인트 차감: ${paymentData.totalAmount}원 - ${pointsToUse}P = ${finalAmount}원`);

        // 2단계: PortOne 결제창 열기
        console.log('2단계: PortOne 결제창 열기...');
        const paymentRequest = {
            storeId: portoneConfig.storeId,
            channelKey: channelKey,
            paymentId: serverPaymentId,  // 서버에서 받은 ID 사용
            orderName: paymentData.orderName || '상품 주문',
            totalAmount: finalAmount,  // 포인트 차감 후 금액
            currency: paymentData.currency || 'KRW',
            payMethod: paymentData.payMethod || 'CARD',
            customer: customer,
            redirectUrl: window.location.href,
            noticeUrls: paymentData.noticeUrls || []
        };

        console.log('PortOne 결제창 열기 (포인트 차감 후):', paymentRequest);

        // 요청 패널에 표시
        updateRequestDisplay(paymentRequest);
        updateEndpointDisplay('SDK', 'PortOne.requestPayment() [Points]');

        // PortOne SDK 호출
        const response = await PortOne.requestPayment(paymentRequest);

        console.log('결제 응답:', response);

        // 결과 표시
        if (response.code != null) {
            // 결제 실패 또는 취소
            displayError({
                code: response.code,
                message: response.message
            });
            throw new Error(response.message);
        } else {
            // 결제 성공
            displaySuccess({
                paymentId: response.paymentId,
                txId: response.txId,
                pointsUsed: pointsToUse,
                message: '결제창 완료 (포인트 차감). 서버에서 검증하세요.'
            });
            return response;
        }
    } catch (error) {
        console.error('결제 오류 (포인트):', error);
        displayError({
            error: error.message,
            stack: error.stack
        });
        throw error;
    }
}

/**
 * PortOne 빌링키 발급 (Mode A - 클라이언트 SDK)
 * @param {Object} billingKeyData - 빌링키 요청 데이터
 * @returns {Promise<Object>} 빌링키 결과
 */
async function issuePortOneBillingKey(billingKeyData) {
    try {
        const portoneConfig = await initPortOne();

        // 정기결제: 토스 채널 키 사용 (고정)
        const channelKeys = portoneConfig.channelKeys || {};
        const channelKey = channelKeys.toss;

        if (!channelKey) {
            throw new Error('toss 채널키가 설정되지 않았습니다.');
        }

        const billingKeyRequest = {
            storeId: portoneConfig.storeId,
            channelKey: channelKey,
            billingKeyMethod: billingKeyData.billingKeyMethod || 'CARD',
            method: {
                card: {
                    credential: {}
                }
            },
            issueId: billingKeyData.issueId || `billing_${Date.now()}`,
            issueName: billingKeyData.issueName || '정기결제 등록',
            customer: billingKeyData.customer || {
                customerId: 'customer_001',
                fullName: '홍길동',
                phoneNumber: '01012345678',
                email: 'customer@example.com'
            },
            redirectUrl: window.location.href,
            noticeUrls: billingKeyData.noticeUrls || []
        };

        console.log('빌링키 발급 시작:', billingKeyRequest);

        // 요청 패널에 표시
        updateRequestDisplay(billingKeyRequest);
        updateEndpointDisplay('SDK', 'PortOne.requestIssueBillingKey()');

        // PortOne SDK 호출
        const response = await PortOne.requestIssueBillingKey(billingKeyRequest);

        console.log('빌링키 응답:', response);

        // 결과 표시
        if (response.code != null) {
            // 실패
            displayError({
                code: response.code,
                message: response.message
            });
            throw new Error(response.message);
        } else {
            // 성공
            const result = {
                billingKey: response.billingKey,
                issueId: response.issueId,
                message: '빌링키 발급 성공. 서버에 저장하세요.',
                // 서버 저장용 페이로드 샘플
                serverPayload: {
                    customerId: billingKeyRequest.customer.customerId,
                    billingKey: response.billingKey,
                    issueId: response.issueId,
                    cardInfo: response.card || null
                }
            };

            displaySuccess(result);
            return result;
        }
    } catch (error) {
        console.error('빌링키 발급 오류:', error);
        displayError({
            error: error.message,
            stack: error.stack
        });
        throw error;
    }
}

/**
 * 템플릿: 결제 확정 플로우
 * 결제창 종료 후 서버에서 결제 검증 및 주문 상태 업데이트
 */
async function confirmPaymentTemplate(paymentId) {
    try {
        const result = await makeApiRequest('confirm-payment', {
            pathParams: paymentId
        });

        showNotification('결제 확정 성공!', 'success');
        return result;
    } catch (error) {
        showNotification('결제 확정 실패', 'error');
        throw error;
    }
}

/**
 * 템플릿: 결제 취소/환불 플로우
 * 결제 취소/환불 및 주문 상태 업데이트
 */
async function cancelPaymentTemplate(paymentId, reason = 'Customer request') {
    try {
        const result = await makeApiRequest('cancel-payment', {
            pathParams: paymentId,
            body: {
                reason: reason
            }
        });

        showNotification('결제 취소 성공!', 'success');
        return result;
    } catch (error) {
        showNotification('결제 취소 실패', 'error');
        throw error;
    }
}
