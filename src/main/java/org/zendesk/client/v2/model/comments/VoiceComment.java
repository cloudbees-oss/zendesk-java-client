package org.zendesk.client.v2.model.comments;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.zendesk.client.v2.model.Comment;

public class VoiceComment extends Comment {

  private VoiceCommentData data;

  @JsonProperty("data")
  public VoiceCommentData getData() {
    return data;
  }
}
