package com.example.sns.dto;

import com.example.sns.entity.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostCreateResponseDto {

    private Integer postId;
    private String message;

    @Builder
    public PostCreateResponseDto(Integer postId, String message) {
        this.postId = postId;
        this.message = message;
    }

    public static PostCreateResponseDto from(Post post) {
        return PostCreateResponseDto.builder()
                .postId(post.getId())
                .message("포스트 등록 완료")
                .build();
    }
}
