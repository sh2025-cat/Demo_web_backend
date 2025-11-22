# Cat Board Backend

Spring Boot 기반의 메모 관리 REST API 서버입니다.

## 기술 스택

- **Java 21**
- **Spring Boot 3.5.7**
- **Spring Data JPA**
- **MySQL** (운영) / **H2** (테스트)
- **Gradle**
- **Docker**

## 프로젝트 구조

```
src/main/java/cat/board/
├── CatBoardBeApplication.java    # 애플리케이션 진입점
├── config/                       # 설정 클래스
│   └── WebConfig.java            # CORS 설정
├── controller/                   # REST 컨트롤러
│   ├── HealthController.java     # 헬스체크 API
│   └── MemoController.java       # 메모 CRUD API
├── service/                      # 비즈니스 로직
│   └── MemoService.java
├── repository/                   # 데이터 접근 계층
│   └── MemoRepository.java
├── entity/                       # JPA 엔티티
│   └── Memo.java
├── dto/                          # 데이터 전송 객체
│   ├── MemoRequest.java
│   └── MemoResponse.java
└── exception/                    # 예외 처리
    └── GlobalExceptionHandler.java
```

## API 엔드포인트

### Health Check

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/` | 루트 헬스체크 |
| GET | `/api/health` | 헬스체크 |

**Response:**
```json
{
  "status": "UP",
  "timestamp": "2025-01-01T12:00:00",
  "service": "cat-board-be"
}
```

### Memo API

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/memos` | 메모 생성 |
| GET | `/api/memos` | 메모 목록 조회 |
| DELETE | `/api/memos/{id}` | 메모 삭제 |

**POST /api/memos - Request:**
```json
{
  "content": "메모 내용"
}
```

**Response:**
```json
{
  "id": 1,
  "content": "메모 내용",
  "createdAt": "2025-01-01T12:00:00"
}
```

## 환경 설정

### 프로파일

| 프로파일 | 설명 | 데이터베이스 |
|---------|------|-------------|
| dev | 개발 환경 (기본값) | localhost MySQL |
| prod | 운영 환경 | 환경변수 기반 MySQL |
| test | 테스트 환경 | H2 In-Memory |

### 운영 환경 변수

```bash
SPRING_PROFILES_ACTIVE=prod
DB_HOST=your-rds-endpoint
DB_PORT=3306          # 기본값
DB_NAME=catboard
DB_USERNAME=admin
DB_PASSWORD=your-password
```

## 로컬 실행

### 요구사항
- Java 21
- MySQL 8.0+
- Gradle 8.x

### 실행 방법

```bash
# 개발 환경 (기본)
./gradlew bootRun

# 운영 환경
SPRING_PROFILES_ACTIVE=prod \
DB_HOST=localhost \
DB_NAME=catboard \
DB_USERNAME=root \
DB_PASSWORD=1234 \
./gradlew bootRun
```

## Docker

### 빌드

```bash
./gradlew clean bootJar
docker build -t cat-board-backend .
```

### 실행

```bash
docker run -d -p 8080:8080 \
  -e DB_HOST=your-db-host \
  -e DB_NAME=catboard \
  -e DB_USERNAME=admin \
  -e DB_PASSWORD=your-password \
  cat-board-backend
```

## 테스트

```bash
# 전체 테스트
./gradlew test

# 특정 테스트
./gradlew test --tests "MemoServiceTest"
```

## CI/CD

GitHub Actions를 통해 자동 빌드 및 배포가 진행됩니다.

1. **Test** - 단위 테스트 실행
2. **SAST** - Snyk 보안 스캔
3. **Build** - Docker 이미지 빌드 및 ECR 푸시

## 라이선스

Private Project - SoftBank 2025 Hackathon
