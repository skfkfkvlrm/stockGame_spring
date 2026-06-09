package com.skfkfkvlrm.stockgame_spring.service.impl;

import com.skfkfkvlrm.stockgame_spring.controller.dto.response.StockOrderResponse;
import com.skfkfkvlrm.stockgame_spring.domain.OrderStatus;
import com.skfkfkvlrm.stockgame_spring.repository.mybatis.StockDetailMapper;
import com.skfkfkvlrm.stockgame_spring.service.StockOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockOrderServiceImpl implements StockOrderService {
    private final StockDetailMapper stockDetailMapper;

    @Override
    @Transactional
    public int cancelOrder(int orderId, String studentId) {
        // 1. 취소할 주문 정보 상세 조회
        StockOrderResponse order = stockDetailMapper.getOrderById(orderId);
        if (order == null){
            throw new IllegalArgumentException("존재하지 않는 주문입니다. 주문 번호: " + orderId);
        }
        // 2. 본인 주문이 맞는지 검증
        if (!order.getStudentId().equals(studentId)) {
            throw new IllegalArgumentException("본인 주문이 아닙니다. 주문 번호: " + orderId);
        }
        // 3. 주문 상태가 취소 가능한 상태('대기')인지 검증
        if (order.getState() == OrderStatus.FILLED) {
            throw new IllegalStateException("이미 체결된 주문은 취소할 수 없습니다.");
        }
        if (order.getState() == OrderStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 주문입니다.");
        }
        // 4. [부분 체결 대비]
        // 매수 취소 시 포인트를 상대 학생에세 환불
        // 5. 데이터베이스 주문 상태를 '취소'로 업데이트
        stockDetailMapper.setOrderStateCancel(orderId);
        // 6. 주식 번호 리턴
        return order.getOrderId();
    }
}
