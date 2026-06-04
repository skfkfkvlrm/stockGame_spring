package com.skfkfkvlrm.stockgame_spring.repository;

import com.skfkfkvlrm.stockgame_spring.repository.domain.Order;
import com.skfkfkvlrm.stockgame_spring.repository.domain.OrderStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface StockDetailRepository {
    // 매도 주문요청
    String setSellOrder(String studentId, int sellPrice, int sellAmount, int stockId);

    // 매수 주문요청
    String setBuyOrder(String studentId, int buyPrice, int buyAmount, int stockId);

    // 주식 기본정보 조회
    Map<String, Object> getStockInfo(int stockId);

    // 주식 현재 가격 조회
    int getStockPrice(int stockId);

    // 주식 이전 가격 대비 조회
    int getStockPriceChange(int stockId);

    // 주식 등락률 조회
    double getChangeRate(int stockId);

    // 주식 이전가격 조회
    int getPervPrice(int stockId);

    // 학생의 가용 보유 포인트 조회
    int getStudentPoint(String studentId);

    // 학생의 특정 주식 보유수량 조회
    int getStudentStockAmount(int stockId, String studentId);

    // 학생의 보유포인트 차감
    boolean setStudentPointDown(int totalPrice, String studentId);

    // 학생의 보유포인트 증가
    boolean setStudentPointUp(int totalPrice, String studentId);

    // 매도, 매수 주문 요청
    boolean setOrderRequest(String content, int price, int amount, OrderStatus state, String studentId, int stockId);

    // 특정 주식 대기중인 매도 매수 주문 모두 조회
    List<Order> getTotalOrder(int stockId);

    // 특정 주식 대기중인 매도 주문 모두 조회
    List<Order> getTotalSellOrder(int stockId);

    // 특정 주식 대기중인 매수 주문 모두 조회
    List<Order> getTotalBuyOrder(int stockId);

    // 내 주문 요청 조회
    List<Order> getTotalMyOrder(int stockId, String studentId);

    // 직전에 등록한 주문요청 no 조회
    int getMyOrderNo(String content, String studentId, int stockId, OrderStatus state, int amount, int price);

    // 주식 발행 정보 조회
    Map<String, Object> getStockPubInfo(int stockId);

    // 주식 발행 개수 차감
    boolean setStockPubBalance(int buyAmount, int stockId);

    // 주문 요청 완료
    boolean setMatchedOrder(int buyOrderId, int sellOrderId);

    // 주문 요청 상태 '대기'변경
    boolean setOrderStatePending(int orderId);

    // 주문 요청 상태 '체결'변경
    boolean setOrderStateMatched(int orderId);

    // 주문 요청 상태 '취소'변경
    boolean setOrderStateCancel(int orderId);

    // 매수 주문 요청 매칭
    Map<String, Object> getMatchOrder(int stockId, int orderPrice, int orderAmount, String studentId, String content);
}
