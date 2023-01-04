package com.example.sns.entity.dto;

import com.example.sns.entity.Comment;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentDeleteResponseDto {

    private Integer id;
    private String message;

    @Builder
    public CommentDeleteResponseDto(Integer id, String message) {
        this.id = id;
        this.message = message;
    }

    public static CommentDeleteResponseDto from(Comment comment) {
        return CommentDeleteResponseDto.builder()
                .id(comment.getId())
                .message("댓글 삭제 완료")
                .build();
    }
}
