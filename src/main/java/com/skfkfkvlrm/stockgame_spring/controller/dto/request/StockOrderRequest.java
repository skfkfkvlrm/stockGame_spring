package com.skfkfkvlrm.stockgame_spring.controller.dto.request;

import com.skfkfkvlrm.stockgame_spring.domain.OrderStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockOrderRequest {
    @NotNull(message = "주식 번호는 필수입니다.")
    private int stockId;
    @Min(value = 1, message = "주문 수량은 1 이상이어야 합니다.")
    private int amount;
    @Min(value = 1, message = "주문 가격은 1 이상이어야 합니다.")
    private int price;
    private String content;
    private String state;
}
