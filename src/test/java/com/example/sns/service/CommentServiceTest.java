package com.example.sns.service;

import com.example.sns.entity.Comment;
import com.example.sns.entity.Post;
import com.example.sns.entity.User;
import com.example.sns.entity.dto.CommentCreateRequestDto;
import com.example.sns.exception.SpringBootAppException;
import com.example.sns.fixture.CommentFixture;
import com.example.sns.fixture.PostInfoFixture;
import com.example.sns.fixture.UserInfoFixture;
import com.example.sns.repository.CommentRepository;
import com.example.sns.repository.PostRepository;
import com.example.sns.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static com.example.sns.exception.ErrorCode.POST_NOT_FOUND;
import static com.example.sns.exception.ErrorCode.USERNAME_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CommentServiceTest {

    CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    PostRepository postRepository = Mockito.mock(PostRepository.class);
    CommentService commentService;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(commentRepository, postRepository, userRepository);
    }

    User givenUser1 = UserInfoFixture.get("user", "password1");
    Post givenPost1 = PostInfoFixture.get(givenUser1.getUserName(), givenUser1.getPassword());
    Comment givenComment = CommentFixture.get(givenUser1.getUserName(), givenUser1.getPassword());

    @Test
    void 댓글_작성_성공() throws Exception {
        // given
        CommentCreateRequestDto requestDto = new CommentCreateRequestDto("testComment");
        when(postRepository.findById(givenPost1.getId())).thenReturn(Optional.of(givenPost1));
        when(userRepository.findByUserName(givenUser1.getUserName())).thenReturn(Optional.of(givenUser1));
        // when
        when(commentRepository.save(any())).thenReturn(givenComment);
        // then
        assertDoesNotThrow(() -> commentService.createComment(requestDto, givenPost1.getId(), givenUser1.getUserName()));
    }

    @Test
    void 댓글_작성_실패_포스트없음() throws Exception {
        // given
        CommentCreateRequestDto requestDto = new CommentCreateRequestDto("testComment");
        when(postRepository.findById(givenPost1.getId())).thenReturn(Optional.empty());
        when(userRepository.findByUserName(givenUser1.getUserName())).thenReturn(Optional.of(givenUser1));
        // when
        when(commentRepository.save(any())).thenReturn(givenComment);
        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            commentService.createComment(requestDto, givenPost1.getId(), givenUser1.getUserName());
        });
        // then
        assertEquals(POST_NOT_FOUND, springBootAppException.getErrorCode());
    }

    @Test
    void 댓글_작성_실패_로그인_안됨() throws Exception {
        // given
        CommentCreateRequestDto requestDto = new CommentCreateRequestDto("testComment");
        when(postRepository.findById(givenPost1.getId())).thenReturn(Optional.of(givenPost1));
        when(userRepository.findByUserName(givenUser1.getUserName())).thenReturn(Optional.empty());
        // when
        when(commentRepository.save(any())).thenReturn(givenComment);
        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            commentService.createComment(requestDto, givenPost1.getId(), givenUser1.getUserName());
        });
        // then
        assertEquals(USERNAME_NOT_FOUND, springBootAppException.getErrorCode());
    }

}