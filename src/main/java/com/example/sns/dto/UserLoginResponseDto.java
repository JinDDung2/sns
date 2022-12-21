package com.example.sns.dto;

import lombok.Getter;

@Getter
public class UserLoginResponseDto {

    String jwt;

    public UserLoginResponseDto(String jwt) {
        this.jwt = jwt;
    }
}
