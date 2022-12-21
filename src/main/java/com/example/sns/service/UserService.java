package com.example.sns.service;

import com.example.sns.dto.UserJoinRequestDto;
import com.example.sns.dto.UserJoinResponseDto;
import com.example.sns.entity.User;
import com.example.sns.exception.ErrorCode;
import com.example.sns.exception.SpringBootAppException;
import com.example.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserJoinResponseDto join(UserJoinRequestDto requestDto) {

        userRepository.findByUserName(requestDto.getUserName()).ifPresent(user -> {
            throw new SpringBootAppException(ErrorCode.DUPLICATED_USER_NAME, user.getUserName() + " 아이디가 중복입니다.");
        });

        String encodePassword = passwordEncoder.encode(requestDto.getPassword());
        User user = requestDto.toEntity(encodePassword);
        userRepository.save(user);

        return UserJoinResponseDto.from(user);
    }

}
