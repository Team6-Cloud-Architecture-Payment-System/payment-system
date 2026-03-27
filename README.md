# 🐾 견생보감

> **단 한 번의 끊김 없는 결제** — 강아지를 위한 모든 것을 한 곳에서

<br>

## 📌 프로젝트 소개

**견생보감**은 강아지 용품을 편리하게 구매할 수 있는 이커머스 서비스입니다.  
PortOne 결제 연동과 웹훅 기반의 안정적인 결제 처리를 핵심으로, 상품 조회부터 주문·결제·환불까지 전 과정을 지원합니다.

- **배포 URL**: https://api.dogpedia.store/
- **개발 기간**: 2025.03.16 ~ 2025.03.27

<br>

## 👥 팀 소개

> 팀명: **6하은칙**

<br>

## 🛠 기술 스택

### Frontend
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=flat-square&logo=javascript&logoColor=black)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=flat-square&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=flat-square&logo=css3&logoColor=white)
![PortOne](https://img.shields.io/badge/PortOne_SDK-6C47FF?style=flat-square)

- Vanilla JavaScript
- HTML / CSS
- PortOne SDK (결제 UI 연동)

### Backend
![Java](https://img.shields.io/badge/Java_17-007396?style=flat-square&logo=openjdk&logoColor=white)
![SpringBoot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white)

- Java 17
- Spring Boot
- Webhook (PortOne 이벤트 수신)

### DB / 인프라
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)
![AWS EC2](https://img.shields.io/badge/AWS_EC2-FF9900?style=flat-square&logo=amazonaws&logoColor=white)
![AWS RDS](https://img.shields.io/badge/AWS_RDS-527FFF?style=flat-square&logo=amazonrds&logoColor=white)

- AWS VPC / EC2 / RDS (MySQL)
- Caddy (Reverse Proxy, SSL/TLS, HTTPS:443)
- 도메인: `api.dogpedia.store` (가비아)
- Allowed Ports: `80`, `443`

### CI/CD
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white)

- GitHub Actions

<br>

## ✨ 주요 기능

### 🔐 회원가입 / 로그인 (JWT 인증)
- 이메일 기반 회원가입 및 로그인
- JWT AccessToken 발급 및 Bearer 인증
- 로그아웃 시 토큰 무효화
- 내 정보 조회 (이름, 이메일, 연락처, 포인트)

### 🛍️ 상품 조회
- 페이지네이션 기반 상품 목록 조회
- 상품 단건 상세 조회 (가격, 재고, 카테고리)

### 📦 주문 생성 및 관리
- 복수 상품 동시 주문 생성
- 포인트 사용 적용 (보유 포인트 부족 시 오류 처리)
- 재고 부족, 수량 오류 등 유효성 검증
- 주문 내역 목록 / 상세 조회 (페이지네이션)
- 주문 확정 처리

### 💳 PortOne 결제 연동 (웹훅 포함)
- PortOne SDK 기반 결제 시도 및 확정
- 결제 금액 서버 검증 (DB 저장 금액 vs 실 결제 금액 비교)
- 웹훅(`POST /api/payments/webhook`)으로 결제 이벤트 실시간 수신
- PortOne API 직접 결제 상태 검증 (`api.portone.io`)
- 결제 취소 처리

### 💰 포인트
- 주문 시 보유 포인트 차감 적용
- 내 정보 조회 시 현재 포인트 확인

### 🔄 환불
- 환불 요청 및 PortOne 실 결제 취소 연동 (전액 환불)
- 환불 가능 상태 검증 (결제 완료 / 주문 완료 상태에서만 가능)
- 주문 확정 이후 환불 불가 처리
- 특정 주문 환불 내역 조회
- 본인 전체 환불 내역 조회

<br>

## 📡 API 명세

> Base URL: `https://api.dogpedia.store`  
> 인증이 필요한 API는 Header에 `Authorization: Bearer {token}` 포함

### 🔐 인증 (Auth)

| Method | Endpoint | Description | 인증 |
|--------|----------|-------------|------|
| POST | `/api/auth/signup` | 회원가입 | ❌ |
| POST | `/api/auth/login` | 로그인 | ❌ |
| POST | `/api/auth/logout` | 로그아웃 | ✅ |
| GET | `/api/auth/me` | 내 정보 조회 | ✅ |

<details>
<summary>POST /api/auth/login — 로그인</summary>

**Request Body**
```json
{
  "email": "user@example.com",
  "password": "securePassword123!"
}
```

**Response**
```json
{
  "status": "SUCCESS",
  "message": "요청이 성공했습니다.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1...",
    "grantType": "Bearer"
  }
}
```
</details>

---

### 🛍️ 상품 (Product)

| Method | Endpoint | Description | 인증 |
|--------|----------|-------------|------|
| GET | `/api/products` | 상품 목록 조회 (페이지네이션) | ❌ |
| GET | `/api/products/{productId}` | 상품 단건 조회 | ❌ |

<details>
<summary>GET /api/products — 상품 목록 조회</summary>

**Response**
```json
{
  "status": "SUCCESS",
  "message": "요청이 성공했습니다.",
  "data": {
    "products": [
      {
        "productId": 101,
        "productName": "맛있는 강아지 사료 500g",
        "price": 12000,
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
</details>

---

### 📦 주문 (Order)

| Method | Endpoint | Description | 인증 |
|--------|----------|-------------|------|
| POST | `/api/orders` | 주문 생성 | ✅ |
| GET | `/api/orders` | 주문 내역 목록 조회 | ✅ |
| GET | `/api/orders/{orderId}` | 주문 상세 조회 | ✅ |
| POST | `/api/orders/{orderId}/confirm` | 주문 확정 | ✅ |

<details>
<summary>POST /api/orders — 주문 생성</summary>

**Request Body**
```json
{
  "orderItems": [
    { "productId": 1, "quantity": 2 },
    { "productId": 3, "quantity": 1 }
  ],
  "usedPoint": 3000
}
```

**Response**
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

**Error Codes**

| Status | Code | Description |
|--------|------|-------------|
| 404 | `PRODUCT_NOT_FOUND` | 상품을 찾을 수 없음 |
| 400 | `INVALID_ORDER_QUANTITY` | 주문 수량은 1개 이상 |
| 400 | `EMPTY_ORDER_ITEMS` | 주문 상품 최소 1개 이상 |
| 409 | `INSUFFICIENT_STOCK` | 재고 부족 |
| 400 | `INSUFFICIENT_POINT` | 보유 포인트 부족 |
</details>

---

### 💳 결제 (Payment)

| Method | Endpoint | Description | 인증 |
|--------|----------|-------------|------|
| POST | `/api/orders/{orderId}/payments` | 결제 시도 (결제 테이블 생성) | ❌ |
| POST | `/api/payments/{paymentId}/confirm` | 결제 확정 (금액 검증 후 PAID 처리) | ❌ |
| POST | `/api/payments/webhook` | 웹훅 수신 (PortOne 이벤트) | ❌ |
| GET | `/api/payments/{paymentId}/verify` | 결제 검증 (우리 서버) | ❌ |
| POST | `/api/payments/{paymentId}/cancel` | 결제 취소 | ✅ |

<details>
<summary>POST /api/payments/webhook — 웹훅 수신</summary>

PortOne에서 결제 이벤트 발생 시 해당 엔드포인트로 이벤트를 수신합니다.

**Request Header**
```
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
    "storeId": "store-ae356798-...",
    "paymentId": "example-payment-id",
    "transactionId": "55451513-..."
  }
}
```

**Response**: `200 OK`
</details>

---

### 🔄 환불 (Refund)

| Method | Endpoint | Description | 인증 |
|--------|----------|-------------|------|
| POST | `/api/payments/{paymentId}/refunds` | 환불 요청 | ✅ |
| GET | `/api/orders/{orderId}/refunds` | 특정 주문 환불 내역 조회 | ✅ |
| GET | `/api/refunds/me` | 본인 전체 환불 내역 조회 | ✅ |

<details>
<summary>POST /api/payments/{paymentId}/refunds — 환불 요청</summary>

**Request Body**
```json
{
  "paymentId": 1,
  "refundPrice": 300000,
  "refundReason": "배송이 너무 늦게 왔어요."
}
```

**Response**
```json
{
  "status": "SUCCESS",
  "message": "환불이 완료되었습니다.",
  "data": {
    "id": 1,
    "refundReason": "배송이 너무 늦게 왔어요.",
    "refundStatus": "환불 완료",
    "refundCreatedAt": "2026-03-03T15:30:00"
  }
}
```

**Error Codes**

| Status | Code | Description |
|--------|------|-------------|
| 401 | `UNAUTHORIZED` | 인증 토큰 만료 |
| 403 | `ACCESS_DENIED` | 접근 권한 없음 |
| 404 | `PAYMENT_NOT_FOUND` | 결제 정보 없음 |
| 400 | `ALREADY_REFUNDED` | 이미 환불 처리됨 |
| 400 | `INVALID_PAYMENT_STATUS` | 결제 완료 상태에서만 환불 가능 |
| 400 | `INVALID_ORDER_STATUS` | 주문 완료 상태에서만 환불 가능 |
| 500 | `EXTERNAL_API_ERROR` | PortOne 통신 실패 |
| 500 | `COMPENSATION_TRANSACTION_FAILED` | 보상 트랜잭션 수행됨 |
</details>

<br>

## 🏗 아키텍처

```
Client (Vanilla JS)
    │
    │ HTTPS (443)
    ▼
Caddy (Reverse Proxy + SSL/TLS)
    │
    ▼
Spring Boot App (EC2)
    ├── MySQL (RDS)
    └── PortOne API ←──→ Webhook (/api/payments/webhook)
```

**AWS 구성**
- **VPC**: 네트워크 격리 환경
- **EC2**: 애플리케이션 서버
- **RDS (MySQL)**: 데이터베이스
- **Caddy**: HTTPS 리버스 프록시 (포트 443)
- **도메인**: `api.dogpedia.store` (가비아)

<br>

## ⚙️ CI/CD 파이프라인

```
GitHub Push
    │
    ▼
GitHub Actions (CI)
    ├── 빌드 및 테스트
    └── EC2 서버 배포
```

<br>

## 🚀 실행 방법

### 사전 요구사항
- Java 17
- MySQL

### 1. 저장소 클론

```bash
git clone https://github.com/Team6-Cloud-Architecture-Payment-System/payment-system.git
cd payment-system
```

### 2. `.env` 파일 생성

이 프로젝트는 애플리케이션 실행 시 루트 경로의 `.env` 파일을 자동으로 읽습니다.  
환경변수는 `src/main/resources/application.yml`에서 `${ENV_NAME:default-value}` 형식으로 참조됩니다.

프로젝트 루트에서 예시 파일을 복사해 `.env`를 만듭니다.

```bash
cp .env.example .env
```

> `.env` 파일은 Git에 포함되지 않으며, 팀에는 `.env.example`만 공유합니다.

### 3. 환경변수 입력

`.env.example`에 정의된 값을 실제 환경 값으로 채워주세요.

```env
PORTONE_API_SECRET=
PORTONE_STORE_ID=
PORTONE_CHANNEL_KG=
PORTONE_CHANNEL_TOSS=
JWT_SECRET=
JWT_VALIDITY=86400
```

| 변수명 | 설명 |
|--------|------|
| `PORTONE_API_SECRET` | PortOne REST API Secret |
| `PORTONE_STORE_ID` | PortOne Store ID |
| `PORTONE_CHANNEL_KG` | KG Inicis 채널 키 |
| `PORTONE_CHANNEL_TOSS` | Toss Payments 채널 키 |
| `JWT_SECRET` | JWT 서명 시크릿 키 |
| `JWT_VALIDITY` | JWT 만료 시간(초), 기본값 `86400` |

### 4. 적용 방식

- 애플리케이션 시작 시 `DotenvInitializer`가 `.env`를 로드합니다.
- 로드된 값은 Spring `Environment`에 우선순위 높게 등록됩니다.
- `.env`에 값이 없으면 `application.yml`의 기본값이 사용됩니다.

```yaml
portone:
  api:
    secret: ${PORTONE_API_SECRET:your-api-secret}
```

### 5. 실행

기본 프로필은 `local`이며, 로컬 실행 시 `application-local.yml` 설정이 함께 적용됩니다.

```bash
./gradlew bootRun
```

### ⚠️ 주의사항

- `.env`는 민감정보가 포함될 수 있으므로 **커밋하지 마세요.**
- 실제 배포 환경에서는 데모용 기본값 대신 반드시 실제 시크릿 값을 사용하세요.
- `JWT_SECRET`은 충분히 길고 예측 불가능한 문자열로 설정하세요.
