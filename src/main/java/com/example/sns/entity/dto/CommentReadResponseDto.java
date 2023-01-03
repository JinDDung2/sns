package com.example.sns.entity.dto;

import com.example.sns.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentReadResponseDto {

    private Integer id;
    private String comment;
    private String userName;
    private Integer postId;
    private LocalDateTime createdAt;

    @Builder
    public CommentReadResponseDto(Integer id, String comment, String userName, Integer postId, LocalDateTime createdAt) {
        this.id = id;
        this.comment = comment;
        this.userName = userName;
        this.postId = postId;
        this.createdAt = createdAt;
    }

    public static CommentReadResponseDto from(Comment comment) {
        return CommentReadResponseDto.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .userName(comment.getCommentUser().getUserName())
                .postId(comment.getPost().getId())
                .createdAt(comment.getCreatedDate())
                .build();
    }
}
