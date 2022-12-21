package com.example.sns.dto;

import com.example.sns.entity.Role;
import com.example.sns.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.example.sns.entity.Role.USER;


@Getter
@NoArgsConstructor
public class UserJoinRequestDto {

    private String userName;
    private String password;
    private Role role;

    @Builder
    public UserJoinRequestDto(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public User toEntity(String password) {
        return User.builder()
                .userName(this.userName)
                .password(this.password)
                .role(USER)
                .build();
    }
}
