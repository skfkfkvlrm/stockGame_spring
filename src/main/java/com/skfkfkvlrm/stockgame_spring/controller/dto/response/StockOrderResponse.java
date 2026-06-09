package com.skfkfkvlrm.stockgame_spring.controller.dto.response;

import com.skfkfkvlrm.stockgame_spring.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockOrderResponse {
    private String studentId;
    private int orderId;
    private String content;
    private int price;
    private int amount;
    private OrderStatus state;
}
