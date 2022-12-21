package com.example.sns.controller;

import com.example.sns.dto.UserJoinRequestDto;
import com.example.sns.dto.UserJoinResponseDto;
import com.example.sns.dto.UserLoginRequestDto;
import com.example.sns.dto.UserLoginResponseDto;
import com.example.sns.service.UserService;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation(value = "회원가입", notes="성공시 결과{userId, userName}, 유저 이름 중복 - 409에러")
    @PostMapping("/join")
    public RsData join(@RequestBody UserJoinRequestDto requestDto) {
        UserJoinResponseDto responseDto = userService.join(requestDto);
        return RsData.success(responseDto);
    }

    @ApiOperation(value = "로그인", notes="성공시 결과{token} 유저 아이디 못찾음 - 404에러, 유저 패스워드 다름 - 401에러")
    @PostMapping("login")
    public RsData login(@RequestBody UserLoginRequestDto requestDto) {
        UserLoginResponseDto responseDto = userService.login(requestDto);
        return RsData.success(responseDto);
    }

}
