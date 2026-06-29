# 📈 Stock Game Spring (Backend API Server)

학생 대상 주식 모의투자 시뮬레이션을 위한 **핵심 매칭 엔진 및 REST API 백엔드 서버**입니다. 
기존의 JSP 모놀리식 구조에서 탈피하여 React 프론트엔드와 독립적으로 통신하는 순수 API 서버이자 실시간 웹소켓 서버로 고도화되었습니다.

---

## 🚀 주요 기능 (Key Features)

### 1. 고성능 주식 매칭 엔진 (부분 체결 로직)
- 매수/매도 수량이 일치하지 않더라도 체결 가능한 수량만큼 즉시 체결되는 **Split(분할) 기반 부분 체결 시스템** 도입
- 트랜잭션 체결 수량(amount) 및 단가(price)를 DB에 1:1로 기록하여 트랜잭션 무결성을 완벽하게 보장하며, 복수의 대기 주문을 순회하며 일괄 체결 처리

### 2. 실시간 웹소켓 (STOMP) 연동
- `spring-boot-starter-websocket` 기반 양방향 실시간 통신
- **호가창 브로드캐스트 (`/topic/orders/{stockId}`)**: 주문이 접수되거나 체결될 때마다 전체 사용자에게 실시간 갱신 이벤트 전송
- **개인별 알림 (`/queue/notifications`)**: 주문이 체결되었을 때 당사자에게 즉시 푸시 알림 전송

### 3. 실시간 OHLCV 및 스케줄러 로직
- 거래 성사 시 `ON DUPLICATE KEY UPDATE`를 활용해 일일 **시가, 고가, 저가, 종가, 거래량** 실시간 갱신
- 매일 자정에 동작하는 **Spring Scheduler**를 통해 기준가(`prev_price`) 자동 동기화

### 4. 하이브리드 보안 & 데이터 접근 계층
- **관리자/교사**: Spring Security 및 JPA를 통한 엄격한 보안 통제 및 권한(Role) 관리
- **학생**: 경량화된 Session 기반 인증과 고성능 MyBatis 트랜잭션 혼용
- 비밀번호 암호화(BCrypt) 및 글로벌 예외 처리(`@RestControllerAdvice`)를 통한 API 안정성 및 응답 구조화(`ApiResponse<T>`) 완료

### 5. 시장 정책 통제 API
- 코스피 시장과 동일한 **가격대별 호가 단위(Tick Size) 강제 검증** 로직
- 교사(관리자)가 시장의 개장/폐장 상태를 실시간 토글할 수 있는 운영 API 제공

---

## 🛠️ 기술 스택 (Tech Stack)

- **Language**: Java 17
- **Framework**: Spring Boot 3.5.x
- **Database**: MariaDB
- **ORM / SQL Mapper**: Spring Data JPA, MyBatis 혼용 적용
- **Security**: Spring Security (BCryptPasswordEncoder)
- **Real-time Communication**: WebSocket (STOMP, SockJS)
- **Build Tool**: Maven

---

## 📂 프로젝트 구조 (Architecture)

```
src/main/java/com/skfkfkvlrm/stockgame_spring/
 ├── config/      # Security, WebSocket, DataInitializer 등 설정
 ├── controller/  # 관리자 API 및 학생 REST API
 ├── domain/      # JPA 엔티티 및 MyBatis DTO 혼용
 ├── exception/   # 커스텀 비즈니스 예외 클래스
 ├── repository/  # Spring Data JPA 및 MyBatis Mapper 인터페이스
 └── service/     # 핵심 비즈니스 로직 및 부분 체결 매칭 알고리즘
```

---

## ⚙️ 실행 방법 (How to Run)

1. **DB 환경 구성**
   - MariaDB에 `stockgame` 스키마 생성
   - `src/main/resources/application.yaml`에서 DB 접근 정보(username/password) 설정

2. **애플리케이션 구동**
   ```bash
   # Windows
   ./mvnw.cmd spring-boot:run
   
   # Mac / Linux
   ./mvnw spring-boot:run
   ```
3. 서버는 기본적으로 `http://localhost:8882` 포트에서 동작합니다.
4. 프론트엔드는 분리된 [stockGame_react](https://github.com/skfkfkvlrm/stockGame_react) 프로젝트를 통해 `5173` 포트에서 실행하여 완벽히 연동됩니다. (JSP에서 React SPA로 100% 마이그레이션 완료)