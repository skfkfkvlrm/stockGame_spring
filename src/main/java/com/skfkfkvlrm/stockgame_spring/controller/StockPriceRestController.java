package com.skfkfkvlrm.stockgame_spring.controller;

import com.skfkfkvlrm.stockgame_spring.controller.dto.response.StockPriceResponse;
import com.skfkfkvlrm.stockgame_spring.service.StockPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockPriceRestController {
    private final StockPriceService stockPriceService;

    @GetMapping("/price")
    public ResponseEntity<List<StockPriceResponse>> getStockPriceList() {
        return ResponseEntity.ok(stockPriceService.getStockPriceList());
    }
}
