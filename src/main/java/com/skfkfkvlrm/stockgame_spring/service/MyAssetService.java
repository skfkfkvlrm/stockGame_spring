package com.skfkfkvlrm.stockgame_spring.service;

import com.skfkfkvlrm.stockgame_spring.controller.dto.response.DashboardResponse;
import com.skfkfkvlrm.stockgame_spring.domain.OrderStatus;

import java.util.List;

public interface MyAssetService {
    DashboardResponse getDashboard(String studentId);
}
