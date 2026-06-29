# 백엔드 프로젝트 진행 상황 (stockGame_spring)

## 현재 상태: 트랙 A (백엔드 개선) 100% 완료 🎉

현재 `stockGame_spring` 프로젝트는 기존의 레거시 JSP 기반 모놀리식 구조에서 탈피하여, 프론트엔드와 분리된 완벽한 **REST API 서버**이자 실시간 통신을 지원하는 **주식 게임 엔진**으로 고도화되었습니다.

### 🚀 완료된 핵심 기능 (1단계 ~ 6단계)
1. **보안 및 아키텍처 개편 (1단계, 3단계)**
   - Spring Security + JPA를 도입하여 교사(관리자)와 학생의 인증 경로 분리 완료
   - BCrypt 암호화, 글로벌 예외 처리(`@RestControllerAdvice`) 및 `ApiResponse<T>` 기반 통합 응답 포맷 구축
2. **거래 엔진 고도화 (2단계, 5단계)**
   - 스플릿(Split) 전략을 사용한 **주식 부분 체결 엔진** 도입
   - `ON DUPLICATE KEY UPDATE`를 활용한 실시간 OHLCV(고가/저가/거래량) 기록 누적 시스템 연동
   - 매일 자정 기준가(`prev_price`)를 갱신하는 자동 스케줄러 도입
3. **사용자 경험 및 시장 운영 정책 (6단계)**
   - `spring-boot-starter-websocket` 기반 STOMP 브로커 설정
   - 호가 단위(Tick Size) 및 시장 개/폐장(MarketSettings) 정책 적용 로직
   - 체결 완료 시 개인 알림(`/queue/notifications`) 및 호가창 브로드캐스트(`/topic/orders`) 발송

### 📌 향후 계획
- 백엔드 자체적인 메인 로드맵은 모두 종료되었습니다.
- 향후 프론트엔드(`stockGame_react`) 개발이 진행됨에 따라 추가적으로 필요한 데이터 필드나 API 수정 요청이 발생할 경우 대응하는 유지보수 형태로 진행될 예정입니다.
