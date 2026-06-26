package com.skfkfkvlrm.stockgame_spring.controller;

import com.skfkfkvlrm.stockgame_spring.controller.dto.request.StudentJoinRequest;
import com.skfkfkvlrm.stockgame_spring.controller.dto.request.StudentLoginRequest;
import com.skfkfkvlrm.stockgame_spring.controller.dto.response.ApiResponse;
import com.skfkfkvlrm.stockgame_spring.controller.dto.response.StudentResponse;
import com.skfkfkvlrm.stockgame_spring.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/join")
    public ApiResponse<Boolean> join(@RequestBody StudentJoinRequest request) {
        boolean isJoined = memberService.join(request);
        return ApiResponse.success("회원가입 성공", isJoined);
    }

    @PostMapping("/login")
    public ApiResponse<StudentResponse> login(@RequestBody StudentLoginRequest request, HttpSession session) {
        StudentResponse response = memberService.login(request);
        session.setAttribute("studentId", response.getStudentId());
        session.setAttribute("loginOk", response.getStudentId());
        session.setAttribute("info", response);
        return ApiResponse.success("로그인 성공", response);
    }

    @PostMapping("/logout")
    public ApiResponse<Boolean> logout(HttpSession session) {
        session.invalidate();
        return ApiResponse.success("로그아웃 성공", true);
    }

    @GetMapping("/id-check")
    public ApiResponse<Boolean> idCheck(@RequestParam("studentId") String studentId) {
        boolean isDuplicate = memberService.getIdCheck(studentId);
        return ApiResponse.success(isDuplicate ? "이미 사용중인 아이디입니다." : "사용 가능한 아이디입니다.", isDuplicate);
    }
}