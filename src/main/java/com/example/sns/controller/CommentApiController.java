package com.example.sns.controller;

import com.example.sns.entity.dto.CommentCreateRequestDto;
import com.example.sns.entity.dto.CommentCreateResponseDto;
import com.example.sns.entity.dto.CommentReadResponseDto;
import com.example.sns.service.CommentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class CommentApiController {

    private final CommentService commentService;

    @ApiOperation(value = "댓글쓰기")
    @ApiResponses({
            @ApiResponse(code = 200, message = "id, comment, userName, postId, createdAt 반환"),
            @ApiResponse(code = 404, message = "POST_NOT_FOUND or USERNAME_NOT_FOUND 반환")
    })
    @PostMapping("/{postId}/comments")
    public RsData<CommentCreateResponseDto> createComment(@PathVariable Integer postId,
                                                          @RequestBody CommentCreateRequestDto commentCreateRequestDto,
                                                          Authentication authentication) {
        CommentCreateResponseDto response = commentService.createComment(commentCreateRequestDto,
                postId, authentication.getName());

        return RsData.success(response);
    }

    @ApiOperation(value = "댓글조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "body, pageable 반환"),
            @ApiResponse(code = 404, message = "POST_NOT_FOUND or USERNAME_NOT_FOUND 반환")
    })
    @GetMapping("{postId}/comments")
    public RsData<Page<CommentReadResponseDto>> findAllComment(@PathVariable Integer postId,
                                                               @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CommentReadResponseDto> pages = commentService.findAllByPage(postId, pageable);
        return RsData.success(pages);
    }
}
