package com.linecorp.bot.spring.boot.support;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.LineMessagingWeChatClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.PushEvent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.response.BotApiResponse;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static java.util.Collections.singletonList;

@Slf4j
@Builder
public class PushByReturnValueConsumer implements Consumer<Object> {

    private final LineMessagingClient lineMessagingClient;
    private final LineMessagingWeChatClient lineMessagingWeChatClient;
    private final Event originalEvent;

    @Component
    public static class PushFactory {
        private final LineMessagingClient lineMessagingClient;
        private final LineMessagingWeChatClient lineMessagingWeChatClient;

        @Autowired
        public PushFactory(final LineMessagingClient lineMessagingClient,final LineMessagingWeChatClient lineMessagingWeChatClient) {
            this.lineMessagingClient = lineMessagingClient;
            this.lineMessagingWeChatClient = lineMessagingWeChatClient;
        }

        PushByReturnValueConsumer createForEvent(final Event event) {
            return builder()
                    .lineMessagingClient(lineMessagingClient)
                    .lineMessagingWeChatClient(lineMessagingWeChatClient)
                    .originalEvent(event)
                    .build();
        }
    }

    @Override
    public void accept(final Object returnValue) {
        if (returnValue instanceof CompletableFuture) {
            // accept when future complete.
            ((CompletableFuture<?>) returnValue)
                    .whenComplete(this::whenComplete);
        } else {
            // accept immediately.
            acceptResult(returnValue);
        }
    }

    private void whenComplete(final Object futureResult, final Throwable throwable) {
        if (throwable != null) {
            log.error("Method return value waited but exception occurred in CompletedFuture", throwable);
            return;
        }

        acceptResult(futureResult);
    }

    private void acceptResult(final Object returnValue) {
        if (returnValue instanceof Message) {
            reply(singletonList((Message) returnValue));
        } else if (returnValue instanceof List) {
            List<?> returnValueAsList = (List<?>) returnValue;

            if (returnValueAsList.isEmpty()) {
                return;
            }

            reply(checkListContents(returnValueAsList));
        }
    }

    private void reply(final List<Message> messages) {
        final PushEvent pushEvent = (PushEvent) originalEvent;
        /*lineMessagingClient.pushMessage(new PushMessage(pushEvent.getTo(), messages))
                .whenComplete(this::logging);*/

        lineMessagingWeChatClient.pushMessage(new PushMessage(pushEvent.getTo(), messages))
                .whenComplete(this::logging);
        // DO NOT BLOCK HERE, otherwise, next message processing will be BLOCKED.
    }

    private void logging(final BotApiResponse botApiResponse, final Throwable throwable) {
        if (throwable == null) {
            log.debug("Push message success. response = {}", botApiResponse);
        } else {
            log.warn("Push message failed: {}", throwable.getMessage(), throwable);
        }
    }

    @VisibleForTesting
    static List<Message> checkListContents(final List<?> list) {
        for (int i = 0; i < list.size(); ++i) {
            final Object item = list.get(i);
            Preconditions.checkNotNull(item, "item is null. index = {} in {}", i, list);
            Preconditions.checkArgument(item instanceof Message,
                    "List contains not Message type object. type = {}",
                    item.getClass());
        }

        @SuppressWarnings("unchecked")
        final List<Message> messageList = (List<Message>) list;
        return messageList;
    }
}
