package com.hp.model;

import org.springframework.stereotype.Component;

@Component
public class HelloWorld {
    public String hello() {
        return "Hello World!";
    }

    public String anotherHello() {
        return "Another Hello World!";
    }
}
