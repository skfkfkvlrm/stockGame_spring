package com.skfkfkvlrm.stockgame_spring.service;

public interface StockOrderService {
    /**
     * 대기 중인 주문을 취소합니다
     * @param orderId 취소할 주문 고유 번호
     * @param studentId 취고 요청을 보낸 학생 ID
     * @return 취소된 주식 번호
     */
    int cancelOrder(int orderId, String studentId);
}
