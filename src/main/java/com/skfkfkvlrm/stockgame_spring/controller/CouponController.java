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

import com.skfkfkvlrm.stockgame_spring.service.CouponService;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponRepository couponRepository;
    private final CouponService couponService;

    @GetMapping
    public ApiResponse<List<Coupon>> getCoupons(@SessionAttribute(name = "studentId", required = false) String studentId) {
        if (studentId == null) {
            return ApiResponse.error("로그인이 필요합니다.");
        }
        List<Coupon> coupons = couponRepository.getCouponList();
        return ApiResponse.success("Coupon data", coupons);
    }

    @GetMapping("/my")
    public ApiResponse<List<com.skfkfkvlrm.stockgame_spring.domain.CouponPurchase>> getMyCoupons(@SessionAttribute(name = "studentId", required = false) String studentId) {
        if (studentId == null) {
            return ApiResponse.error("로그인이 필요합니다.");
        }
        List<com.skfkfkvlrm.stockgame_spring.domain.CouponPurchase> coupons = couponRepository.getMyCouponList(studentId);
        return ApiResponse.success("My Coupons", coupons);
    }

    @org.springframework.web.bind.annotation.PostMapping("/{couponId}/buy")
    public ApiResponse<String> buyCoupon(@SessionAttribute(name = "studentId", required = false) String studentId,
                                         @org.springframework.web.bind.annotation.PathVariable("couponId") int couponId) {
        if (studentId == null) {
            return ApiResponse.error("로그인이 필요합니다.");
        }
        
        List<Coupon> allCoupons = couponRepository.getCouponList();
        Coupon targetCoupon = allCoupons.stream()
                .filter(c -> c.getCouponId() == couponId)
                .findFirst()
                .orElse(null);
                
        if (targetCoupon == null) {
            return ApiResponse.error("존재하지 않는 쿠폰입니다.");
        }

        try {
            couponService.buyCoupon(studentId, targetCoupon.getPrice(), targetCoupon.getName(), couponId);
            return ApiResponse.success("Coupon bought", "쿠폰 구매에 성공했습니다.");
        } catch (Exception e) {
            return ApiResponse.error("포인트가 부족하거나 쿠폰 구매 중 오류가 발생했습니다.");
        }
    }
}
