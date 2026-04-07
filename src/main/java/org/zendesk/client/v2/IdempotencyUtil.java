package org.zendesk.client.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.zendesk.client.v2.model.IdempotentResult;

/**
 * Utility class for handling Zendesk API idempotency keys.
 *
 * <p>Provides methods to add idempotency headers to requests and process idempotency-related
 * response headers. Supports the Zendesk API's idempotency feature which allows safe retries of
 * create operations without creating duplicate resources.
 *
 * @see <a href="https://developer.zendesk.com/api-reference/ticketing/introduction/#idempotency">
 *     Zendesk API Idempotency</a>
 * @since 1.5.0
 */
public class IdempotencyUtil {

  static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";
  static final String IDEMPOTENCY_LOOKUP_HEADER = "x-idempotency-lookup";
  static final String IDEMPOTENCY_LOOKUP_HIT = "hit";
  static final String IDEMPOTENCY_LOOKUP_MISS = "miss";
  static final String IDEMPOTENCY_ERROR_NAME = "IdempotentRequestError";

  public static RequestBuilder addIdempotencyHeader(RequestBuilder builder, String idempotencyKey) {
    // https://developer.zendesk.com/api-reference/ticketing/introduction/#idempotency
    return builder.setHeader(IDEMPOTENCY_KEY_HEADER, idempotencyKey);
  }

  public static <T> AsyncCompletionHandler<IdempotentResult<T>> wrapHandler(
      AsyncCompletionHandler<T> handler) {
    return new AsyncCompletionHandler<>() {
      @Override
      public IdempotentResult<T> onCompleted(Response response) throws Exception {
        T entity = handler.onCompleted(response);
        boolean duplicateRequest = isDuplicateResponse(response);

        return new IdempotentResult<>(entity, duplicateRequest);
      }

      @Override
      public void onThrowable(Throwable t) {
        handler.onThrowable(t);
      }
    };
  }

  public static boolean isIdempotencyConflict(
      Response response,
      ObjectMapper mapper) throws JsonProcessingException {
    if (response.getStatusCode() != 400) {
      return false;
    }

    // Note: Jackson's own docs are a bit outdated in that `readTree` returns
    // `MissingNode.getInstance()` and not `null` when given an essentially empty string.
    JsonNode error = mapper.readTree(response.getResponseBody()).path("error");
    return IDEMPOTENCY_ERROR_NAME.equals(error.textValue());
  }

  private static boolean isDuplicateResponse(Response response) {
    // https://developer.zendesk.com/api-reference/ticketing/introduction/#idempotency
    String idempotencyLookup = response.getHeader(IDEMPOTENCY_LOOKUP_HEADER);
    if (idempotencyLookup == null) {
      idempotencyLookup = "<absent>";
    }

    switch (idempotencyLookup) {
      case IDEMPOTENCY_LOOKUP_HIT:
        return true;
      case IDEMPOTENCY_LOOKUP_MISS:
        return false;
      default:
        throw new IllegalArgumentException(
            String.format(
                "Unexpected value of the idempotency lookup header: %s",
                idempotencyLookup));
    }
  }

  private IdempotencyUtil() {
    throw new UnsupportedOperationException("Utility class");
  }
}
