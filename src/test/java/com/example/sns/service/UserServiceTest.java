package com.example.sns.service;

import com.example.sns.dto.UserJoinRequestDto;
import com.example.sns.entity.User;
import com.example.sns.exception.SpringBootAppException;
import com.example.sns.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Test
    void 회원가입_성공(){
        // given
        UserJoinRequestDto requestDto = new UserJoinRequestDto("testName", "testPwd");
        // when
        userService.join(requestDto);
        User savedUser = userRepository.findByUserName("testName").get();
        // then
        assertEquals(requestDto.getUserName(), savedUser.getUserName());
        assertEquals(requestDto.getPassword(), savedUser.getPassword());
    }

    @Test
    void 회원가입_실패_중복아이디(){
        // given
        User existedUser = User.builder()
                .userName("testName")
                .password("testPwd")
                .build();

        userRepository.save(existedUser);

        UserJoinRequestDto requestDto = new UserJoinRequestDto("testName", "testPwd");
        // when
        // then
        assertThrows(SpringBootAppException.class, () -> {
            userService.join(requestDto);
        });

    }

}