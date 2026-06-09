package com.skfkfkvlrm.stockgame_spring.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockPriceResponse {
    private int stockId;
    private String stockName;
    private int currentPrice;
    private int prevPrice;
    private int priceChange;
    private double changeRate;
}
