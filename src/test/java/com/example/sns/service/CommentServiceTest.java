package com.example.sns.service;

import com.example.sns.entity.Comment;
import com.example.sns.entity.Post;
import com.example.sns.entity.User;
import com.example.sns.entity.dto.CommentCreateRequestDto;
import com.example.sns.entity.dto.CommentUpdateRequestDto;
import com.example.sns.exception.SpringBootAppException;
import com.example.sns.fixture.CommentFixture;
import com.example.sns.fixture.PostInfoFixture;
import com.example.sns.fixture.UserInfoFixture;
import com.example.sns.repository.AlarmRepository;
import com.example.sns.repository.CommentRepository;
import com.example.sns.repository.PostRepository;
import com.example.sns.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static com.example.sns.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CommentServiceTest {

    CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    PostRepository postRepository = Mockito.mock(PostRepository.class);
    AlarmRepository alarmRepository = Mockito.mock(AlarmRepository.class);

    CommentService commentService;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(commentRepository, postRepository, userRepository, alarmRepository);
    }

    User givenUser1 = UserInfoFixture.get("user", "password1");
    User givenUser2 = UserInfoFixture.get("user2", "password2");
    Post givenPost1 = PostInfoFixture.get(givenUser1.getUserName(), givenUser1.getPassword());
    Comment givenComment = CommentFixture.get(givenUser1.getUserName(), givenUser1.getPassword());

    @Test
    void 댓글_작성_성공() throws Exception {
        // given
        CommentCreateRequestDto requestDto = new CommentCreateRequestDto("testComment");
        when(userRepository.findByUserName(givenUser1.getUserName())).thenReturn(Optional.of(givenUser1));
        when(postRepository.findById(givenPost1.getId())).thenReturn(Optional.of(givenPost1));
        // when
        when(commentRepository.save(any())).thenReturn(givenComment);
        // then
        assertDoesNotThrow(() -> commentService.createComment(requestDto, givenPost1.getId(), givenUser1.getUserName()));
    }

    @Test
    void 댓글_작성_실패_포스트없음() throws Exception {
        // given
        CommentCreateRequestDto requestDto = new CommentCreateRequestDto("testComment");
        when(userRepository.findByUserName(givenUser1.getUserName())).thenReturn(Optional.of(givenUser1));
        when(postRepository.findById(givenPost1.getId())).thenReturn(Optional.empty());
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
        when(userRepository.findByUserName(givenUser1.getUserName())).thenReturn(Optional.empty());
        when(postRepository.findById(givenPost1.getId())).thenReturn(Optional.of(givenPost1));
        // when
        when(commentRepository.save(any())).thenReturn(givenComment);
        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            commentService.createComment(requestDto, givenPost1.getId(), givenUser1.getUserName());
        });
        // then
        assertEquals(USERNAME_NOT_FOUND, springBootAppException.getErrorCode());
    }

    @Test
    void 댓글_수정_성공() throws Exception {
        // given
        CommentUpdateRequestDto requestDto = new CommentUpdateRequestDto("testUpdate");
        when(userRepository.findByUserName(givenUser1.getUserName())).thenReturn(Optional.of(givenUser1));
        when(postRepository.findById(givenPost1.getId())).thenReturn(Optional.of(givenPost1));
        when(commentRepository.findById(givenComment.getId())).thenReturn(Optional.of(givenComment));
        // when
        // then
        assertDoesNotThrow(() -> commentService.update(requestDto, givenPost1.getId(),
                givenComment.getId(), givenUser1.getUserName()));
    }

    @Test
    void 댓글_수정_실패_유저없음() throws Exception {
        // given
        CommentUpdateRequestDto requestDto = new CommentUpdateRequestDto("testUpdate");
        when(userRepository.findByUserName(givenUser1.getUserName())).thenReturn(Optional.empty());
        when(postRepository.findById(givenPost1.getId())).thenReturn(Optional.of(givenPost1));
        when(commentRepository.findById(givenComment.getId())).thenReturn(Optional.of(givenComment));
        // when
        // then
        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            commentService.update(requestDto, givenPost1.getId(),
                    givenComment.getId(), givenUser1.getUserName());
        });

        assertEquals(USERNAME_NOT_FOUND, springBootAppException.getErrorCode());
    }

    @Test
    void 댓글_수정_실패_포스트없음() throws Exception {
        // given
        CommentUpdateRequestDto requestDto = new CommentUpdateRequestDto("testUpdate");
        when(userRepository.findByUserName(givenUser1.getUserName())).thenReturn(Optional.of(givenUser1));
        when(postRepository.findById(givenPost1.getId())).thenReturn(Optional.empty());
        when(commentRepository.findById(givenComment.getId())).thenReturn(Optional.of(givenComment));
        // when
        // then
        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            commentService.update(requestDto, givenPost1.getId(),
                    givenComment.getId(), givenUser1.getUserName());
        });

        assertEquals(POST_NOT_FOUND, springBootAppException.getErrorCode());
    }

    @Test
    void 댓글_수정_실패_댓글없음() throws Exception {
        // given
        CommentUpdateRequestDto requestDto = new CommentUpdateRequestDto("testUpdate");
        // 댓글쓴이
        when(userRepository.findByUserName(givenUser1.getUserName())).thenReturn(Optional.of(givenUser1));
        // 댓글수정자
        when(userRepository.findByUserName(givenUser2.getUserName())).thenReturn(Optional.of(givenUser2));
        when(postRepository.findById(givenPost1.getId())).thenReturn(Optional.of(givenPost1));
        when(commentRepository.findById(givenComment.getId())).thenReturn(Optional.empty());
        // when
        // then
        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            commentService.update(requestDto, givenPost1.getId(),
                    givenComment.getId(), givenUser2.getUserName());
        });

        assertEquals(COMMENT_NOT_FOUND, springBootAppException.getErrorCode());
    }

    @Test
    void 댓글_수정_실패_작성자_불일치() throws Exception {
        // given
        CommentUpdateRequestDto requestDto = new CommentUpdateRequestDto("testUpdate");
        // 댓글쓴이
        when(userRepository.findByUserName(givenUser1.getUserName())).thenReturn(Optional.of(givenUser1));
        // 댓글수정자
        when(userRepository.findByUserName(givenUser2.getUserName())).thenReturn(Optional.of(givenUser2));
        when(postRepository.findById(givenPost1.getId())).thenReturn(Optional.of(givenPost1));
        when(commentRepository.findById(givenComment.getId())).thenReturn(Optional.of(givenComment));
        // when
        // then
        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            commentService.update(requestDto, givenPost1.getId(),
                    givenComment.getId(), givenUser2.getUserName());
        });

        assertEquals(INVALID_PERMISSION, springBootAppException.getErrorCode());
    }
    
    @Test
    void 삭제_실패_포스트없음() throws Exception {
        // given
        when(postRepository.findById(givenPost1.getId())).thenReturn(Optional.empty());
        when(commentRepository.findById(givenComment.getId())).thenReturn(Optional.of(givenComment));
        // when
        // then
        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            commentService.deleteById(givenPost1.getId(), givenComment.getId(), givenUser1.getUserName());
        });

        assertEquals(POST_NOT_FOUND, springBootAppException.getErrorCode());
    }

    @Test
    void 삭제_실패_작성자_삭제자_불일치() throws Exception {
        // given
        when(postRepository.findById(givenPost1.getId())).thenReturn(Optional.of(givenPost1));
        when(commentRepository.findById(givenComment.getId())).thenReturn(Optional.of(givenComment));
        when(userRepository.findByUserName(givenUser2.getUserName())).thenReturn(Optional.of(givenUser2));
        // when
        // then
        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            commentService.deleteById(givenPost1.getId(), givenComment.getId(), givenUser2.getUserName());
        });

        assertEquals(INVALID_PERMISSION, springBootAppException.getErrorCode());
    }


}