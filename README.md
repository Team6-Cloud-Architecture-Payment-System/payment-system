## Environment Variables Guide

이 프로젝트는 애플리케이션 실행 시 루트 경로의 `.env` 파일을 자동으로 읽습니다.
환경변수는 `src/main/resources/application.yml`에서 `${ENV_NAME:default-value}` 형식으로 참조됩니다.

### 1. `.env` 파일 생성

프로젝트 루트에서 아래처럼 예시 파일을 복사해 `.env`를 만듭니다.

```bash
cp .env.example .env
```

`.env` 파일은 Git에 포함되지 않으며, 팀에는 `.env.example`만 공유합니다.

### 2. 환경변수 입력

`.env.example`에 정의된 값을 실제 로컬 또는 운영 환경 값으로 채워주세요.

```env
PORTONE_API_SECRET=
PORTONE_STORE_ID=
PORTONE_CHANNEL_KG=
PORTONE_CHANNEL_TOSS=
JWT_SECRET=
JWT_VALIDITY=86400
```

### 3. 각 변수 설명

- `PORTONE_API_SECRET`: PortOne REST API Secret
- `PORTONE_STORE_ID`: PortOne Store ID
- `PORTONE_CHANNEL_KG`: KG Inicis 채널 키
- `PORTONE_CHANNEL_TOSS`: Toss Payments 채널 키
- `JWT_SECRET`: JWT 서명 시크릿 키
- `JWT_VALIDITY`: JWT 만료 시간(초), 기본값 `86400`

### 4. 적용 방식

- 애플리케이션 시작 시 `DotenvInitializer`가 `.env`를 로드합니다.
- 로드된 값은 Spring `Environment`에 우선순위 높게 등록됩니다.
- `.env`에 값이 없으면 `application.yml`의 기본값이 사용됩니다.

예시:

```yaml
portone:
  api:
    secret: ${PORTONE_API_SECRET:your-api-secret}
```

위 설정은 `.env`의 `PORTONE_API_SECRET`이 있으면 그 값을 사용하고, 없으면 `your-api-secret`을 사용합니다.

### 5. 실행

기본 프로필은 `local`이며, 로컬 실행 시 `application-local.yml` 설정이 함께 적용됩니다.

```bash
./gradlew bootRun
```

### 6. 주의사항

- `.env`는 민감정보가 포함될 수 있으므로 커밋하지 마세요.
- 실제 배포 환경에서는 데모용 기본값 대신 반드시 실제 시크릿 값을 사용하세요.
- `JWT_SECRET`은 충분히 길고 예측 불가능한 문자열로 설정하세요.
