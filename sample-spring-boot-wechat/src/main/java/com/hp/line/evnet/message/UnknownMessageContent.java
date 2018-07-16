package com.hp.line.evnet.message;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Value;

/**
 * Fallback message content type for {@link MessageContent}.
 */
@Value
public class UnknownMessageContent implements MessageContent {
    private final String id;

    @JsonCreator
    public UnknownMessageContent(final String id) {
        this.id = id;
    }
}
