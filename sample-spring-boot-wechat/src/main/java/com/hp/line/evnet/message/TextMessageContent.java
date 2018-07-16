package com.hp.line.evnet.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Value;

/**
 * Message content for text
 */
@Value
@JsonTypeName("text")
public class TextMessageContent implements MessageContent{
    private final String id;
    /**
     * Message text
     */
    private final String text;

    @JsonCreator
    public TextMessageContent(
            final String id,
            final String text) {
        this.id = id;
        this.text = text;
    }
}
