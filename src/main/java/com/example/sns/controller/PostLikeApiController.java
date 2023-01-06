package com.example.sns.controller;

import com.example.sns.service.PostLikeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostLikeApiController {

    private final PostLikeService postLikeService;

    @ApiOperation(value = "좋아요 추가")
    @ApiResponses({
            @ApiResponse(code = 200, message = "좋아요를 눌렀습니다."),
            @ApiResponse(code = 404, message = "해당 포스트가 없습니다."),
            @ApiResponse(code = 404, message = "UserName을 찾을 수 없습니다."),
            @ApiResponse(code = 409, message = "이미 좋아요를 눌렀습니다.")
    })
    @PostMapping("/{postId}/likes")
    public RsData<String> insertLike(@PathVariable Integer postId, Authentication authentication) {
        postLikeService.insertLike(postId, authentication.getName());
        return RsData.success("좋아요를 눌렀습니다.");
    }

    @ApiOperation(value = "좋아요 개수 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "좋아요 개수"),
            @ApiResponse(code = 404, message = "해당 포스트가 없습니다."),
    })
    @GetMapping("/{postId}/likes")
    public RsData<Integer> findPostLike(@PathVariable Integer postId) {
        Integer postLike = postLikeService.findPostLike(postId);
        return RsData.success(postLike);
    }

    @ApiOperation(value = "좋아요 취소")
    @ApiResponses({
            @ApiResponse(code = 200, message = "좋아요를 취소했습니다."),
            @ApiResponse(code = 404, message = "해당 포스트가 없습니다."),
            @ApiResponse(code = 404, message = "UserName을 찾을 수 없습니다."),
            @ApiResponse(code = 404, message = "좋아요를 한 적이 없습니다.")
    })
    @DeleteMapping("/{postId}/likes")
    public RsData<String> deleteLike(@PathVariable Integer postId, Authentication authentication) {
        postLikeService.deleteLike(postId, authentication.getName());
        return RsData.success("좋아요를 삭제했습니다.");
    }

}
