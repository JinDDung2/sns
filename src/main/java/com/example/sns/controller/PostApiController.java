package com.example.sns.controller;

import com.example.sns.dto.PostCreateRequestDto;
import com.example.sns.dto.PostCreateResponseDto;
import com.example.sns.service.PostService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostApiController {

    private final PostService postService;

    @ApiOperation(value = "글쓰기")
    @ApiResponses({
            @ApiResponse(code = 200, message = "postId, message 반환")
    })
    @PostMapping("/posts")
    public RsData<PostCreateResponseDto> createPost(@RequestBody PostCreateRequestDto requestDto, Authentication authentication) {
        PostCreateResponseDto responseDto = postService.createPost(requestDto, authentication.getName());
        return RsData.success(responseDto);
    }

}
