package com.skfkfkvlrm.stockgame_spring.controller.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SetStockRequest {
    @NotNull(message = "주식 번호는 필수입니다")
    private int stockId;
    @Min(value = 1, message = "주식 수량은 1 이상이어야 합니다")
    private int amount;
    @NotNull(message = "주식 가격은 필수입니다")
    private int price;
}
