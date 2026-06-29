package com.skfkfkvlrm.stockgame_spring.service;

import com.skfkfkvlrm.stockgame_spring.controller.dto.response.DashboardResponse;
import com.skfkfkvlrm.stockgame_spring.domain.OrderStatus;
import com.skfkfkvlrm.stockgame_spring.repository.MyAssetRepository;
import com.skfkfkvlrm.stockgame_spring.repository.StockDetailRepository;
import com.skfkfkvlrm.stockgame_spring.service.impl.MyAssetServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MyAssetServiceTest {

    @Mock
    private MyAssetRepository myAssetRepository;

    @Mock
    private StockDetailRepository stockDetailRepository;

    @InjectMocks
    private MyAssetServiceImpl myAssetService;

    @Test
    void testGetDashboard_대시보드_조회_성공() {
        // Given
        String studentId = "testUser";

        when(myAssetRepository.getPointValue(studentId)).thenReturn(50000);
        when(myAssetRepository.getTotalCoupon(studentId)).thenReturn(3);

        // 사용자가 체결 상태인 주식 종목 ID(1번)를 하나 가지고 있다고 가정
        when(myAssetRepository.getMyStockNos(studentId, OrderStatus.체결))
                .thenReturn(Arrays.asList(1));

        // 해당 주식에 대한 상세 정보 Mocking
        when(myAssetRepository.getStockAmount(studentId, 1, OrderStatus.체결)).thenReturn(10);
        when(myAssetRepository.getStockName(1)).thenReturn("테스트주식");
        when(stockDetailRepository.getStockPrice(1)).thenReturn(80000);
        when(myAssetRepository.getAveragePrice(studentId, 1, OrderStatus.체결, "매수")).thenReturn(75000);
        when(myAssetRepository.getPurchasePrice(studentId, 1, OrderStatus.체결, "매수")).thenReturn(750000);
        when(myAssetRepository.getStockProfit(studentId, 1, OrderStatus.체결)).thenReturn(50000);

        // When
        DashboardResponse response = myAssetService.getDashboard(studentId);

        // Then
        assertNotNull(response, "대시보드 응답은 null이 아니어야 합니다.");
        assertEquals(50000, response.getTotalPoint(), "총 포인트가 일치해야 합니다.");
        assertEquals(3, response.getTotalCoupon(), "쿠폰 개수가 일치해야 합니다.");

        // 수학적 검증 로직: 총 자산 = 현금 포인트(50,000) + 주식 평가액(10주 * 80,000원 = 800,000원) =
        // 850,000원
        assertEquals(850000, response.getTotalAsset(), "포인트와 보유 주식 가치의 합계가 정확해야 합니다.");
        assertEquals(50000, response.getTotalProfit(), "총 수익이 일치해야 합니다.");

        assertEquals(1, response.getMyStocks().size(), "보유 주식 종류가 1개여야 합니다.");
        assertEquals("테스트주식", response.getMyStocks().get(0).getStockName(), "주식 이름이 일치해야 합니다.");
        assertEquals(10, response.getMyStocks().get(0).getAmount(), "주식 수량이 10개여야 합니다.");
    }
}
