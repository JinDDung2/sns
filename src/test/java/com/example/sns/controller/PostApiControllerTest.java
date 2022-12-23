package com.example.sns.controller;

import com.example.sns.dto.*;
import com.example.sns.entity.Post;
import com.example.sns.exception.SpringBootAppException;
import com.example.sns.fixture.PostInfoFixture;
import com.example.sns.service.PostService;
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

import static com.example.sns.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
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

        mockMvc.perform(post("/api/v1/posts")
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

        mockMvc.perform(post("/api/v1/posts")
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

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void 포스트_리스트_성공() throws Exception {

        mockMvc.perform(get("/api/v1/posts")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(postService).findAllByPage(pageableCaptor.capture());
        PageRequest pageable = (PageRequest) pageableCaptor.getValue();

        assertEquals(0, pageable.getPageNumber());
        assertEquals(3, pageable.getPageSize());
        assertEquals(Sort.by("createdAt", "desc"), pageable.withSort(Sort.by("createdAt", "desc")).getSort());

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

    @Test
    @WithMockUser
    void 포스트_삭제_성공() throws Exception {
        Post givenPost = PostInfoFixture.get("user", "password");

        given(postService.deleteById(any(), any()))
                .willReturn(new PostDeleteResponseDto(givenPost.getId(), "포스트 삭제 완료"));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void 포스트_삭제_실패_인증_실패() throws Exception {
        given(postService.deleteById(any(), any()))
                .willThrow(new SpringBootAppException(USERNAME_NOT_FOUND, "UserName을 찾을 수 없습니다."));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void 포스트_삭제_실패_인증_작성자_불일치() throws Exception {

        given(postService.deleteById(any(), any()))
                .willThrow(new SpringBootAppException(INVALID_PERMISSION, "사용자가 권한이 없습니다."));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void 포스트_삭제_실패_데이터베이스_에러() throws Exception {

        given(postService.deleteById(any(), any()))
                .willThrow(new SpringBootAppException(DATABASE_ERROR, "DB에러입니다"));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }


}