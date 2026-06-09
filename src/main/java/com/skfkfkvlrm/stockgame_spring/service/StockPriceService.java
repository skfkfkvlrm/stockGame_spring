package com.skfkfkvlrm.stockgame_spring.service;

import com.skfkfkvlrm.stockgame_spring.controller.dto.response.StockPriceResponse;

import java.util.List;
import java.util.Map;

public interface StockPriceService {
    //시스템에 상장된 전체 주식 종목의 실시간 시세(현재가, 변동액, 등락률)
    List<StockPriceResponse> getStockPriceList();

    int getIntOrDefault(Map<String, Object> map, String key);
}
