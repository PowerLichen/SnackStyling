package com.snackstyling.spring.login.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class AuthRequest {
    @ApiModelProperty(value = "이메일", example = "kmj123@naver.com", required = true)
    private String email;
    @ApiModelProperty(value = "비밀번호", example = "kmj1234!", required = true)
    private String pwd;
}
