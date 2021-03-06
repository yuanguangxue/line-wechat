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

package com.linecorp.bot.spring.boot;

import java.nio.charset.StandardCharsets;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.servlet.LineBotCallbackRequestParser;
import com.linecorp.bot.spring.boot.interceptor.LineBotServerInterceptor;
import com.linecorp.bot.spring.boot.support.LineBotServerArgumentProcessor;
@Slf4j
@Component
@ConditionalOnWebApplication
public class LineBotWebMvcBeans {
    @Autowired
    private LineBotProperties lineBotProperties;

    @Bean
    public LineBotServerArgumentProcessor lineBotServerArgumentProcessor() {
        return new LineBotServerArgumentProcessor();
    }

    @Bean
    public LineBotServerInterceptor lineBotServerInterceptor() {
        return new LineBotServerInterceptor();
    }

    @Bean
    public LineSignatureValidator lineSignatureValidator() {
        log.info("channelSecret : {}",lineBotProperties.getChannelSecret());
        /*return new LineSignatureValidator(
                lineBotProperties.getChannelSecret().getBytes(StandardCharsets.UTF_8));*/
        return new LineSignatureValidator(
                lineBotProperties.getChannelSecret().getBytes(StandardCharsets.US_ASCII));
    }

    @Bean
    public LineBotCallbackRequestParser lineBotCallbackRequestParser(
            LineSignatureValidator lineSignatureValidator) {
        return new LineBotCallbackRequestParser(lineSignatureValidator);
    }
}
