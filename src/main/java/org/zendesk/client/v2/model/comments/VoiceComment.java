package org.zendesk.client.v2.model.comments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.zendesk.client.v2.model.Comment;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VoiceComment extends Comment {

  private VoiceCommentData data;

  @JsonProperty("data")
  public VoiceCommentData getData() {
    return data;
  }
}
