package com.hp.util;


import com.hp.model.LineMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;

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
}
