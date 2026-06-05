package com.skfkfkvlrm.stockgame_spring.controller.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyAssetDashboardResponse {
    private String studentId;
    private int totalAsset;
    private int totalPoint;
    private int totalProfit;
    private int totalCoupon;
    private List<StockInfoResponse> stockList;
}
