package com.example.sns.controller;

import com.example.sns.dto.*;
import com.example.sns.exception.SpringBootAppException;
import com.example.sns.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.sns.exception.ErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
                .willThrow(new SpringBootAppException(INVALID_TOKEN, "잘못된 토큰입니다."));

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
                .willThrow(new SpringBootAppException(INVALID_TOKEN, "잘못된 토큰입니다."));

        mockMvc.perform(post("/api/v1/posts/posts")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void 포스트_리스트() throws Exception {

        PostReadResponseDto post1 = PostReadResponseDto.builder()
                .id(1)
                .title("t1")
                .content("t2")
                .userName("w1")
                .createdDate(LocalDateTime.now())
                .build();

        PostReadResponseDto post2 = PostReadResponseDto.builder()
                .id(2)
                .title("t2")
                .content("t2")
                .userName("w2")
                .createdDate(LocalDateTime.now())
                .build();

        List<PostReadResponseDto> dtoList = new ArrayList<>();
        dtoList.add(post1);
        dtoList.add(post2);

        given(postService.findAllByPage(any(Pageable.class)))
                .willReturn(new PageImpl<>(dtoList));

        mockMvc.perform(get("/api/v1/posts/posts")
                        .param("pageNumber", "0")
                        .param("size", "20")
                        .param("sorted", "true"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void 포스트_단건조회_성공() throws Exception {

        PostReadResponseDto responseDto = PostReadResponseDto.builder()
                .id(1)
                .title("testTitle")
                .content("testContent")
                .userName("testUser")
                .build();

        given(postService.findById(1))
                .willReturn(responseDto);

        mockMvc.perform(get("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.title").value("testTitle"))
                .andExpect(jsonPath("$.result.content").value("testContent"))
                .andExpect(jsonPath("$.result.userName").value("testUser"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void 포스트_수정_성공() throws Exception {
        PostUpdateRequestDto requestDto = new PostUpdateRequestDto("updateTitle", "updateContent");

        given(postService.update(any(PostUpdateRequestDto.class), any(), any()))
                .willReturn(new PostUpdateResponseDto(100, "포스트 수정 완료"));

        mockMvc.perform(put("/api/v1/posts/100")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.postId").value(100))
                .andExpect(jsonPath("$.result.message").value("포스트 수정 완료"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void 포스트_수정_실패_인증_실패() throws Exception {
        PostUpdateRequestDto requestDto = new PostUpdateRequestDto("updateTitle", "updateContent");

        given(postService.update(any(PostUpdateRequestDto.class), any(), any()))
                .willThrow(new SpringBootAppException(INVALID_TOKEN, "잘못된 토큰입니다."));

        mockMvc.perform(put("/api/v1/posts/100")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void 포스트_수정_실패_작성자_불일치() throws Exception {
        PostUpdateRequestDto requestDto = new PostUpdateRequestDto("updateTitle", "updateContent");

        given(postService.update(any(PostUpdateRequestDto.class), any(), any()))
                .willThrow(new SpringBootAppException(INVALID_PERMISSION, "사용자가 권한이 없습니다."));

        mockMvc.perform(put("/api/v1/posts/100")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void 포스트_작성_실패_데이터베이스() throws Exception {
        PostUpdateRequestDto requestDto = new PostUpdateRequestDto("updateTitle", "updateContent");

        given(postService.update(any(PostUpdateRequestDto.class), any(), any()))
                .willThrow(new SpringBootAppException(DATABASE_ERROR, "DB에러입니다."));

        mockMvc.perform(put("/api/v1/posts/100")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

}