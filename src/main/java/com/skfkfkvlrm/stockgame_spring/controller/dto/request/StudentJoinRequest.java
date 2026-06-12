package com.skfkfkvlrm.stockgame_spring.controller.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StudentJoinRequest {
    private String studentId;
    private String password;
    private String name;
    private int grade;
    private String className;
    private int classNumber;
}
