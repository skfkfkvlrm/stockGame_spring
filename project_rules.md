# 📋 StockGame Spring 프로젝트 개발 규칙 및 가이드라인

본 문서는 프로젝트를 안정적이고 일관성 있게 유지보수하기 위해 제정된 코드 및 아키텍처 규칙입니다. 기존의 레거시 규칙부터 최근 백엔드 개선(0~3단계)을 통해 확립된 규칙, 그리고 향후 고도화(React 마이그레이션 등)를 위해 지켜야 할 규칙을 모두 포함합니다.

---

## 1. 기존 프로젝트 기본 규칙 (Legacy & Core)

- **아키텍처 패턴**: Controller - Service - Repository(Mapper)의 전통적인 3계층(3-Layer) 구조를 따른다.
- **학생 도메인 데이터베이스 연동 (MyBatis)**:
  - 학생 관련 데이터(`students`, `orders`, `transactions` 등)는 기존과 동일하게 **MyBatis**를 사용하여 관리한다.
  - MyBatis의 `map-underscore-to-camel-case: true` 설정에 따라, DB 컬럼(snake_case)과 Java 객체 필드(camelCase) 간의 자동 매핑을 적극 활용한다.
  - Mapper 인터페이스와 XML 쿼리 파라미터 바인딩 시 혼동을 피하기 위해 가급적 `@Param` 어노테이션을 명시적으로 사용한다.
- **학생 세션 기반 인증**:
  - 기존 컨트롤러에서 세션을 처리할 때, 직접 `session.getAttribute()`를 호출하는 대신 **`@SessionAttribute(name = "studentId", required = false)`** 어노테이션을 사용하여 의존성을 낮추고 가독성을 높인다.

---

## 2. 0~3단계 개선 과정에서 추가 확립된 규칙 (New Standards)

### 2.1. 하이브리드 ORM (MyBatis + JPA) 사용 규칙
- **학생 도메인**: 위 기본 규칙에 따라 MyBatis를 유지한다.
- **관리자/교사 도메인 (`AppUser`, `Role` 등)**: 새로 추가되는 보안/관리자 영역은 **Spring Data JPA**를 사용하여 생산성과 객체 지향성을 높인다.

### 2.2. Spring Security 이중 필터 체인(Dual Chain) 규칙
- 애플리케이션은 명확히 두 가지 인증 체계로 분리되어 구동된다.
  - **Chain 1 (`/admin/**`)**: 
    - `AppUserDetailsService` 및 Form 로그인 사용.
    - 보안 강화를 위해 `CookieCsrfTokenRepository`를 사용한 CSRF 보호가 강제 적용된다.
  - **Chain 2 (`그 외 학생 경로`)**: 
    - Spring Security의 개입을 최소화(`permitAll()`)하고, 레거시 HttpSession 기반 인증을 유지한다.

### 2.3. 부분 체결(Partial Fill) 엔진 무결성 유지 규칙
- `transactions` 테이블은 설계상 매수 주문(`buy_order_id`)과 매도 주문(`sell_order_id`)을 **1:1로만 연결**할 수 있다.
- **따라서 수량이 쪼개지는 부분 체결 발생 시 절대 기존 주문 ID 하나를 여러 거래에 재사용해서는 안 된다.**
- 체결된 수량만큼 **새로운 상태(`체결`)의 주문을 `INSERT`**하여 거래 내역에 매핑하고, 원래의 `대기` 주문은 남은 수량만큼만 **`UPDATE`**하는 분할(Split) 전략을 반드시 고수해야 한다.

### 2.4. 비밀번호 암호화 및 하위 호환(마이그레이션) 규칙
- 새로 가입하거나 변경하는 모든 사용자의 비밀번호는 `BCryptPasswordEncoder`를 통해 암호화 저장해야 한다.
- 테스트 데이터 등 **기존 평문 비밀번호 사용자**를 보호하기 위해, 로그인 로직 단에서 `$2a$`(BCrypt) 시작 여부를 검사하고, 평문 매칭 시 즉시 BCrypt로 업그레이드(UPDATE) 해주는 유연한 구조를 유지한다.

---

## 3. 추후 진행 단계(4~6단계 및 React 연동)를 위한 제안 규칙 (Future Guidelines)

### 3.1. 에러 처리 및 통합 API 응답 구조 (4단계 연관)
- **도메인 예외 분리**: Service 계층에서 오류 발생 시 단순 `String`을 반환하지 않고, `InsufficientPointException`, `OrderNotFoundException` 등 명확한 도메인 커스텀 예외(Exception)를 `throw` 하도록 구조를 개편한다.
- **글로벌 예외 처리**: `@RestControllerAdvice`를 도입하여 컨트롤러 밖으로 던져지는 예외를 일괄 포착해 일관된 JSON 형식으로 반환한다.
- **공통 응답 포맷**: 프론트엔드(React)와의 원활한 통신을 위해 모든 REST API는 `ApiResponse<T>` 형태의 래퍼 클래스(상태 코드, 메시지, 데이터 객체 포함)를 통해 일관성 있게 반환한다.

### 3.2. 프론트엔드 React 마이그레이션 규칙 (트랙 B 연관)
- **상태 관리**: 학생의 로그인 정보, 보유 자산(Point), 현재 쿠폰 개수 등 여러 컴포넌트에서 공유되는 전역 상태는 `Context API`나 `Zustand` 같은 가벼운 상태 관리 라이브러리를 통해 한 곳에서 관리한다.
- **API 레이어 캡슐화**: 컴포넌트 내부에 `axios.get`을 하드코딩하지 않고, 별도의 `api.js` (또는 커스텀 훅)로 분리하여 백엔드 URL 변경이나 토큰/세션 만료 처리(Interceptors)에 중앙집중적으로 대응한다.

### 3.3. 거래 무결성 보장 (Transaction Management)
- 주식 매매 로직은 주문 업데이트 + 트랜잭션 기록 + 포인트 차감 등 여러 테이블이 연쇄적으로 수정된다.
- 향후 기능이 복잡해지더라도 트랜잭션(Transaction)이 꼬이지 않도록, 변경이 일어나는 모든 Service 메서드에는 `@Transactional` 어노테이션을 꼼꼼하게 적용하고, Checked/Unchecked Exception 발생 시 롤백 정책을 철저히 확인한다.

### 3.4. 비동기 및 실시간 처리 (6단계 연관)
- 호가창 갱신이나 체결 알림 등 실시간성이 요구되는 기능은 기존 동기 방식의 HTTP API 대신 **WebSocket(STOMP)**을 사용한다.
- 데이터베이스 커밋이 완전히 끝난 이후에 웹소켓 메시지 큐로 데이터를 푸시(Push)하여 DB의 정보와 화면의 정보 간 싱크로율이 엇갈리지 않도록 설계한다.
