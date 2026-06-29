# 📊 SchoolStock 프로젝트 종합 분석서

> 최종 작성일: 2026-06-29  
> 분석 대상: `stockGame_spring` (백엔드) / `stockGame_react` (프론트엔드)

---

## 1. 프로젝트 개요

학생 대상 **주식 모의투자 시뮬레이션** 서비스. 교사(관리자)는 시장을 운영하고, 학생은 가상 포인트로 주식을 매수/매도한다.

| 항목 | 상세 |
|---|---|
| 프로젝트명 | SchoolStock |
| 서비스 대상 | 학생(플레이어) / 교사(관리자) |
| 구조 | 백엔드(Spring Boot) + 프론트엔드(React) 분리형 SPA |
| 개발 상태 | 백엔드 로드맵 완료, 프론트엔드 마이그레이션 완료 / 일부 API 연동 버그 잔존 |

---

## 2. 기술 스택

### 백엔드 (`stockGame_spring`)
| 분류 | 기술 |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.x |
| DB | MariaDB |
| ORM | Spring Data JPA + MyBatis (혼용) |
| 보안 | Spring Security (이중 필터 체인, BCrypt) |
| 실시간 통신 | WebSocket (STOMP, SockJS) |
| 빌드 | Maven |
| 포트 | 8882 |

### 프론트엔드 (`stockGame_react`)
| 분류 | 기술 |
|---|---|
| Language | JavaScript (ES6+) |
| Framework | React 19 + Vite 8 |
| 라우팅 | React Router DOM v7 |
| HTTP | Axios (인터셉터, credentials 설정) |
| 스타일링 | Vanilla CSS (글래스모피즘 다크 테마) |
| 차트 | ApexCharts (`react-apexcharts`) |
| 실시간 통신 | `@stomp/stompjs` + `sockjs-client` |
| 포트 | 5173 (Vite 개발 서버) |

---

## 3. 아키텍처 구조

```
[브라우저]
    │ 5173
    ▼
[Vite Dev Server] ──proxy(/api, /ws)──► [Spring Boot 8882]
                                              │
                                         ┌───┴───┐
                               [Spring Security]  [MyBatis / JPA]
                                    │                   │
                              ┌─────┴─────┐          [MariaDB]
                       [관리자(Admin)  학생(Student)]
                        폼 로그인         세션 인증
```

### 보안 구조 (이중 필터 체인)
- **Chain 1 (Order 1)**: `/admin/**` → AppUser(교사) → Form Login / JPA 인증 / CSRF 활성화
- **Chain 2 (Order 2)**: 그 외 모든 경로 → Student → Session 기반 인증 / CSRF 비활성화

### 데이터 접근 전략
| MyBatis (복잡한 조인/쿼리) | JPA (단순 저장/조회) |
|---|---|
| memberMapper.xml | AppUserRepository |
| myAssetMapper.xml | MarketSettingsRepository |
| stockDetailMapper.xml | NewsRepository |
| myPointHistoryMapper.xml | CouponRepository |
| couponMapper.xml | StockPriceHistoryRepository |
| stockPriceHistoryMapper.xml | |

---

## 4. 패키지 구조

### 백엔드 (`stockGame_spring`)
```
src/main/java/com/skfkfkvlrm/stockgame_spring/
 ├── config/      # Security, WebSocket, DataInitializer 등 설정
 ├── controller/  # REST API 엔드포인트 (9개 컨트롤러)
 │   └── dto/     # request / response DTO
 ├── domain/      # JPA 엔티티 및 Enum (14개)
 ├── exception/   # 커스텀 비즈니스 예외 + GlobalExceptionHandler
 ├── repository/  # Spring Data JPA + MyBatis Mapper 인터페이스
 ├── service/     # 비즈니스 로직 인터페이스 + impl/
 └── resources/mappers/  # MyBatis XML 쿼리 (8개)
```

### 프론트엔드 (`stockGame_react`)
```
src/
├── App.jsx / main.jsx        # 앱 진입점 + BrowserRouter
├── api/axiosConfig.js        # Axios 인스턴스 + 401 인터셉터
├── components/layout/
│   ├── MainLayout.jsx        # 공통 레이아웃 (Sidebar + Outlet)
│   └── Sidebar.jsx           # 메뉴 내비게이션 + 유저 포인트 표시
└── pages/
    ├── auth/                 # Login.jsx, Register.jsx
    ├── dashboard/            # Dashboard.jsx
    ├── stock/                # StockList.jsx, StockDetail.jsx
    ├── news/                 # NewsList.jsx
    ├── points/               # PointsHistory.jsx
    └── coupons/              # CouponStore.jsx, MyCoupons.jsx
```

---

## 5. API 명세

### 인증 & 회원 (`/api/members`, `/api/auth`)
| Method | Endpoint | 설명 |
|---|---|---|
| POST | `/api/members/login` | 학생 로그인 (세션 발급) |
| POST | `/api/members/join` | 학생 회원가입 |
| GET | `/api/members/me` | 현재 로그인 학생 정보 조회 |
| GET | `/api/members/id-check` | 아이디 중복 확인 |
| GET | `/api/auth/status` | 현재 인증 상태 조회 (학생/관리자/비로그인) |

### 주식 (`/api/stock`)
| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/api/stock` | 전체 주식 목록 (현재가 `nowPrice`, 이전가, 등락률) |
| GET | `/api/stock/{stockId}` | 특정 주식 상세 정보 |
| GET | `/api/stock/{stockId}/history` | OHLCV 차트 데이터 |
| GET | `/api/stock/{stockId}/orders` | 호가창 (대기 주문 목록) |

### 주문 (`/api/orders`)
| Method | Endpoint | 설명 |
|---|---|---|
| POST | `/api/orders/buy` | 매수 주문 접수 (부분 체결 엔진) |
| POST | `/api/orders/sell` | 매도 주문 접수 (부분 체결 엔진) |
| POST | `/api/orders/cancel` | 대기 주문 취소 + 포인트 환불 |

### 자산 & 포인트 (`/api/asset`, `/api/history`)
| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/api/asset` | 내 자산 현황 (총 자산, 포인트, 손익, 쿠폰 수) |
| GET | `/api/history` | 포인트 내역 (매수/매도/쿠폰구매/지급 통합 UNION 쿼리) |

### 쿠폰 (`/api/coupons`)
| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/api/coupons` | 전체 쿠폰 목록 조회 |
| POST | `/api/coupons/{couponId}/buy` | 쿠폰 구매 |
| GET | `/api/coupons/my` | 내 보유 쿠폰 목록 조회 |

### 관리자 (`/api/admin`, `/admin/**`)
| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/api/admin/market/status` | 시장 개/폐장 상태 조회 |
| POST | `/api/admin/market/toggle` | 시장 개/폐장 토글 |

---

## 6. 백엔드 개발 이력 (0~7단계)

| 단계 | 핵심 내용 | 상태 |
|---|---|---|
| **0단계** | 주문 취소 환불 버그(`Enum 비교 오류`) 수정, 세션 검증 취약점 패치 | ✅ 완료 |
| **1단계** | 이중 필터 체인(관리자/학생 분리), Spring Security + JPA `AppUser` 도입 | ✅ 완료 |
| **2단계** | Split 전략 기반 부분 체결(Partial Fill) 매칭 엔진 전면 리팩터링 | ✅ 완료 |
| **3단계** | BCrypt 해싱 도입, 평문 → 해시 무중단 마이그레이션 로직 | ✅ 완료 |
| **4단계** | `@RestController` 전환, `ApiResponse<T>` 통합 포맷, `GlobalExceptionHandler` | ✅ 완료 |
| **5단계** | OHLCV 일봉 기록(`stock_price_history`), 자정 `@Scheduled` 기준가 갱신 | ✅ 완료 |
| **6단계** | WebSocket STOMP 브로드캐스트(`/topic/orders`), 호가 단위(Tick Size), 시장 개폐장 정책 | ✅ 완료 |
| **7단계 (클린업)** | `ddl-auto: create → update`, `Transaction` 무결성 복구(amount/price), IPO 로직 버그 수정 | ✅ 완료 |

---

## 7. 프론트엔드 개발 이력

### 화면 구성 (완료)
| 경로 | 컴포넌트 | 기능 |
|---|---|---|
| `/login` | `Login.jsx` | 학생 로그인 |
| `/register` | `Register.jsx` | 회원가입 (아이디 중복 체크) |
| `/` | `Dashboard.jsx` | 내 자산 현황 (총 자산, 포인트, 손익, 보유 쿠폰) |
| `/stocks` | `StockList.jsx` | 전체 주식 목록 + 클릭 시 상세 페이지 이동 |
| `/stocks/:id` | `StockDetail.jsx` | 개별 주식 (캔들스틱 차트, 매수/매도 폼, WebSocket 연동) |
| `/news` | `NewsList.jsx` | 시장 뉴스 목록 |
| `/history` | `PointsHistory.jsx` | 포인트 변동 내역 |
| `/coupons` | `CouponStore.jsx` | 쿠폰 상점 |
| `/my-coupons` | `MyCoupons.jsx` | 내 보유 쿠폰 목록 (신규 추가) |

### 버그 픽스 이력
| 분류 | 내용 |
|---|---|
| **프론트** | `StockDetail.jsx` WebSocket `deactivate()` 미호출 → 좀비 커넥션(메모리 누수) 해결 |
| **프론트** | `vite.config.js` 프록시 설정 도입 → 하드코딩 제거, CORS 원천 차단 |
| **프론트** | `StockList.jsx` 행 클릭 이벤트 누락 → `useNavigate` 연결 완료 |
| **프론트** | `StockDetail.jsx` 필드명 불일치 → `currentPrice` → `nowPrice` 수정 |
| **프론트** | `PointsHistory.jsx` 데이터 없을 때 화이트 스크린 → null/옵셔널 체이닝 처리 |
| **프론트** | `CouponStore.jsx` 쿠폰 Key 오류 → `coupon.id` → `coupon.couponId` 수정 |
| **백엔드** | `Transaction` 엔티티 테이블명 불일치(`transactions` → `stock_transactions`) → 전체 Mapper 수정 |
| **백엔드** | `DataInitializer` 서버 재기동 시 더미 데이터 중복 삽입 → `COUNT` 체크 + `DELETE` 청소 추가 |
| **백엔드** | `students` 테이블 중복 레코드로 인한 서브쿼리 `"Expected one, found N"` 500 에러 해결 |
| **백엔드** | `CouponPurchase` 엔티티 `studentId`, `couponId` 필드 누락 → 추가 |
| **백엔드** | `couponMapper.xml` `getMyCouponList` 쿼리 `cp.student_id` 컬럼 인식 오류 → 수정 |

---

## 8. DB 스키마 요약

| 테이블 | 설명 |
|---|---|
| `students` | 학생 계정 (student_id, password, total_point, total_coupon) |
| `app_users` | 관리자(교사) 계정 (Spring Security용) |
| `stocks` | 주식 종목 (name, now_price, prev_price, publication_price, publication_balance) |
| `orders` | 주문 (content: 매수/매도, state: 대기/체결/취소, price, amount, student_id, stock_id) |
| `stock_transactions` | 체결 기록 (buy_order_id, sell_order_id, amount, price) |
| `stock_price_history` | OHLCV 일봉 기록 (stock_id, date, open/high/low/close, volume) |
| `coupons` | 쿠폰 마스터 (name, price) |
| `coupon_purchase` | 쿠폰 구매 내역 (student_id, coupon_id, price, name, state) |
| `get_points` | 포인트 지급 내역 (student_id, point, content) |
| `news` | 뉴스 목록 |
| `market_settings` | 시장 개/폐장 상태 (is_market_open) |

---

## 9. 현재 상태 및 잔여 이슈

### ✅ 정상 작동 확인 항목
- 로그인 / 로그아웃
- 대시보드 자산 현황 조회 (총 자산, 포인트, 손익, 쿠폰)
- 주식 목록 조회 및 상세 페이지 클릭 진입
- 주식 상세 페이지 현재가(`nowPrice`) 표시 및 캔들스틱 차트
- 뉴스 목록
- 포인트 내역 (`/api/history`)
- 내 보유 쿠폰 조회 (`/api/coupons/my`)
- 쿠폰 상점 목록 조회

### ⚠️ 잔여 미해결 이슈

| 우선순위 | 분류 | 내용 |
|---|---|---|
| 🔴 높음 | 백엔드 | 주식 **매수 주문 실행** (`POST /api/orders/buy`) 처리 중 500 에러 (트랜잭션 로직 점검 필요) |
| 🔴 높음 | 백엔드 | **쿠폰 구매** (`POST /api/coupons/{id}/buy`) 처리 중 실패 (`CouponServiceImpl` 검증 필요) |
| 🟡 중간 | 프론트 | 주문/구매 실패 시 사용자 에러 메시지 UX 개선 필요 |
| 🟡 중간 | 백엔드 | 호가창 UI (`/api/stock/{id}/orders`) 프론트 미연동 (백엔드 API는 존재) |
| 🟢 낮음 | 백엔드 | 관리자 대시보드 미구현 (사용자 관리, 주식/쿠폰 관리 Admin UI) |

---

## 10. 전체 연동 흐름 요약

```mermaid
sequenceDiagram
    actor 학생
    participant React as React (5173)
    participant Spring as Spring Boot (8882)
    participant DB as MariaDB

    학생->>React: 로그인 폼 제출
    React->>Spring: POST /api/members/login
    Spring->>DB: SELECT FROM students
    DB-->>Spring: 학생 정보
    Spring-->>React: { success: true } + JSESSIONID Cookie

    학생->>React: 주식 목록 클릭
    React->>Spring: GET /api/stock (with Cookie)
    Spring-->>React: 주식 목록 JSON (nowPrice, prevPrice ...)

    학생->>React: 종목 클릭 → 매수 주문 제출
    React->>Spring: POST /api/orders/buy (with Cookie)
    Spring->>DB: 포인트 검증 + 주문 매칭 + 포인트 이동
    Spring-->>React: { success: true, data: "매수 체결..." }
    Spring->>React: WebSocket /topic/orders/{id} → ORDER_UPDATED
    React->>Spring: GET /api/stock/{id} (화면 자동 갱신)
```

---

## 11. 로컬 개발 환경 실행 방법

```bash
# 1. 백엔드 실행 (포트 8882)
cd stockGame_spring
./mvnw.cmd spring-boot:run

# 2. 프론트엔드 실행 (포트 5173)
cd stockGame_react
npm run dev

# 3. 브라우저 접속
http://localhost:5173

# 테스트 계정 (DataInitializer 자동 생성)
# 학생: abc / abc123!
# 관리자: admin / admin1234
```

---

## 12. GitHub 저장소

| 프로젝트 | URL |
|---|---|
| 백엔드 | https://github.com/skfkfkvlrm/stockGame_spring |
| 프론트엔드 | https://github.com/skfkfkvlrm/stockGame_react |

---

## 1. 아키텍처 개요

```mermaid
graph TB
    subgraph "stockGame_react (포트 5173)"
        UI[React 19 + Vite 8]
        Router[React Router v7]
        Axios[Axios + withCredentials]
        WS[SockJS + STOMP]
    end

    subgraph "stockGame_spring (포트 8882)"
        Security[Spring Security — 이중 FilterChain]
        Controller[REST Controllers]
        Service[Service Layer]
        MyBatis[MyBatis — 학생/주문/자산]
        JPA[JPA/Hibernate — 관리자/도메인]
        DB[(MariaDB mydb)]
        WebSocket[WebSocket/STOMP]
    end

    UI --> Axios --> Controller
    WS --> WebSocket --> Controller
    Controller --> Security
    Controller --> Service
    Service --> MyBatis --> DB
    Service --> JPA --> DB
```

---

## 2. stockGame_spring 백엔드 분석

### 2-1. 패키지 구조

| 패키지 | 역할 | 파일 수 |
|---|---|---|
| `controller/` | REST API 엔드포인트 | 9개 컨트롤러 + dto/ |
| `controller/dto/request/` | 요청 DTO | 3개 |
| `controller/dto/response/` | 응답 DTO | 8개 |
| `domain/` | JPA 엔티티 + Enum | 14개 |
| `service/` | 비즈니스 로직 인터페이스 | 9개 |
| `service/impl/` | 비즈니스 로직 구현체 | 8개 |
| `repository/` | JPA Repository + MyBatis | 10개 |
| `config/` | 설정 클래스 | 4개 |
| `auth/` | Spring Security UserDetails | 2개 |
| `exception/` | 커스텀 예외 + 전역 핸들러 | 10개 |
| `resources/mappers/` | MyBatis XML | 8개 |

### 2-2. API 엔드포인트 목록

| 컨트롤러 | 메서드 | 경로 | 설명 |
|---|---|---|---|
| `MemberController` | POST | `/api/members/join` | 회원가입 |
| | POST | `/api/members/login` | 로그인 (HttpSession) |
| | POST | `/api/members/logout` | 로그아웃 |
| | GET | `/api/members/id-check` | 아이디 중복 확인 |
| | GET | `/api/members/me` | 내 정보 조회 |
| `StockDetailController` | GET | `/api/stocks` | 전체 주식 목록 |
| | GET | `/api/stock/{stockId}` | 종목 상세 |
| | GET | `/api/stock/{stockId}/history` | 가격 히스토리 |
| | GET | `/api/stock/{stockId}/orders` | 호가창 |
| `StockOrderController` | POST | `/api/orders/buy` | 매수 |
| | POST | `/api/orders/sell` | 매도 |
| | POST | `/api/orders/cancel` | 주문 취소 |
| `MyAssetController` | GET | `/api/asset` | 내 자산 조회 |
| `MyPointHistoryController` | GET | `/api/history` | 포인트 내역 |
| `CouponController` | GET | `/api/coupons` | 쿠폰 목록 |
| | POST | `/api/coupons/buy` | 쿠폰 구매 |
| `NewsController` | GET | `/api/news` | 뉴스 목록 |
| `AuthApiController` | POST | `/api/auth/login` | 관리자 로그인 |
| `AdminController` | GET/POST | `/admin/**` | 관리자 화면 |

### 2-3. 도메인 엔티티 현황

| 엔티티 | 테이블 | 기술 | 상태 |
|---|---|---|---|
| `Student` | `students` | JPA | ✅ 정상 |
| `Order` | `orders` | JPA + MyBatis 혼용 | ✅ 정상 |
| `Stock` | `stocks` | JPA | ✅ 정상 |
| `Coupon` | `coupons` | JPA | ✅ 정상 |
| `CouponPurchase` | `coupon_purchases` | JPA | ✅ 정상 |
| `News` | `news` | JPA | ✅ 정상 |
| `Transaction` | `transaction` | JPA | ⚠️ amount/price 필드 없음 |
| `StockPriceHistory` | `stock_price_history` | JPA | ✅ 정상 |
| `AppUser` | `app_users` | JPA | ✅ 관리자 전용 |
| `MarketSettings` | `market_settings` | JPA | ✅ 정상 |
| `GetPoint` | `get_point` | MyBatis only | 조회 전용 |

### 2-4. Spring Security 구조

```
Chain 1 (@Order(1)): /admin/**
  → MANAGER/ADMIN 권한 필요 (app_users 테이블)
  → FormLogin + BCrypt 인증
  → CSRF 토큰 활성화

Chain 2 (@Order(2)): 나머지 모든 경로
  → 전체 permitAll (학생 인증은 컨트롤러 레벨 HttpSession으로 처리)
  → CSRF 비활성화
```

### 2-5. 데이터 레이어 혼용 구조 (JPA + MyBatis Hybrid)

| MyBatis (복잡한 조인/쿼리) | JPA (단순 저장/조회) |
|---|---|
| memberMapper.xml | AppUserRepository |
| myAssetMapper.xml | MarketSettingsRepository |
| stockDetailMapper.xml | NewsRepository |
| myPointHistoryMapper.xml | CouponRepository |
| couponMapper.xml | StockPriceHistoryRepository |
| newsMapper.xml | OrderRepository (JPA도 병행) |
| stockPriceHistoryMapper.xml | |

> [!NOTE]
> MyBatis로 복잡한 조인 쿼리를 처리하고, JPA로 단순한 엔티티 저장을 담당하는 하이브리드 구조입니다.

---

## 3. stockGame_react 프론트엔드 분석

### 3-1. 프로젝트 구조

```
src/
├── App.jsx                  # 라우팅 정의 (BrowserRouter)
├── main.jsx                 # React 앱 진입점
├── App.css / index.css      # 전역 스타일
├── api/
│   └── axiosConfig.js       # Axios 인스턴스 + 401 인터셉터
├── components/
│   └── layout/
│       ├── MainLayout.jsx   # 공통 레이아웃 (Sidebar + Outlet)
│       └── Sidebar.jsx      # 우측 사이드바 (유저 정보 + 내비게이션)
└── pages/
    ├── auth/
    │   ├── Login.jsx        # 로그인 폼 + API 연동
    │   └── Register.jsx     # 회원가입 폼 + 아이디 중복체크
    ├── dashboard/
    │   └── Dashboard.jsx    # 내 자산 현황
    ├── stock/
    │   ├── StockList.jsx    # 주식 목록 (테이블)
    │   └── StockDetail.jsx  # 종목 상세 + 차트 + 매수/매도 폼
    ├── news/
    │   └── NewsList.jsx     # 뉴스 목록
    ├── points/
    │   └── PointsHistory.jsx # 포인트 내역
    └── coupons/
        └── CouponStore.jsx  # 쿠폰 상점
```

### 3-2. 라우팅 구조

| 경로 | 컴포넌트 | 레이아웃 |
|---|---|---|
| `/login` | `Login.jsx` | 단독 (Sidebar 없음) |
| `/register` | `Register.jsx` | 단독 |
| `/` | `Dashboard.jsx` | MainLayout (Sidebar 포함) |
| `/stocks` | `StockList.jsx` | MainLayout |
| `/stocks/:stockId` | `StockDetail.jsx` | MainLayout |
| `/news` | `NewsList.jsx` | MainLayout |
| `/points` | `PointsHistory.jsx` | MainLayout |
| `/coupons` | `CouponStore.jsx` | MainLayout |

### 3-3. 의존성 목록

| 패키지 | 버전 | 용도 |
|---|---|---|
| `react` | ^19.2.7 | 코어 |
| `react-dom` | ^19.2.7 | DOM 렌더링 |
| `react-router-dom` | ^7.18.0 | SPA 라우팅 |
| `axios` | ^1.18.1 | HTTP 클라이언트 |
| `apexcharts` | ^5.15.2 | 차트 라이브러리 |
| `react-apexcharts` | ^2.1.1 | React 차트 래퍼 |
| `@stomp/stompjs` | ^7.3.0 | WebSocket STOMP |
| `sockjs-client` | ^1.6.1 | WebSocket 폴백 |

### 3-4. 프론트엔드 ↔ 백엔드 API 연동 현황

| 화면 | API 호출 | 연동 상태 |
|---|---|---|
| **로그인** | `POST /api/members/login` | ✅ 완료 |
| **회원가입** | `POST /api/members/join`, `GET /api/members/id-check` | ✅ 완료 |
| **사이드바** | `GET /api/members/me` | ✅ 완료 |
| **대시보드** | `GET /api/asset` | ✅ 완료 |
| **주식 목록** | `GET /api/stocks` | ✅ 완료 |
| **주식 상세** | `GET /api/stock/{id}`, `GET /api/stock/{id}/history` | ✅ 완료 |
| **매수/매도** | `POST /api/orders/buy`, `POST /api/orders/sell` | ✅ 완료 |
| **WebSocket** | `/ws` → `/topic/orders/{id}`, `/user/queue/notifications` | ✅ 기본 구현 |
| **뉴스** | `GET /api/news` | ✅ 완료 |
| **포인트 내역** | `GET /api/history` | ✅ 완료 |
| **쿠폰 상점** | `GET /api/coupons`, `POST /api/coupons/buy` | ✅ 완료 |
| **호가창 UI** | `GET /api/stock/{id}/orders` | ❌ API 있음, UI 미구현 |

---

## 4. 발견된 이슈 및 개선 필요 사항

### 🔴 Critical (즉시 수정 필요)

#### ① `ddl-auto: create` 운영 사용 금지
- **파일**: `src/main/resources/application.yaml`
- **문제**: 현재 `create` 설정으로 서버 재시작 시 **모든 테이블이 DROP 후 재생성**되어 데이터 유실 위험.
- **해결**: 초기 세팅 완료 후 `none` 또는 `validate`로 변경 필요.

```yaml
# 변경 전
ddl-auto: create

# 변경 후 (운영)
ddl-auto: none
```

#### ② `vite.config.js` Proxy 미설정
- **파일**: `stockGame_react/vite.config.js`
- **문제**: `axiosConfig.js`에 `baseURL: 'http://localhost:8882'` 하드코딩. 배포 환경에서 URL 변경 필요.
- **해결**: Vite proxy 설정 추가 권장.

```js
// vite.config.js 개선안
server: {
  proxy: {
    '/api': 'http://localhost:8882',
    '/ws': { target: 'ws://localhost:8882', ws: true }
  }
}
```

### 🟡 Warning (주의 필요)

#### ③ WebSocket 연결 누수 위험
- **파일**: `stockGame_react/src/pages/stock/StockDetail.jsx`
- **문제**: `useEffect` cleanup에서 `stompClient.deactivate()` 미호출 → 페이지 이동 후에도 연결 유지.
- **해결**: `useRef`로 클라이언트 참조 관리 후 cleanup 시 `deactivate()`.

#### ④ `Transaction` 엔티티 체결 정보 누락
- **파일**: `src/main/java/.../domain/Transaction.java`
- **문제**: `buyOrderNo`, `sellOrderNo`, `createdDate`만 있고 **체결 수량/가격 없음** → 부분체결 기록 불완전.
- **해결**: `amount`, `price` 필드 추가 및 MyBatis 쿼리 보완.

#### ⑤ `StockDetail.jsx` 등락률 표시 미완성
- **파일**: `stockGame_react/src/pages/stock/StockDetail.jsx L121`
- **문제**: `priceDiff` 계산 코드는 있으나 UI에 미표시. `averagePrice`를 이전가로 혼용.
- **해결**: `prevPrice` 필드 활용하여 정확한 등락률 표시.

### 🟢 Minor (향후 개선 권장)

| 항목 | 설명 |
|---|---|
| **비밀번호 암호화** | 학생 비밀번호가 평문 저장 (`MemberServiceImpl`). BCrypt 적용 필요. |
| **전역 상태 관리** | 로그인 정보를 매 요청마다 `/api/members/me`로 조회. `Context API` 또는 `Zustand` 도입 권장. |
| **호가창 UI** | `StockDetail.jsx`에 `/api/stock/{id}/orders` 연동 미구현. 백엔드 API는 이미 존재. |
| **에러 UI** | 각 페이지 에러 처리가 `console.error`에만 의존. 사용자 친화적 에러 메시지 필요. |
| **static 폴더 정리** | `stockGame_spring/src/main/resources/static/` 폴더가 빈 상태. 삭제 가능. |

---

## 5. 전체 연동 흐름 요약

```mermaid
sequenceDiagram
    actor 학생
    participant React as React (5173)
    participant Spring as Spring Boot (8882)
    participant DB as MariaDB

    학생->>React: 로그인 폼 제출
    React->>Spring: POST /api/members/login
    Spring->>DB: SELECT FROM students
    DB-->>Spring: 학생 정보
    Spring-->>React: { success: true, data: {...} } + JSESSIONID Cookie
    React->>React: / 로 리다이렉트

    학생->>React: 주식 목록 클릭
    React->>Spring: GET /api/stocks (with Cookie)
    Spring-->>React: 주식 목록 JSON

    학생->>React: 종목 클릭 → 매수 주문 제출
    React->>Spring: POST /api/orders/buy (with Cookie)
    Spring->>DB: 포인트 검증 + 주문 매칭 + 포인트 이동
    Spring-->>React: { success: true, data: "매수 체결..." }
    Spring->>React: WebSocket /topic/orders/{id} → ORDER_UPDATED
    React->>Spring: GET /api/stock/{id} (화면 자동 갱신)
```

---

## 6. 다음 스프린트 추천 작업 우선순위

| 순위 | 작업 | 예상 공수 |
|---|---|---|
| ① | `ddl-auto: create` → `none`으로 변경 | 5분 |
| ② | `vite.config.js` Proxy 설정 추가 | 10분 |
| ③ | `StockDetail.jsx` WebSocket cleanup 처리 | 30분 |
| ④ | `Transaction` 엔티티 `amount`, `price` 필드 추가 | 1시간 |
| ⑤ | 학생 비밀번호 BCrypt 적용 | 1~2시간 |
| ⑥ | 호가창 UI (`/api/stock/{id}/orders`) 연동 | 2~3시간 |
| ⑦ | 전역 상태 관리 (로그인 Context) 도입 | 2~3시간 |
