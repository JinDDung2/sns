package com.example.sns.entity.dto;

import com.example.sns.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentUpdateResponseDto {

    private Integer id;
    private String comment;
    private String userName;
    private Integer postId;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    @Builder
    public CommentUpdateResponseDto(Integer id, String comment, String userName, Integer postId, LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        this.id = id;
        this.comment = comment;
        this.userName = userName;
        this.postId = postId;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
    }

    public static CommentUpdateResponseDto from(Comment comment) {
        return CommentUpdateResponseDto.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .userName(comment.getCommentUser().getUserName())
                .postId(comment.getPost().getId())
                .createdAt(comment.getCreatedDate())
                .lastModifiedAt(comment.getModifiedDate())
                .build();
    }
}
