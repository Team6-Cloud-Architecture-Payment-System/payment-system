# 커밋하고가6 — 전체 API 명세서

---

## 📦 주문 (Order)

### POST /api/orders — 주문 생성

**Description** : 주문을 생성합니다.

**Header**

```
Content-Type: application/json
Authorization: Bearer {token}
```

**Request Body**

```json
{
  "orderItems": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 3,
      "quantity": 1
    }
  ],
  "usedPoint": 3000
}
```

**Response Body**

```json
{
  "status": "SUCCESS",
  "message": "주문이 생성되었습니다.",
  "data": {
    "orderId": 101,
    "orderNumber": "ORD-20260317-0001",
    "totalPrice": 32000,
    "usedPoint": 3000,
    "paymentPrice": 29000,
    "orderStatus": "결제대기",
    "orderedCreatedAt": "2026-03-17T12:30:00"
  }
}
```

**Error**

```json
{
  "status": 404,
  "code": "PRODUCT_NOT_FOUND",
  "message": "주문할 상품을 찾을 수 없습니다.",
  "timestamp": "2026-03-17T12:30:00"
}
```

```json
{
  "status": 400,
  "code": "INVALID_ORDER_QUANTITY",
  "message": "주문 수량은 1개 이상이어야 합니다.",
  "timestamp": "2026-03-17T12:30:00"
}
```

```json
{
  "status": 400,
  "code": "EMPTY_ORDER_ITEMS",
  "message": "주문 상품은 최소 1개 이상 포함되어야 합니다.",
  "timestamp": "2026-03-17T12:30:00"
}
```

```json
{
  "status": 409,
  "code": "INSUFFICIENT_STOCK",
  "message": "재고가 부족하여 주문을 생성할 수 없습니다.",
  "timestamp": "2026-03-17T12:30:00"
}
```

```json
{
  "status": 400,
  "code": "INSUFFICIENT_POINT",
  "message": "보유 포인트가 부족합니다.",
  "timestamp": "2026-03-17T12:30:00"
}
```

---

### GET /api/orders — 주문 내역 조회

**Description** : 내 주문 내역을 조회합니다.

**Header**

```
Content-Type: application/json
Authorization: Bearer {token}
```

**Request Body** : 없음

**Response Body**

```json
{
  "status": "SUCCESS",
  "message": "주문 내역 조회에 성공했습니다.",
  "data": {
    "orders": [
      {
        "orderId": 101,
        "orderNumber": "ORD-20260317-0001",
        "totalPrice": 32000,
        "usedPoint": 3000,
        "paymentPrice": 29000,
        "orderStatus": "결제완료",
        "orderedCreatedAt": "2026-03-17T12:30:00"
      },
      {
        "orderId": 102,
        "orderNumber": "ORD-20260316-0002",
        "totalPrice": 15000,
        "usedPoint": 0,
        "paymentPrice": 15000,
        "orderStatus": "주문취소",
        "orderedCreatedAt": "2026-03-16T09:10:00"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "size": 10,
      "totalCount": 58,
      "totalPages": 6
    }
  }
}
```

**Error**

```json
{
  "status": 400,
  "code": "INVALID_PAGE_REQUEST",
  "message": "페이지 요청 값이 올바르지 않습니다.",
  "timestamp": "2026-03-17T12:30:00"
}
```

```json
{
  "status": 400,
  "code": "INVALID_ORDER_STATUS",
  "message": "유효하지 않은 주문 상태입니다.",
  "timestamp": "2026-03-17T12:30:00"
}
```

```json
{
  "status": 401,
  "code": "UNAUTHORIZED",
  "message": "로그인이 필요합니다.",
  "timestamp": "2026-03-17T12:30:00"
}
```

---

### GET /api/orders/{orderId} — 주문 상세 조회

**Description** : 특정 주문의 상세 정보를 조회합니다.

**Header**

```
Content-Type: application/json
Authorization: Bearer {token}
```

**Request Body** : 없음

**Response Body**

```json
{
  "status": "SUCCESS",
  "message": "주문 상세 조회에 성공했습니다.",
  "data": {
    "orderId": 101,
    "orderNumber": "ORD-20260317-0001",
    "totalPrice": 32000,
    "usedPoint": 3000,
    "paymentPrice": 29000,
    "orderStatus": "결제완료",
    "orderedCreatedAt": "2026-03-17T12:30:00",
    "payment": {
      "paymentId": 5001,
      "paymentStatus": "결제완료",
      "paymentPrice": 29000,
      "paymentCreatedAt": "2026-03-17T12:35:00"
    },
    "orderItems": [
      {
        "productId": 1,
        "productName": "맛있는 강아지 사료 500g",
        "productPrice": 12000,
        "quantity": 2
      },
      {
        "productId": 3,
        "productName": "삑삑이 장난감",
        "productPrice": 8000,
        "quantity": 1
      }
    ]
  }
}
```

**Error**

```json
{
  "status": 404,
  "code": "ORDER_NOT_FOUND",
  "message": "해당 주문을 찾을 수 없습니다.",
  "timestamp": "2026-03-17T12:30:00"
}
```

```json
{
  "status": 403,
  "code": "ORDER_ACCESS_DENIED",
  "message": "해당 주문에 접근할 수 없습니다.",
  "timestamp": "2026-03-17T12:30:00"
}
```

---

### POST /api/orders/{orderId}/confirm — 주문 상태 변경

**Description** : 주문을 확정합니다.

**Header**

```
Content-Type: application/json
Authorization: Bearer {token}
```

**Request Body**

```json
{
  "status": "ORDER_CONFIRMED"
}
```

**Response Body**

```json
{
  "status": "SUCCESS",
  "message": "주문이 확정되었습니다.",
  "data": {
    "orderId": 101,
    "orderStatus": "주문 확정",
    "confirmedAt": "2026-03-17T20:30:00"
  }
}
```

**Error**

```json
{
  "status": 400,
  "code": "INVALID_ORDER_STATUS",
  "message": "주문 완료 상태에서만 주문 확정이 가능합니다.",
  "timestamp": "2026-03-17T20:30:00"
}
```

```json
{
  "status": 400,
  "code": "REFUND_NOT_ALLOWED",
  "message": "주문 확정 이후에는 환불이 불가능합니다.",
  "timestamp": "2026-03-17T20:30:00"
}
```

```json
{
  "status": 409,
  "code": "ALREADY_CONFIRMED_ORDER",
  "message": "이미 주문 확정된 주문입니다.",
  "timestamp": "2026-03-17T20:30:00"
}
```

---

## 💳 환불 (Refund)

### POST /api/payments/{paymentId}/refunds — 환불 요청

**Description** : 사용자의 환불 요청을 처리합니다.

**Header**

```
Content-Type: application/json
Authorization: Bearer {token}
```

**Request Body**

```json
{
  "refundReason": "배송이 너무 늦게왔어요."
}
```

**Response Body**

```json
{
  "status": "SUCCESS",
  "message": "환불이 완료되었습니다.",
  "data": {
    "id": 1,
    "paymentId": "portone_payment_id",
    "refundPrice": 300000,
    "refundReason": "배송이 너무 늦게왔어요.",
    "refundStatus": "환불 완료",
    "refundCreatedAt": "2026-03-03T15:30:00"
  }
}
```

**Error**

```json
{
  "status": 401,
  "code": "UNAUTHORIZED",
  "message": "인증 토큰이 유효하지 않거나 만료되었습니다.",
  "timestamp": "2026-03-18T19:00:00"
}
```

```json
{
  "status": 403,
  "code": "ACCESS_DENIED",
  "message": "해당 결제 내역에 대한 접근 권한이 없습니다.",
  "timestamp": "2026-03-18T19:00:00"
}
```

```json
{
  "status": 404,
  "code": "PAYMENT_NOT_FOUND",
  "message": "해당 결제 정보를 찾을 수 없습니다.",
  "timestamp": "2026-03-18T19:00:00"
}
```

```json
{
  "status": 400,
  "code": "ALREADY_REFUNDED",
  "message": "이미 환불 처리가 완료된 결제건입니다.",
  "timestamp": "2026-03-18T19:00:00"
}
```

```json
{
  "status": 400,
  "code": "INVALID_PAYMENT_STATUS",
  "message": "결제 완료 상태인 경우에만 환불이 가능합니다.",
  "timestamp": "2026-03-18T19:00:00"
}
```

```json
{
  "status": 400,
  "code": "INVALID_ORDER_STATUS",
  "message": "주문 완료 상태인 경우에만 환불이 가능합니다.",
  "timestamp": "2026-03-18T19:00:00"
}
```

```json
{
  "status": 500,
  "code": "EXTERNAL_API_ERROR",
  "message": "외부 결제 시스템(PortOne)과의 통신에 실패했습니다.",
  "timestamp": "2026-03-18T19:00:00"
}
```

```json
{
  "status": 500,
  "code": "COMPENSATION_TRANSACTION_FAILED",
  "message": "환불 처리 중 내부 시스템 오류가 발생하여 보상 트랜잭션이 수행되었습니다.",
  "timestamp": "2026-03-18T19:00:00"
}
```

---

### GET /api/orders/{orderId}/refunds — 특정 주문 환불 내역 조회

**Description** : 사용자들이 자신들의 특정 주문에 대한 환불 내역을 조회합니다.

**Header**

```
Content-Type: application/json
Authorization: Bearer {token}
```

**Request Body** : 없음

**Response Body**

```json
{
  "status": "SUCCESS",
  "message": "해당 주문에 대한 환불 내역이 성공적으로 조회되었습니다.",
  "data": {
    "id": 2,
    "paymentId": "portone_payment_id",
    "refundPrice": 1000000,
    "refundReason": "제품이 망가져 있어요!!",
    "refundStatus": "환불 완료",
    "refundCreatedAt": "2026-03-05T13:16:00"
  }
}
```

**Error**

```json
{
  "status": 401,
  "code": "UNAUTHORIZED",
  "message": "인증 토큰이 유효하지 않거나 만료되었습니다.",
  "timestamp": "2026-03-18T19:05:00"
}
```

```json
{
  "status": 403,
  "code": "ORDER_ACCESS_DENIED",
  "message": "해당 주문 내역을 조회할 권한이 없습니다.",
  "timestamp": "2026-03-18T19:05:00"
}
```

```json
{
  "status": 404,
  "code": "ORDER_NOT_FOUND",
  "message": "요청하신 주문 번호를 찾을 수 없습니다.",
  "timestamp": "2026-03-18T19:05:00"
}
```

```json
{
  "status": 404,
  "code": "REFUND_RECORD_NOT_FOUND",
  "message": "해당 주문에 대해 처리된 환불 내역이 존재하지 않습니다.",
  "timestamp": "2026-03-18T19:05:00"
}
```

---

### GET /api/refunds/me — 환불 내역 전체 조회 (개인)

**Description** : 사용자가 자신의 전체 환불 내역을 조회합니다.

**Header**

```
Content-Type: application/json
Authorization: Bearer {token}
```

**Request Body** : 없음

**Response Body**

```json
{
  "status": "SUCCESS",
  "message": "환불 내역이 성공적으로 조회되었습니다.",
  "data": {
    "refund": [
      {
        "id": 1,
        "paymentId": "portone_payment_id_1",
        "refundPrice": 300000,
        "refundReason": "배송이 너무 늦게왔어요.",
        "refundStatus": "환불 완료",
        "refundCreatedAt": "2026-03-03T15:30:00"
      },
      {
        "id": 2,
        "paymentId": "portone_payment_id_2",
        "refundPrice": 1000000,
        "refundReason": "제품이 망가져 있어요!!",
        "refundStatus": "환불 완료",
        "refundCreatedAt": "2026-03-03T13:16:00"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "size": 10,
      "totalCount": 2,
      "totalPages": 1
    }
  }
}
```

**Error**

```json
{
  "status": 401,
  "code": "AUTH_TOKEN_MISSING_OR_EXPIRED",
  "message": "로그인이 만료되었거나 인증 토큰이 없습니다. 다시 로그인해주세요.",
  "timestamp": "2026-03-18T19:10:00"
}
```

```json
{
  "status": 404,
  "code": "REFUND_HISTORY_NOT_FOUND",
  "message": "해당 주문번호로 진행된 환불 기록을 찾을 수 없습니다.",
  "timestamp": "2026-03-18T19:10:00"
}
```

---

## 🎯 포인트 (Point)

### GET /api/points/me — 내 포인트 조회

**Description** : 사용자 자신의 남은 포인트를 조회합니다.

**Header**

```
Content-Type: application/json
Authorization: Bearer {token}
```

**Request Body** : 없음

**Response Body**

```json
{
  "status": "SUCCESS",
  "message": "포인트 조회가 완료되었습니다.",
  "data": {
    "userId": 1,
    "myPoint": 5000
  }
}
```

**Error**

```json
{
  "status": 401,
  "code": "AUTH_TOKEN_INVALID",
  "message": "인증 정보가 없거나 세션이 만료되었습니다. 다시 로그인 후 이용해주세요.",
  "timestamp": "2026-03-18T19:20:00"
}
```

```json
{
  "status": 404,
  "code": "USER_POINT_NOT_FOUND",
  "message": "해당 사용자의 포인트 정보를 찾을 수 없습니다.",
  "timestamp": "2026-03-18T19:20:00"
}
```

---

### GET /api/points/me/history — 포인트 거래 내역 조회

**Description** : 사용자의 포인트 거래 내역을 모두 조회합니다.

**Header**

```
Content-Type: application/json
Authorization: Bearer {token}
```

**Request Body** : 없음

**Response Body**

```json
{
  "status": "SUCCESS",
  "message": "포인트 거래 내역 조회가 완료되었습니다.",
  "data": [
    {
      "id": 1,
      "orderId": 10,
      "point": 500,
      "type": "EARNED",
      "pointCreatedAt": "2026-03-03T15:30:00",
      "pointExpiredAt": "2027-03-03T15:30:00"
    },
    {
      "id": 2,
      "orderId": 11,
      "point": -300,
      "type": "SPENT",
      "pointCreatedAt": "2026-03-10T10:00:00",
      "pointExpiredAt": null
    }
  ]
}
```

**Error**

```json
{
  "status": 401,
  "code": "AUTH_TOKEN_INVALID_OR_EXPIRED",
  "message": "인증 정보가 누락되었거나 세션이 만료되었습니다. 다시 로그인해 주세요.",
  "timestamp": "2026-03-18T19:30:00"
}
```

```json
{
  "status": 404,
  "code": "POINT_TRANSACTION_NOT_FOUND",
  "message": "조회 가능한 포인트 이용 내역이 없습니다.",
  "timestamp": "2026-03-18T19:30:00"
}
```

---

## 👑 멤버십 (Membership)

### GET /api/membership/me — 나의 멤버십 등급 조회

**Description** : 사용자의 멤버십 등급을 조회합니다.

**Header**

```
Content-Type: application/json
Authorization: Bearer {token}
```

**Request Body** : 없음

**Response Body**

```json
{
  "status": "SUCCESS",
  "message": "멤버십 등급 조회가 완료되었습니다.",
  "data": {
    "id": 1,
    "userId": 1,
    "gradeNow": "NORMAL",
    "totalPrice": 35000,
    "gradeUpdatedAt": "2026-03-03T15:30:00"
  }
}
```

**Error**

```json
{
  "status": 401,
  "code": "AUTH_TOKEN_MISSING",
  "message": "멤버십 정보를 확인하려면 로그인이 필요합니다.",
  "timestamp": "2026-03-18T19:50:00"
}
```

```json
{
  "status": 404,
  "code": "USER_MEMBERSHIP_NOT_FOUND",
  "message": "해당 사용자의 멤버십 등급 정보를 찾을 수 없습니다.",
  "timestamp": "2026-03-18T19:50:00"
}
```

---

### GET /api/membership/tiers — 멤버십 등급별 정책 조회

**Description** : 멤버십 등급 정책을 조회합니다.

**Header**

```
Content-Type: application/json
```

**Request Body** : 없음

**Response Body**

```json
{
  "status": "SUCCESS",
  "message": "멤버십 등급 정책 조회가 완료되었습니다.",
  "data": [
    {
      "id": 1,
      "rewardRate": 0.01,
      "promotionCriteria": "누적 결제 금액 5만원 이하",
      "gradeName": "NORMAL",
      "minPrice": 0,
      "maxPrice": 50000
    },
    {
      "id": 2,
      "rewardRate": 0.05,
      "promotionCriteria": "누적 결제 금액 10만원 이하",
      "gradeName": "VIP",
      "minPrice": 50001,
      "maxPrice": 100000
    },
    {
      "id": 3,
      "rewardRate": 0.10,
      "promotionCriteria": "누적 결제 금액 15만원 이상",
      "gradeName": "VVIP",
      "minPrice": 100001,
      "maxPrice": null
    }
  ]
}
```

**Error**

```json
{
  "status": 404,
  "code": "GRADE_POLICY_NOT_FOUND",
  "message": "해당 등급에 대한 운영 정책 데이터를 찾을 수 없습니다.",
  "timestamp": "2026-03-18T19:55:00"
}
```

```json
{
  "status": 500,
  "code": "GRADE_POLICY_RETRIEVAL_FAILED",
  "message": "등급 정책을 불러오는 중 서버 내부 오류가 발생했습니다.",
  "timestamp": "2026-03-18T19:55:00"
}
```

---

## 🔐 인증/인가 (Auth)

### POST /api/auth/signup — 회원가입

**Description** : 회원가입을 처리합니다.

**Header**

```
Content-Type: application/json
```

**Request Body**

```json
{
  "email": "user@example.com",
  "password": "securePassword123!",
  "name": "홍길동",
  "phone": "010-1234-5678"
}
```

**Response Body**

```json
{
  "status": "SUCCESS",
  "message": "요청이 성공했습니다.",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "name": "홍길동",
    "phone": "010-1234-5678"
  }
}
```

**Error**

```json
{
  "status": 400,
  "code": "EMAIL_IS_NULL",
  "message": "이메일은 필수항목입니다.",
  "timestamp": "2026-03-17T12:30:00"
}
```

```json
{
  "status": 400,
  "code": "PASSWORD_IS_NULL",
  "message": "비밀번호는 필수항목입니다.",
  "timestamp": "2026-03-17T12:30:00"
}
```

---

### POST /api/auth/login — 로그인

**Description** : 로그인을 처리합니다.

**Header**

```
Content-Type: application/json
```

**Request Body**

```json
{
  "email": "user@example.com",
  "password": "securePassword123!"
}
```

**Response Body**

```json
{
  "status": "SUCCESS",
  "message": "요청이 성공했습니다.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

**Error**

```json
{
  "status": 404,
  "code": "USER_NOT_FOUND",
  "message": "존재하지 않는 사용자입니다.",
  "timestamp": "2026-03-17T12:30:00"
}
```

```json
{
  "status": 400,
  "code": "PASSWORD_NOT_CORRECT",
  "message": "비밀번호가 일치하지 않습니다.",
  "timestamp": "2026-03-17T12:30:00"
}
```

---

### POST /api/auth/logout — 로그아웃

**Description** : 로그아웃을 처리합니다.

**Header**

```
Content-Type: application/json
Authorization: Bearer {AccessToken}
```

**Request Body**

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1..."
}
```

**Response Body**

```json
{
  "status": "SUCCESS",
  "message": "로그아웃되었습니다.",
  "data": null
}
```

**Error**

```json
{
  "status": 404,
  "code": "USER_NOT_FOUND",
  "message": "존재하지 않는 사용자입니다.",
  "timestamp": "2026-03-17T12:30:00"
}
```

```json
{
  "status": 403,
  "code": "NOT_AUTHORIZED",
  "message": "유효하지 않거나 만료된 토큰입니다.",
  "timestamp": "2026-03-17T12:30:00"
}
```

---

## 🛍️ 상품 (Product)

### GET /api/products — 상품 목록 조회

**Description** : 판매 중인 상품 목록을 조회합니다.

**Header**

```
Content-Type: application/json
```

**Request Body** : 없음

**Response Body**

```json
{
  "status": "SUCCESS",
  "message": "요청이 성공했습니다.",
  "data": {
    "products": [
      {
        "productId": 101,
        "productName": "프리미엄 무선 키보드",
        "price": 89000,
        "stock": 45,
        "status": "ON_SALE",
        "categoryId": 5
      }
    ]
  },
  "pagination": {
    "currentPage": 1,
    "size": 10,
    "totalCount": 58,
    "totalPages": 6
  }
}
```

**Error**

```json
{
  "status": 404,
  "code": "PRODUCT_NOT_FOUND",
  "message": "주문할 상품을 찾을 수 없습니다.",
  "timestamp": "2026-03-17T12:30:00"
}
```

---

### GET /api/products/{productId} — 상품 단건 조회

**Description** : 특정 상품의 상세 정보를 조회합니다.

**Header**

```
Content-Type: application/json
```

**Request Body** : 없음

**Response Body**

```json
{
  "status": "SUCCESS",
  "message": "요청이 성공했습니다.",
  "data": {
    "productId": 101,
    "productName": "프리미엄 무선 키보드",
    "price": 89000,
    "stock": 45,
    "status": "ON_SALE",
    "categoryId": 5
  }
}
```

---

## 🔔 결제 (Payment)

### POST /api/payments/webhook — 웹훅 엔드포인트

**Description** : 특정 이벤트(결제창 열림, 결제 승인 등)가 발생했을 때 수신하는 엔드포인트입니다.

**Header**

```
Content-Type: application/json
webhook-id: test-id
webhook-signature: aW52YWxpZCBzaWduYXR1cmU=
webhook-timestamp: 100
```

**Request Body**

```json
{
  "type": "Transaction.Cancelled",
  "timestamp": "2024-04-25T10:00:00.000Z",
  "data": {
    "storeId": "store-ae356798-3d20-4969-b739-14c6b0e1a667",
    "paymentId": "example-payment-id",
    "transactionId": "55451513-9763-4a7a-bb43-78a4c65be843"
  }
}
```

**Response Code** : `200 OK`

---

### GET /api.portone.io/payments/{paymentId} — 결제 검증

**Description** : PortOne에서 결제 최종 상태를 직접 확인합니다.

**Header**

```
Content-Type: application/json
Authorization: PortOne {token}
```

**Response Body**

```json
{
  "status": "결제상태",
  "id": "결제 건 아이디",
  "transactionId": "결제건 포트원 채번 아이디",
  "merchantId": "고객사 아이디",
  "storeId": "상점아이디"
}
```

**Error**

```json
{
  "status": 400,
  "code": "InvalidRequestError",
  "message": "요청된 입력 정보가 유효하지 않은 경우"
}
```

```json
{
  "status": 401,
  "code": "UnauthorizedError",
  "message": "인증 정보가 올바르지 않은 경우"
}
```

---

### POST /api.portone.io/payments/{paymentId}/cancel — 결제 취소

**Description** : 사용자가 환불 요청 시 실제 결제 취소를 수행합니다 (전액 환불).

**Header**

```
Authorization: PortOne {token}
Content-Type: application/json
```

**Request Body**

```json
{
  "amount": 8000,
  "reason": "환불사유",
  "skipWebhook": false
}
```

**Response Body**

```json
{
  "status": "SUCCEEDED",
  "id": "can_20260318_abcdef123",
  "pgCancellationId": "PG_TID_987654321",
  "totalPrice": 11000,
  "reason": "고객 단순 변심으로 인한 주문 취소",
  "cancelledAt": "2026-03-18T23:35:00Z",
  "requestedAt": "2026-03-18T23:34:50Z"
}
```

**Error**

```json
{
  "status": 400,
  "code": "InvalidRequestError",
  "message": "요청된 입력 정보가 유효하지 않은 경우"
}
```

```json
{
  "status": 401,
  "code": "UnauthorizedError",
  "message": "인증 정보가 올바르지 않은 경우"
}
```