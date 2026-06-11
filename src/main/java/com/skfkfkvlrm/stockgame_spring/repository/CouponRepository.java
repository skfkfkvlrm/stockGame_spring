package com.skfkfkvlrm.stockgame_spring.repository;

import com.skfkfkvlrm.stockgame_spring.domain.Coupon;
import com.skfkfkvlrm.stockgame_spring.domain.CouponPurchase;
import com.skfkfkvlrm.stockgame_spring.domain.CouponPurchaseStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CouponRepository {
    // 등록된 쿠폰 모두 조회
    List<Coupon> getCouponList();

    // 쿠폰 구매
    String setBuyCoupon(@Param("studentId") String studentId, @Param("couponPrice") int couponPrice, @Param("couponName") String couponName, @Param("state") CouponPurchaseStatus state, @Param("couponId") int couponId);

    // 학생이 구매한 쿠폰 개수 조회
    int getMyCouponCount(@Param("studentId") String studentId);

    // 학생 보유 포인트 조회
    int getStudentPoint(@Param("studentId") String studentId);

    // 쿠폰 구매 내역 등록
    int setPurchaseRecord(@Param("studentId") String studentId, @Param("couponId") int couponId, @Param("couponName") String couponName, @Param("couponPrice") int couponPrice, @Param("state") CouponPurchaseStatus state);

    // 학생 포인트 차감 및 보유 쿠폰 수량 증가
    int setStudentAssets(@Param("studentId") String studentId, @Param("couponPrice") int couponPrice);

    // 내가 보유한 쿠폰 조회
    List<CouponPurchase> getMyCouponList(@Param("studentId") String studentId);
}