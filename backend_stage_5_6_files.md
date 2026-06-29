# 백엔드 5, 6단계 주요 변경 및 생성 파일 목록

본 문서는 트랙 A(백엔드 개선)의 마지막 단계인 5단계(거래 기록 고도화) 및 6단계(UX 및 추가 기능)를 진행하면서 새로 생성되거나 변경된 주요 파일들을 요약한 것입니다.

---

## 📈 5단계: 거래 기록 고도화 (OHLCV) 및 스케줄러

### 1. 신규 생성된 파일 (New)
* **`StockPriceHistory.java`**
  * 위치: `src/main/java/com/skfkfkvlrm/stockgame_spring/domain/`
  * 역할: 일자별 OHLCV(시가, 고가, 저가, 종가, 거래량) 정보를 담는 JPA 엔티티.
* **`StockPriceHistoryRepository.java`**
  * 위치: `src/main/java/com/skfkfkvlrm/stockgame_spring/repository/`
  * 역할: `StockPriceHistory` 엔티티를 관리하는 Spring Data JPA 저장소. 차트 데이터 조회를 위한 메서드 포함.
* **`StockPriceHistoryMapper.java`** & **`stockPriceHistoryMapper.xml`**
  * 위치: `src/main/java/.../repository/` 및 `src/main/resources/mappers/`
  * 역할: 거래 발생 시 실시간으로 고가/저가/거래량을 업데이트(`ON DUPLICATE KEY UPDATE`)하기 위한 MyBatis 매퍼.
* **`StockSchedulerService.java`**
  * 위치: `src/main/java/com/skfkfkvlrm/stockgame_spring/service/`
  * 역할: 매일 자정에 어제 종가를 `stocks` 테이블의 `prev_price`에 덮어씌우는 자동 스케줄러 로직.

### 2. 수정된 파일 (Modified)
* **`StockGameSpringApplication.java`**
  * 역할: 스케줄러 활성화를 위해 `@EnableScheduling` 어노테이션 추가.
* **`StockDetailController.java`**
  * 역할: 프론트엔드 차트 렌더링을 위해 특정 종목의 OHLCV 배열을 반환하는 `GET /api/stock/{stockId}/history` API 추가.
* **`StockOrderServiceImpl.java`**
  * 역할: 체결(Match) 로직이 수행될 때마다 `stockPriceHistoryMapper.upsertDailyPrice(...)`를 호출하여 현재가와 거래량을 누적 업데이트.

---

## 🚦 6단계: UX 및 추가 기능 (WebSocket & 시장 정책)

### 1. 신규 생성된 파일 (New)
* **`WebSocketConfig.java`**
  * 위치: `src/main/java/com/skfkfkvlrm/stockgame_spring/config/`
  * 역할: STOMP 및 SockJS를 활용한 실시간 통신 브로커(`/ws`, `/topic`, `/queue`) 설정.
* **`MarketSettings.java`**
  * 위치: `src/main/java/com/skfkfkvlrm/stockgame_spring/domain/`
  * 역할: 전체 주식 시장의 개장 여부(`marketOpen`) 및 일일 거래 한도를 제어하기 위한 JPA 엔티티 (단일 레코드로 관리).
* **`MarketSettingsRepository.java`**
  * 위치: `src/main/java/com/skfkfkvlrm/stockgame_spring/repository/`
  * 역할: `MarketSettings`를 조회 및 저장하는 Spring Data JPA 저장소.

### 2. 수정된 파일 (Modified)
* **`pom.xml`**
  * 역할: 웹소켓 기능 지원을 위해 `spring-boot-starter-websocket` 의존성 추가.
* **`DataInitializer.java`**
  * 역할: 서버 기동 시 `MarketSettings` 기본 레코드(ID=1, 개장 상태)가 없을 경우 자동 생성하도록 로직 추가.
* **`StockOrderServiceImpl.java`**
  * 역할:
    - 시장 개장 상태(`marketOpen`) 확인 및 폐장 시 주문 거부 로직(`validateMarketOpen`) 추가.
    - 코스피 기준 호가 단위(Tick size) 강제 검증 로직(`validateTickSize`) 추가.
    - 매수/매도 대기 및 체결 완료 시점에 `SimpMessagingTemplate`을 활용한 호가 브로드캐스트 전송 추가.
    - 부분/전량 체결 시 당사자에게 개인 알림 메시지 발송 추가.
* **`StockOrderResponse.java`** (DTO)
  * 역할: 주문 취소 후 브로드캐스팅 시 종목 ID가 필요하여 `stockId` 필드 추가.
* **`stockDetailMapper.xml`**
  * 역할: 주문 취소용 `getOrderById` 쿼리 누락분 신규 작성 (DB의 `orders` 내역 단건 조회).
* **`AdminController.java`**
  * 역할: 관리자가 시장 개장/폐장 상태를 토글할 수 있는 `POST /api/admin/market/toggle` 및 조회 API 추가.
