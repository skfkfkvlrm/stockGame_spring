package com.skfkfkvlrm.stockgame_spring.controller;

import com.skfkfkvlrm.stockgame_spring.controller.dto.response.ApiResponse;
import com.skfkfkvlrm.stockgame_spring.controller.dto.response.StockDetailResponse;
import com.skfkfkvlrm.stockgame_spring.service.StockDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockDetailController {
    private final StockDetailService stockDetailService;

    @GetMapping("/{stockId}")
    public ApiResponse<StockDetailResponse> getStockDetail(
            @PathVariable("stockId") int stockId,
            @SessionAttribute(name = "studentId", required = false) String studentId) {
        
        if (studentId == null) {
            return ApiResponse.error("로그인이 필요합니다.");
        }
        
        StockDetailResponse response = stockDetailService.getStockDetailInfo(stockId);
        return ApiResponse.success("Stock details", response);
    }
}
