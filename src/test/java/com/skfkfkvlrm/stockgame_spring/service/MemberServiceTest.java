package com.skfkfkvlrm.stockgame_spring.service;

import com.skfkfkvlrm.stockgame_spring.controller.dto.request.StudentJoinRequest;
import com.skfkfkvlrm.stockgame_spring.controller.dto.request.StudentLoginRequest;
import com.skfkfkvlrm.stockgame_spring.controller.dto.response.StudentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class MemberServiceTest {
    @Autowired
    private MemberService memberService;

    private StudentJoinRequest createTestUserRequest() {
        return StudentJoinRequest.builder()
                .studentId("testUser123")
                .password("password123")
                .name("테스터")
                .grade(1)
                .className("A")
                .classNumber(1)
                .build();
    }

    @Test
    void testJoin_회원가입_성공() {
        //Given
        StudentJoinRequest request = createTestUserRequest();
        //When
        boolean result = memberService.join(request);
        //Then
        assertTrue(result, "회원가입 성공");
    }

    @Test
    void testGetIdCheck_아이디중복_확인() {
        //Given
        memberService.join(createTestUserRequest());
        //When
        boolean isDuplicate = memberService.getIdCheck("testUser123");
        boolean notDuplicate = memberService.getIdCheck("testUser999");
        //Then
        assertTrue(isDuplicate, "가입된 아이디");
        assertFalse(notDuplicate, "가입되지 않은 아이디");
    }

    @Test
    void testLogin_로그인_성공() {
        //Given
        memberService.join(createTestUserRequest());
        StudentLoginRequest loginRequest = StudentLoginRequest.builder()
                .studentId("testUser123")
                .password("password123")
                .build();
        //When
        StudentResponse response = memberService.login(loginRequest);
        //Then
        assertNotNull(response, "로그인 성공");
        assertEquals("testUser123", response.getStudentId());
        assertEquals("테스터", response.getName());
        assertEquals(30000, response.getTotalPoint());
    }

    @Test
    void testLogin_로그인_실패_비밀번호오류() {
        //Given
        memberService.join(createTestUserRequest());
        StudentLoginRequest loginRequest = StudentLoginRequest.builder()
                .studentId("testUser123")
                .password("wrongPassword")
                .build();
        //When
        StudentResponse response = memberService.login(loginRequest);
        //Then
        assertNull(response, "로그인 실패");
    }
}
