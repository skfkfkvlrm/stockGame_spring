package com.skfkfkvlrm.stockgame_spring.controller.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StudentLoginRequest {
    private String studentId;
    private String password;
}
