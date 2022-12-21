package com.example.sns.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginRequestDto {

    private String userName;
    private String password;

    @Builder
    public UserLoginRequestDto(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}
