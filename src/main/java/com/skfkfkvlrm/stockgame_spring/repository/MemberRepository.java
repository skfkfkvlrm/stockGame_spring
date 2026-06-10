package com.skfkfkvlrm.stockgame_spring.repository;

import com.skfkfkvlrm.stockgame_spring.controller.dto.request.StudentJoinRequest;
import com.skfkfkvlrm.stockgame_spring.controller.dto.response.StudentResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface MemberRepository {
    // 회원가입
    int setMember(StudentJoinRequest request);

    // 로그인
    Map<String, Object> login(String studentId, String password);

    // 아이디 중복체크
    int getIdCheck(String studentId);
}
