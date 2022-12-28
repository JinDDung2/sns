package com.example.sns.entity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class UserLoginRequestDto {

    @NotNull
    private String userName;
    @NotNull
    private String password;

    @Builder
    public UserLoginRequestDto(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}
