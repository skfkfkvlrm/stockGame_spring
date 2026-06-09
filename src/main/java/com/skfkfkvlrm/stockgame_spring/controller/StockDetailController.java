package com.skfkfkvlrm.stockgame_spring.controller;

import com.skfkfkvlrm.stockgame_spring.controller.dto.response.StockDetailResponse;
import com.skfkfkvlrm.stockgame_spring.service.StockDetailService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockDetailController {
    private final StockDetailService stockDetailService;

    // 주식 상세 페이지 진입
    @GetMapping("/{stockId}")
    public String getStockDetail(int stockId, HttpSession session, Model model) {
        String studentId = (String) session.getAttribute("studentId");
        if (studentId == null) {
            return "redirect:/login";
        }

        try {
            StockDetailResponse detailInfo = stockDetailService.getStockDetailInfo(stockId);
            model.addAttribute("stock", detailInfo);
            model.addAttribute("stockName", detailInfo.getStockName());
            model.addAttribute("content", detailInfo.getContent());
            model.addAttribute("nowPrice", detailInfo.getNowPrice());
            model.addAttribute("prevPrice", detailInfo.getPrevPrice());
            model.addAttribute("pubPrice", detailInfo.getPubPrice());
            model.addAttribute("pubAmount", detailInfo.getPubAmount());
        }catch (IllegalArgumentException e) {
            return "redirect:/asset/";
        }
        return "templates/view/StockDetail";
    }
}
