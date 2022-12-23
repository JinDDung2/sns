package com.example.sns.service;

import com.example.sns.dto.PostCreateRequestDto;
import com.example.sns.dto.PostCreateResponseDto;
import com.example.sns.dto.PostReadResponseDto;
import com.example.sns.dto.PostUpdateRequestDto;
import com.example.sns.entity.Post;
import com.example.sns.entity.Role;
import com.example.sns.entity.User;
import com.example.sns.exception.SpringBootAppException;
import com.example.sns.fixture.PostInfoFixture;
import com.example.sns.fixture.TestInfoFixture;
import com.example.sns.fixture.UserInfoFixture;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    PostRepository postRepository = Mockito.mock(PostRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    PostService postService;
    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository, userRepository);
    }

    @Test
    void 등록_성공() throws Exception {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        Post givenPost = mock(Post.class);
        User givenUser = mock(User.class);
        PostCreateRequestDto requestDto = new PostCreateRequestDto("title", "content");

        when(userRepository.findByUserName(fixture.getUserName()))
                .thenReturn(Optional.of(givenUser));

        when(postRepository.save(any())).thenReturn(givenPost);

        assertDoesNotThrow(() -> postService.createPost(requestDto, fixture.getUserName()));
    }

    @Test
    void 등록_실패_유저_없음() throws Exception {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        Post givenPost = mock(Post.class);
        PostCreateRequestDto requestDto = new PostCreateRequestDto("title", "content");

        when(userRepository.findByUserName(fixture.getUserName()))
                .thenReturn(Optional.empty());

        when(postRepository.save(any())).thenReturn(givenPost);

        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            postService.createPost(requestDto, fixture.getUserName());
        });

        assertEquals(springBootAppException.getErrorCode(), USERNAME_NOT_FOUND);
    }

    @Test
    void 포스트_단건조회() throws Exception {
        User givenUser = UserInfoFixture.get("user", "password");

        Post givenPost = Post.builder()
                .id(1)
                .title("testTitle")
                .content("testContent")
                .user(givenUser)
                .build();

        when(postRepository.findById(givenPost.getId()))
                .thenReturn(Optional.of(givenPost));

        PostReadResponseDto responseDto = postService.findById(1);

        assertEquals(responseDto.getId(), givenPost.getId());
        assertEquals(responseDto.getTitle(), givenPost.getTitle());
        assertEquals(responseDto.getContent(), givenPost.getContent());
        assertEquals(responseDto.getUserName(), givenPost.getUser().getUserName());
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
                .user(givenUser)
                .build();

        PostCreateResponseDto responseDto =
                new PostCreateResponseDto(1, "포스트 등록 완료");

        when(postRepository.save(givenPost)).then(returnsFirstArg());

        assertEquals(responseDto.getPostId(), 1);
        assertEquals(responseDto.getMessage(), "포스트 등록 완료");
    }

    @Test
    void 수정_실패_포스트_없음() throws Exception {
        User givenUser = UserInfoFixture.get("user", "password");

        given(postRepository.findById(any(Integer.class)))
                .willReturn(Optional.empty());

        Post givenPost = Post.builder()
                .id(1)
                .title("t")
                .content("c")
                .user(givenUser)
                .build();

        when(postRepository.findById(givenPost.getId())).thenReturn(Optional.of(mock(Post.class)));
        when(userRepository.findByUserName(givenPost.getUser().getUserName())).thenReturn(Optional.empty());


        PostUpdateRequestDto requestDto = new PostUpdateRequestDto("updateTitle", "updateContent");

        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            postService.update(requestDto, 100, givenPost.getUser().getUserName());
        });

        assertEquals(springBootAppException.getErrorCode(), POST_NOT_FOUND);
    }

    @Test
    void 수정_실패_작성자_수정자_불일치() throws Exception {
        // 글쓴 유저
        User givenUser1 = UserInfoFixture.get("user", "password1");
        // 수정 유저
        User givenUser2 = UserInfoFixture.get("user2", "password2");
        Post givenPost = PostInfoFixture.get(givenUser1.getUserName(), givenUser1.getPassword());

        PostUpdateRequestDto requestDto = new PostUpdateRequestDto("updateTitle", "updateContent");

        when(userRepository.findByUserName(givenUser2.getUserName())).thenReturn(Optional.of(givenUser2));
        when(postRepository.findById(givenPost.getId())).thenReturn(Optional.of(givenPost));
        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            postService.update(requestDto, givenPost.getId(), givenUser2.getUserName());
        });

        assertEquals(springBootAppException.getErrorCode(), INVALID_PERMISSION);
    }

    @Test
    void 수정_실패_작성자_없음() throws Exception {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        PostUpdateRequestDto requestDto = new PostUpdateRequestDto("updateTitle", "updateContent");

        when(postRepository.findById(fixture.getPostId())).thenReturn(Optional.of(mock(Post.class)));
        when(userRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());


        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            postService.update(requestDto, fixture.getPostId(), fixture.getUserName());
        });

        assertEquals(springBootAppException.getErrorCode(), USERNAME_NOT_FOUND);
    }

    @Test
    void 삭제_실패_유저_없음() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        Post givenPost = PostInfoFixture.get("user", "password");

        when(userRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());
        when(postRepository.findById(fixture.getPostId())).thenReturn(Optional.of(givenPost));

        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            postService.deleteById(fixture.getPostId(), fixture.getUserName());
        });

        assertEquals(springBootAppException.getErrorCode(), USERNAME_NOT_FOUND);
    }

    @Test
    void 삭제_실패_포스트_없음() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        User givenUser = UserInfoFixture.get("user", "password");

        when(userRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.of(givenUser));
        when(postRepository.findById(fixture.getPostId())).thenReturn(Optional.empty());

        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            postService.deleteById(fixture.getPostId(), fixture.getUserName());
        });

        assertEquals(springBootAppException.getErrorCode(), POST_NOT_FOUND);
    }

    @Test
    void 삭제_실패_포스트_작성자_삭제자_다름() {
        User givenUser1 = UserInfoFixture.get("user", "password");
        User givenUser2 = UserInfoFixture.get("user2", "password");
        Post givenPost = PostInfoFixture.get(givenUser1.getUserName(), givenUser1.getPassword());

        when(userRepository.findByUserName(givenUser2.getUserName())).thenReturn(Optional.of(givenUser2));
        when(postRepository.findById(givenPost.getId())).thenReturn(Optional.of(givenPost));

        SpringBootAppException springBootAppException = assertThrows(SpringBootAppException.class, () -> {
            postService.deleteById(givenPost.getId(), givenUser2.getUserName());
        });

        assertEquals(springBootAppException.getErrorCode(), INVALID_PERMISSION);
    }

}