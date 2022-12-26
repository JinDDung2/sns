package com.example.sns.dto;

import com.example.sns.entity.Role;
import com.example.sns.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

import static com.example.sns.entity.Role.USER;


@Getter
@NoArgsConstructor
public class UserJoinRequestDto {

    @NotNull
    private String userName;
    @NotNull
    private String password;
    private Role role;

    @Builder
    public UserJoinRequestDto(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public User toEntity(String encodedPassword) {
        return User.builder()
                .userName(this.userName)
                .password(encodedPassword)
                .role(USER)
                .build();
    }
}
