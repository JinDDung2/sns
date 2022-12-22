package com.example.sns.service;

import com.example.sns.dto.PostCreateRequestDto;
import com.example.sns.dto.PostCreateResponseDto;
import com.example.sns.entity.Post;
import com.example.sns.entity.Role;
import com.example.sns.entity.User;
import com.example.sns.exception.SpringBootAppException;
import com.example.sns.repository.PostRepository;
import com.example.sns.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.example.sns.exception.ErrorCode.USERNAME_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    PostRepository postRepository = Mockito.mock(PostRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);

    PostService postService = new PostService(postRepository, userRepository);

    @Test
    void 등록_성공() throws Exception {

        User givenUser = User.builder()
                .userName("test1")
                .role(Role.USER)
                .build();

        Mockito.when(userRepository.findByUserName("test1"))
                .thenReturn(Optional.of(givenUser));

        Post givenPost = Post.builder()
                .id(1)
                .title("title1")
                .content("content1")
                .writer(givenUser.getUserName())
                .build();

        PostCreateResponseDto responseDto =
                new PostCreateResponseDto(1, "포스트 등록 완료");

        Mockito.when(postRepository.save(givenPost)).then(returnsFirstArg());

        assertEquals(responseDto.getPostId(), 1);
        assertEquals(responseDto.getMessage(), "포스트 등록 완료");
    }

    @Test
    void 등록_실패_유저_없음() throws Exception {

        User givenUser = User.builder()
                .userName("test1")
                .role(Role.USER)
                .build();

        Mockito.when(userRepository.findByUserName("test1"))
                .thenThrow(new SpringBootAppException(USERNAME_NOT_FOUND, givenUser.getUserName() + "을 찾을 수 없습니다."));

        Post givenPost = Post.builder()
                .id(1)
                .title("title1")
                .content("content1")
                .writer(givenUser.getUserName())
                .build();

        Mockito.when(postRepository.save(any(Post.class)))
                .thenReturn(givenPost);

        try {
            PostCreateResponseDto responseDto =
                    postService.createPost(new PostCreateRequestDto("title1", "content1"), givenUser.getUserName());
        } catch (SpringBootAppException e) {
            assertEquals(USERNAME_NOT_FOUND, e.getErrorCode());
            assertEquals("test1을 찾을 수 없습니다.", e.getMessage());
        }

        verify(postRepository, never()).save(any());
    }

}