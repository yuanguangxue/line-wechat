package com.hp.web;

import com.hp.model.HelloWorld;
import com.hp.model.UserInfo;
import com.hp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by yteng on 4/3/17.
 */
@Slf4j
@RestController
public class HelloController {

    private HelloWorld helloWorld;

    @Autowired
    private UserRepository userRepository;


    public HelloController(HelloWorld helloWorld) {
        this.helloWorld = helloWorld;
    }

    @GetMapping("")
    public String hello() {
        List<UserInfo> list = userRepository.findAll();
        log.info("lis {} ",list);
        return helloWorld.hello();
    }




    @GetMapping("/anotherHelloWorld")
    public String anotherHello() {
        return helloWorld.anotherHello();
    }
}
