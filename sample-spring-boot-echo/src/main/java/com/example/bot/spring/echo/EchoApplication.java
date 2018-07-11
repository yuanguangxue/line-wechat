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

import com.google.common.annotations.VisibleForTesting;
import com.linecorp.bot.spring.boot.support.PushByReturnValueConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;



@Slf4j
@SpringBootApplication
@LineMessageHandler
@Import({PushByReturnValueConsumer.PushFactory.class})
@ConditionalOnProperty(name = "line.bot.handler.enabled", havingValue = "true", matchIfMissing = true)
public class EchoApplication {

    public static void main(String[] args) {
        SpringApplication.run(EchoApplication.class, args);
    }

    private final PushByReturnValueConsumer.PushFactory returnValueConsumerPushFactory;

    @Autowired
    public EchoApplication(PushByReturnValueConsumer.PushFactory returnValueConsumerPushFactory){
        this.returnValueConsumerPushFactory = returnValueConsumerPushFactory;
    }

    @VisibleForTesting
    void dispatch(Event event) {
        try {
            dispatchInternal(event);
        } catch (Error | Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void dispatchInternal(final Event event) {
        if(event instanceof MessageEvent){
            final String originalMessageText = ((MessageEvent<TextMessageContent>)event).getMessage().getText();
            returnValueConsumerPushFactory.createForEvent(event)
                    .accept(originalMessageText);
        }
    }

    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
        dispatch(event);
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        dispatch(event);
    }
}
