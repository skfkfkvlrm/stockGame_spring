package com.skfkfkvlrm.stockgame_spring.controller;

import com.skfkfkvlrm.stockgame_spring.service.StockOrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class StockOrderController {
    private final StockOrderService stockOrderService;

    @PostMapping("/cancel")
    public String cancelOrder(@RequestParam int orderId, @RequestParam int stockId,
                              @SessionAttribute(name = "studentId", required = false) String studentId,
                              RedirectAttributes redirectAttributes) {
        // 1. 세션 체크
        if (studentId == null) {
            return "redirect:/login";
        }
        try {
            stockOrderService.cancelOrder(orderId, studentId);
            redirectAttributes.addFlashAttribute("Message", "주문이 취소되었습니다.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("Message", e.getMessage());
        }
        return "redirect:/stock/" + stockId;
    }
}
