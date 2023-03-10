package com.example.sns.service;

import com.example.sns.entity.Alarm;
import com.example.sns.entity.Post;
import com.example.sns.entity.PostLike;
import com.example.sns.entity.User;
import com.example.sns.exception.SpringBootAppException;
import com.example.sns.repository.AlarmRepository;
import com.example.sns.repository.PostLikeRepository;
import com.example.sns.repository.PostRepository;
import com.example.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.sns.entity.AlarmType.NEW_LIKE_ON_POST;
import static com.example.sns.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;

    /**
     * 비지니스 로직 insert, find, delete
     */
    public void pushLike(Integer postId, String userName) {

        Post post = findPost(postId);
        User user = findUser(userName);

        postLikeRepository.findByPostAndUser(post, user).ifPresentOrElse( (like) -> {
            postLikeRepository.delete(like);
            postRepository.decreaseLikeCounts(postId);

            alarmRepository.findByUserAndTargetId(user, postId).ifPresent(alarm -> {
                alarmRepository.delete(alarm);
            });

        }, () -> {
            postLikeRepository.save(new PostLike(post, user));
            postRepository.increaseLikeCounts(postId);

            // 내 게시물에 내 좋아요는 알람 저장 x
            if (!post.getUser().getUserName().equals(userName)) {
                alarmRepository.save(new Alarm(user.getId(), postId, NEW_LIKE_ON_POST, post.getUser()));
            }
        });
    }

    public void insertLike(Integer postId, String userName) {

        Post post = findPost(postId);
        User user = findUser(userName);

        // 이미 좋아요가 있을 경우 에러
        postLikeRepository.findByPostAndUser(post, user).ifPresent(like -> {
            throw new SpringBootAppException(DUPLICATED_LIKE, "이미 좋아요를 눌렀습니다.");
        });

        postLikeRepository.save(new PostLike(post, user));
        postRepository.increaseLikeCounts(postId);

        // 내 게시물에 내 좋아요는 알람 저장 x
        if (!post.getUser().getUserName().equals(userName)) {
            alarmRepository.save(new Alarm(user.getId(), postId, NEW_LIKE_ON_POST, post.getUser()));
        }
    }

    @Transactional(readOnly = true)
    public Integer findPostLike(Integer postId) {
        findPost(postId);
        return postRepository.findByLikeCounts(postId);
    }

    public void deleteLike(Integer postId, String userName) {

        Post post = findPost(postId);
        User user = findUser(userName);

        PostLike postLike = postLikeRepository.findByPostAndUser(post, user).orElseThrow(() -> {
            throw new SpringBootAppException(LIKE_NOT_FOUND, "좋아요를 한 적이 없습니다.");
        });

        postLikeRepository.deleteById(postLike.getId());
        postRepository.decreaseLikeCounts(postId);
        alarmRepository.findByUserAndTargetId(user, postId).ifPresent(alarm -> {
            alarmRepository.deleteById(alarm.getId());
        });
    }

    /**
     * 중복메서드 정리
     */
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
