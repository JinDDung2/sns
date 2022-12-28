package com.example.sns.entity.dto;

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
    private String body;

    @Builder
    public PostUpdateRequestDto(String title, String body) {
        this.title = title;
        this.body = body;
    }

}
