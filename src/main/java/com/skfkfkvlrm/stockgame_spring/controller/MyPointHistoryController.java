package com.skfkfkvlrm.stockgame_spring.controller;

import com.skfkfkvlrm.stockgame_spring.controller.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.skfkfkvlrm.stockgame_spring.controller.dto.response.PointHistoryResponse;
import com.skfkfkvlrm.stockgame_spring.repository.MyPointHistoryRepository;
import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class MyPointHistoryController {

    private final MyPointHistoryRepository myPointHistoryRepository;

    @GetMapping({"", "/"})
    public ApiResponse<List<PointHistoryResponse>> getHistory(@SessionAttribute(name = "studentId", required = false) String studentId) {
        if (studentId == null) {
            return ApiResponse.error("로그인이 필요합니다.");
        }
        List<PointHistoryResponse> history = myPointHistoryRepository.getMyPointHistoryList(studentId);
        return ApiResponse.success("Point history data", history);
    }
}
