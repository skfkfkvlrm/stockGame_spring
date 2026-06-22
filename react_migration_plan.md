# React SPA 마이그레이션 계획 (구현 계획)

기존 애플리케이션은 서버 사이드 렌더링을 위해 Spring과 JSP(JavaServer Pages)를 사용하고 있습니다. 이 아키텍처를 React 기반의 SPA(Single Page Application)로 마이그레이션하려면 프론트엔드와 백엔드를 완전히 분리해야 합니다.

## 검토 필요 사항 (User Review Required)

> [!IMPORTANT]
> React SPA로 전환하려면 프론트엔드와 백엔드 모두에 변경 사항이 필요합니다. Spring 백엔드는 HTML 페이지를 렌더링하는 대신 JSON 데이터(RESTful API)만 제공하도록 수정되어야 하며, React 프론트엔드가 모든 라우팅 및 UI 렌더링을 처리하게 됩니다.

## 열린 질문 (Open Questions)

1. **프론트엔드 프로젝트 설정**: React 프로젝트 설정에 Vite를 사용하시겠습니까, 아니면 Create React App을 사용하시겠습니까? (빠른 속도 때문에 최신 React 애플리케이션에는 Vite를 강력히 권장합니다). 현재 PC에 Node.js(npm)가 설치되어 있어야 진행이 가능합니다.
2. **상태 관리**: 표준 React 훅(useState, Context API)을 사용하시겠습니까, 아니면 사용자 상태(로그인 세션, 자산 등) 관리를 위해 Redux나 Zustand 같은 라이브러리를 선호하시나요?
3. **스타일링 (CSS)**: 기존의 CSS 파일을 그대로 유지하시겠습니까, 아니면 이 과정에서 CSS Modules, Styled Components 또는 Tailwind CSS로 전환하시겠습니까?
4. **백엔드 API 준비 상태**: 이미 Spring Controller를 `@RestController`로 변경하여 JSON을 반환하도록 수정하셨나요, 아니면 백엔드 쪽도 제가 가이드/수정해드려야 하나요?

## 제안하는 아키텍처 변경 사항

### 1. 프론트엔드: React 애플리케이션 (SPA)
새로운 독립적인 React 프로젝트를 생성합니다. 이 프로젝트는 자체 개발 서버(예: Vite가 포함된 Node.js)에서 실행되며 HTTP 통신을 통해 Spring 백엔드와 통신합니다.

*   **라우팅**: `react-router-dom`을 사용하여 페이지 전체 새로고침 없는 SPA 화면 전환을 구현합니다.
*   **컴포넌트 구조**: JSP 페이지들은 React 함수형 컴포넌트로 변환됩니다.

**컴포넌트 매핑 전략:**
*   `SideBar.jsp` -> `<Sidebar />` 컴포넌트 (페이지 전환 시에도 계속 유지되도록 렌더링).
*   `StockList.jsp` -> `<StockList />` 페이지 컴포넌트.
*   `StockDetail.jsp` -> `<StockDetail />` 페이지 컴포넌트.
*   `Login.jsp` -> `<Login />` 페이지 컴포넌트.
*   `AddMember.jsp` -> `<Register />` 페이지 컴포넌트.
*   `MyAssets.jsp` -> `<MyAssets />` 페이지 컴포넌트.
*   기타 JSP 파일들도 독립적인 페이지 컴포넌트로 분리됩니다.

### 2. 백엔드: Spring Boot API
현재 뷰 이름(예: `return "view/StockList"`)을 반환하는 Spring Controller들을 REST API로 변환해야 합니다.
*   `@Controller`를 `@RestController`로 변경합니다.
*   Model에 데이터를 담아 뷰를 반환하는 대신, 데이터 객체(DTO)를 직접 반환하면 Spring이 이를 JSON으로 직렬화합니다.
*   Spring 애플리케이션에 CORS(Cross-Origin Resource Sharing) 설정을 추가하여 React 개발 서버(일반적으로 localhost:5173)에서 API 요청을 보낼 수 있도록 허용해야 합니다.

## 마이그레이션 단계별 계획

### 1단계: React 프로젝트 초기화
1.  Vite를 사용하여 새로운 React 프로젝트를 초기화합니다. (npm 환경 필요)
2.  필수 라이브러리 설치: `react-router-dom`(라우팅용), `axios`(API 요청용).
3.  기본 레이아웃 및 라우팅 구조를 설정합니다. (예: Sidebar가 고정되도록 배치)

### 2단계: UI 컴포넌트 정적 변환
1.  JSP 파일의 HTML 구조와 CSS 클래스를 React JSX 형식으로 변환합니다.
2.  `templates/css/`의 기존 CSS 파일들을 React 프로젝트로 가져와 import 합니다.
3.  `<c:choose>`, `<c:forEach>`와 같은 JSP 문법을 React의 조건부 렌더링(`{condition && ...}`) 및 배열 매핑(`array.map(...)`)으로 변환합니다.

### 3단계: 백엔드 API 변환
1.  JSP 뷰 대신 JSON을 반환하도록 Spring Controller를 수정합니다.
2.  Spring Boot에 CORS 설정을 추가합니다.

### 4단계: 데이터 연동 (API 통신)
1.  React 컴포넌트 내에서 `useEffect`와 `fetch`/`axios`를 사용하여 새로운 Spring REST API에서 데이터를 가져옵니다.
2.  가져온 데이터를 컴포넌트의 State에 바인딩하여 동적으로 화면을 렌더링합니다.

## 검증 계획

### 수동 검증 (Manual Verification)
- Spring Boot 백엔드 서버와 React 프론트엔드 개발 서버를 모두 실행합니다.
- React 애플리케이션 내에서 페이지 이동을 해보며 전체 새로고침 없이 화면 전환이 부드럽게 이루어지는지 확인합니다.
- 주식 목록, 내 자산 등의 페이지에서 Spring API로부터 데이터를 올바르게 받아와 화면에 표시하는지 검증합니다.
