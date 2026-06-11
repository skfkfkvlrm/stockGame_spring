package com.skfkfkvlrm.stockgame_spring.service;

import com.skfkfkvlrm.stockgame_spring.controller.dto.response.PointHistoryResponse;

import java.util.List;

public interface MyPointHistoryService {
    List<PointHistoryResponse> getMyPointHistoryList(String studentId);
}
