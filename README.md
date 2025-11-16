# LLMetter

음성 일기 작성 및 감정 분석 웹 서비스

## 프로젝트 소개

LLMetter는 사용자의 음성을 통해 일기를 작성하고, AI가 감정을 분석하여 시각화해주는 웹 서비스입니다.

### 구현된 주요 기능

#### 백엔드 (완료)
- JWT 기반 인증/인가 (Access Token + Refresh Token)
- 음성 파일 업로드 및 AES-256-GCM 암호화 저장
- Whisper AI를 통한 STT (Speech-to-Text)
- Claude 4.5 Sonnet을 통한 감정 분석 (12개 카테고리, -20~+20 점수)
- 일기 CRUD API
- 감정 그래프 및 통계 API
- 비동기 처리 (Kotlin Coroutines)

#### 프론트엔드 (부분 완료)
- 홈페이지 (서비스 소개)
- 로그인 페이지 (Admin 계정)
- 대시보드 레이아웃
- JWT 토큰 자동 관리
- 반응형 디자인 (Tailwind CSS)

#### 추후 구현 예정
- 음성 녹음 UI (Web Audio API + Wavesurfer.js)
- 일기 목록 및 상세 페이지
- 감정 그래프 시각화 (Recharts)
- Google OAuth 로그인

## 기술 스택

### 백엔드
- Kotlin + Spring Boot 3.2.5
- Spring AI (Claude 4.5 Sonnet + Whisper)
- Spring Security + JWT
- Spring Data JPA + PostgreSQL
- Redis (Refresh Token 관리)
- Liquibase (DB Migration)
- AES-256-GCM (음성 파일 암호화)

### 프론트엔드
- React 18 + TypeScript + Vite
- Tailwind CSS v3
- Zustand (상태관리)
- React Router v6
- Axios + 자동 토큰 관리
- Recharts (차트, 추후 구현)
- Wavesurfer.js (오디오 파형, 추후 구현)

## 시작하기

### 사전 요구사항

- JDK 17 이상
- Docker & Docker Compose
- Node.js 18 이상
- npm 또는 yarn

### 환경 설정

1. 환경 변수 설정

```bash
cp .env.example .env
```

`.env` 파일을 열어 다음 값들을 설정하세요:

- `ANTHROPIC_API_KEY`: Claude API 키 (필수)
- `OPENAI_API_KEY`: OpenAI API 키 (Whisper용, 필수)
- `JWT_SECRET`: JWT 서명용 시크릿 키 (기본값 사용 가능)
- `ENCRYPTION_KEY`: AES-256 암호화 키 (기본값 사용 가능)

2. Docker 컨테이너 시작 (PostgreSQL + Redis)

```bash
docker-compose up -d
```

3. 데이터베이스 확인

데이터베이스는 Docker Compose가 자동으로 생성합니다.
- Database: llmetter
- Port: 5433 (host) → 5432 (container)
- User: moon-kun-woong
- Password: (없음, trust 모드)

### 빌드 및 실행

#### 백엔드

```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun
```

서버는 `http://localhost:8080`에서 실행됩니다.

#### 프론트엔드

```bash
cd frontend
npm install
npm run dev
```

프론트엔드는 `http://localhost:5173`에서 실행됩니다.

## 프로젝트 구조

```
src/main/kotlin/org/llmetter/
├── LLMetterApplication.kt          # 메인 애플리케이션
├── config/                         # 설정 파일
│   ├── SecurityConfig.kt
│   ├── RedisConfig.kt
│   └── CoroutineConfig.kt
├── domain/                         # 도메인 모델
│   ├── user/
│   │   ├── User.kt
│   │   ├── RefreshToken.kt
│   │   └── UserRepository.kt
│   ├── diary/
│   │   ├── DiaryEntry.kt
│   │   ├── VoiceRecording.kt
│   │   └── DiaryRepository.kt
│   └── emotion/
│       ├── EmotionAnalysis.kt
│       ├── EmotionCategory.kt
│       └── EmotionRepository.kt
├── controller/                     # REST 컨트롤러 (추가 예정)
├── service/                        # 비즈니스 로직 (추가 예정)
├── dto/                            # DTO (추가 예정)
└── util/                           # 유틸리티
    ├── JwtUtil.kt
    └── EncryptionUtil.kt
```

## API 문서

API 엔드포인트는 추후 추가될 예정입니다.

주요 엔드포인트:
- `POST /api/auth/google` - Google OAuth 로그인
- `POST /api/auth/admin` - Admin 로그인
- `POST /api/diaries` - 음성 업로드 및 일기 작성
- `GET /api/diaries` - 일기 목록 조회
- `GET /api/emotions/graph` - 감정 그래프 데이터

## 데이터베이스 스키마

ERD 및 테이블 상세 정보는 [DESIGN.md](./DESIGN.md)를 참조하세요.

## 개발 로드맵

### Phase 1: 백엔드 기반 구축 ✅
- [x] 프로젝트 초기 설정
- [x] DB 스키마 생성
- [x] Entity, Repository 작성
- [x] 기본 설정 (Security, Redis, Coroutine)
- [x] JWT 및 암호화 유틸리티

### Phase 2: 백엔드 핵심 기능 ✅
- [x] 인증/인가 서비스
- [x] 음성 업로드 API
- [x] Spring AI + Whisper STT 연동
- [x] Claude 감정 분석
- [x] 일기 CRUD API
- [x] 감정 그래프/통계 API

### Phase 3: 프론트엔드 (진행 중)
- [x] React 프로젝트 설정
- [x] 홈페이지 & 로그인
- [x] 기본 라우팅 및 상태 관리
- [ ] 녹음 UI
- [ ] 일기 목록/상세
- [ ] 감정 그래프 시각화

### Phase 4: 고도화
- [ ] 성능 최적화
- [ ] 테스트 코드
- [ ] 배포 준비

## Admin 계정

테스트용 Admin 계정:
```
Email: admin@llmetter.com
Password: qwe123
```

## API 엔드포인트

### 인증
- POST /api/auth/login
- POST /api/auth/refresh
- POST /api/auth/logout

### 일기
- POST /api/diaries (음성 업로드)
- GET /api/diaries (일기 목록)
- GET /api/diaries/{id} (일기 상세)
- PATCH /api/diaries/{id} (일기 수정)
- DELETE /api/diaries/{id} (일기 삭제)
- POST /api/diaries/{id}/retry-stt (STT 재처리)

### 감정 분석
- GET /api/emotions/graph (감정 그래프)
- GET /api/emotions/statistics (감정 통계)

자세한 API 명세는 `DESIGN.md`를 참고하세요.

## 라이선스

이 프로젝트는 개인 프로젝트입니다.
