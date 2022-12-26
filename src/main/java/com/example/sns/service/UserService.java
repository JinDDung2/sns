package com.example.sns.service;

import com.example.sns.dto.*;
import com.example.sns.entity.User;
import com.example.sns.exception.SpringBootAppException;
import com.example.sns.jwt.JwtTokenUtils;
import com.example.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.sns.entity.Role.*;
import static com.example.sns.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;

    @Value("${jwt.token.secret}")
    private String secretKey;
    private long expiredTimeMs = 1000 * 60 * 60;

    @Transactional
    public UserJoinResponseDto join(UserJoinRequestDto requestDto) {

        userRepository.findByUserName(requestDto.getUserName()).ifPresent(user -> {
            throw new SpringBootAppException(DUPLICATED_USER_NAME, user.getUserName() + " 아이디가 중복입니다.");
        });

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        User user = requestDto.toEntity(encodedPassword);
        userRepository.save(user);

        return UserJoinResponseDto.from(user);
    }

    @Transactional(readOnly = true)
    public UserLoginResponseDto login(UserLoginRequestDto requestDto) {

        User findUser = userRepository.findByUserName(requestDto.getUserName()).orElseThrow(() -> {
            throw new SpringBootAppException(USERNAME_NOT_FOUND, requestDto.getUserName() + " 아이디가 존재하지 않습니다.");
        });

        if (!passwordEncoder.matches(requestDto.getPassword(), findUser.getPassword())) {
            throw new SpringBootAppException(INVALID_PASSWORD, "비밀번호가 다릅니다.");
        }

        String token = jwtTokenUtils.createToken(requestDto.getUserName(), secretKey, expiredTimeMs);
        return new UserLoginResponseDto(token);
    }

    @Transactional
    public UserRoleResponseDto upgradeRole(Integer changeId, String userName) {
        User admin = userRepository.findByUserName(userName).orElseThrow(() -> {
            throw new SpringBootAppException(USERNAME_NOT_FOUND, "admin 유저가 존재하지 않습니다.");
        });

        User changeUser = userRepository.findById(changeId).orElseThrow(() -> {
            throw new SpringBootAppException(USERNAME_NOT_FOUND, "유저가 존재하지 않습니다.");
        });

        if (admin.getRole() == ADMIN || changeUser.getRole() == USER) {
            changeUser.upgradeAdmin(changeUser);
        }

        return UserRoleResponseDto.from(changeUser);
    }
}
