package com.hp;



import com.google.common.annotations.VisibleForTesting;
import com.hp.model.LineMessage;
import com.hp.service.LineMessageService;
import com.hp.service.PushRequestService;
import com.hp.util.LineUtils;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import com.linecorp.bot.spring.boot.support.PushByReturnValueConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;

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
    public Application(PushByReturnValueConsumer.PushFactory returnValueConsumerPushFactory){
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

    @SuppressWarnings("unchecked")
    private void dispatchInternal(final Event event) {
        if(event instanceof MessageEvent){
            LineMessage lineMessage = LineUtils.messageEventToEntity((MessageEvent)event);
            lineMessageService.save(lineMessage);
            pushRequestService.pushLineMsg();
        }
    }

    private void sendTextMessage(Event event,String originalMessageText){
        returnValueConsumerPushFactory.createForEvent(event)
                .accept(new TextMessage(originalMessageText));
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
