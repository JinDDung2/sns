package com.example.sns.service;

import com.example.sns.dto.UserJoinRequestDto;
import com.example.sns.dto.UserJoinResponseDto;
import com.example.sns.dto.UserLoginRequestDto;
import com.example.sns.dto.UserLoginResponseDto;
import com.example.sns.entity.User;
import com.example.sns.exception.ErrorCode;
import com.example.sns.exception.SpringBootAppException;
import com.example.sns.jwt.JwtTokenUtils;
import com.example.sns.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.example.sns.entity.Role.ADMIN;
import static com.example.sns.entity.Role.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

class UserServiceTest {

    UserRepository userRepository = Mockito.mock(UserRepository.class);
    BCryptPasswordEncoder passwordEncoder = Mockito.mock(BCryptPasswordEncoder.class);
    JwtTokenUtils jwtTokenUtils = Mockito.mock(JwtTokenUtils.class);
    @Value("${jwt.token.secret}")
    private String secretKey;
    private long expiredTimeMs = 1000 * 60 * 60;
    UserService userService;

    @BeforeEach
    void setup() {
        userService = new UserService(userRepository, passwordEncoder, jwtTokenUtils);
    }

    User user = User.builder()
            .id(1)
            .userName("user")
            .password("user")
            .role(USER)
            .build();
    User admin = User.builder()
            .id(0)
            .userName("admin")
            .password("admin")
            .role(ADMIN)
            .build();

    UserLoginRequestDto loginRequest = new UserLoginRequestDto(user.getUserName(), user.getPassword());

    @Test
    void 회원가입_성공(){
        given(userRepository.existsByUserName(user.getUserName())).willReturn(false);
        given(passwordEncoder.encode(user.getPassword())).willReturn(user.getPassword());
        when(userRepository.save(any(User.class))).thenReturn(user);


        UserJoinRequestDto requestDto = new UserJoinRequestDto(user.getUserName(), user.getPassword());
        UserJoinResponseDto response = userService.join(requestDto);

        assertEquals(response.getUserName(), user.getUserName());
    }

    @Test
    void 회원가입_실패_중복아이디(){
        UserJoinRequestDto requestDto = new UserJoinRequestDto("user", "user");

        when(userRepository.existsByUserName(requestDto.getUserName())).thenReturn(true);

        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            userService.join(requestDto);
        });

        assertEquals(springBootAppException.getErrorCode(), ErrorCode.DUPLICATED_USER_NAME);
    }

    @Test
    void 로그인_성공() {
        given(jwtTokenUtils.createToken(user.getUserName(), secretKey, expiredTimeMs)).willReturn("jwt");
        given(userRepository.findByUserName(user.getUserName())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(any(String.class), any(String.class))).willReturn(true);

        UserLoginResponseDto responseDto = userService.login(loginRequest);

        assertEquals(responseDto.getJwt(), "jwt");
    }

    @Test
    void 로그인_실패_아이디_못찾음() {
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.empty());

        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            userService.login(loginRequest);
        });

        assertEquals(springBootAppException.getErrorCode(), ErrorCode.USERNAME_NOT_FOUND);
    }

    @Test
    void 로그인_실패_비밀번호_틀림() {
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(false);

        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            userService.login(loginRequest);
        });

        assertEquals(springBootAppException.getErrorCode(), ErrorCode.INVALID_PASSWORD);
    }

    @Test
    void 역할수정() {
        given(userRepository.findByUserName(admin.getUserName())).willReturn(Optional.of(admin));
        given(userRepository.findByUserName(user.getUserName())).willReturn(Optional.of(admin));

        User changeUser = admin.upgradeAdmin(user);

        assertEquals(changeUser.getRole(), ADMIN);
    }

}