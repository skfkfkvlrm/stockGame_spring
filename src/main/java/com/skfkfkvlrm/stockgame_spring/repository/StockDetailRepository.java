package com.skfkfkvlrm.stockgame_spring.repository;

import com.skfkfkvlrm.stockgame_spring.controller.dto.response.StockOrderResponse;
import com.skfkfkvlrm.stockgame_spring.domain.Order;
import com.skfkfkvlrm.stockgame_spring.domain.OrderStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface StockDetailRepository {
    // 매도 주문요청
    String setSellOrder(@Param("studentId") String studentId, @Param("sellPrice") int sellPrice, @Param("sellAmount") int sellAmount, @Param("stockId") int stockId);

    // 매수 주문요청
    String setBuyOrder(@Param("studentId") String studentId, @Param("buyPrice") int buyPrice, @Param("buyAmount") int buyAmount, @Param("stockId") int stockId);

    // 주식 기본정보 조회
    Map<String, Object> getStockInfo(@Param("stockId") int stockId);

    // 주식 현재 가격 조회
    int getStockPrice(@Param("stockId") int stockId);

    // 주식 이전 가격 대비 조회
    int getStockPriceChange(@Param("stockId") int stockId);

    // 주식 등락률 조회
    double getChangeRate(@Param("stockId") int stockId);

    // 주식 이전가격 조회
    int getPervPrice(@Param("stockId") int stockId);

    // 학생의 가용 보유 포인트 조회
    int getStudentPoint(@Param("studentId") String studentId);

    // 학생의 특정 주식 보유수량 조회
    int getStudentStockAmount(@Param("stockId") int stockId, @Param("studentId") String studentId);

    // 학생의 보유포인트 차감
    boolean setStudentPointDown(@Param("totalPrice") int totalPrice, @Param("studentId") String studentId);

    // 학생의 보유포인트 증가
    boolean setStudentPointUp(@Param("totalPrice") int totalPrice, @Param("studentId") String studentId);

    // 매도, 매수 주문 요청
    boolean setOrderRequest(@Param("content") OrderStatus content, @Param("price") int price, @Param("amount") int amount, @Param("state") OrderStatus state, @Param("studentId") String studentId, @Param("stockId") int stockId);

    // 특정 주식 대기중인 매도 매수 주문 모두 조회
    List<Order> getTotalOrder(@Param("stockId") int stockId);

    // 특정 주식 대기중인 매도 주문 모두 조회
    List<Order> getTotalSellOrder(@Param("stockId") int stockId);

    // 특정 주식 대기중인 매수 주문 모두 조회
    List<Order> getTotalBuyOrder(@Param("stockId") int stockId);

    // 내 주문 요청 조회
    List<Order> getTotalMyOrder(@Param("stockId") int stockId, @Param("studentId") String studentId);

    // 직전에 등록한 주문요청 no 조회
    int getMyOrderNo(@Param("content") OrderStatus content, @Param("studentId") String studentId, @Param("stockId") int stockId, @Param("state") OrderStatus state, @Param("amount") int amount, @Param("price") int price);

    // 주식 발행 정보 조회
    Map<String, Object> getStockPubInfo(@Param("stockId") int stockId);

    // 주식 발행 개수 차감
    boolean setStockPubBalance(@Param("buyAmount") int buyAmount, @Param("stockId") int stockId);

    // 주문 요청 완료
    boolean setMatchedOrder(@Param("buyOrderId") int buyOrderId, @Param("sellOrderId") Integer sellOrderId);

    // 주문 요청 상태 '대기'변경
    boolean setOrderStatePending(@Param("orderId") int orderId);

    // 주문 요청 상태 '체결'변경
    boolean setOrderStateMatched(@Param("orderId") int orderId);



    // 주문 요청 상태 '취소'변경
    boolean setOrderStateCancel(@Param("orderId") int orderId);

    // 매수 주문 요청 매칭
    Map<String, Object> getMatchOrder(@Param("stockId") int stockId, @Param("orderPrice") int orderPrice, @Param("orderAmount") int orderAmount, @Param("studentId") String studentId, @Param("content") OrderStatus content);

    //편의성 메서드 추가
    StockOrderResponse getOrderById(@Param("orderId") int orderId);

    int insertOrder(Order order);
}