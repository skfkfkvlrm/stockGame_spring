package com.skfkfkvlrm.stockgame_spring.controller.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentJoinRequest {
    private String studentId;
    private String password;
    private String name;
    private int grade;
    private String className;
    private int classNumber;
}
