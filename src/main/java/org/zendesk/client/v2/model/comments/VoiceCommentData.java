package org.zendesk.client.v2.model.comments;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 * @author besbes
 * @since 05/03/2024 13:51
 */
public class VoiceCommentData {

  private String from;
  private String to;
  private String recordingUrl;
  private Long callDuration;
  private Date startedAt;

  @JsonProperty("from")
  public String getFrom() {
    return from;
  }

  @JsonProperty("to")
  public String getTo() {
    return to;
  }

  @JsonProperty("recording_url")
  public String getRecordingUrl() {
    return recordingUrl;
  }

  @JsonProperty("call_duration")
  public Long getCallDuration() {
    return callDuration;
  }

  @JsonProperty("started_at")
  public Date getStartedAt() {
    return startedAt;
  }
}
