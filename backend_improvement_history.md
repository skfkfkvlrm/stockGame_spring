# 🚀 StockGame Spring - 백엔드 개선 작업 진행 로그

본 문서는 `project_todo_summary.md`의 백엔드 개선 로드맵에 따라 **현재까지 성공적으로 완료된 작업(0단계 ~ 3단계)**의 핵심 수정 사항과 내역을 요약한 문서입니다.

---

## 🔴 0단계: 치명적 버그 수정 (즉시 조치)
가장 시급했던 시스템 상의 핵심 버그 2건을 최우선으로 해결했습니다.

1. **`cancelOrder` (주문 취소) 시 포인트 환불 불가 버그 수정**
   - **문제**: 문자열 `"BUY"`와 한국어 Enum `OrderStatus.매수`를 비교하여 항상 `false`가 반환되어 환불이 진행되지 않음.
   - **해결**: `OrderStatus.매수.name().equals(order.getContent())`로 Enum의 문자열 이름을 추출해 비교하도록 로직 정상화.
2. **세션 검증 로직 무력화 취약점 패치**
   - **문제**: 컨트롤러에서 `setAttribute`로 세션을 강제 주입한 직후 검증하여 비로그인 사용자도 주문이 가능한 심각한 오류 존재.
   - **해결**: `StockOrderController`의 파라미터에 `@SessionAttribute(name = "studentId", required = false)`를 도입하고, `null`일 경우 `/login`으로 리다이렉트하도록 깔끔하게 구조 개편.

---

## 🛠️ 1단계: Spring Security 기반 관리자 인증 체계 구축
기존의 학생(Student) 세션 인증 체계를 건드리지 않고, 관리자(Admin/Manager) 전용 보안 환경을 분리하여 구축했습니다.

1. **이중 필터 체인(Dual Filter Chain) 도입 (`SecurityConfig.java`)**
   - **Chain 1 (`@Order(1)`)**: `/admin/**` 경로 전용. Form 로그인 및 AppUserDetailsService 인증 사용. (관리자용)
   - **Chain 2 (`@Order(2)`)**: 그 외 모든 학생 경로. 기존 HttpSession 체계를 100% 호환 유지.
2. **관리자 엔티티 및 권한 설정**
   - JPA를 적용하여 `AppUser` 엔티티와 Repository 생성.
   - 서버 기동 시 `DataInitializer`를 통해 최초 관리자 계정(`admin` / `admin1234`)이 자동 생성되도록 구현.
   - *(참고: 교사(Teacher) 액터는 당장 필요치 않다는 요청에 따라 생략함)*

---

## ⚙️ 2단계: 핵심 비즈니스 로직 - 부분체결(Partial Fill) 엔진 개편
가장 까다로웠던 주문 매칭 엔진을 전면 리팩터링하여 실제 주식 시장과 유사한 거래가 가능해졌습니다.

1. **오류 수정 (동일 포지션 매칭 버그)**
   - 매수자가 '매수 대기'를, 매도자가 '매도 대기'를 검색하던 치명적인 쿼리 버그를, 반대 포지션(`content`)을 제대로 검색하도록 쿼리 파라미터(`@Param`) 명시 및 로직 수정.
2. **부분 체결(Order Split) 로직 구현 (`StockOrderServiceImpl.java`)**
   - 수량이 일치하지 않아도 가격이 맞으면 여러 대기 주문건과 **순차적으로 매칭**되며 거래되도록 반복문(Loop) 로직 구축.
   - 기존의 `transactions` 테이블(1:1 매핑) 스키마를 훼손하지 않기 위해, **대기 주문의 수량을 차감(Update)**하고 **체결된 수량만큼 새로운 체결 주문을 삽입(Insert)**하는 스마트한 우회(Split) 전략 성공적 도입.

---

## 🔒 3단계: 학생 정보 보안 강화 및 암호화
평문으로 저장되던 학생들의 비밀번호를 안전하게 보호하고 관리자 페이지 보안을 챙겼습니다.

1. **관리자 페이지 CSRF 방어선 구축**
   - `SecurityConfig`의 관리자 Chain에 한하여 `CookieCsrfTokenRepository`를 활성화하여, 폼 제출 시 CSRF 토큰을 필수로 요구하도록 설정 (관리자 권한 보호).
2. **학생 계정 비밀번호 BCrypt 해싱 도입**
   - `MemberServiceImpl`의 `join(회원가입)` 시 비밀번호를 `BCrypt`로 단방향 암호화하여 데이터베이스에 저장하도록 수정.
3. **평문 패스워드 사용자를 위한 무중단 마이그레이션(하위 호환) 적용**
   - 기존 평문 비밀번호를 쓰는 학생들이 로그인을 못하는 사태를 방지하기 위해 로직 고도화.
   - 로그인 시 비밀번호가 평문인지 해시 포맷(`$2a$`)인지 판별하여 유연하게 검증.
   - **평문 사용자가 로그인 성공 시, 백그라운드에서 즉시 비밀번호를 암호화(BCrypt)하여 DB를 업데이트(UPDATE)하는 무중단 마이그레이션 로직** 적용 완료 (`MemberMapper.xml` 수정).

---

## 🌐 4단계 (완료): REST API 전환 및 글로벌 예외 구조화
프론트엔드(React) 연동을 위해 기존의 JSP 반환 컨트롤러들을 모두 API 서버용으로 리팩터링하고, 견고한 글로벌 예외 처리기를 도입했습니다.

1. **`ApiResponse<T>` 기반 통합 응답 포맷 도입**
   - 프론트엔드에서 성공/실패 여부(`success`), 메시지(`message`), 데이터(`data`)를 일관되게 파싱할 수 있도록 공통 포맷을 확립했습니다.
2. **모든 뷰 컨트롤러의 `@RestController` 전환**
   - `MemberController`, `StockOrderController`, `AdminController` 등 기존에 View 이름(String)이나 Redirect를 반환하던 컨트롤러를 100% JSON 데이터를 반환하도록 변경했습니다.
   - 불필요하게 분리되어 있던 `~RestController` 파일들을 삭제하고 메인 컨트롤러 하나로 일원화했습니다.
3. **Security (인증/인가) 연동 전용 API 추가**
   - **`GET /api/auth/status` (`AuthApiController`)**: 프론트엔드 앱 로드 시 현재 사용자가 접속한 권한(학생, 관리자, 비로그인)을 한 번에 파악할 수 있는 API를 개통했습니다.
   - **관리자 폼 로그인 응답 개선**: 기존의 302 Redirect 방식을 버리고, Spring Security의 `successHandler` / `failureHandler`를 활용해 로그인 성공 여부를 JSON(200 OK / 401 Unauthorized)으로 직접 반환하도록 `SecurityConfig`를 개선했습니다.
4. **글로벌 예외 처리기(`GlobalExceptionHandler`) 도입**
   - **문제**: 기존 서비스 로직에서는 에러 상황에 단순 문자열을 반환하거나 일괄적으로 `IllegalArgumentException`을 던졌고, 이를 컨트롤러에서 하나하나 `try-catch`로 묶어 처리해야 했습니다.
   - **해결**: `ErrorCode` Enum을 만들어 HTTP 상태 코드와 메시지를 중앙화하고, `InsufficientPointException` 등 명확한 의미의 커스텀 예외 6종을 생성했습니다.
   - 이제 서비스에서는 예외를 `throw` 하기만 하면 `@RestControllerAdvice`가 이를 낚아채어 400, 401, 403, 404 상태 코드와 함께 JSON(`ApiResponse.error`)으로 프론트엔드에 깔끔하게 반환합니다. 덕분에 컨트롤러 내부의 복잡한 분기 로직이 완전히 사라졌습니다.

---

> ✅ **결론 및 다음 단계 제안**
> 
> 치명적 결함 해소(0단계), 부분 체결 엔진 도입(2단계), 이중 보안 체계 구축(1,3단계)에 이어, **백엔드의 순수 REST API 서버 전환 및 에러 핸들링 구조화(4단계)**까지 모두 성공적으로 마무리되었습니다!
> 
> 다음은 트랙 A의 5단계(거래 기록 고도화)를 진행할 수도 있지만, 우선은 백엔드가 완벽히 준비되었으므로 대기 중인 **React 마이그레이션(트랙 B)**을 시작하여 화면을 연동해보는 것을 강력히 추천합니다!
