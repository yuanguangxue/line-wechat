package com.hp.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hp.model.HelloWorld;
import com.hp.model.Result;
import com.hp.model.UserInfo;
import com.hp.repository.UserRepository;
import com.linecorp.bot.cli.RichMenuCreateCommand;
import com.linecorp.bot.cli.arguments.PayloadArguments;
import com.linecorp.bot.cli.arguments.PayloadProvider;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.spring.boot.LineBotProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @Autowired
    private LineBotProperties lineBotProperties;

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

    @RequestMapping("/createRichMenu")
    public String createRichMenu(@RequestBody String data) throws JsonProcessingException {
        log.info("data is {} ",data);
        try {
            final LineMessagingClient client = LineMessagingClient
                    .builder(lineBotProperties.getChannelToken())
                    .build();
            PayloadArguments arguments = new PayloadArguments();
            arguments.setData(data);
            PayloadProvider payloadProvider = new PayloadProvider(arguments);
            RichMenuCreateCommand richMenuCreateCommand =
                    new RichMenuCreateCommand(client,payloadProvider);
            richMenuCreateCommand.execute();
        }catch (Exception e){
            log.error("error ",e);
        }
        return Result.success("message is send").toJson();
    }
}
