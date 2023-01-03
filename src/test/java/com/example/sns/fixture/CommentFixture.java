package com.example.sns.fixture;

import com.example.sns.entity.Comment;

public class CommentFixture {

    public static Comment get(String userName, String password) {
        return Comment.builder()
                .id(1)
                .comment("testComment")
                .post(PostInfoFixture.get(userName, password))
                .commentUser(UserInfoFixture.get(userName, password))
                .build();
    }
}
