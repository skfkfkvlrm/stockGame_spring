package com.skfkfkvlrm.stockgame_spring.repository;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MyAssetRepository {
    int getMyValue(int stockNo, String studentId);

    int getPointValue(String studentId);

    int getTotalProfit(int stockNo, String studentId, String state);

    int getTotalCoupon(String studentId);

    String getStockName(int stockNo);

    int getStockAmount(String studentId, int stockNo, String state);

    int getAveragePrice(String studentId, int stockNo, String state, String content);

    int getPurchasePrice(String studentId, int stockNo, String state, String content);

    int getStockProfit(String studentId, int stockNo, String state);

    List<Integer> getMyStockNos(String studentId, String state);
}
