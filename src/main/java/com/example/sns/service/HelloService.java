package com.example.sns.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public Integer sumOfDigit(int num) {
        int result = 0;
        int n = Integer.valueOf(num);

        while (n != 0) {
            result += n % 10;
            n /= 10;
        }
        return result;
    }

}
