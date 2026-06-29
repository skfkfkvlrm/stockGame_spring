package com.skfkfkvlrm.stockgame_spring.controller;

import com.skfkfkvlrm.stockgame_spring.controller.dto.response.ApiResponse;
import com.skfkfkvlrm.stockgame_spring.domain.Coupon;
import com.skfkfkvlrm.stockgame_spring.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponRepository couponRepository;

    @GetMapping
    public ApiResponse<List<Coupon>> getCoupons(@SessionAttribute(name = "studentId", required = false) String studentId) {
        if (studentId == null) {
            return ApiResponse.error("로그인이 필요합니다.");
        }
        List<Coupon> coupons = couponRepository.getCouponList();
        return ApiResponse.success("Coupon data", coupons);
    }
}
