package com.skfkfkvlrm.stockgame_spring.repository;

import com.skfkfkvlrm.stockgame_spring.controller.dto.response.PointHistoryResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MyPointHistoryRepository {
    List<PointHistoryResponse> getMyPointHistoryList(String studentId);
}
