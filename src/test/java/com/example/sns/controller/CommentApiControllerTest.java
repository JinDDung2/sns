package com.example.sns.controller;

import com.example.sns.entity.dto.CommentCreateRequestDto;
import com.example.sns.entity.dto.CommentCreateResponseDto;
import com.example.sns.exception.SpringBootAppException;
import com.example.sns.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.sns.exception.ErrorCode.POST_NOT_FOUND;
import static com.example.sns.exception.ErrorCode.USERNAME_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentApiController.class)
class CommentApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CommentService commentService;

    @Test
    @WithMockUser
    void 댓글_작성_성공() throws Exception {
        CommentCreateRequestDto requestDto = new CommentCreateRequestDto("testComment");
        CommentCreateResponseDto responseDto = CommentCreateResponseDto.builder()
                .id(1)
                .comment("testComment")
                .userName("user1")
                .postId(10)
                .build();

        given(commentService.createComment(any(CommentCreateRequestDto.class), any(), any()))
                .willReturn(responseDto);

        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.comment").value("testComment"))
                .andExpect(jsonPath("$.result.userName").value("user1"))
                .andExpect(jsonPath("$.result.postId").value(10))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void 댓글_작성_실패_포스트_없음() throws Exception {
        CommentCreateRequestDto requestDto = new CommentCreateRequestDto("testComment");

        given(commentService.createComment(any(CommentCreateRequestDto.class), any(), any()))
                .willThrow(new SpringBootAppException(POST_NOT_FOUND, "해당 포스트가 없습니다."));

        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(POST_NOT_FOUND.getHttpStatus().value()))
                .andDo(print());

    }

    @Test
    @WithMockUser
    void 댓글_작성_실패_로그인_안됨() throws Exception {
        CommentCreateRequestDto requestDto = new CommentCreateRequestDto("testComment");

        given(commentService.createComment(any(CommentCreateRequestDto.class), any(), any()))
                .willThrow(new SpringBootAppException(USERNAME_NOT_FOUND, "UserName을 찾을 수 없습니다."));

        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(USERNAME_NOT_FOUND.getHttpStatus().value()))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void 댓글_리스트_조회_성공() throws Exception {
        mockMvc.perform(get("/api/v1/posts/1/comments")
                        .param("size", "10")
                        .param("sort", "id, DESC"))
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(commentService).findAllByPage(any(), pageableArgumentCaptor.capture());
        PageRequest pageRequest = (PageRequest) pageableArgumentCaptor.getValue();

        assertEquals(10, pageRequest.getPageSize());
        assertEquals(Sort.by("id", "DESC"), pageRequest.withSort(Sort.by("id", "DESC")).getSort());
    }
}