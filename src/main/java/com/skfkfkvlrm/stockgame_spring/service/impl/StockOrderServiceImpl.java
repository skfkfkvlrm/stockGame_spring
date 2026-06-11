package com.skfkfkvlrm.stockgame_spring.service.impl;

import com.skfkfkvlrm.stockgame_spring.controller.dto.request.StockOrderRequest;
import com.skfkfkvlrm.stockgame_spring.controller.dto.response.StockOrderResponse;
import com.skfkfkvlrm.stockgame_spring.domain.Order;
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
    public String buyStock(StockOrderRequest request) {
        int totalOrderPrice = request.getPrice() * request.getAmount();
        // 1. 보유 포인트 확인
        int currentPoints = stockDetailRepository.getStudentPoint(request.getStudentId());
        if (currentPoints < totalOrderPrice) {
            return "보유 포인트가 부족합니다";
        }
        // 2. 발행 정보 확인
        Map<String, Object> pubInfo = stockDetailRepository.getStockPubInfo(request.getStockId());
        int pubAmount = getIntOrDefault(pubInfo, "publication_balance");
        int pubPrice = getIntOrDefault(pubInfo, "publication_price");
        // a. 발행 주식 거래
        if (pubAmount > 0) {
            if (request.getAmount() > pubAmount) {
                return "발행 잔량보다 많은 수량을 매수할 수 없습니다. (남은 수량: " + pubAmount +")";
            }
            if (request.getPrice() < pubPrice) {
                return "발행 가격(" + pubPrice + ")보다 낮은 가격으로 매수할 수 없습니다.";
            }

            Order order = createOrder(request, OrderStatus.SELL, OrderStatus.FILLED);
            stockDetailRepository.insertOrder(order);
            stockDetailRepository.setMatchedOrder(order.getOrderId(), null);
            stockDetailRepository.setStockPubBalance(request.getAmount(), request.getStockId());
            stockDetailRepository.setStudentPointDown(totalOrderPrice, request.getStudentId());

            return "매수 주문이 체결되었습니다.";
        }
        // b. 학생 간 거래
        Map<String, Object> matchOrder = stockDetailRepository.getMatchOrder(request.getStockId(), request.getPrice(), request.getAmount(), request.getStudentId(), OrderStatus.BUY);
        if (matchOrder != null) {
            int sellOrderId = getIntOrDefault(matchOrder, "order_id");
            String sellerId = (String) matchOrder.get("student_id");
            // 대상 주문 '대기' -> '체결'
            stockDetailRepository.setOrderStateMatched(sellOrderId);
            // 내 주문 '체결'로 등록
            Order order = createOrder(request, OrderStatus.BUY, OrderStatus.FILLED);
            stockDetailRepository.insertOrder(order);
            // 포인트 처리 및 거래내역 등록
            stockDetailRepository.setMatchedOrder(order.getOrderId(), sellOrderId);
            stockDetailRepository.setStudentPointDown(totalOrderPrice, request.getStudentId());
            stockDetailRepository.setStudentPointUp(totalOrderPrice, sellerId);
            return "매수 주문이 체결되었습니다.";
        }
        // c. 매수 대기 등록
        Order order = createOrder(request, OrderStatus.BUY, OrderStatus.PENDING);
        stockDetailRepository.insertOrder(order);
        stockDetailRepository.setStudentPointDown(totalOrderPrice, request.getStudentId());
        return "매수 주문이 등록되었습니다.";
    }

    @Override
    @Transactional
    public String sellStock(StockOrderRequest request) {
        Map<String, Object> pubInfo = stockDetailRepository.getStockPubInfo(request.getStockId());
        int pubAmount = getIntOrDefault(pubInfo, "publication_balance");
        if (pubAmount > 0) {
            return "발행 잔량이 남아 매도요청 할 수 없습니다.";
        }
        // 1. 보유 주식 수량 검증
        int stockAmount = stockDetailRepository.getStudentStockAmount(request.getStockId(), request.getStudentId());
        if (request.getAmount() > stockAmount) {
            return "보유 주식량보다 많은 매도 요청은 할 수 없습니다.";
        }
        int totalOrderPrice = request.getPrice() * request.getAmount();
        // 2. 학생 간 거래
        Map<String, Object> matchOrder = stockDetailRepository.getMatchOrder(request.getStockId(), request.getPrice(), request.getAmount(), request.getStudentId(), OrderStatus.SELL);
        if (matchOrder != null) {
            int buyOrderId = getIntOrDefault(matchOrder, "order_id");
            // 대상 주문 '대기' -> '체결'
            stockDetailRepository.setOrderStateMatched(buyOrderId);
            // 내 주문 '체결'로 등록
            Order order = createOrder(request, OrderStatus.SELL, OrderStatus.FILLED);
            stockDetailRepository.insertOrder(order);
            // 포인트 처리 및 거래내역 등록
            stockDetailRepository.setMatchedOrder(order.getOrderId(), buyOrderId);
            stockDetailRepository.setStudentPointUp(totalOrderPrice, request.getStudentId());

            return "매도가 완료되었습니다.";
        }
        // 3. 매도 대기 등록
        Order order = createOrder(request, OrderStatus.SELL, OrderStatus.PENDING);
        stockDetailRepository.insertOrder(order);
        return "매도주문이 대기처리 되었습니다.";
    }

    private Order createOrder(StockOrderRequest request, OrderStatus content, OrderStatus state) {
        return Order.builder()
                .content(content)
                .state(state.equals(OrderStatus.FILLED) ? OrderStatus.FILLED : OrderStatus.PENDING)
                .price(request.getPrice())
                .amount(request.getAmount())
                .build();
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
        if ("BUY".equals(order.getContent())) {
            int refundAmount = order.getPrice() * order.getAmount();
            stockDetailRepository.setStudentPointUp(refundAmount, studentId);
        }
        // 5. 데이터베이스 주문 상태를 '취소'로 업데이트
        stockDetailRepository.setOrderStateCancel(orderId);
        // 6. 주식 번호 리턴
        return order.getOrderId();
    }
}
