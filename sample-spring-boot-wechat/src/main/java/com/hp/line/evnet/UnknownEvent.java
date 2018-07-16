package com.hp.line.evnet;

import lombok.Value;

/**
 * Fallback event type for {@link Event}.
 */
@Value
public class UnknownEvent implements Event{

    private final String to;

}
