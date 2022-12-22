package com.example.sns.service;

import com.example.sns.dto.PostCreateRequestDto;
import com.example.sns.dto.PostCreateResponseDto;
import com.example.sns.dto.PostReadResponseDto;
import com.example.sns.entity.Post;
import com.example.sns.exception.SpringBootAppException;
import com.example.sns.repository.PostRepository;
import com.example.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.sns.exception.ErrorCode.POST_NOT_FOUND;
import static com.example.sns.exception.ErrorCode.USERNAME_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostCreateResponseDto createPost(PostCreateRequestDto requestDto, String userName) {

        userRepository.findByUserName(userName).orElseThrow(() -> {
            throw new SpringBootAppException(USERNAME_NOT_FOUND, "UserName을 찾을 수 없습니다.");
        });

        Post post = requestDto.toEntity(userName);
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
        Post post = postRepository.findById(postId).orElseThrow(() -> {
            throw new SpringBootAppException(POST_NOT_FOUND, postId +" 해당 포스트가 없습니다.");
        });

        return PostReadResponseDto.from(post);
    }
}
