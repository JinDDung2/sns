package com.example.sns.repository;

import com.example.sns.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(Integer id);
    Optional<Post> deleteById(Integer id);


    Page<Post> findMyFeedByUserId(Integer userId, Pageable pageable);
}
