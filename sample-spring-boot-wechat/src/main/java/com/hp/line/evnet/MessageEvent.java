package com.hp.line.evnet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.hp.line.evnet.message.MessageContent;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@JsonTypeName("message")
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public class MessageEvent<T extends MessageContent> implements Event {

    private final String to;

    private final List<T> messages;

}
