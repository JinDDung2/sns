package com.example.sns.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class PostUpdateRequestDto {
    @NotNull
    private String title;
    @NotNull
    private String content;

    @Builder
    public PostUpdateRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
