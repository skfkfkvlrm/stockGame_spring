package com.skfkfkvlrm.stockgame_spring.controller;

import com.skfkfkvlrm.stockgame_spring.controller.dto.response.DashboardResponse;
import com.skfkfkvlrm.stockgame_spring.service.MyAssetService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/asset")
@RequiredArgsConstructor
public class MyAssetRestController {
    private final MyAssetService myAssetService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard(HttpSession session) {
        String studentId = (String) session.getAttribute("studentId");
        if (studentId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(myAssetService.getDashboard(studentId));
    }
}
