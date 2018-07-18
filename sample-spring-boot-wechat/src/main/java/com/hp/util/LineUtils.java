package com.hp.util;



import com.hp.model.LineMessage;
import com.hp.model.LineUserProfile;
import com.hp.model.PushMsg;
import com.hp.model.SenderLineMessage;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.AudioMessageContent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.spring.boot.LineBotProperties;
import lombok.extern.slf4j.Slf4j;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

@Slf4j
public class LineUtils {

    public static LineMessage messageEventToEntity(MessageEvent<? extends MessageContent> messageEvent,LineBotProperties lineBotProperties){
        LineMessage lineMessage = new LineMessage();
        MessageContent messageContent = messageEvent.getMessage();
        if(messageContent instanceof TextMessageContent){
            lineMessage.setMsgType("text");
            lineMessage.setText(((TextMessageContent)messageContent).getText());
        }else if(messageContent instanceof ImageMessageContent
                || messageContent instanceof AudioMessageContent){
            if(messageContent instanceof ImageMessageContent){
                lineMessage.setText("image");
            }else {
                lineMessage.setText("audio");
            }
            final LineMessagingClient client = LineMessagingClient
                    .builder(lineBotProperties.getChannelToken())
                    .build();
            try {
                final MessageContentResponse messageContentResponse = client.getMessageContent(messageContent.getId()).get();
                Path path = Files.createTempFile("foo", "bar");
                log.info("path is {} " , path.toString());
                Files.copy(messageContentResponse.getStream(),path);
            } catch (InterruptedException | ExecutionException | IOException e) {
                /*e.printStackTrace();*/
                log.error("error", e);
            }
            lineMessage.setText(messageContent.getId());
        }
        lineMessage.setTimestamp(messageEvent.getTimestamp().toEpochMilli());
        lineMessage.setUserId(messageEvent.getTo());
        return lineMessage;
    }

    public static LineUserProfile UserProfileResponseToEntity(UserProfileResponse userProfileResponse){
        LineUserProfile userProfile = new LineUserProfile();
        userProfile.setDisplayName(userProfileResponse.getDisplayName());
        userProfile.setUserId(userProfileResponse.getUserId());
        userProfile.setPictureUrl(userProfileResponse.getPictureUrl());
        return userProfile;
    }

    public static PushMessage pushMsgToLinePushMessage(PushMsg pushMsg){
        if(pushMsg.getTarget().equals("me")){
            return null;
        }
        return new PushMessage(pushMsg.getTarget(), new TextMessage(pushMsg.getExtra()));
    }

    public static SenderLineMessage senderLineMessageToEntity(PushMsg pushMsg){
        SenderLineMessage senderLineMessage = new SenderLineMessage();
        senderLineMessage.setMsgType("text");
        senderLineMessage.setStatus("1");
        senderLineMessage.setTimestamp(System.currentTimeMillis());
        senderLineMessage.setUserId(pushMsg.getTarget());
        senderLineMessage.setText(pushMsg.getExtra());
        return senderLineMessage;
    }
}
