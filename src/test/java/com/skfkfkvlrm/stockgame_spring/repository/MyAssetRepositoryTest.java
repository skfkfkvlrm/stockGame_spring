package com.skfkfkvlrm.stockgame_spring.repository;

import com.skfkfkvlrm.stockgame_spring.controller.dto.request.StudentJoinRequest;
import com.skfkfkvlrm.stockgame_spring.domain.Student;
import org.hibernate.validator.internal.constraintvalidators.bv.AssertTrueValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.awaitility.Awaitility.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MyAssetRepositoryTest {
    @Autowired
    private MyAssetRepository myAssetRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        StudentJoinRequest request = StudentJoinRequest.builder()
                .studentId("testUser")
                .password("password123")
                .name("테스터")
                .grade(1)
                .className("A")
                .classNumber(1)
                .build();
        memberRepository.setMember(request);
    }

    @Test
    void testSelectUserPoint_보유포인트_조회() {
        //Given
        //When
        Integer pointValue = myAssetRepository.getPointValue("testUser");
        //Then
        assertNotNull(pointValue);
        assertEquals(30000, pointValue);
    }

    @Test
    void testSelectUserStock_보유주식_조회() {
    }

    @Test
    void testUpdateUserPoint_포인트_차감_및_증가() {
    }

    @Test
    void testUpdateUserStock_주식_차감_및_증가() {
    }
}
