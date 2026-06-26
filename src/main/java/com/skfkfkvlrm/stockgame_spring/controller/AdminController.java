package com.skfkfkvlrm.stockgame_spring.controller;

import com.skfkfkvlrm.stockgame_spring.controller.dto.response.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, String>> dashboard(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.error("로그인이 필요합니다.");
        }
        Map<String, String> data = new HashMap<>();
        data.put("username", authentication.getName());
        data.put("role", authentication.getAuthorities().iterator().next().getAuthority());
        return ApiResponse.success("Dashboard data", data);
    }

    @GetMapping("/students")
    public ApiResponse<String> students() {
        return ApiResponse.success("Student list", "TODO: 학생 목록 데이터");
    }

    @GetMapping("/stocks")
    public ApiResponse<String> stocks() {
        return ApiResponse.success("Stock list", "TODO: 종목 목록 데이터");
    }

    @GetMapping("/coupons")
    public ApiResponse<String> coupons() {
        return ApiResponse.success("Coupon list", "TODO: 쿠폰 목록 데이터");
    }
}
