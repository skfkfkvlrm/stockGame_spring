package com.skfkfkvlrm.stockgame_spring.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface MemberRepository {
    // 회원가입
    boolean setMember(String studentId, String password, String name, int grade, String className, int classNumber);

    // 로그인
    Map<String, Object> login(String studentId, String password);

    // 아이디 중복체크
    boolean getIdCheck(String studentId);
}
