package com.skfkfkvlrm.stockgame_spring.repository;

import com.skfkfkvlrm.stockgame_spring.repository.domain.OrderStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MyAssetRepository {
    //내 자산 조회
    int getMyValue(int stockId, String studentId);
    //
    int getPointValue(String studentId);
    //총 손익 조회
    int getTotalProfit(int stockId, String studentId, OrderStatus state);
    //보유 쿠폰 수량 조회
    int getTotalCoupon(String studentId);
    //보유 주식명 조회
    String getStockName(int stockId);
    //보유 주식 수량 조회
    int getStockAmount(String studentId, int stockId, OrderStatus state);
    //보유 주식 가격 조회
    int getAveragePrice(String studentId, int stockId, OrderStatus state, String content);

    int getPurchasePrice(String studentId, int stockId, OrderStatus state, String content);

    int getStockProfit(String studentId, int stockId, OrderStatus state);

    List<Integer> getMyStockNos(String studentId, OrderStatus state);
}
