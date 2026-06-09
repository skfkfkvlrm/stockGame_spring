package com.skfkfkvlrm.stockgame_spring.repository.mybatis;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MyPointHistoryRepository {
    List<Map<String, Object>> getMyPointHistoryList(String studentId);
}
