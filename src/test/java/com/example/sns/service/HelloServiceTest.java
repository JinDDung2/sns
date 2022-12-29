package com.example.sns.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class HelloServiceTest {

    HelloService helloService = Mockito.mock(HelloService.class);

    @Test
    void 각_자릿수_합() {

        when(helloService.sumOfDigit("123")).thenReturn("6");

        assertEquals(helloService.sumOfDigit("123"), "6");
    }

}