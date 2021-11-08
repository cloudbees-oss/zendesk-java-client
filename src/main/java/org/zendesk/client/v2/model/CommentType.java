package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * https://developer.zendesk.com/api-reference/ticketing/tickets/ticket_comments/
 */
public enum CommentType {

    @JsonProperty("Comment") COMMENT,
    @JsonProperty("VoiceComment") VOICE_COMMENT;

}
