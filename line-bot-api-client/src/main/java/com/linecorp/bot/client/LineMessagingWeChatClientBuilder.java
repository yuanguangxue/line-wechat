package com.linecorp.bot.client;

import lombok.NonNull;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class LineMessagingWeChatClientBuilder {

    public static final String DEFAULT_API_END_POINT = "https://guangxue-test.herokuapp.com/";
    public static final long DEFAULT_CONNECT_TIMEOUT = 10_000;
    public static final long DEFAULT_READ_TIMEOUT = 10_000;
    public static final long DEFAULT_WRITE_TIMEOUT = 10_000;

    //TODO: Move into all builder logic into this class from LineMessagingClientBuilder.
    private final LineMessagingServiceBuilder delegate;

    /**
     * Create a new {@link LineMessagingServiceBuilder} with specified given fixed channelToken.
     */
    public LineMessagingWeChatClientBuilder(final String fixedChannelToken) {
        delegate = LineMessagingServiceBuilder.create(fixedChannelToken);
    }

    /**
     * Create a new {@link LineMessagingServiceBuilder} with specified {@link ChannelTokenSupplier}.
     */
    public LineMessagingWeChatClientBuilder(final ChannelTokenSupplier channelTokenSupplier) {
        delegate = LineMessagingServiceBuilder.create(channelTokenSupplier);
    }

    /**
     * Set apiEndPoint.
     */
    public LineMessagingWeChatClientBuilder apiEndPoint(@NonNull String apiEndPoint) {
        delegate.apiEndPoint(apiEndPoint);
        return this;
    }

    /**
     * Set connectTimeout in milliseconds.
     */
    public LineMessagingWeChatClientBuilder connectTimeout(long connectTimeout) {
        delegate.connectTimeout(connectTimeout);
        return this;
    }

    /**
     * Set readTimeout in milliseconds.
     */
    public LineMessagingWeChatClientBuilder readTimeout(long readTimeout) {
        delegate.readTimeout(readTimeout);
        return this;
    }

    /**
     * Set writeTimeout in milliseconds.
     */
    public LineMessagingWeChatClientBuilder writeTimeout(long writeTimeout) {
        delegate.writeTimeout(writeTimeout);
        return this;
    }

    /**
     * Add interceptor
     */
    public LineMessagingWeChatClientBuilder addInterceptor(Interceptor interceptor) {
        delegate.addInterceptor(interceptor);
        return this;
    }

    /**
     * Add interceptor first
     */
    public LineMessagingWeChatClientBuilder addInterceptorFirst(Interceptor interceptor) {
        delegate.addInterceptorFirst(interceptor);
        return this;
    }

    /**
     * Remove all interceptors
     */
    public LineMessagingWeChatClientBuilder removeAllInterceptors() {
        delegate.removeAllInterceptors();
        return this;
    }

    /**
     * <p>If you want to use your own setting, specify {@link OkHttpClient.Builder} instance.</p>
     *
     * @deprecated use {@link #okHttpClientBuilder(OkHttpClient.Builder, boolean)} instead.
     */
    @Deprecated
    public LineMessagingWeChatClientBuilder okHttpClientBuilder(
            @NonNull final OkHttpClient.Builder okHttpClientBuilder) {
        delegate.okHttpClientBuilder(okHttpClientBuilder);
        return this;
    }

    /**
     * <p>If you want to use your own setting, specify {@link OkHttpClient.Builder} instance.</p>
     *
     * @param resetDefaultInterceptors If true, all default okhttp interceptors ignored.
     * You should insert authentication headers yourself.
     */
    public LineMessagingWeChatClientBuilder okHttpClientBuilder(
            @NonNull final OkHttpClient.Builder okHttpClientBuilder,
            final boolean resetDefaultInterceptors) {
        delegate.okHttpClientBuilder(okHttpClientBuilder, resetDefaultInterceptors);
        return this;
    }

    /**
     * <p>If you want to use your own setting, specify {@link Retrofit.Builder} instance.</p>
     *
     * <p>ref: {@link LineMessagingServiceBuilder#createDefaultRetrofitBuilder()} ()}.</p>
     */
    public LineMessagingWeChatClientBuilder retrofitBuilder(@NonNull Retrofit.Builder retrofitBuilder) {
        delegate.retrofitBuilder(retrofitBuilder);
        return this;
    }

    /**
     * Creates a new {@link LineMessagingService}.
     */
    public LineMessagingWeChatClient build() {
        return new LineMessagingWeChatClientImpl(delegate.build());
    }
}
