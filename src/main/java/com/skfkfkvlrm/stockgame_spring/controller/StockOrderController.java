package com.skfkfkvlrm.stockgame_spring.controller;

import com.skfkfkvlrm.stockgame_spring.controller.dto.request.StockOrderRequest;
import com.skfkfkvlrm.stockgame_spring.controller.dto.response.ApiResponse;
import com.skfkfkvlrm.stockgame_spring.exception.UnauthorizedAccessException;
import com.skfkfkvlrm.stockgame_spring.service.StockOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class StockOrderController {
    private final StockOrderService stockOrderService;

    @PostMapping("/buy")
    public ApiResponse<String> buyStock(@Valid @RequestBody StockOrderRequest request,
                           @SessionAttribute(name = "studentId", required = false) String studentId) {
        if (studentId == null) {
            throw new UnauthorizedAccessException();
        }
        request.setStudentId(studentId);
        String result = stockOrderService.buyStock(request);
        return ApiResponse.success(result, result);
    }

    @PostMapping("/sell")
    public ApiResponse<String> sellStock(@Valid @RequestBody StockOrderRequest request,
                            @SessionAttribute(name = "studentId", required = false) String studentId) {
        if (studentId == null) {
            throw new UnauthorizedAccessException();
        }
        request.setStudentId(studentId);
        String result = stockOrderService.sellStock(request);
        return ApiResponse.success(result, result);
    }

    @PostMapping("/cancel")
    public ApiResponse<String> cancelOrder(@RequestParam("orderId") int orderId, 
                                           @RequestParam("stockId") int stockId,
                              @SessionAttribute(name = "studentId", required = false) String studentId) {
        if (studentId == null) {
            throw new UnauthorizedAccessException();
        }
        stockOrderService.cancelOrder(orderId, studentId);
        return ApiResponse.success("주문이 취소되었습니다.", "Success");
    }
}