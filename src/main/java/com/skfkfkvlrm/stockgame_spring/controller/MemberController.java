package com.skfkfkvlrm.stockgame_spring.controller;

import com.skfkfkvlrm.stockgame_spring.controller.dto.request.StudentJoinRequest;
import com.skfkfkvlrm.stockgame_spring.controller.dto.request.StudentLoginRequest;
import com.skfkfkvlrm.stockgame_spring.controller.dto.response.StudentResponse;
import com.skfkfkvlrm.stockgame_spring.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/join")
    public String joinForm() {
        return "AddMember";
    }

    @PostMapping("/join")
    public String join(StudentJoinRequest request, Model model) {
        boolean isJoined = memberService.join(request);
        if (isJoined) {
            return "redirect:/members/login";
        } else {
            model.addAttribute("errorMessage", "회원가입에 실패했습니다.");
            return "AddMember";
        }
    }

    @GetMapping("/login")
    public String loginForm() {
        return "Login";
    }

    @PostMapping("/login")
    public String login(StudentLoginRequest request, Model model, HttpSession session) {
        StudentResponse response = memberService.login(request);
        if (response != null) {
            session.setAttribute("studentId", response.getStudentId());
            session.setAttribute("loginOk", response.getStudentId());
            session.setAttribute("info", response);
            return "redirect:/asset/";
        } else {
            model.addAttribute("errorMessage", "로그인에 실패했습니다.");
            return "Login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/member/login";
    }

    @ResponseBody
    @GetMapping("/id-check")
    public boolean idCheck(@RequestParam("studentId") String studentId) {
        return memberService.getIdCheck(studentId);
    }
}
