package com.example.sns.fixture;

import com.example.sns.entity.User;

import static com.example.sns.entity.Role.USER;

public class UserInfoFixture {

    public static User get(String userName, String password) {
        User user =  User.builder()
                .id(1)
                .userName(userName)
                .password(password)
                .role(USER)
                .build();
        return user;
    }


}
