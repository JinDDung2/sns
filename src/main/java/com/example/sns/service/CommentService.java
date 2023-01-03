package com.example.sns.service;

import com.example.sns.entity.Comment;
import com.example.sns.entity.Post;
import com.example.sns.entity.User;
import com.example.sns.entity.dto.CommentCreateRequestDto;
import com.example.sns.entity.dto.CommentCreateResponseDto;
import com.example.sns.exception.SpringBootAppException;
import com.example.sns.repository.CommentRepository;
import com.example.sns.repository.PostRepository;
import com.example.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.sns.exception.ErrorCode.POST_NOT_FOUND;
import static com.example.sns.exception.ErrorCode.USERNAME_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 비지니스 로직 CRUD
     */

    public CommentCreateResponseDto createComment(Integer postId, CommentCreateRequestDto commentCreateRequestDto,
                                                  String userName) {
        // 포스터 존재 유무
        Post post = findPost(postId);
        // 유저아이디 일치
        User user = findUser(userName);

        Comment comment = commentCreateRequestDto.toEntity(user, post);
        commentRepository.save(comment);
        return CommentCreateResponseDto.from(comment);
    }

    /**
     * 중복 메서드 추출
     */

    private Post findPost(Integer postId) {
        return postRepository.findById(postId).orElseThrow(() -> {
            throw new SpringBootAppException(POST_NOT_FOUND, "해당 포스트가 없습니다.");
        });
    }

    private User findUser(String userName) {
        return userRepository.findByUserName(userName).orElseThrow(() -> {
            throw new SpringBootAppException(USERNAME_NOT_FOUND, "UserName을 찾을 수 없습니다.");
        });
    }
}
