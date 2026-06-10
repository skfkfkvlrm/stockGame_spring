package com.skfkfkvlrm.stockgame_spring.controller;

import com.skfkfkvlrm.stockgame_spring.controller.dto.response.DashboardResponse;
import com.skfkfkvlrm.stockgame_spring.service.MyAssetService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequestMapping("/asset")
@RequiredArgsConstructor
public class MyAssetController {
    private final MyAssetService myAssetService;

    @GetMapping("/")
    public String getDashboard(@SessionAttribute(name = "studentId", required = false) String studentId, Model model) {
        if (studentId == null) {
            return "redirect:/login";
        }
        DashboardResponse response = myAssetService.getDashboard(studentId);
        model.addAttribute("dashboard", response);

        return "MyAssets";
    }
}
