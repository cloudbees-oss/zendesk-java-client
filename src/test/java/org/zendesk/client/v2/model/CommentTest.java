package org.zendesk.client.v2.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.function.Function;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.zendesk.client.v2.Zendesk;
import org.zendesk.client.v2.model.comments.VoiceComment;

public class CommentTest {

  private static final long COMMENT_ID = 123L;
  private static final String COMMENT_BODY = "Foo";
  private static final String COMMENT_VOICE_URL = "http://yourdomain.com/recordings/1.mp3";

  private static final ObjectMapper MAPPER = Zendesk.createMapper(Function.identity());

  @Test
  public void defaultType() throws JsonProcessingException {
    String json = createCommentJson(null);
    Comment comment = parseJson(json);
    assertEquals(Comment.class, comment.getClass());
    assertEquals(Long.valueOf(COMMENT_ID), comment.getId());
    assertEquals(COMMENT_BODY, comment.getBody());
  }

  @Test
  public void explicitTypeComment() throws JsonProcessingException {
    String json = createCommentJson("Comment");
    Comment comment = parseJson(json);
    assertEquals(Comment.class, comment.getClass());
    assertEquals(Long.valueOf(COMMENT_ID), comment.getId());
    assertEquals(COMMENT_BODY, comment.getBody());
  }

  @Test
  public void explicitTypeVoiceComment() throws JsonProcessingException {
    String json = createCommentJson("VoiceComment");
    Comment comment = parseJson(json);
    assertEquals(VoiceComment.class, comment.getClass());

    VoiceComment voiceComment = (VoiceComment) comment;
    assertEquals(Long.valueOf(COMMENT_ID), voiceComment.getId());
    assertEquals(COMMENT_BODY, voiceComment.getBody());
    assertEquals(COMMENT_VOICE_URL, voiceComment.getData().getRecordingUrl());
  }

  @Test
  public void explicitTypeTpeVoiceCommentType() throws JsonProcessingException {
    String json = createCommentJson("TpeVoiceComment");
    Comment comment = parseJson(json);
    assertEquals(VoiceComment.class, comment.getClass());

    VoiceComment voiceComment = (VoiceComment) comment;
    assertEquals(Long.valueOf(COMMENT_ID), voiceComment.getId());
    assertEquals(COMMENT_BODY, voiceComment.getBody());
    assertEquals(COMMENT_VOICE_URL, voiceComment.getData().getRecordingUrl());
  }

  @Test
  public void invalidType() throws JsonProcessingException {
    String json = createCommentJson("InvalidCommentType");
    assertThrows(InvalidFormatException.class, () -> parseJson(json));
  }

  private static String createCommentJson(@Nullable String type) throws JsonProcessingException {
    // https://developer.zendesk.com/documentation/ticketing/managing-tickets/adding-voice-comments-to-tickets/
    ObjectNode voiceData =
        MAPPER
            .createObjectNode()
            .put("from", "+16617480240")
            .put("to", "+16617480123")
            .put("recording_url", COMMENT_VOICE_URL)
            .put("started_at", "2019-04-16T09:14:57Z")
            .put("call_duration", 42)
            .put("answered_by_id", 28765)
            .put("transcription_text", "The transcription of the call")
            .put("location", "Topeka, Kansas");

    ObjectNode res =
        MAPPER.createObjectNode().put("id", COMMENT_ID).put("body", "Foo").set("data", voiceData);

    if (type != null) {
      res.put("type", type);
    }

    return MAPPER.writeValueAsString(res);
  }

  private static Comment parseJson(String json) throws JsonProcessingException {
    return MAPPER.readValue(json, Comment.class);
  }
}
