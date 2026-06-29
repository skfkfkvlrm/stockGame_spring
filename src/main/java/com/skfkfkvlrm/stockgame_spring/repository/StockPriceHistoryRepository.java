package com.skfkfkvlrm.stockgame_spring.repository;

import com.skfkfkvlrm.stockgame_spring.domain.StockPriceHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StockPriceHistoryRepository {
    
    void upsertDailyPrice(@Param("stockId") int stockId, 
                          @Param("baseDate") LocalDate baseDate, 
                          @Param("price") int price, 
                          @Param("amount") int amount);

    List<StockPriceHistory> findHistoryByStockId(@Param("stockId") int stockId);

    void updatePrevPricesToLatestClose();
}
