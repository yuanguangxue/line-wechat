package com.hp.line.evnet.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Abstract inteface of the message content
 */
@JsonSubTypes({
        @JsonSubTypes.Type(TextMessageContent.class)
})
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        defaultImpl = UnknownMessageContent.class,
        visible = true
)
public interface MessageContent {
    /**
     * Get the message ID
     */
    String getId();
}
