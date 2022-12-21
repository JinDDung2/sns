package com.example.sns.controller;

import com.example.sns.dto.UserJoinRequestDto;
import com.example.sns.dto.UserJoinResponseDto;
import com.example.sns.dto.UserLoginRequestDto;
import com.example.sns.dto.UserLoginResponseDto;
import com.example.sns.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserApiController {

    private final UserService userService;

    @ApiOperation(value = "회원가입")
    @ApiResponses({
            @ApiResponse(code = 200, message = "{userId, userName}"),
            @ApiResponse(code = 409, message = "유저 이름이 이미 존재")
    })
    @PostMapping("/join")
    public RsData<UserJoinResponseDto> join(@RequestBody UserJoinRequestDto requestDto) {
        UserJoinResponseDto responseDto = userService.join(requestDto);
        return RsData.success(responseDto);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "{token}"),
            @ApiResponse(code = 404, message = "유저 이름을 찾을 수 없음"),
            @ApiResponse(code = 401, message = "패스워드를 잘못 입력")
    })
    @ApiOperation(value = "로그인")
    @PostMapping("login")
    public RsData<UserLoginResponseDto> login(@RequestBody UserLoginRequestDto requestDto) {
        UserLoginResponseDto responseDto = userService.login(requestDto);
        return RsData.success(responseDto);
    }

}
