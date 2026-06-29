# 백엔드 잔여 이슈 1차 해결 (Cleanup & Integrity Fix)

본 문서는 프로젝트 문서(`project_analysis.md`) 및 실제 코드베이스 간의 교차 검증을 통해 발견된 치명적 결함을 해결한 작업 내역을 기록합니다.

## 🛠 수정된 핵심 내용

### 1. `application.yaml` 설정 안정화 (`ddl-auto`)
- **이전 상태**: `hibernate.ddl-auto: create`로 설정되어 있어, 서버를 재시작할 때마다 데이터베이스 테이블이 드랍(Drop)되고 새로 생성되는 심각한 데이터 유실 위험이 있었습니다.
- **수정 내역**: 개발 단계의 편의와 기존 데이터 보존을 위해 `ddl-auto: update`로 변경 완료했습니다. 

### 2. 체결 기록(Transaction) 무결성 복구
- **이전 상태**: 매칭 엔진(`StockOrderServiceImpl`)이 스플릿(부분 체결) 전략을 정상 수행하고 있었으나, 최종 체결 내역을 저장하는 `Transaction` 엔티티와 DB에 `amount`(수량)와 `price`(가격) 컬럼이 없어 "누가 누구와 거래했다"만 남고 구체적인 거래 내용이 누락되었습니다.
- **수정 내역**: 
  - `Transaction.java` 도메인에 `amount`, `price` 필드 추가
  - `ddl.txt`에 해당 테이블 컬럼 명세 업데이트
  - `StockDetailRepository.java` 인터페이스 및 `stockDetailMapper.xml` 쿼리에 매개변수 바인딩 추가
  - 매칭 로직 내부에서 `setMatchedOrder` 호출 시 실제 계산된 `matchAmount`와 `matchPrice`를 넘겨주어 정확한 부분 체결 단위가 1:1로 기록되도록 연동 완료

### 3. IPO(발행 주식) 거래 로직 버그 픽스
- **이전 상태**: 시스템이 발행한 신규 주식을 학생이 매수할 때, 기록용 더미 주문을 `OrderStatus.매도`로 잘못 생성하고 트랜잭션 기록(Transaction)을 아예 남기지 않았습니다.
- **수정 내역**: `OrderStatus.매수` 주문으로 바로잡고, P2P 거래와 동일하게 체결 기록이 남도록 `setMatchedOrder` 로직을 추가했습니다.

### 4. 레거시 디렉터리 확인
- 프론트엔드가 React로 분리되면서 불필요해진 `src/main/resources/static/` 및 `templates/` 폴더는 이미 로컬 환경에서 완전히 제거된 것을 확인했습니다.

---

> **팩트 체크 완료 사항**
> 문서상 이슈로 등록되어 있던 "학생 비밀번호 평문 저장 및 BCrypt 적용 필요" 건은 이미 `MemberServiceImpl.java`에 마이그레이션 로직까지 포함되어 완벽히 구현되어 있었음을 확인하고, `PROGRESS.md`에서 거짓 이슈를 삭제 조치했습니다.
