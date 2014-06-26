package org.zendesk.client.v2.model.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author stephenc
 * @since 05/04/2013 11:53
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CommentEvent.class, name = "Comment"),
        @JsonSubTypes.Type(value = VoiceCommentEvent.class, name = "VoiceComment"),
        @JsonSubTypes.Type(value = CommentPrivacyChangeEvent.class, name = "CommentPrivacyChange"),
        @JsonSubTypes.Type(value = CreateEvent.class, name = "Create"),
        @JsonSubTypes.Type(value = ChangeEvent.class, name = "Change"),
        @JsonSubTypes.Type(value = NotificationEvent.class, name = "Notification"),
        @JsonSubTypes.Type(value = CcEvent.class, name = "Cc"),
        @JsonSubTypes.Type(value = ErrorEvent.class, name = "Error"),
        @JsonSubTypes.Type(value = ExternalEvent.class, name = "External"),
        @JsonSubTypes.Type(value = FacebookEvent.class, name = "FacebookEvent"),
        @JsonSubTypes.Type(value = LogMeInTranscriptEvent.class, name = "LogMeInTranscript"),
        @JsonSubTypes.Type(value = PushEvent.class, name = "Push"),
        @JsonSubTypes.Type(value = TweetEvent.class, name = "Tweet"),
        @JsonSubTypes.Type(value = SMSEvent.class, name = "SMS"),
        @JsonSubTypes.Type(value = TicketSharingEvent.class, name = "TicketSharingEvent")
})

public abstract class Event {
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Event");
        sb.append("{id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}
