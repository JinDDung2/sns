package com.example.sns.dto;

import com.example.sns.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostCreateRequestDto {

    private String title;
    private String content;

    @Builder
    public PostCreateRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Post toEntity(String userName) {
        return Post.builder()
                .title(this.title)
                .content(this.content)
                .writer(userName)
                .build();
    }
}
