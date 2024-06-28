package org.zendesk.client.v2.model.comments;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author besbes
 * @since 05/03/2024 13:51
 */
public class VoiceCommentData {

  private Long callDuration;

  @JsonProperty("call_duration")
  public Long getCallDuration() {
    return callDuration;
  }
}
