package com.hp.web;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.hp.model.Result;
import com.hp.util.LineSignatureValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
public class PushMessageCallbackController {

    //private LineSignatureValidator lineSignatureValidator = null;
    private static final String CHANNEL_SECRET = "747855baf414406edc111e4929f84dd2";
    private static final String CHANNEL_TOKEN = "q0J6vjffZaciiUv0HcJhR6wCSxKM2JNy9YkGG3fZx+FAoeST1DaPX8nd89/dDA5XP/p5NDhVeOWm4GzGdyo8HarfxiDNJeBlX6s54U/jetSQ+Kw5ORtzfplVQTZVyfdTvm4s4Ob6SlQ9aJwfUPkyNAdB04t89/1O/w1cDnyilFU=";
    private LineSignatureValidator lineSignatureValidator;

    @PostConstruct
    public void init(){
        lineSignatureValidator = new LineSignatureValidator(
                CHANNEL_SECRET.getBytes(StandardCharsets.UTF_8),CHANNEL_TOKEN);
    }

    @RequestMapping("/v2/bot/message/push")
    public String callback(HttpServletRequest req, @RequestBody String message) throws JsonProcessingException {
        String authorization = req.getHeader("Authorization");
        try {
            boolean validate = lineSignatureValidator.validateAuthorization(authorization);
            if(validate){
                //do line
               /* ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(message);

                MessageEvent messageEvent = objectMapper.readValue(message,MessageEvent.class);*/
                //log.info("messageEvent : ",messageEvent);
                log.error("message is {}",message);
            }else {
                log.error("validate false");
            }
        }catch (Exception e){
            log.error("error : ",e);
        }
        return Result.success("ok").toJson();
    }
}
