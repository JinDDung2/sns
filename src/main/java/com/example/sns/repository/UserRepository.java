package com.example.sns.repository;

import com.example.sns.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUserName(String userName);
    boolean existsByUserName(String userName);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.deletedDate = current_timestamp where u.id = :userId")
    void deleteById(@Param("userId") Integer userId);
}
