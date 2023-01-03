package com.example.sns.entity.dto;

import com.example.sns.entity.Comment;
import com.example.sns.entity.Post;
import com.example.sns.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateRequestDto {
    private String comment;

    @Builder
    public CommentCreateRequestDto(String comment) {
        this.comment = comment;
    }

    public Comment toEntity(User user, Post post) {
        return Comment.builder()
                .comment(comment)
                .commentUser(user)
                .post(post)
                .build();
    }
}
