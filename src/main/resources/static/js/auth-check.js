/**
 * 인증 체크 스크립트
 * JWT 토큰 확인 및 로그아웃 처리
 */

/**
 * 로그인 여부 확인
 * JWT 토큰이 없으면 로그인 페이지로 리다이렉트
 */
function checkAuthentication() {
    // 쿠키에서 토큰 확인
    const token = typeof getToken === 'function' ? getToken() : null;
    const currentPath = window.location.pathname;

    // 로그인/회원가입 페이지는 체크 제외
    if (currentPath === '/pages/login' || currentPath === '/pages/register') {
        return;
    }

    // 토큰이 없으면 로그인 페이지로 이동
    if (!token) {
        window.location.href = '/pages/login';
        return;
    }

    // 사용자 정보 표시
    displayUserInfo();
}

window.sharedCurrentUserPromise = null;
function getSharedCurrentUser() {
    if (!window.sharedCurrentUserPromise) {
        window.sharedCurrentUserPromise = makeApiRequest('get-current-user', {});
    }
    return window.sharedCurrentUserPromise;
}

/**
 * 네비게이션 바에 사용자 정보 표시
 */
async function displayUserInfo() {
    // API 호출 전 랜더링을 위해 토큰 유무만 확인
    const token = typeof getToken === 'function' ? getToken() : null;
    const navbarActions = document.querySelector('.navbar-actions');

    if (token && navbarActions) {
        // 사용자 정보 요소 추가 준비
        const userInfo = document.createElement('div');
        userInfo.style.cssText = 'display: flex; align-items: center; gap: 1rem; margin-right: 1rem;';
        
        // 초기 로딩 상태 렌더링
        userInfo.innerHTML = `
            <div style="display: flex; flex-direction: column; align-items: flex-end; line-height: 1.2;">
                <span style="font-weight: 800; font-size: 0.9rem; color: var(--text-secondary);">정보 로딩중...</span>
            </div>
            <button class="btn btn-outline" style="padding: 0.4rem 1rem; font-size: 0.875rem;" disabled>
                로그아웃
            </button>
        `;

        // navbar-actions 맨 앞에 삽입
        navbarActions.insertBefore(userInfo, navbarActions.firstChild);

        try {
            // 사용자 세부 정보(이름, 포인트 등) /api/auth/me (get-current-user)에서 가져오기
            // 중복 요청 방지를 위해 공유된 Promise 이용
            const response = await getSharedCurrentUser();
            const userData = response?.data || response || {};
            
            const nameToDisplay = userData.name || "사용자";
            const pointsToDisplay = userData.point !== undefined ? userData.point.toLocaleString() : '0';

            userInfo.innerHTML = `
                <div style="display: flex; flex-direction: column; align-items: flex-end; line-height: 1.2;">
                    <span style="font-weight: 800; font-size: 0.95rem;">👋 ${nameToDisplay}님 안녕하세요</span>
                    <span style="font-size: 0.85rem; font-weight: 800; color: var(--primary);">💎 ${pointsToDisplay} P</span>
                </div>
                <button onclick="handleLogout()" class="btn btn-outline" style="padding: 0.4rem 1rem; font-size: 0.875rem;">
                    로그아웃
                </button>
            `;
        } catch (error) {
            console.error('Failed to load user details for header:', error);
            // 에러 시 렌더링
            userInfo.innerHTML = `
                <div style="display: flex; flex-direction: column; align-items: flex-end; line-height: 1.2;">
                    <span style="font-weight: 800; font-size: 0.95rem;">오류 발생</span>
                </div>
                <button onclick="handleLogout()" class="btn btn-outline" style="padding: 0.4rem 1rem; font-size: 0.875rem;">
                    로그아웃
                </button>
            `;
        }
    }
}

/**
 * 로그아웃 처리
 */
function handleLogout() {
    // 쿠키에서 토큰 제거
    if (typeof removeToken === 'function') removeToken();

    // 로그인 페이지로 이동
    window.location.href = '/pages/login';
}

// 페이지 로드 시 인증 체크
document.addEventListener('DOMContentLoaded', function() {
    checkAuthentication();
});
