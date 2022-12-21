package com.example.sns.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RsData<T> {

    private String resultCode;
    private T result;

    public RsData(String resultCode, T result) {
        this.resultCode = resultCode;
        this.result = result;
    }

    public static <T> RsData<T> error (T result) {
        return new RsData("ERROR", result);
    }

    public static <T> RsData<T> success (T result) {
        return new RsData("SUCCESS", result);
    }
}
