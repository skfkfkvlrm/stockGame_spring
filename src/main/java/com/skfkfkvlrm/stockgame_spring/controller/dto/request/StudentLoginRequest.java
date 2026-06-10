package com.skfkfkvlrm.stockgame_spring.controller.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentLoginRequest {
    private String studentId;
    private String password;
}
