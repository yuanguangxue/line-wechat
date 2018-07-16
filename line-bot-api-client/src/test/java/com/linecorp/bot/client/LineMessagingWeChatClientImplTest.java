package com.linecorp.bot.client;

import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import okhttp3.Request;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.OngoingStubbing;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LineMessagingWeChatClientImplTest {

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public final Timeout timeoutRule = Timeout.seconds(1);

    @Mock
    private LineMessagingService retrofitMock;

    @InjectMocks
    private LineMessagingWeChatClientImpl target;

    private static final BotApiResponse BOT_API_SUCCESS_RESPONSE = new BotApiResponse("", emptyList());

    // Utility methods

    private static <T> void whenCall(Call<T> call, T value) {
        final OngoingStubbing<Call<T>> callOngoingStubbing = when(call);
        callOngoingStubbing.thenReturn(enqueue(value));
    }

    private static <T> Call<T> enqueue(T value) {
        return new Call<T>() {
            @Override
            public Response<T> execute() throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void enqueue(Callback<T> callback) {
                callback.onResponse(this, Response.success(value));
            }

            @Override
            public boolean isExecuted() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void cancel() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isCanceled() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Call<T> clone() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Request request() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Test
    public void pushMessage()  throws Exception {
        whenCall(retrofitMock.pushMessage(any()),
                BOT_API_SUCCESS_RESPONSE);
        final PushMessage pushMessage = new PushMessage("TO", new TextMessage("text"));

        // Do
        final BotApiResponse botApiResponse =
                target.pushMessage(pushMessage).get();

        // Verify
        verify(retrofitMock, only()).pushMessage(pushMessage);
        assertThat(botApiResponse).isEqualTo(BOT_API_SUCCESS_RESPONSE);
    }
}
