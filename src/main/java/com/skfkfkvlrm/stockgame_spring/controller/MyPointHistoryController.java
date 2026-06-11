package com.skfkfkvlrm.stockgame_spring.controller;

import com.skfkfkvlrm.stockgame_spring.controller.dto.response.PointHistoryResponse;
import com.skfkfkvlrm.stockgame_spring.service.MyPointHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MyPointHistoryController {
    private final MyPointHistoryService myPointHistoryService;

    @GetMapping("/history")
    public String getMyPointHistory(@SessionAttribute(name = "studentId", required = false) String studentId, Model model) {
        if (studentId == null) {
            return "redirect:/login";
        }
        List<PointHistoryResponse> historyList = myPointHistoryService.getMyPointHistoryList(studentId);
        model.addAttribute("historyList", historyList);
        return "MyPointHistory";
    }
}
