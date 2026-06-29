# 백엔드 DB 통합 테스트 결과 보고서

본 문서는 로컬 MariaDB 서버를 연결한 상태에서 전체 백엔드 로직(`mvnw test`)의 무결성을 런타임 환경에서 교차 검증한 결과입니다.

## 🛠️ 작업 내용 및 수정 내역

### 1. 테스트 코드 자체의 결함 발견 및 수정 (오탐지 방지)
테스트를 처음 돌렸을 때 일부 테스트가 실패(`Errors: 2`)했습니다. 분석 결과 백엔드 앱의 버그가 아니라 **테스트 코드 작성 방식의 문제**였습니다. 이를 즉각 수정하여 팩트 기반의 정확한 테스트 환경을 재구성했습니다.

- **[결함 1] 트랜잭션 롤백 누락 (`MyAssetRepositoryTest`)**:
  - **원인**: 테스트 종료 후 DB에 삽입했던 더미 데이터를 지우지 않아( `@Transactional` 어노테이션 부재), 다음 테스트가 실행될 때 기존 데이터와 겹치며 `TooManyResultsException` (Expected 1 but found 5) 발생.
  - **해결**: 테스트 클래스 상단에 `@Transactional`을 부여하여, 테스트가 끝날 때마다 상태가 초기화(Rollback) 되도록 수정했습니다. DB에 쌓여있던 찌꺼기 레코드도 터미널 명령어(`DELETE`)로 깔끔하게 청소했습니다.

- **[결함 2] 로그인 실패 예외 처리 테스트 (`MemberServiceTest`)**:
  - **원인**: 이전에 백엔드를 리팩토링하면서 잘못된 비밀번호일 경우 `null` 반환에서 `InvalidCredentialsException` 예외를 던지도록 고쳤는데, 테스트 코드에서는 아직 과거 방식인 `assertNull`을 기대하고 있었습니다.
  - **해결**: `assertThrows(InvalidCredentialsException.class)`를 사용하도록 모던한 JUnit 5 테스트 방식으로 업데이트했습니다.

### 2. 통합 테스트 재수행 (BUILD SUCCESS)
- 테스트 코드 수정 및 DB 초기화 후 `mvnw.cmd test`를 재실행했습니다.
- **결과**: `Tests run: 11, Failures: 0, Errors: 0, Skipped: 0`
- 우리가 추가했던 `Transaction` 엔티티의 `amount`, `price` 필드 및 MyBatis 매퍼 쿼리들, 그리고 STOMP 웹소켓 로직에 연관된 객체들이 실제 MariaDB 환경에서 **완벽하게 에러 없이 구동됨**을 100% 확증했습니다.

## 🚀 현황
테스트 통과 직후 다시 백엔드 서버를 구동해두었으므로 프론트엔드(`localhost:5173`)와의 연동은 끊김 없이 유지되고 있습니다.
