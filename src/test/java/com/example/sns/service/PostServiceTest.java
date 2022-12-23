package com.example.sns.service;

import com.example.sns.dto.PostCreateRequestDto;
import com.example.sns.dto.PostCreateResponseDto;
import com.example.sns.dto.PostReadResponseDto;
import com.example.sns.dto.PostUpdateRequestDto;
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

import static com.example.sns.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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

        when(userRepository.findByUserName("test1"))
                .thenReturn(Optional.of(givenUser));

        Post givenPost = Post.builder()
                .id(1)
                .title("title1")
                .content("content1")
                .writer(givenUser.getUserName())
                .build();

        PostCreateResponseDto responseDto =
                new PostCreateResponseDto(1, "포스트 등록 완료");

        when(postRepository.save(givenPost)).then(returnsFirstArg());

        assertEquals(responseDto.getPostId(), 1);
        assertEquals(responseDto.getMessage(), "포스트 등록 완료");
    }

    @Test
    void 등록_실패_유저_없음() throws Exception {

        User givenUser = User.builder()
                .userName("test1")
                .role(Role.USER)
                .build();

        when(userRepository.findByUserName("test1"))
                .thenThrow(new SpringBootAppException(USERNAME_NOT_FOUND, givenUser.getUserName() + "을 찾을 수 없습니다."));

        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            postService.createPost(new PostCreateRequestDto("title1", "content1"), givenUser.getUserName());
        });

        assertEquals(springBootAppException.getErrorCode(), USERNAME_NOT_FOUND);

        verify(postRepository, never()).save(any());
    }

    @Test
    void 포스트_단건조회() throws Exception {

        Post givenPost = Post.builder()
                .id(1)
                .title("testTitle")
                .content("testContent")
                .writer("testUser")
                .build();

        when(postRepository.findById(1))
                .thenReturn(Optional.of(givenPost));

        PostReadResponseDto responseDto = postService.findById(1);

        assertEquals(responseDto.getId(), givenPost.getId());
        assertEquals(responseDto.getTitle(), givenPost.getTitle());
        assertEquals(responseDto.getContent(), givenPost.getContent());
        assertEquals(responseDto.getUserName(), givenPost.getWriter());
    }

    @Test
    void 수정_성공() throws Exception {

        User givenUser = User.builder()
                .userName("test1")
                .role(Role.USER)
                .build();

        when(userRepository.findByUserName("test1"))
                .thenReturn(Optional.of(givenUser));

        Post givenPost = Post.builder()
                .id(1)
                .title("title1")
                .content("content1")
                .writer(givenUser.getUserName())
                .build();

        PostCreateResponseDto responseDto =
                new PostCreateResponseDto(1, "포스트 등록 완료");

        when(postRepository.save(givenPost)).then(returnsFirstArg());

        assertEquals(responseDto.getPostId(), 1);
        assertEquals(responseDto.getMessage(), "포스트 등록 완료");
    }

    @Test
    void 수정_실패_포스트_없음() throws Exception {

        given(postRepository.findById(any(Integer.class)))
                .willReturn(Optional.empty());

        Post post = Post.builder()
                .id(1)
                .title("t")
                .content("c")
                .writer("testUser")
                .build();

        PostUpdateRequestDto requestDto = new PostUpdateRequestDto("updateTitle", "updateContent");

        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            postService.update(requestDto, 0, post.getWriter());
        });

        assertEquals(springBootAppException.getErrorCode(), POST_NOT_FOUND);
    }

    @Test
    void 수정_실패_작성자_불일치() throws Exception {

        PostUpdateRequestDto requestDto = new PostUpdateRequestDto("updateTitle", "updateContent");

        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            postService.update(requestDto, 1, "");
        });

        assertEquals(springBootAppException.getErrorCode(), INVALID_PERMISSION);
    }

    @Test
    void 수정_실패_작성자_없음() throws Exception {

        PostUpdateRequestDto requestDto = new PostUpdateRequestDto("updateTitle", "updateContent");

        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            postService.update(requestDto, 1, null);
        });

        assertEquals(springBootAppException.getErrorCode(), USERNAME_NOT_FOUND);
    }



}