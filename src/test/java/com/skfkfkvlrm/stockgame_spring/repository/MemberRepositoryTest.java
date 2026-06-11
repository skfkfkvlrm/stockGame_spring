package com.skfkfkvlrm.stockgame_spring.repository;

import com.skfkfkvlrm.stockgame_spring.controller.dto.request.StudentJoinRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    private StudentJoinRequest createTestUserRequest() {
        return StudentJoinRequest.builder()
                .studentId("testUser")
                .password("password123")
                .name("테스터")
                .grade(1)
                .className("A")
                .classNumber(1)
                .build();
    }

    @Test
    void testSetMember_회원가입() {
        // Given
        StudentJoinRequest request = createTestUserRequest();

        // When
        int result = memberRepository.setMember(request);

        // Then
        assertEquals(1, result);
    }

    @Test
    void testLogin_로그인() {
        // Given
        memberRepository.setMember(createTestUserRequest()); // Ensure the user exists
        String studentId = "testUser";
        String password = "password123";

        // When
        java.util.Map<String, Object> result = memberRepository.login(studentId, password);

        // Then
        assertNotNull(result);
        assertEquals("테스터", result.get("name"));
    }

    @Test
    void testGetIdCheck_아이디중복체크() {
        // Given
        memberRepository.setMember(createTestUserRequest()); // Ensure the user exists
        String studentId = "testUser";

        // When
        int count = memberRepository.getIdCheck(studentId);

        // Then
        assertTrue(count > 0);
    }
}