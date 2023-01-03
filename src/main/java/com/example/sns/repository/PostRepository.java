package com.example.sns.repository;

import com.example.sns.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(Integer id);
    Optional<Post> deleteById(Integer id);


    List<Post> findMyFeedByUserId(Integer userId);
}
