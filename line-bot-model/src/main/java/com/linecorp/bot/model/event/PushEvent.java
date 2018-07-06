package com.linecorp.bot.model.event;

import com.linecorp.bot.model.PushMessage;

/**
 * Interface for reply support.
 *
 * @see Event
 */
public interface PushEvent {
    /**
     * Token for replying to this event.
     *
     * @see PushMessage
     * @see <a href="https://developers.line.me/en/reference/messaging-api/#send-push-message">//devdocs.line.me/#send-push-message</a> &gt; Request Body
     */
    String getTo();
}
