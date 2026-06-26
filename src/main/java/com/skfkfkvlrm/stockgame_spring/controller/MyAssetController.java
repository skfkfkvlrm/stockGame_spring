package com.skfkfkvlrm.stockgame_spring.controller;

import com.skfkfkvlrm.stockgame_spring.controller.dto.response.ApiResponse;
import com.skfkfkvlrm.stockgame_spring.controller.dto.response.DashboardResponse;
import com.skfkfkvlrm.stockgame_spring.service.MyAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("/api/asset")
@RequiredArgsConstructor
public class MyAssetController {
    private final MyAssetService myAssetService;

    @GetMapping("/")
    public ApiResponse<DashboardResponse> getDashboard(@SessionAttribute(name = "studentId", required = false) String studentId) {
        if (studentId == null) {
            return ApiResponse.error("로그인이 필요합니다.");
        }
        DashboardResponse dashboardData = myAssetService.getDashboard(studentId);
        return ApiResponse.success("자산 대시보드 조회 성공", dashboardData);
    }
}
