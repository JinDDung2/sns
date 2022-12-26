package com.example.sns.dto;

import com.example.sns.entity.Post;
import com.example.sns.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class PostCreateRequestDto {

    @NotNull
    private String title;
    @NotNull
    private String content;

    @Builder
    public PostCreateRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Post toEntity(User user) {
        return Post.builder()
                .title(this.title)
                .content(this.content)
                .user(user)
                .build();
    }
}
