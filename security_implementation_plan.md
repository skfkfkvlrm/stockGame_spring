# Spring Security 구현 계획 및 결과 — stockGame_spring

## 배경 및 핵심 설계 결정

현재 프로젝트에는 **두 개의 다른 인증 방식**이 공존합니다.

| 대상 | 현재 방식 | Security 도입 후 |
|---|---|---|
| **학생 (USER)** | 수동 HttpSession (MemberController) | HttpSession 유지 + Security permitAll로 통과 |
| **관리자 (MANAGER/ADMIN)** | 없음 | Spring Security Form Login 신규 적용 |

학생 인증을 Spring Security UsernamePasswordAuthenticationFilter로 전환하면 기존 JSP/HTML 폼 구조, 세션 처리 로직이 전부 영향을 받기 때문에, **학생 로그인을 건드리지 않고** MANAGER/ADMIN 계정만 Spring Security로 관리하도록 이중 FilterChain 구조로 설계하여 적용을 완료했습니다.

---

## 역할(Role) 정의

| Role | 대상 | 접근 가능 경로 |
|---|---|---|
| `ROLE_USER` | 학생 | 기존 모든 경로 (세션 인증) |
| `ROLE_MANAGER` | 운영자 | `/admin/**` (ADMIN과 공유) |
| `ROLE_ADMIN` | 최고 관리자 | `/admin/**` 전체 |

---

## 신규 생성 파일 목록 (구현 완료)

```
src/main/java/com/skfkfkvlrm/stockgame_spring/
├── config/
│   ├── SecurityConfig.java          Security 핵심 설정 (이중 FilterChain)
│   └── DataInitializer.java         ADMIN/MANAGER 초기 계정 자동 생성
├── domain/
│   ├── AppUser.java                 JPA 엔티티 (MANAGER/ADMIN 계정용 app_users 테이블)
│   └── Role.java                    Enum (ROLE_USER, ROLE_MANAGER, ROLE_ADMIN)
├── repository/
│   └── AppUserRepository.java       Spring Data JPA Repository
└── auth/
    ├── AppUserDetails.java          UserDetails 구현체
    └── AppUserDetailsService.java   UserDetailsService 구현체
```

---

## 상세 구현 내용

### 1. `Role.java` & `AppUser.java` (엔티티)
- `Role` Enum을 통해 사용자 권한을 세 가지로 분리.
- `AppUser` 엔티티를 생성하고 기존 `Student`와 분리된 `app_users` 테이블에 매핑하여 책임을 분리. 로그인 ID(username), BCrypt 해싱된 비밀번호(password), 역할(role) 저장.

### 2. `AppUserRepository.java` (리포지토리)
- `username` 기반으로 사용자를 조회할 수 있도록 `findByUsername` 메서드 제공.
- 초기 데이터 생성 중복 방지를 위한 `existsByUsername` 메서드 제공.

### 3. `AppUserDetails.java` & `AppUserDetailsService.java` (Security 인증)
- `AppUser` 엔티티를 감싸는 `UserDetails` 구현체.
- 폼 로그인 요청 시 `AppUserDetailsService`가 DB를 조회하여 계정을 반환.

### 4. `SecurityConfig.java` (이중 FilterChain 핵심 설정)
- **Chain 1 (`@Order(1)`)**: `/admin/**` 경로 전용. Form Login, 로그아웃 처리, 권한 체크(MANAGER, ADMIN) 담당.
- **Chain 2 (`@Order(2)`)**: 나머지 모든 경로. `permitAll()`을 통해 통과시키며, 기존 컨트롤러에 작성된 HttpSession 기반 로그인 방식이 동작하도록 간섭 최소화.
- `BCryptPasswordEncoder` Bean 등록.

### 5. `DataInitializer.java` (초기 데이터)
- 서버 실행 시 `admin`(비밀번호: `admin1234`), `manager`(비밀번호: `manager1234`) 계정이 `app_users` 테이블에 없으면 자동 생성.

### 6. 관리자용 View & Controller
- `AdminController`: 관리자 로그인 페이지, 대시보드, 학생/종목/쿠폰 관리 페이지 라우팅 구성.
- `static/admin/login.html`: 다크 글래스모피즘 테마의 관리자용 로그인 폼. `/admin/login-process` 로 요청 전송.
- `static/admin/dashboard.html`: 관리자 대시보드 구조 작성. (사이드바, 요약 카드, 로그아웃 기능 포함)

---

## 영향 받는 기존 파일
- 기존 학생 기능 관련 코드나 세션 처리는 **전혀 건드리지 않음**.
- `application.yaml` 내 불필요해진 `spring.web` 속성 제거. (사용자 요청에 따라 반영됨)

---

## 검증 내역
1. **서버 시작 시 계정 생성**: DB `app_users` 테이블에 ADMIN, MANAGER 계정이 정상적으로 생성되는지 확인 필요.
2. **관리자 로그인**: `http://localhost:8882/admin/login` 에서 정상 로그인 후 대시보드 이동.
3. **학생 로그인**: `http://localhost:8882/members/login` 등 기존 방식이 문제없이 작동하는지 점검.
4. **접근 제어**: MANAGER 권한으로 ADMIN 전용인 `/admin/stocks` 접속 시도 시 403 Forbidden 발생 확인 필요.
