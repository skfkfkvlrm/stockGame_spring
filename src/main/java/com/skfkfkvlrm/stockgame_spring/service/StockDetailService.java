package com.skfkfkvlrm.stockgame_spring.service;

import com.skfkfkvlrm.stockgame_spring.controller.dto.response.StockDetailResponse;
import com.skfkfkvlrm.stockgame_spring.domain.Order;

import java.util.List;

public interface StockDetailService {
    // 주식 기본 정보 및 시세 지표 조회
    StockDetailResponse getStockDetailInfo(int stockId);
    // 등록된 주문 현황 목록 조회
    List<Order> getLiveOrderList(int stockId, String type);
    // 내 요청 주문 목록 조회
    List<Order> getwaitingOrderList(int stockId, String studentId);
}
