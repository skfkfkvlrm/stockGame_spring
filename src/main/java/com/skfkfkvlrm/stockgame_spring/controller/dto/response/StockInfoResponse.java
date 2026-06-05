package com.skfkfkvlrm.stockgame_spring.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockInfoResponse {
    private String name;
    private int amount;
    private int publicationPrice;
    private int profit;
}
