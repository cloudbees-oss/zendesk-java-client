package org.zendesk.client.v2;

import java.util.Optional;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.zendesk.client.v2.model.IdempotencyState;
import org.zendesk.client.v2.model.IdempotencyState.Status;
import org.zendesk.client.v2.model.IdempotentEntity;

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

  /**
   * Adds an idempotency key header to the request if the state is present and pending.
   *
   * @param request the HTTP request to modify
   * @param state the idempotency state, or null if idempotency is not being used
   * @return a new request with the idempotency key header added, or the original request if state
   *     is null
   * @throws IllegalArgumentException if the state is not in PENDING status
   */
  public static Request addIdempotencyState(Request request, IdempotencyState state) {
    if (state == null) {
      return request;
    }

    if (state.getStatus() != Status.PENDING) {
      throw new IllegalArgumentException("Idempotency state must be PENDING to add to a request");
    }

    // https://developer.zendesk.com/api-reference/ticketing/introduction/#idempotency
    return request.toBuilder()
        .setHeader(IDEMPOTENCY_KEY_HEADER, state.getIdempotencyKey())
        .build();
  }

  /**
   * Wraps an async completion handler to process idempotency response headers.
   *
   * <p>The wrapped handler will automatically update the entity's idempotency fields based on the
   * response headers returned by the Zendesk API. If the {@code x-idempotency-lookup} header
   * indicates a "hit", the entity will be marked as previously created. If "miss", it will be
   * marked as newly created.
   *
   * @param <T> the entity type that implements IdempotentEntity
   * @param handler the original async completion handler
   * @param idempotencyState the idempotency state, or null if idempotency is not being used
   * @return a wrapped handler that processes idempotency headers, or the original handler if state
   *     is null
   */
  public static <T extends IdempotentEntity> AsyncCompletionHandler<T> wrapHandler(
      AsyncCompletionHandler<T> handler,
      IdempotencyState idempotencyState) {
    if (idempotencyState == null) {
      return handler;
    }

    return new AsyncCompletionHandler<>() {
      @Override
      public T onCompleted(Response response) throws Exception {
        T entity = handler.onCompleted(response);
        transitionIdempotencyState(idempotencyState, response)
            .ifPresent(newState -> newState.apply(entity));
        return entity;
      }

      @Override
      public void onThrowable(Throwable t) {
        handler.onThrowable(t);
      }
    };
  }

  private static Optional<IdempotencyState> transitionIdempotencyState(
      IdempotencyState state,
      Response response) {
    if (state == null) {
      return Optional.empty();
    }

    // https://developer.zendesk.com/api-reference/ticketing/introduction/#idempotency
    String idempotencyLookup = response.getHeader(IDEMPOTENCY_LOOKUP_HEADER);
    if (idempotencyLookup == null) {
      return Optional.empty();
    }

    switch (idempotencyLookup) {
      case IDEMPOTENCY_LOOKUP_HIT:
        return Optional.of(state.toPreviouslyCreated());
      case IDEMPOTENCY_LOOKUP_MISS:
        return Optional.of(state.toCreated());
      default:
        return Optional.empty();
    }
  }

  private IdempotencyUtil() {
    throw new UnsupportedOperationException("Utility class");
  }
}
