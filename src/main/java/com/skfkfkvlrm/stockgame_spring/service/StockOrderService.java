package com.skfkfkvlrm.stockgame_spring.service;

import com.skfkfkvlrm.stockgame_spring.controller.dto.request.StockOrderRequest;

public interface StockOrderService {
    /**
     * 매수 로직(기존 로직)
     * @param studentId 매수 요청한 학생 ID
     * @param request 매수 요청 데이터
     */
    String buyStock(String studentId, StockOrderRequest request);
    /**
     * 매도 로직(기존 로직)
     * @param studentId 매도 요청한 학생 ID
     * @param request 매도 요청 데이터
     */
    String sellStock(String studentId, StockOrderRequest request);
    /**
     * 대기 중인 주문을 취소합니다
     * @param orderId 취소할 주문 고유 번호
     * @param studentId 취고 요청을 보낸 학생 ID
     * @return 취소된 주식 번호
     */
    int cancelOrder(int orderId, String studentId);
}
