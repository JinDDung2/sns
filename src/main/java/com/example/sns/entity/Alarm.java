package com.example.sns.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "alarms")
@Where(clause = "deleted_date IS NULL")
public class Alarm extends BaseTime{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Integer id;

    private Integer fromUserId;
    private Integer targetId;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Alarm(Integer fromUserId, Integer targetId, AlarmType alarmType, User user) {
        this.fromUserId = fromUserId;
        this.targetId = targetId;
        this.alarmType = alarmType;
        this.user = user;
    }
}
