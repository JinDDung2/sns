package com.example.sns.controller;

import com.example.sns.dto.*;
import com.example.sns.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/login")
    public RsData<UserLoginResponseDto> login(@RequestBody UserLoginRequestDto requestDto) {
        UserLoginResponseDto responseDto = userService.login(requestDto);
        return RsData.success(responseDto);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "role"),
            @ApiResponse(code = 404, message = "유저 이름을 찾을 수 없음"),
    })
    @ApiOperation(value = "역할 변경")
    @PostMapping("{userId}/role/change")
    public RsData<UserRoleResponseDto> changeRole(@PathVariable Integer userId, Authentication authentication) {
        UserRoleResponseDto responseDto = userService.upgradeRole(userId, authentication.getName());
        return RsData.success(responseDto);
    }

}
