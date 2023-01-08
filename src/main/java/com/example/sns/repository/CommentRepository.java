package com.example.sns.repository;

import com.example.sns.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findCommentsByPostId(Integer postId, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Comment c set c.deletedDate = current_timestamp where c.post.id = :postId")
    void deleteAllByPost(@Param("postId") Integer postId);

    @Modifying(clearAutomatically = true)
    @Query("update Comment c set c.deletedDate = current_timestamp where c.id = :commentId")
    void deleteById(@Param("commentId") Integer commentId);
}
