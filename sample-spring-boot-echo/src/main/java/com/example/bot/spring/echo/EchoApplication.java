/*
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.example.bot.spring.echo;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.spring.boot.LineBotProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import java.util.concurrent.ExecutionException;

@Slf4j
@SpringBootApplication
@LineMessageHandler
public class EchoApplication {



    public static void main(String[] args) {
        SpringApplication.run(EchoApplication.class, args);
    }

    @Autowired
    private LineBotProperties lineBotProperties;

    @EventMapping
    public Message handleTextMessageEvent(MessageEvent<TextMessageContent> event) {

        System.out.println("event: " + event);
        final String originalMessageText = event.getMessage().getText();

        //获取用户信息
        try {
            final LineMessagingClient client = LineMessagingClient
                    .builder(lineBotProperties.getChannelToken())
                    .build();
            final UserProfileResponse userProfileResponse;
            userProfileResponse = client.getProfile(event.getTo()).get();
            log.info("userProfileResponse : {}",userProfileResponse);
        } catch (InterruptedException | ExecutionException e) {
            //e.printStackTrace();
            log.error("error : ",e);
        }

        switch (originalMessageText.toUpperCase()) {
            case "FLEX":
                return new ExampleFlexMessageSupplier().get();
            default:
                return new TextMessage(originalMessageText);
        }
    }

    @EventMapping
    public Message handleDefaultMessageEvent(Event event) {
        System.out.println("event: " + event);
        return new TextMessage("你好！这是自动回复的消息，你无须理会");
    }
}
