package com.linecorp.bot.client;

import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.response.BotApiResponse;

import java.util.concurrent.CompletableFuture;

public interface LineMessagingWeChatClient {
    /**
     * Send messages to users when you want to.
     *
     * <p>INFO: Use of the Push Message API is limited to certain plans.
     *
     *
     * @see <a href="https://devdocs.line.me?java#push-message">//devdocs.line.me#push-message</a>
     */
    CompletableFuture<BotApiResponse> pushMessage(PushMessage pushMessage);

    static LineMessagingWeChatClientBuilder builder(String channelToken) {
        return new LineMessagingWeChatClientBuilder(channelToken);
    }

    static LineMessagingWeChatClientBuilder builder(ChannelTokenSupplier channelTokenSupplier) {
        return new LineMessagingWeChatClientBuilder(channelTokenSupplier);
    }
}
