package com.linecorp.bot.client;

import com.linecorp.bot.client.exception.GeneralLineMessagingException;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.concurrent.CompletableFuture;

@Slf4j
@AllArgsConstructor
public class LineMessagingWeChatClientImpl implements LineMessagingWeChatClient{

    private static final ExceptionConverter EXCEPTION_CONVERTER = new ExceptionConverter();

    private final LineMessagingService retrofitImpl;

    @Override
    public CompletableFuture<BotApiResponse> pushMessage(PushMessage pushMessage) {
        return toFuture(retrofitImpl.pushMessage(pushMessage));
    }

    // TODO: Extract this method.
    static <T> CompletableFuture<T> toFuture(Call<T> callToWrap) {
        final LineMessagingWeChatClientImpl.CallbackAdaptor<T> completableFuture = new LineMessagingWeChatClientImpl.CallbackAdaptor<>();
        callToWrap.enqueue(completableFuture);
        return completableFuture;
    }

    static class CallbackAdaptor<T> extends CompletableFuture<T> implements Callback<T> {
        @Override
        public void onResponse(final Call<T> call, final Response<T> response) {
            if (response.isSuccessful()) {
                complete(response.body());
            } else {
                completeExceptionally(EXCEPTION_CONVERTER.apply(response));
            }
        }

        @Override
        public void onFailure(final Call<T> call, final Throwable t) {
            completeExceptionally(
                    new GeneralLineMessagingException(t.getMessage(), null, t));
        }
    }

}
