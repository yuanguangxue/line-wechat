package com.hp;



import com.google.common.annotations.VisibleForTesting;
import com.hp.model.LineMessage;
import com.hp.model.LineUserProfile;
import com.hp.service.LineMessageService;
import com.hp.service.LineUserProfileService;
import com.hp.service.PushRequestService;
import com.hp.util.LineUtils;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.spring.boot.LineBotProperties;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import com.linecorp.bot.spring.boot.support.PushByReturnValueConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ExecutionException;

@Slf4j
@SpringBootApplication
@LineMessageHandler
@Import({PushByReturnValueConsumer.PushFactory.class})
@ConditionalOnProperty(name = "line.bot.handler.enabled", havingValue = "true", matchIfMissing = true)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private final PushByReturnValueConsumer.PushFactory returnValueConsumerPushFactory;

    @Autowired
    private LineMessageService lineMessageService;

    @Autowired
    private PushRequestService pushRequestService;

    @Autowired
    private LineUserProfileService lineUserProfileService;

    private final LineBotProperties lineBotProperties;

    @Autowired
    public Application(
            PushByReturnValueConsumer.PushFactory returnValueConsumerPushFactory,
            LineBotProperties lineBotProperties){
        this.returnValueConsumerPushFactory = returnValueConsumerPushFactory;
        this.lineBotProperties = lineBotProperties;
    }

    @VisibleForTesting
    void dispatch(Event event) {
        try {
            dispatchInternal(event);
        } catch (Error | Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    private void dispatchInternal(final Event event) throws Exception {
        if(event instanceof MessageEvent){
            MessageEvent messageEvent = (MessageEvent)event;
            LineUserProfile lineUserProfile = null;
            try {
                lineUserProfile =  lineUserProfileService.getLineUserProfile(messageEvent.getTo());
            }catch (Exception e){
            }
            if(lineUserProfile == null){
                doSaveUserProfile(messageEvent.getTo());
            }
            LineMessage lineMessage = LineUtils.messageEventToEntity(messageEvent);
            lineMessageService.save(lineMessage);
            pushRequestService.pushLineMsg();
        }
    }

    private void sendTextMessage(Event event,String originalMessageText){
        returnValueConsumerPushFactory.createForEvent(event)
                .accept(new TextMessage(originalMessageText));
    }

    private LineUserProfile doSaveUserProfile(String userId) throws Exception {
        UserProfileResponse userProfileResponse = getUserProfile(userId);
        if(userProfileResponse!=null){
            LineUserProfile lineUserProfile = LineUtils.UserProfileResponseToEntity(userProfileResponse);
            lineUserProfileService.save(lineUserProfile);
            return lineUserProfile;
        }
        throw new Exception("获取用户信息出错！");
    }

    public UserProfileResponse getUserProfile(String userId){
        final LineMessagingClient client = LineMessagingClient
                .builder(lineBotProperties.getChannelToken())
                .build();
        try {
            return client.getProfile(userId).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
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
