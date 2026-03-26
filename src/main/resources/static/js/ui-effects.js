/**
 * UI Effects Module — 전역 애니메이션, 스켈레톤, 페이지 전환 유틸리티
 */

/* ============================================
   1. withMinDelay — API 호출 강제 최소 딜레이
   ============================================ */

/**
 * API 호출에 최소 딜레이(기본 500ms)를 보장하여 스켈레톤이 충분히 표시되도록 합니다.
 * @param {Function} asyncFn - 실행할 비동기 함수
 * @param {number} minMs - 최소 딜레이 (ms)
 * @returns {Promise<*>} asyncFn의 결과
 */
async function withMinDelay(asyncFn, minMs = 500) {
    const [result] = await Promise.all([
        asyncFn(),
        new Promise(resolve => setTimeout(resolve, minMs))
    ]);
    return result;
}

/* ============================================
   2. Skeleton Generators
   ============================================ */

/**
 * 상품 카드 스켈레톤 HTML 생성
 * @param {number} count - 스켈레톤 카드 수
 */
function generateProductSkeletons(count = 6) {
    let html = '';
    for (let i = 0; i < count; i++) {
        html += `
            <div class="skeleton-card" style="animation-delay: ${i * 0.08}s;">
                <div class="skeleton-image"></div>
                <div class="skeleton-text short"></div>
                <div class="skeleton-text long"></div>
                <div class="skeleton-text medium" style="height: 1.5rem;"></div>
                <div class="skeleton-text short"></div>
                <div class="skeleton-btn"></div>
            </div>
        `;
    }
    return html;
}

/**
 * 테이블 행 스켈레톤 HTML 생성
 * @param {number} rows - 행 수
 * @param {number} cols - 열 수
 */
function generateTableSkeletons(rows = 5, cols = 5) {
    let html = '';
    for (let i = 0; i < rows; i++) {
        html += '<div class="skeleton-table-row" style="grid-template-columns: repeat(' + cols + ', 1fr);">';
        for (let j = 0; j < cols; j++) {
            html += `
                <div class="skeleton-table-cell">
                    <div class="skeleton-text ${j === 0 ? 'long' : 'medium'}" style="animation-delay: ${(i * cols + j) * 0.05}s;"></div>
                </div>
            `;
        }
        html += '</div>';
    }
    return html;
}

/**
 * 정보 카드 스켈레톤 HTML 생성 (마이페이지 등)
 * @param {number} lines - 줄 수
 */
function generateInfoSkeletons(lines = 4) {
    let html = '<div style="display: flex; flex-direction: column; gap: 0.75rem;">';
    for (let i = 0; i < lines; i++) {
        html += `
            <div style="display: flex; justify-content: space-between; align-items: center; padding: 0.75rem; border: 3px solid var(--border); box-shadow: 4px 4px 0px var(--shadow);">
                <div class="skeleton-text short" style="animation-delay: ${i * 0.1}s;"></div>
                <div class="skeleton-text medium" style="animation-delay: ${i * 0.1 + 0.05}s;"></div>
            </div>
        `;
    }
    html += '</div>';
    return html;
}

/**
 * 등급 프로그레스 스켈레톤 HTML 생성
 */
function generateGradeSkeletons() {
    return `
        <div style="display: flex; flex-direction: column; align-items: center; gap: 1rem;">
            <div class="skeleton-circle" style="width: 60px; height: 60px;"></div>
            <div class="skeleton-text medium" style="height: 1.5rem;"></div>
            <div style="width: 100%; padding: 0.75rem; border: 3px solid var(--border); box-shadow: 4px 4px 0px var(--shadow);">
                <div class="skeleton-text long" style="margin-bottom: 0.5rem;"></div>
                <div class="skeleton-text" style="height: 28px; width: 100%; border: 3px solid var(--border);"></div>
            </div>
            <div class="skeleton-text short"></div>
        </div>
    `;
}

/**
 * 플랜 카드 스켈레톤 HTML 생성
 * @param {number} count - 카드 수
 */
function generatePlanSkeletons(count = 3) {
    let html = '';
    for (let i = 0; i < count; i++) {
        html += `
            <div class="skeleton-card" style="animation-delay: ${i * 0.1}s; text-align: center;">
                <div class="skeleton-circle" style="width: 60px; height: 60px; margin: 0 auto;"></div>
                <div class="skeleton-text medium" style="height: 1.5rem; margin: 0.5rem auto;"></div>
                <div class="skeleton-text short" style="height: 2rem; margin: 0.5rem auto;"></div>
                <div style="display: flex; flex-direction: column; gap: 0.5rem; margin: 1rem 0;">
                    <div class="skeleton-text long"></div>
                    <div class="skeleton-text long"></div>
                    <div class="skeleton-text medium"></div>
                    <div class="skeleton-text long"></div>
                </div>
                <div class="skeleton-btn"></div>
            </div>
        `;
    }
    return html;
}

/* ============================================
   3. Stagger Animation for Cards
   ============================================ */

/**
 * 컨테이너 내 자식 요소에 stagger fadeInUp 애니메이션 적용
 * @param {string} containerId - 컨테이너 ID
 * @param {number} delayStep - 각 아이템 간 딜레이 (ms)
 * @param {string} animClass - 적용할 애니메이션 클래스
 */
function animateStaggerItems(containerId, delayStep = 80, animClass = 'stagger-item') {
    const container = document.getElementById(containerId);
    if (!container) return;

    const children = container.children;
    for (let i = 0; i < children.length; i++) {
        const child = children[i];
        child.style.opacity = '0';
        child.classList.add(animClass);
        child.style.animationDelay = `${i * delayStep}ms`;
    }
}

/**
 * 테이블 행에 stagger slideInLeft 애니메이션 적용
 * @param {string} tbodyId - tbody ID
 * @param {number} delayStep - 각 행 간 딜레이 (ms)
 */
function animateTableRows(tbodyId, delayStep = 60) {
    const tbody = document.getElementById(tbodyId);
    if (!tbody) return;

    const rows = tbody.querySelectorAll('tr');
    rows.forEach((row, i) => {
        row.style.opacity = '0';
        row.style.animation = `slideInLeft 0.4s cubic-bezier(0.22, 1, 0.36, 1) ${i * delayStep}ms both`;
    });
}

/* ============================================
   4. Page Transition System
   ============================================ */

/**
 * 페이지 전환 오버레이 초기화 & 링크 인터셉트
 */
function initPageTransitions() {
    const overlay = document.getElementById('page-transition-overlay');
    if (!overlay) return;

    // 페이지 진입 시 leaving 애니메이션 (커튼 올라감)
    requestAnimationFrame(() => {
        overlay.classList.add('leaving');
        // 전환 완료 후 클래스 제거
        setTimeout(() => {
            overlay.classList.remove('leaving');
        }, 500);
    });

    // 내부 링크 클릭 인터셉트
    document.addEventListener('click', (e) => {
        const link = e.target.closest('a[href]');
        if (!link) return;

        const href = link.getAttribute('href');

        // 외부 링크, 앵커, JS 호출 등은 무시
        if (!href ||
            href.startsWith('#') ||
            href.startsWith('javascript:') ||
            href.startsWith('http') ||
            href.startsWith('mailto:') ||
            link.target === '_blank' ||
            link.hasAttribute('data-no-transition') ||
            e.ctrlKey || e.metaKey || e.shiftKey) {
            return;
        }

        e.preventDefault();

        // 커튼 내려오는 애니메이션
        overlay.classList.remove('leaving');
        overlay.classList.add('entering');

        // 애니메이션 완료 후 이동
        setTimeout(() => {
            window.location.href = href;
        }, 450);
    });
}

/* ============================================
   5. Scroll Animation (Intersection Observer)
   ============================================ */

function initScrollAnimations() {
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('is-visible');
                observer.unobserve(entry.target);
            }
        });
    }, {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    });

    document.querySelectorAll('.animate-on-scroll').forEach(el => {
        observer.observe(el);
    });
}

/* ============================================
   6. Count-Up Animation
   ============================================ */

/**
 * 숫자 카운트업 애니메이션
 * @param {HTMLElement} element - 대상 요소
 * @param {number} target - 목표 숫자
 * @param {number} duration - 애니메이션 시간 (ms)
 * @param {string} suffix - 숫자 뒤에 붙일 텍스트
 */
function animateCountUp(element, target, duration = 800, suffix = '') {
    if (!element) return;
    const start = 0;
    const startTime = performance.now();

    function update(currentTime) {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);
        // ease-out 커브
        const eased = 1 - Math.pow(1 - progress, 3);
        const current = Math.round(start + (target - start) * eased);
        element.textContent = current.toLocaleString() + suffix;

        if (progress < 1) {
            requestAnimationFrame(update);
        }
    }

    requestAnimationFrame(update);
}

/* ============================================
   7. Enhanced showNotification (overrides api-handler.js)
   ============================================ */

// Notification 스택 관리
let _notifStack = [];
const _notifGap = 8;

function showNotification(message, type = 'info', duration = 3000) {
    const toast = document.createElement('div');
    toast.className = `notification-toast toast-${type}`;

    // 아이콘 매핑
    const icons = { success: '✅', error: '❌', warning: '⚠️', info: 'ℹ️' };
    const icon = icons[type] || icons.info;

    toast.innerHTML = `
        <div style="display: flex; align-items: center; gap: 0.5rem;">
            <span style="font-size: 1.2rem;">${icon}</span>
            <span>${message}</span>
        </div>
        <div class="notif-progress" style="width: 100%;"></div>
    `;

    document.body.appendChild(toast);

    // 스택 위치 계산
    _notifStack.push(toast);
    _repositionNotifications();

    // 프로그레스 바 애니메이션
    const progressBar = toast.querySelector('.notif-progress');
    requestAnimationFrame(() => {
        progressBar.style.transitionDuration = duration + 'ms';
        progressBar.style.width = '0%';
    });

    // 자동 제거
    const dismissTimer = setTimeout(() => {
        _dismissNotification(toast);
    }, duration);

    // 클릭으로 즉시 제거
    toast.addEventListener('click', () => {
        clearTimeout(dismissTimer);
        _dismissNotification(toast);
    });
    toast.style.cursor = 'pointer';
}

function _dismissNotification(toast) {
    toast.classList.add('dismissing');
    setTimeout(() => {
        const idx = _notifStack.indexOf(toast);
        if (idx > -1) _notifStack.splice(idx, 1);
        toast.remove();
        _repositionNotifications();
    }, 350);
}

function _repositionNotifications() {
    let topOffset = 100;
    _notifStack.forEach((toast) => {
        toast.style.top = topOffset + 'px';
        topOffset += toast.offsetHeight + _notifGap;
    });
}

/* ============================================
   8. Auto-Init on DOMContentLoaded
   ============================================ */

document.addEventListener('DOMContentLoaded', () => {
    initPageTransitions();
    initScrollAnimations();
});
