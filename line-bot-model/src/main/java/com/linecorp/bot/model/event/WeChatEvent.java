package com.linecorp.bot.model.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.linecorp.bot.model.event.source.Source;
import lombok.Value;

import java.time.Instant;

@Value
@JsonTypeName("weChat")
public class WeChatEvent implements Event, PushEvent{
    /**
     *  userId to this event
     */
    private final String to;

    /**
     * JSON object which contains the source of the event
     */
    private final Source source;

    /**
     * Time of the event
     */
    private final Instant timestamp;

    @JsonCreator
    public WeChatEvent(
            final String to,
            final Source source,
            final Instant timestamp) {
        this.to = to;
        this.source = source;
        this.timestamp = timestamp;
    }
}
