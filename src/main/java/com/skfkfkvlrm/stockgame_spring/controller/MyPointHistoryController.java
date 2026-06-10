package com.skfkfkvlrm.stockgame_spring.controller;

import com.skfkfkvlrm.stockgame_spring.service.MyPointHistoryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MyPointHistoryController {
    private final MyPointHistoryService myPointHistoryService;

    @GetMapping("/history")
    public String getMyPointHistory(HttpSession session, Model model) {
        String studentId = (String) session.getAttribute("studentId");
        if (studentId == null) {
            return "redirect:/login";
        }
        List<Map<String, Object>> historyList = myPointHistoryService.getMyPointHistoryList(studentId);
        model.addAttribute("historyList", historyList);
        return "view/MyPointHistory";
    }
}
