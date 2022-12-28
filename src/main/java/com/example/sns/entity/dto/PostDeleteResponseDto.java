package com.example.sns.entity.dto;

import com.example.sns.entity.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostDeleteResponseDto {

    private Integer postId;
    private String message;

    @Builder
    public PostDeleteResponseDto(Integer postId, String message) {
        this.postId = postId;
        this.message = message;
    }

    public static PostDeleteResponseDto from(Post post) {
        return PostDeleteResponseDto.builder()
                .postId(post.getId())
                .message("포스트 삭제 완료")
                .build();
    }
}
