package com.example.sns.entity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CommentUpdateRequestDto {

    private String comment;

    @Builder
    public CommentUpdateRequestDto(String comment) {
        this.comment = comment;
    }

}
