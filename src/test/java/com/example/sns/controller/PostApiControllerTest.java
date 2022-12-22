package com.example.sns.controller;

import com.example.sns.dto.PostCreateRequestDto;
import com.example.sns.dto.PostCreateResponseDto;
import com.example.sns.exception.ErrorCode;
import com.example.sns.exception.SpringBootAppException;
import com.example.sns.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostApiController.class)
class PostApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PostService postService;

    @Test
    @WithMockUser
    void 포스트_작성_성공() throws Exception {
        PostCreateRequestDto requestDto = new PostCreateRequestDto("testTitle", "testContent");

        given(postService.createPost(any(PostCreateRequestDto.class), any()))
                .willReturn(new PostCreateResponseDto(100, "포스트 작성 완료"));

        mockMvc.perform(post("/api/v1/posts/posts")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.postId").value(100))
                .andExpect(jsonPath("$.result.message").value("포스트 작성 완료"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void 포스트_작성_실패_토큰_없음() throws Exception {
        PostCreateRequestDto requestDto = new PostCreateRequestDto("testTitle", "testContent");

        given(postService.createPost(any(PostCreateRequestDto.class), any()))
                .willThrow(new SpringBootAppException(ErrorCode.INVALID_TOKEN, "잘못된 토큰입니다."));

        mockMvc.perform(post("/api/v1/posts/posts")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void 포스트_작성_실패_토큰_만료() throws Exception {
        PostCreateRequestDto requestDto = new PostCreateRequestDto("testTitle", "testContent");

        given(postService.createPost(any(PostCreateRequestDto.class), any()))
                .willThrow(new SpringBootAppException(ErrorCode.INVALID_TOKEN, "잘못된 토큰입니다."));

        mockMvc.perform(post("/api/v1/posts/posts")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

}