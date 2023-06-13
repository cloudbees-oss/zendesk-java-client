package org.zendesk.client.v2.model.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.zendesk.client.v2.model.serializer.LocalDateTimeDeserializer;
import org.zendesk.client.v2.model.serializer.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatStartedEvent extends Event {
    private static final long serialVersionUID = 1L;
    @JsonProperty("value")
    private ChatStartedEventValue value;

    public ChatStartedEventValue getValue() {
        return value;
    }

    public void setValue(ChatStartedEventValue value) {
        this.value = value;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChatStartedEventValue {
        @JsonProperty("history")
        private List<ChatEventHistory> historyList;

        public List<ChatEventHistory> getHistoryList() {
            return historyList;
        }

        public void setHistoryList(List<ChatEventHistory> historyList) {
            this.historyList = historyList;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChatEventHistory {
        @JsonProperty("actor_name")
        private String actorName;
        @JsonProperty("actor_type")
        private String actorType;
        @JsonProperty("actor_id")
        private String actorId;
        @JsonProperty("type")
        private String type;
        @JsonProperty("message")
        private String message;

        @JsonProperty("message_id")
        private String messageId;

        @JsonProperty("original_message")
        private OriginalMessage originalMessage;

        @JsonProperty("filename")
        private String fileName;

        @JsonProperty("mime_type")
        private String mimeType;

        @JsonProperty("url")
        private String url;

        @JsonProperty("size")
        private Long size;

        @JsonProperty("timestamp")
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        private LocalDateTime timestamp;

        public String getActorName() {
            return actorName;
        }

        public void setActorName(String actorName) {
            this.actorName = actorName;
        }

        public String getActorType() {
            return actorType;
        }

        public void setActorType(String actorType) {
            this.actorType = actorType;
        }

        public String getActorId() {
            return actorId;
        }

        public void setActorId(String actorId) {
            this.actorId = actorId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMessageId() {
            return messageId;
        }

        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }

        public OriginalMessage getOriginalMessage() {
            return originalMessage;
        }

        public void setOriginalMessage(OriginalMessage originalMessage) {
            this.originalMessage = originalMessage;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Long getSize() {
            return size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OriginalMessage {
        @JsonProperty("id")
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
