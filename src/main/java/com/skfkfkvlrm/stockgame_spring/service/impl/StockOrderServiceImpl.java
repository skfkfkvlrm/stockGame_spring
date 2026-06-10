package com.skfkfkvlrm.stockgame_spring.service.impl;

import com.skfkfkvlrm.stockgame_spring.controller.dto.request.StockOrderRequest;
import com.skfkfkvlrm.stockgame_spring.controller.dto.response.StockOrderResponse;
import com.skfkfkvlrm.stockgame_spring.domain.OrderStatus;
import com.skfkfkvlrm.stockgame_spring.repository.StockDetailRepository;
import com.skfkfkvlrm.stockgame_spring.service.StockOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockOrderServiceImpl implements StockOrderService {
    private final StockDetailRepository stockDetailRepository;

    @Override
    @Transactional
    public String buyStock(String studentId, StockOrderRequest request) {
        int totalOrderPrice = request.getPrice() * request.getAmount();
        // 1. 보유 포인트 확인
        int currentPoints = stockDetailRepository.getStudentPoint(studentId);
        if (currentPoints < totalOrderPrice) {
            return "잔여 포인트가 부족합니다";
        }
        // 2. 발행 정보 확인
        Map<String, Object> pubInfo = stockDetailRepository.getStockPubInfo(request.getStockId());
        int pubAmount = getIntOrDefault(pubInfo, "publication_balance");
        if (pubAmount < request.getAmount()) {
            stockDetailRepository.setOrderRequest(request.getContent(), request.getPrice(), request.getAmount(),
                    request.getState(), studentId, request.getStockId());
        }

        // a. 발행 주식 거래
        // b. 학생 간 거래
        // 대상 주문 '대기' -> '체결'
        // 매수 주문 '체결'로 등록
        // 포인트 처리 및 거래내역 등록
        // c. 매수 대기 등록
    }

    @Override
    @Transactional
    public String sellStock(String studentId, StockOrderRequest request) {
        int totalOrderPrice = request.getPrice() * request.getAmount();
        // 1. 보유 주식 수량 검증
        // 2. 학생 간 거래
        // 대상 주문 '대기' -> '체결'
        // 매도 주문 '체결'로 등록
        // 포인트 처리 및 거래내역 등록
        // 3. 매도 대기 등록
    }

    private int getIntOrDefault(Map<String, Object> map, String key) {
        if (map == null || map.get(key) == null) {
            return 0;
        }
        return ((Number) map.get(key)).intValue();
    }

    @Override
    @Transactional
    public int cancelOrder(int orderId, String studentId) {
        // 1. 취소할 주문 정보 상세 조회
        StockOrderResponse order = stockDetailRepository.getOrderById(orderId);
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
        if ("매수".equals(order.getContent())) {
            int refundAmount = order.getPrice() * order.getAmount();
            stockDetailRepository.setStudentPointUp(refundAmount, studentId);
        }
        // 5. 데이터베이스 주문 상태를 '취소'로 업데이트
        stockDetailRepository.setOrderStateCancel(orderId);
        // 6. 주식 번호 리턴
        return order.getOrderId();
    }
}
