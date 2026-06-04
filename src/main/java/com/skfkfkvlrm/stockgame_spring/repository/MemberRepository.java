package com.skfkfkvlrm.stockgame_spring.repository;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface MemberRepository {
    // 회원가입
    public boolean setMember(String studentId, String password, String name, int grade, String className, int classNumber);
    // 로그인
    public Map<String, Object> login(String studentId, String password);
    // 아이디 중복체크
    public boolean getIdCheck(String studentId);
}
