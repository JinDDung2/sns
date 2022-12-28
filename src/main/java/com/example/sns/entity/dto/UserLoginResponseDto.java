package com.example.sns.entity.dto;

import lombok.Getter;

@Getter
public class UserLoginResponseDto {

    String jwt;

    public UserLoginResponseDto(String jwt) {
        this.jwt = jwt;
    }
}
