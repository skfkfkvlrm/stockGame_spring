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

import java.util.List;
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

            Order order = createOrder(request, OrderStatus.매도, OrderStatus.체결);
            stockDetailRepository.insertOrder(order);
            stockDetailRepository.setMatchedOrder(order.getOrderId(), null);
            stockDetailRepository.setStockPubBalance(request.getAmount(), request.getStockId());
            stockDetailRepository.setStudentPointDown(totalOrderPrice, request.getStudentId());

            return "매수 주문이 체결되었습니다.";
        }
        // b. 학생 간 거래 (부분 체결 로직)
        int remainingAmount = request.getAmount();
        // 매수 시 "매도" 대기열을 조회
        List<Order> sellOrders = stockDetailRepository.getMatchOrderList(
                request.getStockId(), OrderStatus.매도.name(), request.getPrice(), request.getStudentId());

        for (Order sellOrder : sellOrders) {
            int matchAmount = Math.min(remainingAmount, sellOrder.getAmount());
            int matchPrice = sellOrder.getPrice();
            int matchTotalPrice = matchPrice * matchAmount;

            // 매도 주문 처리
            int sellOrderId;
            if (matchAmount == sellOrder.getAmount()) {
                stockDetailRepository.setOrderStateMatched(sellOrder.getOrderId());
                sellOrderId = sellOrder.getOrderId();
            } else {
                stockDetailRepository.updateOrderAmount(matchAmount, sellOrder.getOrderId());
                Order sellFilled = Order.builder()
                        .content(OrderStatus.매도).state(OrderStatus.체결)
                        .price(matchPrice).amount(matchAmount)
                        .studentId(sellOrder.getStudentId()).stockId(request.getStockId()).build();
                stockDetailRepository.insertOrder(sellFilled);
                sellOrderId = sellFilled.getOrderId();
            }

            // 매수 주문 처리 (체결용)
            Order buyFilled = Order.builder()
                    .content(OrderStatus.매수).state(OrderStatus.체결)
                    .price(matchPrice).amount(matchAmount)
                    .studentId(request.getStudentId()).stockId(request.getStockId()).build();
            stockDetailRepository.insertOrder(buyFilled);
            int buyOrderId = buyFilled.getOrderId();

            // 거래내역 및 포인트 정산
            stockDetailRepository.setMatchedOrder(buyOrderId, sellOrderId);
            stockDetailRepository.setStudentPointDown(matchTotalPrice, request.getStudentId());
            stockDetailRepository.setStudentPointUp(matchTotalPrice, sellOrder.getStudentId());

            remainingAmount -= matchAmount;
            if (remainingAmount == 0) {
                break;
            }
        }

        // c. 남은 수량이 있으면 대기 등록
        if (remainingAmount > 0) {
            Order order = Order.builder()
                    .content(OrderStatus.매수).state(OrderStatus.대기)
                    .price(request.getPrice()).amount(remainingAmount)
                    .studentId(request.getStudentId()).stockId(request.getStockId()).build();
            stockDetailRepository.insertOrder(order);
            stockDetailRepository.setStudentPointDown(request.getPrice() * remainingAmount, request.getStudentId());
            
            if (remainingAmount < request.getAmount()) {
                return "부분 체결 완료 및 남은 수량 매수 대기 등록되었습니다.";
            } else {
                return "매수 주문이 대기 등록되었습니다.";
            }
        }
        
        return "매수 주문이 전량 체결되었습니다.";
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
        // 2. 학생 간 거래 (부분 체결 로직)
        int remainingAmount = request.getAmount();
        // 매도 시 "매수" 대기열을 조회
        List<Order> buyOrders = stockDetailRepository.getMatchOrderList(
                request.getStockId(), OrderStatus.매수.name(), request.getPrice(), request.getStudentId());

        for (Order buyOrder : buyOrders) {
            int matchAmount = Math.min(remainingAmount, buyOrder.getAmount());
            int matchPrice = buyOrder.getPrice();
            int matchTotalPrice = matchPrice * matchAmount;

            // 매수 주문 처리
            int buyOrderId;
            if (matchAmount == buyOrder.getAmount()) {
                stockDetailRepository.setOrderStateMatched(buyOrder.getOrderId());
                buyOrderId = buyOrder.getOrderId();
            } else {
                stockDetailRepository.updateOrderAmount(matchAmount, buyOrder.getOrderId());
                Order buyFilled = Order.builder()
                        .content(OrderStatus.매수).state(OrderStatus.체결)
                        .price(matchPrice).amount(matchAmount)
                        .studentId(buyOrder.getStudentId()).stockId(request.getStockId()).build();
                stockDetailRepository.insertOrder(buyFilled);
                buyOrderId = buyFilled.getOrderId();
            }

            // 매도 주문 처리 (체결용)
            Order sellFilled = Order.builder()
                    .content(OrderStatus.매도).state(OrderStatus.체결)
                    .price(matchPrice).amount(matchAmount)
                    .studentId(request.getStudentId()).stockId(request.getStockId()).build();
            stockDetailRepository.insertOrder(sellFilled);
            int sellOrderId = sellFilled.getOrderId();

            // 거래내역 및 포인트 정산
            stockDetailRepository.setMatchedOrder(buyOrderId, sellOrderId);
            stockDetailRepository.setStudentPointUp(matchTotalPrice, request.getStudentId());
            // 매수자는 이미 매수 대기 시점에 포인트를 차감당했으므로 체결 시점에 차감할 필요 없음.

            remainingAmount -= matchAmount;
            if (remainingAmount == 0) {
                break;
            }
        }

        // 3. 남은 수량이 있으면 대기 등록
        if (remainingAmount > 0) {
            Order order = Order.builder()
                    .content(OrderStatus.매도).state(OrderStatus.대기)
                    .price(request.getPrice()).amount(remainingAmount)
                    .studentId(request.getStudentId()).stockId(request.getStockId()).build();
            stockDetailRepository.insertOrder(order);
            
            if (remainingAmount < request.getAmount()) {
                return "부분 체결 완료 및 남은 수량 매도 대기 등록되었습니다.";
            } else {
                return "매도 주문이 대기 등록되었습니다.";
            }
        }

        return "매도 주문이 전량 체결되었습니다.";
    }

    private Order createOrder(StockOrderRequest request, OrderStatus content, OrderStatus state) {
        return Order.builder()
                .content(content)
                .state(state.equals(OrderStatus.체결) ? OrderStatus.체결 : OrderStatus.대기)
                .price(request.getPrice())
                .amount(request.getAmount())
                .studentId(request.getStudentId())
                .stockId(request.getStockId())
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
            throw new com.skfkfkvlrm.stockgame_spring.exception.OrderNotFoundException();
        }
        // 2. 본인 주문이 맞는지 검증
        if (!order.getStudentId().equals(studentId)) {
            throw new com.skfkfkvlrm.stockgame_spring.exception.NotYourOrderException();
        }
        // 3. 주문 상태가 취소 가능한 상태('대기')인지 검증
        if (order.getState() == OrderStatus.체결) {
            throw new com.skfkfkvlrm.stockgame_spring.exception.InvalidOrderStateException();
        }
        if (order.getState() == OrderStatus.취소) {
            throw new com.skfkfkvlrm.stockgame_spring.exception.InvalidOrderStateException();
        }
        // 4. 매수 취소 시 포인트 환불
        // order.getContent()는 DB에 한국어("매수")로 저장되므로 OrderStatus enum 이름으로 비교해야 함
        if (OrderStatus.매수.name().equals(order.getContent())) {
            int refundAmount = order.getPrice() * order.getAmount();
            stockDetailRepository.setStudentPointUp(refundAmount, studentId);
        }
        // 5. 주문 상태를 '취소'로 업데이트
        stockDetailRepository.setOrderStateCancel(orderId);
        // 6. 주식 번호 리턴
        return order.getOrderId();
    }
}
