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
}
