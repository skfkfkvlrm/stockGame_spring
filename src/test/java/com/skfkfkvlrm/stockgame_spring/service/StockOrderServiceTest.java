package com.skfkfkvlrm.stockgame_spring.service;

import com.skfkfkvlrm.stockgame_spring.controller.dto.request.StockOrderRequest;
import com.skfkfkvlrm.stockgame_spring.controller.dto.response.StockOrderResponse;
import com.skfkfkvlrm.stockgame_spring.domain.MarketSettings;
import com.skfkfkvlrm.stockgame_spring.domain.Order;
import com.skfkfkvlrm.stockgame_spring.domain.OrderStatus;
import com.skfkfkvlrm.stockgame_spring.repository.MarketSettingsRepository;
import com.skfkfkvlrm.stockgame_spring.repository.StockDetailRepository;
import com.skfkfkvlrm.stockgame_spring.repository.StockPriceHistoryRepository;
import com.skfkfkvlrm.stockgame_spring.service.impl.StockOrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockOrderServiceTest {

    @Mock
    private StockDetailRepository stockDetailRepository;

    @Mock
    private StockPriceHistoryRepository stockPriceHistoryRepository;

    @Mock
    private MarketSettingsRepository marketSettingsRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private StockOrderServiceImpl stockOrderService;

    @BeforeEach
    void setUp() {
        // 시장은 항상 열려있다고 가정 (예외 발생 방지)
        MarketSettings settings = MarketSettings.builder().marketOpen(true).build();
        lenient().when(marketSettingsRepository.findById(1)).thenReturn(Optional.of(settings));
    }

    @Test
    void testBuyStock_포인트부족_실패() {
        // Given
        StockOrderRequest request = StockOrderRequest.builder()
                .studentId("testUser")
                .stockId(1)
                .price(1000)
                .amount(10) // 총 10,000원 필요
                .build();
        
        when(stockDetailRepository.getStudentPoint("testUser")).thenReturn(5000); // 5,000원밖에 없음

        // When
        String result = stockOrderService.buyStock(request);

        // Then
        assertEquals("보유 포인트가 부족합니다", result);
        verify(stockDetailRepository, never()).insertOrder(any(Order.class));
    }

    @Test
    void testBuyStock_발행주식매수_성공() {
        // Given
        StockOrderRequest request = StockOrderRequest.builder()
                .studentId("testUser")
                .stockId(1)
                .price(1000)
                .amount(10)
                .build();

        when(stockDetailRepository.getStudentPoint("testUser")).thenReturn(50000); // 50,000 포인트 보유

        Map<String, Object> pubInfo = new HashMap<>();
        pubInfo.put("publication_balance", 100);
        pubInfo.put("publication_price", 1000);
        when(stockDetailRepository.getStockPubInfo(1)).thenReturn(pubInfo);

        // When
        String result = stockOrderService.buyStock(request);

        // Then
        assertEquals("매수 주문이 체결되었습니다.", result);
        verify(stockDetailRepository, times(1)).insertOrder(any(Order.class));
        verify(stockDetailRepository, times(1)).setStudentPointDown(10000, "testUser"); // 10,000 포인트 차감 확인
        verify(stockDetailRepository, times(1)).setStockPubBalance(10, 1); // 10주 잔량 차감 확인
        // 웹소켓 이벤트 전송 여부 검증
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/orders/1"), anyString());
    }

    @Test
    void testSellStock_보유수량초과_실패() {
        // Given
        StockOrderRequest request = StockOrderRequest.builder()
                .studentId("testUser")
                .stockId(1)
                .price(1000)
                .amount(50) // 50주 매도 시도
                .build();

        Map<String, Object> pubInfo = new HashMap<>();
        pubInfo.put("publication_balance", 0); // 발행 물량 소진됨
        when(stockDetailRepository.getStockPubInfo(1)).thenReturn(pubInfo);
        
        when(stockDetailRepository.getStudentStockAmount(1, "testUser")).thenReturn(10); // 실제 보유량은 10주

        // When
        String result = stockOrderService.sellStock(request);

        // Then
        assertEquals("보유 주식량보다 많은 매도 요청은 할 수 없습니다.", result);
        verify(stockDetailRepository, never()).insertOrder(any(Order.class));
    }

    @Test
    void testCancelOrder_매수대기주문_성공및환불() {
        // Given
        StockOrderResponse order = StockOrderResponse.builder()
                .orderId(999)
                .studentId("testUser")
                .stockId(1)
                .content("매수") // 매수 상태
                .state(OrderStatus.대기)
                .price(1000)
                .amount(10)
                .build();
                
        when(stockDetailRepository.getOrderById(999)).thenReturn(order);

        // When
        int canceledOrderId = stockOrderService.cancelOrder(999, "testUser");

        // Then
        assertEquals(999, canceledOrderId);
        // 매수 대기 취소 시 묶여있던 10,000 포인트 환불 로직 검증
        verify(stockDetailRepository, times(1)).setStudentPointUp(10000, "testUser");
        verify(stockDetailRepository, times(1)).setOrderStateCancel(999);
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/orders/1"), anyString());
    }
}
