package com.skfkfkvlrm.stockgame_spring.repository;

import com.skfkfkvlrm.stockgame_spring.controller.dto.request.StudentJoinRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@org.springframework.transaction.annotation.Transactional
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
        // Given
        // When
        Integer pointValue = myAssetRepository.getPointValue("testUser");
        // Then
        assertNotNull(pointValue);
        assertEquals(30000, pointValue);
    }

    @Test
    void testSelectUserStock_보유주식_조회() {
        // Given
        // When
        java.util.List<Integer> stockNos = myAssetRepository.getMyStockNos("testUser", com.skfkfkvlrm.stockgame_spring.domain.OrderStatus.체결);
        // Then
        assertNotNull(stockNos);
        assertTrue(stockNos.isEmpty());
    }

    @Test
    void testGetTotalCoupon_쿠폰조회() {
        // Given
        // When
        Integer totalCoupon = myAssetRepository.getTotalCoupon("testUser");
        // Then
        assertNotNull(totalCoupon);
    }
}
