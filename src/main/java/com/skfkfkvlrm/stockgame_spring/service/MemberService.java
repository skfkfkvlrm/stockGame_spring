package com.skfkfkvlrm.stockgame_spring.service;

import com.skfkfkvlrm.stockgame_spring.controller.dto.request.StudentJoinRequest;
import com.skfkfkvlrm.stockgame_spring.controller.dto.request.StudentLoginRequest;
import com.skfkfkvlrm.stockgame_spring.controller.dto.response.StudentResponse;

public interface MemberService {
    // 회원가입
    boolean join(StudentJoinRequest request);
    // 로그인
    StudentResponse login(StudentLoginRequest request);
    // 아이디 중복체크
    boolean getIdCheck(String studentId);
}
