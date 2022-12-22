package com.example.sns.dto;

import com.example.sns.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserJoinResponseDto {

    private Integer userId;
    private String userName;

    @Builder
    public UserJoinResponseDto(Integer userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public static UserJoinResponseDto from(User user) {
        return UserJoinResponseDto.builder()
                .userId(user.getId())
                .userName(user.getUserName())
                .build();
    }
}
