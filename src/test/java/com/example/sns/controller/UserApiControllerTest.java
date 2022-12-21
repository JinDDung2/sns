package com.example.sns.controller;

import com.example.sns.dto.UserJoinRequestDto;
import com.example.sns.dto.UserJoinResponseDto;
import com.example.sns.exception.SpringBootAppException;
import com.example.sns.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.sns.exception.ErrorCode.DUPLICATED_USER_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserApiController.class)
class UserApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @Test
    void 회원가입_성공() throws Exception {
        // given
        UserJoinRequestDto requestDto = new UserJoinRequestDto("testName", "testPwd");
        given(userService.join(any(UserJoinRequestDto.class)))
                .willReturn(new UserJoinResponseDto(100L, requestDto.getUserName()));
        // when
        // then
        mockMvc.perform(post("/api/v1/users/join")
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.userId").value(100L))
                .andExpect(jsonPath("$.result.userName").value("testName"))
                .andDo(print());
    }

    @Test
    void 회원가입_실패_중복이름() throws Exception {
        UserJoinRequestDto requestDto = new UserJoinRequestDto("testName", "testPwd");

        given(userService.join(any(UserJoinRequestDto.class)))
                .willThrow(new SpringBootAppException(DUPLICATED_USER_NAME, requestDto.getUserName() + " 아이디가 중복입니다."));

        mockMvc.perform(post("/api/v1/users/join")
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andDo(print());

    }

}