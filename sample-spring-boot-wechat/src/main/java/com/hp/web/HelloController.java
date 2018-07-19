package com.hp.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hp.model.HelloWorld;
import com.hp.model.Result;
import com.hp.model.UserInfo;
import com.hp.repository.UserRepository;
import com.linecorp.bot.cli.*;
import com.linecorp.bot.cli.arguments.Arguments;
import com.linecorp.bot.cli.arguments.PayloadArguments;
import com.linecorp.bot.cli.arguments.PayloadProvider;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.model.richmenu.RichMenuIdResponse;
import com.linecorp.bot.model.richmenu.RichMenuListResponse;
import com.linecorp.bot.model.richmenu.RichMenuResponse;
import com.linecorp.bot.spring.boot.LineBotProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Futures.getUnchecked;

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

    private LineMessagingClient client;

    @PostConstruct
    public void init(){
        client = LineMessagingClient
                .builder(lineBotProperties.getChannelToken())
                .build();
    }

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

    @RequestMapping("/deleteRichMenu")
    public String deleteRichMenu(@RequestBody Arguments arguments) throws JsonProcessingException {
        log.info("arguments is {} ",arguments);
        try {
            RichMenuDeleteCommand command
                = new RichMenuDeleteCommand(client,arguments);
            command.execute();
        }catch (Exception e){
            log.error("error ",e);
        }
        return Result.success("message is send").toJson();
    }

    @RequestMapping("/getRichMenuList")
    public List<RichMenuResponse> getRichMenuList(){
        final RichMenuListResponse richMenuListResponse = getUnchecked(client.getRichMenuList());
        log.info("Successfully finished.");

        final List<RichMenuResponse> richMenus = richMenuListResponse.getRichMenus();
        log.info("You have {} RichMenues", richMenus.size());
        richMenus.forEach(richMenuResponse -> {
            log.info("{}", richMenuResponse);
        });
        return richMenus;
    }

    @RequestMapping("/linkRichMenu")
    public String linkRichMenu(@RequestBody Arguments arguments) throws JsonProcessingException {
        log.info("arguments is {} ",arguments);
        try {
            RichMenuLinkRichMenuIdToUserCommand
                    command = new RichMenuLinkRichMenuIdToUserCommand(client,arguments);
            command.execute();
        }catch (Exception e){
            log.error("error ",e);
        }
        return Result.success("message is send").toJson();
    }

    @RequestMapping("/getRichMenuIdByUser")
    public RichMenuIdResponse getRichMenuIdByUser(@RequestBody Arguments arguments){
        try {
            final String userId = checkNotNull(arguments.getUserId(), "--user-id= is not set.");
            final RichMenuIdResponse richMenuIdResponse =
                    getUnchecked(client.getRichMenuIdOfUser(userId));
            log.info("Successfully finished.");
            log.info("response = {}", richMenuIdResponse);
            return richMenuIdResponse;
        }catch (Exception e){
            log.error("error ",e);
        }
        return null;
    }

    @RequestMapping("/unLinkRichMenu")
    public String unLinkRichMenu(@RequestBody Arguments arguments) throws JsonProcessingException {
        log.info("arguments is {} ",arguments);
        try {
            RichMenuUnlinkRichMenuIdFromUserCommand
                    command = new RichMenuUnlinkRichMenuIdFromUserCommand(client,arguments);
            command.execute();
        }catch (Exception e){
            log.error("error ",e);
        }
        return Result.success("message is send").toJson();
    }

    @RequestMapping(value="/uploadRichMenuImage", method = RequestMethod.POST)
    public @ResponseBody String uploadRichMenuImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("richMenuId") String richMenuIdParam)
            throws JsonProcessingException {
        String contentType = file.getContentType();
        log.info("Content-Type: {}", contentType);
        log.info("richMenuIdParam: {}", richMenuIdParam);
        try {
            final String richMenuId = checkNotNull(richMenuIdParam, "--rich-menu-id= is not set.");
            byte[] bytes = file.getBytes();
            final BotApiResponse botApiResponse =
                    getUnchecked(client.setRichMenuImage(richMenuId, contentType, bytes));
            log.info("Request Successfully finished. {}", botApiResponse);
        } catch (Exception e) {
            // TODO: handle exception
            log.error("error : ",e);
        }
        //返回json
        return Result.success("message is send").toJson();
    }
}
