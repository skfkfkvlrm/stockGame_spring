package com.skfkfkvlrm.stockgame_spring.controller;

import com.skfkfkvlrm.stockgame_spring.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    @GetMapping("/")
    public String getCouponList(Model model) {
        model.addAttribute("couponList", couponService.getCouponList());
        return "CouponMarket";
    }

    @GetMapping("/my-coupons")
    public String getMyCouponList(@SessionAttribute(name = "studentId", required = false) String studentId,
            Model model) {
        if (studentId == null) {
            return "redirect:/login";
        } else {
            model.addAttribute("couponList", couponService.getMyCouponList(studentId));
            return "CouponPersonal";
        }
    }

    @PostMapping("/buy")
    public String buyCoupon(int couponId, int couponPrice, String couponName,
            @SessionAttribute(name = "studentId", required = false) String studentId,
            RedirectAttributes redirectAttributes) {
        if (studentId == null) {
            return "redirect:/login";
        }

        try {
            couponService.buyCoupon(studentId, couponPrice, couponName, couponId);
            redirectAttributes.addFlashAttribute("Message", "쿠폰 구매가 완료되었습니다.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("Message", e.getMessage());
        }
        return "redirect:/coupons/";
    }
}
