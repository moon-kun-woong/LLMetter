# LLMetter

음성 일기 작성 및 감정 분석 웹 서비스

## 프로젝트 소개

LLMetter는 사용자의 음성을 통해 일기를 작성하고, AI가 감정을 분석하여 시각화해주는 웹 서비스입니다.

### 주요 기능

#### 백엔드
- JWT 기반 인증/인가 (Access Token + Refresh Token)
- 음성 파일 업로드 및 AES-256-GCM 암호화 저장
- Whisper AI를 통한 STT (Speech-to-Text)
- Claude 4.5 Sonnet을 통한 감정 분석 (12개 카테고리, -20~+20 점수)
- 일기 CRUD API
- 감정 그래프 및 통계 API
- 비동기 처리 (Kotlin Coroutines)
- 일기 재처리 기능

#### 프론트엔드
- 홈페이지 (서비스 소개)
- 로그인 페이지 및 세션 관리
- 대시보드 (감정 그래프 시각화)
- 음성 녹음 UI (Web Audio API + Wavesurfer.js)
- 일기 목록 및 상세 페이지
- 감정 변화 그래프 (Recharts)
- 일기 재처리 UI
- JWT 토큰 자동 관리 및 새로고침 시 로그인 유지
- 반응형 디자인 (Tailwind CSS)
- 깔끔한 모노톤(흑백회색) UI

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
- Recharts (차트)
- Wavesurfer.js (오디오 파형)

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
├── controller/                     # REST 컨트롤러
│   ├── AuthController.kt
│   ├── DiaryController.kt
│   └── EmotionController.kt
├── service/                        # 비즈니스 로직
│   ├── AuthService.kt
│   ├── DiaryService.kt
│   ├── EmotionService.kt
│   ├── EmotionAnalysisService.kt
│   ├── VoiceService.kt
│   └── STTService.kt
├── dto/                            # DTO
│   ├── request/
│   └── response/
└── util/                           # 유틸리티
    ├── JwtUtil.kt
    └── EncryptionUtil.kt
```

## API 엔드포인트

### 인증
- `POST /api/auth/login` - 로그인
- `POST /api/auth/refresh` - 토큰 갱신
- `POST /api/auth/logout` - 로그아웃

### 일기
- `POST /api/diaries` - 음성 업로드
- `GET /api/diaries` - 일기 목록 조회
- `GET /api/diaries/{id}` - 일기 상세 조회
- `PATCH /api/diaries/{id}` - 일기 수정
- `DELETE /api/diaries/{id}` - 일기 삭제
- `POST /api/diaries/{id}/retry-stt` - STT 재처리

### 감정 분석
- `GET /api/emotions/graph` - 감정 그래프 데이터
- `GET /api/emotions/statistics` - 감정 통계

자세한 API 명세는 [DESIGN.md](./DESIGN.md)를 참고하세요.

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
- [x] 일기 재처리 기능

### Phase 3: 프론트엔드 ✅
- [x] React 프로젝트 설정
- [x] 홈페이지 & 로그인
- [x] 기본 라우팅 및 상태 관리
- [x] 녹음 UI
- [x] 일기 목록/상세
- [x] 감정 그래프 시각화
- [x] 일기 재처리 UI
- [x] 로그인 상태 유지
- [x] 모노톤 UI 디자인

### Phase 4: 고도화 (진행 중)
- [ ] 성능 최적화
- [ ] 테스트 코드
- [ ] 배포 준비
- [ ] Google OAuth 로그인

## 최근 업데이트

### 버그 수정
- 감정 분석 데이터 연결 및 저장 로직 개선
- 새로고침 시 로그인 상태 유지 기능 추가
- 감정 그래프 날짜 기준을 일기 작성일로 수정
- 일기 재처리 시 중복 키 에러 해결

### UI 개선
- 전체 UI를 깔끔한 모노톤(흑백회색)으로 통일
- 일기 재처리 기능 UI 추가
- 로그인 화면 보안 강화

### 보안 강화
- 암호화 키 SHA-256 해싱 적용
- 로그인 화면 계정 정보 노출 제거

## 라이선스

이 프로젝트는 개인 프로젝트입니다.


## 아키텍처 구성
![KakaoTalk_Photo_2025-11-24-22-44-55](https://github.com/user-attachments/assets/9d710e12-6904-4e40-9ee4-d42fb73f6375)

