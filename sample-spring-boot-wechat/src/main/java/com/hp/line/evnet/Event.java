package com.hp.line.evnet;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Abstract interface of events.
 */
@JsonSubTypes({
        @JsonSubTypes.Type(MessageEvent.class)
})
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        defaultImpl = UnknownEvent.class,
        visible = true
)
public interface Event {

    String getTo();

}
