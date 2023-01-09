package com.example.sns.service;

import com.example.sns.entity.dto.PostCreateRequestDto;
import com.example.sns.entity.dto.PostCreateResponseDto;
import com.example.sns.entity.dto.PostReadResponseDto;
import com.example.sns.entity.dto.PostUpdateRequestDto;
import com.example.sns.entity.Post;
import com.example.sns.entity.User;
import com.example.sns.exception.SpringBootAppException;
import com.example.sns.fixture.PostInfoFixture;
import com.example.sns.fixture.UserInfoFixture;
import com.example.sns.repository.CommentRepository;
import com.example.sns.repository.PostLikeRepository;
import com.example.sns.repository.PostRepository;
import com.example.sns.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.example.sns.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    PostRepository postRepository = Mockito.mock(PostRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    PostLikeRepository postLikeRepository = Mockito.mock(PostLikeRepository.class);
    CommentRepository commentRepository = Mockito.mock(CommentRepository.class);

    PostService postService;
    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository, userRepository, postLikeRepository, commentRepository);
    }

    // 글쓴 유저
    User givenUser1 = UserInfoFixture.get("user", "password1");
    // 수정 유저
    User givenUser2 = UserInfoFixture.get("user2", "password2");
    Post givenPost = PostInfoFixture.get(givenUser1.getUserName(), givenUser1.getPassword());

    @Test
    void 등록_성공() throws Exception {

        PostCreateRequestDto requestDto = new PostCreateRequestDto("title", "body");

        when(userRepository.findByUserName(givenUser1.getUserName()))
                .thenReturn(Optional.of(givenUser1));

        when(postRepository.save(any())).thenReturn(givenPost);

        assertDoesNotThrow(() -> postService.createPost(requestDto, givenUser1.getUserName()));
    }

    @Test
    void 등록_실패_유저_없음() throws Exception {
        PostCreateRequestDto requestDto = new PostCreateRequestDto("title", "body");

        when(userRepository.findByUserName(givenUser1.getUserName()))
                .thenReturn(Optional.empty());

        when(postRepository.save(any())).thenReturn(givenPost);

        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            postService.createPost(requestDto, givenUser1.getUserName());
        });

        assertEquals(springBootAppException.getErrorCode(), USERNAME_NOT_FOUND);
    }

    @Test
    void 포스트_단건조회() throws Exception {
        when(postRepository.findById(givenPost.getId()))
                .thenReturn(Optional.of(givenPost));

        PostReadResponseDto responseDto = postService.findById(1);

        assertEquals(responseDto.getId(), givenPost.getId());
        assertEquals(responseDto.getTitle(), givenPost.getTitle());
        assertEquals(responseDto.getBody(), givenPost.getBody());
        assertEquals(responseDto.getUserName(), givenPost.getUser().getUserName());
    }

    @Test
    void 수정_성공() throws Exception {

        when(userRepository.findByUserName(givenUser1.getUserName()))
                .thenReturn(Optional.of(givenUser1));


        when(postRepository.save(givenPost)).then(returnsFirstArg());

        PostCreateResponseDto responseDto =
                new PostCreateResponseDto(1, "포스트 등록 완료");

        assertEquals(responseDto.getPostId(), 1);
        assertEquals(responseDto.getMessage(), "포스트 등록 완료");
    }

    @Test
    void 수정_실패_포스트_없음() throws Exception {

        when(postRepository.findById(givenPost.getId())).thenReturn(Optional.empty());
        when(userRepository.findByUserName(givenPost.getUser().getUserName())).thenReturn(Optional.of(givenUser1));


        PostUpdateRequestDto requestDto = new PostUpdateRequestDto("updateTitle", "updateBody");

        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            postService.update(requestDto, 100, givenPost.getUser().getUserName());
        });

        assertEquals(springBootAppException.getErrorCode(), POST_NOT_FOUND);
    }

    @Test
    void 수정_실패_작성자_수정자_불일치() throws Exception {
        PostUpdateRequestDto requestDto = new PostUpdateRequestDto("updateTitle", "updatebody");

        // 수정자
        when(userRepository.findByUserName(givenUser2.getUserName())).thenReturn(Optional.of(givenUser2));
        // 글쓴이
        when(postRepository.findById(givenPost.getId())).thenReturn(Optional.of(givenPost));

        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            postService.update(requestDto, givenPost.getId(), givenUser2.getUserName());
        });

        assertEquals(springBootAppException.getErrorCode(), INVALID_PERMISSION);
    }

    @Test
    void 수정_실패_작성자_없음() throws Exception {
        PostUpdateRequestDto requestDto = new PostUpdateRequestDto("updateTitle", "updatebody");

        when(postRepository.findById(givenPost.getId())).thenReturn(Optional.of(givenPost));
        when(userRepository.findByUserName(givenUser1.getUserName())).thenReturn(Optional.empty());


        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            postService.update(requestDto, givenPost.getId(), givenUser1.getUserName());
        });

        assertEquals(springBootAppException.getErrorCode(), USERNAME_NOT_FOUND);
    }

    @Test
    void 삭제_실패_유저_없음() {
        Post givenPost = PostInfoFixture.get("user", "password");

        when(userRepository.findByUserName(givenUser1.getUserName())).thenReturn(Optional.empty());
        when(postRepository.findById(givenPost.getId())).thenReturn(Optional.of(givenPost));

        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            postService.deleteById(givenPost.getId(), givenUser1.getUserName());
        });

        assertEquals(springBootAppException.getErrorCode(), USERNAME_NOT_FOUND);
    }

    @Test
    void 삭제_실패_포스트_없음() {

        when(userRepository.findByUserName(givenUser1.getUserName())).thenReturn(Optional.of(givenUser1));
        when(postRepository.findById(givenPost.getId())).thenReturn(Optional.empty());

        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            postService.deleteById(givenPost.getId(), givenUser1.getUserName());
        });

        assertEquals(springBootAppException.getErrorCode(), POST_NOT_FOUND);
    }

    @Test
    void 삭제_실패_포스트_작성자_삭제자_다름() {

        when(userRepository.findByUserName(givenUser2.getUserName())).thenReturn(Optional.of(givenUser2));
        when(postRepository.findById(givenPost.getId())).thenReturn(Optional.of(givenPost));

        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            postService.deleteById(givenPost.getId(), givenUser2.getUserName());
        });

        assertEquals(springBootAppException.getErrorCode(), INVALID_PERMISSION);
    }

}