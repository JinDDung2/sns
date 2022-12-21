package com.example.sns.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class ErrorResult {

    private ErrorCode errorCode;
    private String message;

}
