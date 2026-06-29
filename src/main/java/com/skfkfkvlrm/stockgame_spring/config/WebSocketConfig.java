package com.skfkfkvlrm.stockgame_spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 클라이언트가 구독(Subscribe)할 브로커의 프리픽스
        config.enableSimpleBroker("/topic", "/queue");
        // 클라이언트가 서버로 메시지를 보낼 때(Publish) 사용할 프리픽스
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 연결할 STOMP 엔드포인트: /ws
        // SockJS fallback을 지원하기 위해 withSockJS() 추가 가능 (React 등에서 SockJS-client 사용 시)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // CORS 허용 (실전에서는 구체적인 도메인 설정)
                .withSockJS();
    }
}
