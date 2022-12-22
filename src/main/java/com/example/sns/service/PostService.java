package com.example.sns.service;

import com.example.sns.dto.PostCreateRequestDto;
import com.example.sns.dto.PostCreateResponseDto;
import com.example.sns.entity.Post;
import com.example.sns.jwt.JwtTokenUtils;
import com.example.sns.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final JwtTokenUtils jwtTokenUtils;

    @Value("${jwt.token.secret}")
    private String secretKey;

    public PostCreateResponseDto createPost(PostCreateRequestDto requestDto, String token) {

        Post post = requestDto.toEntity();
        postRepository.save(post);

        return PostCreateResponseDto.from(post);
    }
}
