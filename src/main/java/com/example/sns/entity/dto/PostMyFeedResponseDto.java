package com.example.sns.entity.dto;

import com.example.sns.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostMyFeedResponseDto {

    private Integer id;
    private String title;
    private String body;
    private String userName;
    private LocalDateTime createdAt;

    @Builder
    public PostMyFeedResponseDto(Integer id, String title, String body, String userName, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.userName = userName;
        this.createdAt = createdAt;
    }

    public static PostMyFeedResponseDto from(Post post) {
        return PostMyFeedResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .userName(post.getUser().getUserName())
                .createdAt(post.getCreatedDate())
                .build();
    }
}
