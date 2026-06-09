package com.skfkfkvlrm.stockgame_spring.controller;

import com.skfkfkvlrm.stockgame_spring.domain.Order;
import com.skfkfkvlrm.stockgame_spring.service.StockDetailService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockDetailRestController {
    private final StockDetailService stockDetailService;

    // 실시간 주식 정보 조회
    @GetMapping("/live-orders")
    public ResponseEntity<List<Order>> getLiveOrderList(@RequestParam int stockId,
                                                        @RequestParam String type, HttpSession session) {
        if (session.getAttribute("studentId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Order> orderList = stockDetailService.getLiveOrderList(stockId, type);
        return ResponseEntity.ok(orderList);
    }

    // 내 요청 주문 목록 조회
    public ResponseEntity<List<Order>> getWaitingList(@RequestParam int stockId, HttpSession session) {
        String studentId = (String) session.getAttribute("studentId");
        if (studentId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Order> orderList = stockDetailService.getwaitingOrderList(stockId, studentId);
        return ResponseEntity.ok(orderList);
    }
}
