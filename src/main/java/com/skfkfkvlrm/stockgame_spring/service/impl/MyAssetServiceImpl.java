package com.skfkfkvlrm.stockgame_spring.service.impl;

import com.skfkfkvlrm.stockgame_spring.controller.dto.response.DashboardResponse;
import com.skfkfkvlrm.stockgame_spring.controller.dto.response.StockInfoResponse;
import com.skfkfkvlrm.stockgame_spring.domain.OrderStatus;
import com.skfkfkvlrm.stockgame_spring.repository.MyAssetRepository;
import com.skfkfkvlrm.stockgame_spring.repository.StockDetailRepository;
import com.skfkfkvlrm.stockgame_spring.service.MyAssetService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyAssetServiceImpl implements MyAssetService {
    private final MyAssetRepository myAssetRepository;
    private final StockDetailRepository stockDetailRepository;



    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(String studentId) {
        int totalPoint = myAssetRepository.getPointValue(studentId);
        int totalCoupon = myAssetRepository.getTotalCoupon(studentId);

        List<Integer> myStockNos = myAssetRepository.getMyStockNos(studentId, OrderStatus.FILLED);

        List<StockInfoResponse> stockList = new ArrayList<>();
        int totalStockValue = 0;
        int totalProfit = 0;

        for (int stockId : myStockNos) {
            int amount = myAssetRepository.getStockAmount(studentId, stockId, OrderStatus.FILLED);

            if (amount > 0) {
                String stockName = myAssetRepository.getStockName(stockId);
                int currentPrice = stockDetailRepository.getStockPrice(stockId);
                int averagePrice = myAssetRepository.getAveragePrice(studentId, stockId, OrderStatus.FILLED, "매수");
                int purchasePrice = myAssetRepository.getPurchasePrice(studentId, stockId, OrderStatus.FILLED, "매수");
                int profit = myAssetRepository.getStockProfit(studentId, stockId, OrderStatus.FILLED);

                totalStockValue += amount * currentPrice;
                totalProfit += profit;

                stockList.add(StockInfoResponse.builder()
                        .stockName(stockName)
                        .amount(amount)
                        .currentPrice(currentPrice)
                        .averagePrice(averagePrice)
                        .purchasePrice(purchasePrice)
                        .profit(profit)
                        .build());
            }
        }
        int totalAsset = totalPoint + totalStockValue;

        return DashboardResponse.builder()
                .totalPoint(totalPoint)
                .totalCoupon(totalCoupon)
                .totalAsset(totalAsset)
                .totalProfit(totalProfit)
                .myStocks(stockList)
                .build();
    }
}
