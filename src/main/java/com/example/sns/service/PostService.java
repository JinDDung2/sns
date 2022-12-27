package com.example.sns.service;

import com.example.sns.dto.*;
import com.example.sns.entity.Post;
import com.example.sns.entity.User;
import com.example.sns.exception.SpringBootAppException;
import com.example.sns.repository.PostRepository;
import com.example.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.sns.entity.Role.ADMIN;
import static com.example.sns.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostCreateResponseDto createPost(PostCreateRequestDto requestDto, String userName) {
        User user = findUser(userName);
        Post post = requestDto.toEntity(user);

        postRepository.save(post);
        return PostCreateResponseDto.from(post);
    }

    @Transactional(readOnly = true)
    public Page<PostReadResponseDto> findAllByPage(Pageable pageable) {
        Page<Post> pages = postRepository.findAll(pageable);
        return pages.map(PostReadResponseDto::from);
    }

    @Transactional(readOnly = true)
    public PostReadResponseDto findById(Integer postId) {
        Post post = findPost(postId);
        return PostReadResponseDto.from(post);
    }

    public PostUpdateResponseDto update(PostUpdateRequestDto requestDto, Integer postId, String userName) {
        Post post = findPost(postId);
        findUser(userName);

        if (!post.getUser().getUserName().equals(userName) && post.getUser().getRole() != ADMIN) {
            throw new SpringBootAppException(INVALID_PERMISSION, "사용자가 권한이 없습니다.");
        }

        post.update(requestDto.getTitle(), requestDto.getBody());
        return PostUpdateResponseDto.from(post);
    }

    public PostDeleteResponseDto deleteById(Integer postId, String userName) {
        Post post = findPost(postId);
        findUser(userName);

        if (!post.getUser().getUserName().equals(userName) && post.getUser().getRole() != ADMIN) {
            throw new SpringBootAppException(INVALID_PERMISSION, "사용자가 권한이 없습니다.");
        }

        postRepository.deleteById(postId);
        return PostDeleteResponseDto.from(post);
    }

    private Post findPost(Integer postId) {
        return postRepository.findById(postId).orElseThrow(() -> {
            throw new SpringBootAppException(POST_NOT_FOUND, postId + " 해당 포스트가 없습니다.");
        });
    }

    private User findUser(String userName) {
        return userRepository.findByUserName(userName).orElseThrow(() -> {
            throw new SpringBootAppException(USERNAME_NOT_FOUND, "UserName을 찾을 수 없습니다.");
        });
    }
}
