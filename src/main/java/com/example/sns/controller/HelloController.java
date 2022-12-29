package com.example.sns.controller;

import com.example.sns.service.HelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class HelloController {

    private final HelloService helloService;

    @GetMapping("/hello")
    public String hello() {
        return "허진혁";
    }

    @GetMapping("{num}")
    public String sumOfDigit(@PathVariable int num) {
        return helloService.sumOfDigit(num);
    }
}
