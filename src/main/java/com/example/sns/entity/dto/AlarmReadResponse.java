package com.example.sns.entity.dto;

import com.example.sns.entity.Alarm;
import com.example.sns.entity.AlarmType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AlarmReadResponse {

    private Integer id;
    private AlarmType alarmType;
    private Integer userId;
    private Integer targetId;
    private String text;
    private LocalDateTime createdAt;

    @Builder
    public AlarmReadResponse(Integer id, AlarmType alarmType, Integer userId, Integer targetId, String text, LocalDateTime createdAt) {
        this.id = id;
        this.alarmType = alarmType;
        this.userId = userId;
        this.targetId = targetId;
        this.text = text;
        this.createdAt = createdAt;
    }

    public static AlarmReadResponse from(Alarm alarm) {
        return AlarmReadResponse.builder()
                .id(alarm.getId())
                .alarmType(alarm.getAlarmType())
                .userId(alarm.getFromUserId())
                .targetId(alarm.getTargetId())
                .text(alarm.getAlarmType().getMessage())
                .build();
    }
}
