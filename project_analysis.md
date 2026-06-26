# JwtApp 프로젝트 분석 문서

> Spring Boot 3.x 기반의 JWT 인증/인가 실습 프로젝트

---

## 📋 프로젝트 개요

| 항목 | 내용 |
|------|------|
| **프로젝트명** | JwtApp |
| **Group ID** | com.oopsw |
| **Artifact ID** | JwtApp |
| **버전** | 0.0.1-SNAPSHOT |
| **Spring Boot 버전** | 3.5.15 |
| **Java 버전** | 17 |
| **서버 포트** | 8889 |
| **데이터베이스** | MariaDB (`jdbc:mariadb://127.0.0.1:3306/mydb`) |
| **빌드 도구** | Maven |

---

## 🎯 프로젝트 목적

Spring Security와 JWT(JSON Web Token)를 결합한 **Stateless 인증/인가 시스템** 구현 실습 프로젝트입니다.

- 세션 없이 JWT 토큰만으로 인증 상태를 유지하는 구조
- 역할 기반 접근 제어(RBAC): `USER`, `MANAGER`, `ADMIN`
- CORS 정책 설정 및 REST API 설계

---

## 📦 기술 스택 & 의존성

| 분류 | 라이브러리 | 버전 |
|------|-----------|------|
| **JWT** | com.auth0:java-jwt | 4.4.0 |
| **웹** | spring-boot-starter-web | (Boot 관리) |
| **보안** | spring-boot-starter-security | (Boot 관리) |
| **JPA** | spring-boot-starter-data-jpa | (Boot 관리) |
| **DB** | mariadb-java-client | (Boot 관리) |
| **코드 간소화** | lombok | (Boot 관리) |
| **테스트** | spring-boot-starter-test | (Boot 관리) |
| **보안 테스트** | spring-security-test | (Boot 관리) |

---

## 🗂️ 패키지 구조

```
src/main/java/com/oopsw/jwtapp/
├── JwtAppApplication.java              # 애플리케이션 진입점
│
├── auth/                               # 보안 & JWT 관련 클래스
│   ├── AppUserDetails.java             # UserDetails 구현체
│   ├── AppUserDetailsService.java      # UserDetailsService 구현체
│   ├── CorsConfig.java                 # CORS 설정
│   ├── JwtAuthenticationFilter.java    # 로그인 처리 필터
│   ├── JwtAuthorizationFilter.java     # 요청별 인가 필터
│   ├── JwtProperties.java              # JWT 상수 정의 인터페이스
│   └── SecurityConfig.java             # Spring Security 설정
│
├── controller/                         # REST API 컨트롤러
│   ├── RestApiController.java          # API 엔드포인트
│   └── UserRequest.java                # 회원가입 요청 DTO
│
├── repository/                         # 데이터 액세스 계층
│   ├── User.java                       # JPA 엔티티
│   └── UserRepository.java             # JPA Repository 인터페이스
│
└── service/                            # 비즈니스 로직 계층
    ├── UserService.java                # 회원 서비스
    └── UserVO.java                     # 서비스 계층 VO
```

---

## 🔐 인증/인가 흐름

### 1. 로그인 흐름 (`JwtAuthenticationFilter`)

```
클라이언트 → POST /login
     ↓
JwtAuthenticationFilter.attemptAuthentication()
     ↓ (JSON Body에서 username, password 파싱)
AuthenticationManager.authenticate()
     ↓ (AppUserDetailsService 호출 → DB 조회)
successfulAuthentication()
     ↓
JWT 토큰 생성 (HMAC256, 유효기간 1시간)
     ↓
Response Header: Authorization: Bearer <JWT>
```

### 2. API 요청 흐름 (`JwtAuthorizationFilter`)

```
클라이언트 → 모든 API 요청 (Authorization: Bearer <JWT>)
     ↓
JwtAuthorizationFilter.doFilterInternal()
     ↓ (토큰 추출 및 서명 검증)
username 추출 → DB 조회
     ↓
SecurityContextHolder에 Authentication 저장
     ↓
권한 체크 → 허가된 경우 컨트롤러로 진행
```

---

## 🏗️ 주요 컴포넌트 상세

### `JwtProperties` (인터페이스)

JWT 관련 상수를 정의합니다.

| 상수 | 값 | 설명 |
|------|----|------|
| `SECRET` | `"oopsw"` | HMAC256 서명 비밀키 |
| `EXPIRATION_TIME` | `1000 * 60 * 60` (1시간) | 토큰 만료 시간 (ms) |
| `TOKEN_PREFIX` | `"Bearer "` | 토큰 접두사 |
| `HEADER_STRING` | `"Authorization"` | HTTP 헤더명 |

> **⚠️ 주의:** SECRET 키(`"oopsw"`)가 소스코드에 하드코딩되어 있습니다.
> 운영 환경에서는 반드시 환경변수나 Vault로 분리해야 합니다.

---

### `SecurityConfig`

Spring Security의 핵심 설정 클래스입니다.

| 설정 | 값 |
|------|----|
| CSRF | **비활성화** (REST API 환경) |
| 세션 정책 | **STATELESS** (세션 미사용) |
| Form 로그인 | **비활성화** |
| HTTP Basic | **비활성화** |

**URL 접근 권한 매핑:**

| URL 패턴 | 필요 권한 |
|---------|---------|
| `/api/v1/user/**` | 인증된 사용자 (모든 로그인 유저) |
| `/api/v1/manager/**` | `ROLE_ADMIN` 또는 `ROLE_MANAGER` |
| `/api/v1/admin/**` | `ROLE_ADMIN` 전용 |
| 그 외 | 모두 허용 |

---

### `CorsConfig`

| 설정 | 값 |
|------|----|
| 허용 출처 | `*` (모든 오리진) |
| 허용 헤더 | `*` |
| 허용 메서드 | `*` (GET, POST, PUT, DELETE 등) |
| Credentials | `true` |
| 노출 헤더 | `Authorization`, `Access-Control-Allow-Headers` |

---

### `User` (JPA 엔티티)

```java
@Entity
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;    // BCrypt 암호화 저장
    private String email;
    private String role;        // "ROLE_USER", "ROLE_MANAGER", "ROLE_ADMIN"
    @CreationTimestamp
    private Timestamp created;
}
```

---

### `UserRepository` (JPA Repository)

```java
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);       // 이메일로 조회
    User findByUsername(String username); // 사용자명으로 조회
}
```

---

## 🌐 API 엔드포인트

| 메서드 | URL | 인증 필요 | 설명 |
|--------|-----|----------|------|
| `GET` | `/home` | ❌ | 공개 홈 메시지 |
| `POST` | `/join` | ❌ | 회원가입 |
| `POST` | `/login` | ❌ | 로그인 → JWT 발급 (Spring Security 자동 처리) |
| `GET` | `/api/v1/user` | ✅ USER 이상 | 인증 사용자 전용 |
| `GET` | `/api/v1/manager` | ✅ MANAGER 이상 | 매니저 전용 |
| `GET` | `/api/v1/admin` | ✅ ADMIN 전용 | 관리자 전용 |

### 회원가입 요청 예시

```json
POST /join
Content-Type: application/json

{
  "username": "john",
  "password": "secret123",
  "email": "john@example.com"
}
```

### 로그인 요청 예시

```json
POST /login
Content-Type: application/json

{
  "username": "john",
  "password": "secret123"
}
```

### 로그인 성공 응답

```
HTTP Response Header:
  Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...

Response Body:
  {message=success}
```

---

## 📊 클래스 관계도

```
[클라이언트]
    │
    ├──(POST /login)──▶ [JwtAuthenticationFilter]
    │                         │
    │                         ▼
    │                  [AuthenticationManager]
    │                         │
    │                         ▼
    │                  [AppUserDetailsService]
    │                         │
    │                         ▼
    │                   [UserRepository] ──▶ [(MariaDB)]
    │                         │
    │                  [JWT 토큰 생성 & 반환]
    │
    └──(API 요청 + JWT)──▶ [JwtAuthorizationFilter]
                                 │
                          [토큰 서명 검증]
                                 │
                          [UserRepository] ──▶ [(MariaDB)]
                                 │
                    [SecurityContextHolder 저장]
                                 │
                          [RestApiController]
                                 │
                           [UserService]
```

---

## ⚙️ 애플리케이션 설정 (`application.yml`)

```yaml
spring:
  security:
    user:
      name: admin
      password: 1234
  application:
    name: JwtApp
  datasource:
    driver-class-name: org.mariadb.jdbc.Driver
    url: jdbc:mariadb://127.0.0.1:3306/mydb
    username: mydbid
    password: mydbpw
  jpa:
    open-in-view: false
    generate-ddl: true
    show-sql: true
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        show_sql: true
        format_sql: true

server:
  port: 8889
```

> **📌 참고:** `ddl-auto: update` 설정으로 애플리케이션 실행 시 `User` 엔티티를 기반으로 DB 테이블이 자동 생성/수정됩니다.

---

## 🔄 서비스 계층 동작

### 회원가입 (`UserService.join`)

1. `username` 중복 여부 확인 (`findByUsername`)
2. 이미 존재하면 `RuntimeException("Username 이미 존재")` 발생
3. **BCryptPasswordEncoder**로 비밀번호 암호화
4. `role = "ROLE_USER"` 로 자동 설정
5. DB에 저장

> **💡 팁:** 현재 회원가입 시 역할이 항상 `ROLE_USER`로 고정됩니다.
> ADMIN, MANAGER 계정은 직접 DB에서 `role` 컬럼을 수정해야 합니다.

---

## 🚨 개선 포인트

| 항목 | 현재 상태 | 권장 개선 방향 |
|------|----------|---------------|
| JWT SECRET 키 | 소스코드 하드코딩 (`"oopsw"`) | `application.yml` 환경변수로 분리 |
| 역할 관리 | 가입 시 `ROLE_USER` 고정 | 관리자 계정 초기 데이터(DataInit) 구성 |
| 토큰 만료 처리 | 예외 처리 없음 | 만료 시 명시적 401 응답 처리 |
| Refresh Token | 미구현 | Access/Refresh Token 분리 구현 고려 |
| 비밀번호 정책 | Validation 없음 | `@Valid` + 최소 길이 제약 추가 |
| UserVO | 불필요한 JPA import 존재 | 순수 DTO로 정리 |
| 예외 처리 | 전역 처리 없음 | `@ControllerAdvice`로 전역 예외 핸들러 추가 |

---

## 🚀 실행 방법

```bash
# Maven Wrapper로 실행 (Windows)
mvnw.cmd spring-boot:run

# 또는 JAR 빌드 후 실행
mvnw.cmd clean package
java -jar target/JwtApp-0.0.1-SNAPSHOT.jar
```

서버 시작 후 `http://localhost:8889` 로 접근합니다.
