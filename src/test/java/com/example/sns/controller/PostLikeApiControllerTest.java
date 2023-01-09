package com.example.sns.controller;

import com.example.sns.exception.SpringBootAppException;
import com.example.sns.service.PostLikeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.sns.exception.ErrorCode.INVALID_TOKEN;
import static com.example.sns.exception.ErrorCode.POST_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostLikeApiController.class)
class PostLikeApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PostLikeService postLikeService;



    @Test
    @WithMockUser
    void 좋아요_성공() throws Exception {

        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result").value("좋아요를 눌렀습니다."))
                .andDo(print());
    }

    @Test
    @WithAnonymousUser
    void 좋아요_비로그인_실패() throws Exception {
        doThrow(new SpringBootAppException(INVALID_TOKEN, "잘못된 토큰입니다."))
                .when(postLikeService).insertLike(any(), any());

        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void 좋아요_포스트_없음_실패() throws Exception {
        doThrow(new SpringBootAppException(POST_NOT_FOUND, "해당 포스트가 없습니다."))
                .when(postLikeService).insertLike(any(), any());

        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

}