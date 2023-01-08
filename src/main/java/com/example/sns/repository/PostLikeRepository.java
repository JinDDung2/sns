package com.example.sns.repository;

import com.example.sns.entity.Post;
import com.example.sns.entity.PostLike;
import com.example.sns.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {
    Optional<PostLike> findByPostAndUser(Post post, User user);

    @Modifying(clearAutomatically = true)
    @Query("update PostLike pl set pl.deletedDate = CURRENT_TIMESTAMP where pl.post.id = :postId")
    void deleteAllByPost(@Param("postId") Integer postId);

    @Modifying(clearAutomatically = true)
    @Query("update PostLike pl set pl.deletedDate = CURRENT_TIMESTAMP where pl.id = :likeId")
    void deleteById(@Param("likeId") Integer likeId);
}
