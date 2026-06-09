package com.skfkfkvlrm.stockgame_spring.controller;

import com.skfkfkvlrm.stockgame_spring.controller.dto.response.DashboardResponse;
import com.skfkfkvlrm.stockgame_spring.service.MyAssetService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/asset")
@RequiredArgsConstructor
public class MyAssetController {
    private final MyAssetService myAssetService;

    @GetMapping("/")
    public String getDashboard(HttpSession session, Model model) {
        String studentId = (String) session.getAttribute("studentId");
        if (studentId == null) {
            return "redirect:/login";
        }
        DashboardResponse response = myAssetService.getDashboard(studentId);
        model.addAttribute("dashboard", response);

        return "templates/view/MyAssets";
    }
}
