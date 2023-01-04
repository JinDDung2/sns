package com.example.sns.controller;

import com.example.sns.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostLikeApiController {

    private final PostLikeService postLikeService;

    @PostMapping("/{postId}/likes")
    public RsData<String> insertLike(@PathVariable Integer postId, Authentication authentication) {
        postLikeService.insertLike(postId, authentication.getName());
        return RsData.success("좋아요를 눌렀습니다.");
    }

    @GetMapping("/{postId}/likes")
    public RsData<Integer> findPostLike(@PathVariable Integer postId) {
        Integer postLike = postLikeService.findPostLike(postId);
        return RsData.success(postLike);
    }

    @DeleteMapping("/{postId}/likes")
    public RsData<String> deleteLike(@PathVariable Integer postId, Authentication authentication) {
        postLikeService.deleteLike(postId, authentication.getName());
        return RsData.success("좋아요를 삭제했습니다.");
    }

}
