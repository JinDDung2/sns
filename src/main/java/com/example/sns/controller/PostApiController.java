package com.example.sns.controller;

import com.example.sns.dto.PostCreateRequestDto;
import com.example.sns.dto.PostCreateResponseDto;
import com.example.sns.dto.PostReadResponseDto;
import com.example.sns.service.PostService;
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

    @ApiOperation(value = "최신 순으로 20개씩 표시 (Pageable 사용)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "content, pageable 반환")
    })

    @GetMapping("")
    public RsData<Page<PostReadResponseDto>> findAllPost(@PageableDefault(sort = "id", direction = Sort.Direction.DESC,size = 20) Pageable pageable) {
        Page<PostReadResponseDto> pages = postService.findAllByPage(pageable);
        return RsData.success(pages);
    }

    @ApiOperation(value = "포스트 단건 조회")
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "id, title, content, userName, createdDate(yyyy-mm-dd hh:mm:ss), modifiedDate(yyyy-mm-dd hh:mm:ss)")
    })

    @GetMapping("/{postId}")
    public RsData<PostReadResponseDto> findById(@PathVariable Integer postId) {
        PostReadResponseDto responseDto = postService.findById(postId);
        return RsData.success(responseDto);
    }

}
