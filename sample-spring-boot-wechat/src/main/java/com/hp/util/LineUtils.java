package com.hp.util;


import com.hp.model.LineMessage;
import com.hp.model.LineUserProfile;
import com.hp.model.PushMsg;
import com.hp.model.SenderLineMessage;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.profile.UserProfileResponse;

public class LineUtils {

    public static LineMessage messageEventToEntity(MessageEvent<? extends MessageContent> messageEvent){
        LineMessage lineMessage = new LineMessage();
        MessageContent messageContent = messageEvent.getMessage();
        if(messageContent instanceof TextMessageContent){
            lineMessage.setMsgType("text");
            lineMessage.setText(((TextMessageContent)messageContent).getText());
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
        return senderLineMessage;
    }
}
