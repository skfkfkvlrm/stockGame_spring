package com.skfkfkvlrm.stockgame_spring.service.impl;

import com.skfkfkvlrm.stockgame_spring.controller.dto.request.StudentJoinRequest;
import com.skfkfkvlrm.stockgame_spring.controller.dto.request.StudentLoginRequest;
import com.skfkfkvlrm.stockgame_spring.controller.dto.response.StudentResponse;
import com.skfkfkvlrm.stockgame_spring.repository.MemberRepository;
import com.skfkfkvlrm.stockgame_spring.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public boolean join(StudentJoinRequest request) {
        int member = memberRepository.setMember(request);
        return member > 0;
    }

    @Override
    public StudentResponse login(StudentLoginRequest request) {
        Map<String, Object> savedData = memberRepository.login(request.getStudentId(), request.getPassword());

        if (savedData != null) {
            return StudentResponse.builder()
                    .studentId((String) savedData.get("student_id"))
                    .name((String) savedData.get("name"))
                    .grade((Integer) savedData.get("grade"))
                    .className((String) savedData.get("class_name"))
                    .totalPoint((Integer) savedData.get("total_point"))
                    .build();
        }
        return null;
    }

    @Override
    public boolean getIdCheck(String studentId) {
        return memberRepository.getIdCheck(studentId) > 0;
    }
}
