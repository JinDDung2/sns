package com.example.sns.repository;

import com.example.sns.entity.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Integer> {
    Page<Alarm> findAllByUserId(Integer userId, Pageable pageable);
}
