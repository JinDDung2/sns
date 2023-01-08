package com.example.sns.repository;

import com.example.sns.entity.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Integer> {
    Page<Alarm> findAllByUserId(Integer userId, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Alarm a SET a.deletedDate = CURRENT_TIMESTAMP where a.id = :alarmId")
    void deleteById(@Param("alarmId") Integer alarmId);
}
