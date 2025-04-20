# 🚀 Real-Time Delivery Platform

실시간 주문 및 배송 처리를 위한 백엔드 중심의 플랫폼입니다.  
JWT 기반 인증, Redis 캐싱, OAuth2 로그인, 테스트 코드, CI/CD 등 실무에서 사용하는 구조와 기술을 기반으로 구현됩니다.

---

## 📌 프로젝트 개요

| 항목 | 내용 |
|------|------|
| 프로젝트명 | Real-Time Delivery Platform |
| 성격 | 사이드 프로젝트 |
| 언어 | Java 17 |
| 프레임워크 | Spring Boot 3.x, Spring Security, JPA |
| 인프라 | AWS EC2, RDS(MySQL), S3, Docker |
| 인증 방식 | OAuth2 로그인 + JWT + Cookie 기반 인증 |
| 목표 | 실시간 처리, 캐싱, 테스트 전략 및 확장 가능한 백엔드 구조 구현 |

---

## ✅ 주요 기능

| 기능 | 설명 |
|------|------|
| OAuth2 로그인 | Google OAuth2 연동 후 JWT 발급 및 HttpOnly 쿠키 저장 |
| JWT 인증 필터 | JwtAuthenticationFilter로 쿠키의 토큰을 검증해 인증 처리 |
| 상품 목록 조회 | Redis 캐시를 통한 고속 응답, TTL 설정 및 무효화 전략 적용 예정 |
| 인증/인가 예외 처리 | RestAuthenticationEntryPoint 및 글로벌 예외 핸들러 적용 |
| 표준 응답 포맷 | 모든 응답은 ResponseDto로 통일 |
| 테스트 전략 | 기능 단위마다 단위 테스트 및 통합 테스트 분리 적용 |
| Redis TTL | 설정값을 외부 yml에서 받아와 Duration 형태로 관리 |
| CI/CD | GitHub Actions 기반 자동화 구성 예정 |

---

## 🧱 기술 스택

### 백엔드
- Java 17
- Spring Boot 3.x
- Spring Security (JWT + OAuth2 + Cookie)
- Spring Data JPA
- Validation
- Flyway (DB 마이그레이션)

### 인프라 및 DevOps
- AWS EC2, RDS(MySQL), S3
- Docker, GitHub Actions
- Redis (Spring Data Redis)
- Kafka (주문/결제 이벤트용, 예정)

### 테스트 & 문서화
- JUnit 5, Mockito
- WebMvcTest / SpringBootTest
- Swagger / OpenAPI 3.0
- Conventional Commits + CommitLint

---

## 📁 디렉토리 구조 (일부)
```bash
com.som.deliveryplatform
├── domain
│   ├── user
│   └── product
├── global
│   ├── auth
│   ├── common
│   ├── config
│   └── util.redis
```

---

## 🔍 진행 상황

- [x] 프로젝트 초기 세팅 및 Git 설정
- [x] Spring Boot + Gradle 세팅
- [x] Multi Profile (local, test, prod)
- [x] Flyway 도입 및 DB 마이그레이션 자동화
- [x] OAuth2 + JWT 로그인 흐름 구현
- [x] 인증 필터(JwtAuthenticationFilter) 구현
- [x] 상품 목록 조회 API + Redis 캐시 적용
- [x] TTL 설정값 yml 외부 관리 및 테스트 완료
- [ ] 상품 생성/수정 시 캐시 무효화 전략 적용 예정
- [ ] Kafka 연동 주문/결제 이벤트 처리 예정
- [ ] 관리자 대시보드 + 모니터링 도입 예정

---

## 🧪 테스트 전략

| 구분 | 설명 |
|------|------|
| 단위 테스트 | Service, Controller는 `MockMvc`, `Mockito`를 이용해 단위 테스트 |
| 통합 테스트 | 인증 필터, DB 연동은 `@SpringBootTest` 환경에서 통합 테스트 |
| 테스트 흐름 | 기능 구현 → 단위 테스트 → 통합 테스트 순으로 작성 |
| 테스트 환경 | H2 DB + Flyway 적용 + application-test.yml 사용 |

---

## ⚙️ 개발 원칙 및 구조 설계

- 모든 서비스는 interface 분리 설계 (특별한 사유가 있는 경우에만 예외)
- 모든 응답은 `ResponseDto` 기반 JSON 포맷
- 인증 실패 시 JSON 형태로 일관된 에러 반환
- 쿠키는 `HttpOnly` 속성을 이용해 클라이언트 접근 차단
- 테스트 코드 작성 시점은 기능 단위로 정의 (단계별 안내에 따라 작성)
- 테스트 환경에서는 `Embedded Redis`를 사용하지 않음. 단위 테스트는 Mock 기반

---

## 📌 기타 정보

- Git 커밋 메시지는 Conventional Commit 형식으로 작성 (`feat:`, `fix:`, `test:` 등)
- CI/CD 시에는 master/main 브랜치로 머지될 때 자동 배포 예정 (GitHub Actions 사용)
- 테스트 커버리지 및 코드 품질 유지를 위한 정기 회고 및 리팩토링 진행

---

## 👋 Author
SM N

Backend Developer / Interested in Distributed Systems, Observability, and Clean Architecture

📧 Email: ndanl4647@gmail.com

📌 GitHub: [https://github.com/som-bob](https://github.com/Sumsan38)


