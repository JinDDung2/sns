package com.example.sns.entity.dto;

import com.example.sns.entity.Role;
import com.example.sns.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserRoleResponseDto {

    private Role role;

    @Builder
    public UserRoleResponseDto(Role role) {
        this.role = role;
    }

    public static UserRoleResponseDto from (User user) {
        return UserRoleResponseDto.builder()
                .role(user.getRole())
                .build();
    }
}
