package com.skfkfkvlrm.stockgame_spring.controller;

import com.skfkfkvlrm.stockgame_spring.controller.dto.request.StockOrderRequest;
import com.skfkfkvlrm.stockgame_spring.service.StockOrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class StockOrderController {
    private final StockOrderService stockOrderService;

    @PostMapping("/buy")
    public String buyStock(@Valid StockOrderRequest request,
                           HttpSession session, RedirectAttributes redirectAttributes) {
        session.setAttribute("studentId", request.getStudentId());
        if (session.getAttribute("studentId") == null) {
            return "redirect:/login";
        }
        String result = stockOrderService.buyStock(request);
        redirectAttributes.addFlashAttribute("Message", result);
        @SuppressWarnings("unchecked")
        Map<String, Object> info = (Map<String, Object>) session.getAttribute("info");
        if (info != null) {
            session.setAttribute("info", info);
        }
        return "redirect:/stock/" + request.getStockId();
    }

    @PostMapping("/sell")
    public String sellStock(@Valid StockOrderRequest request,
                            HttpSession session, RedirectAttributes redirectAttributes) {
        session.setAttribute("studentId", request.getStudentId());
        if (session.getAttribute("studentId") == null) {
            return "redirect:/login";
        }
        String result = stockOrderService.sellStock(request);
        redirectAttributes.addFlashAttribute("Message", result);
        @SuppressWarnings("unchecked")
        Map<String, Object> info = (Map<String, Object>) session.getAttribute("info");
        if (info != null) {
            session.setAttribute("info", info);
        }
        return "redirect:/stock/" + request.getStockId();
    }

    @PostMapping("/cancel")
    public String cancelOrder(@RequestParam int orderId, @RequestParam int stockId,
                              @SessionAttribute(name = "studentId", required = false) String studentId,
                              RedirectAttributes redirectAttributes) {
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
