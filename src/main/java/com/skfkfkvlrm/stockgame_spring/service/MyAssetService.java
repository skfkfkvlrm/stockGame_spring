package com.skfkfkvlrm.stockgame_spring.service;

import com.skfkfkvlrm.stockgame_spring.controller.dto.response.DashboardResponse;

public interface MyAssetService {
    DashboardResponse getDashboard(String studentId);
}
