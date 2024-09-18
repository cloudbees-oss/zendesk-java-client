package org.zendesk.client.v2.model;

/** https://developer.zendesk.com/api-reference/ticketing/tickets/ticket_comments/ */
public enum CommentType {
  COMMENT("Comment"),
  VOICE_COMMENT("VoiceComment"),
  // Talk Partner Edition
  TPE_VOICE_COMMENT("TpeVoiceComment");

  private final String name;

  CommentType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
