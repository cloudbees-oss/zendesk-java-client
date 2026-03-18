package org.zendesk.client.v2;

import java.util.Optional;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.zendesk.client.v2.model.IdempotencyState;
import org.zendesk.client.v2.model.IdempotencyState.Status;
import org.zendesk.client.v2.model.IdempotentEntity;

public class IdempotencyUtil {

  public static Request addIdempotencyState(Request request, IdempotencyState state) {
    if (state == null) {
      return request;
    }

    if (state.getStatus() != Status.PENDING) {
      throw new IllegalArgumentException("Idempotency state must be PENDING to add to a request");
    }

    // https://developer.zendesk.com/api-reference/ticketing/introduction/#idempotency
    return request.toBuilder()
        .setHeader("Idempotency-Key", state.getIdempotencyKey())
        .build();
  }

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
    String idempotencyLookup = response.getHeader("x-idempotency-lookup");
    if (idempotencyLookup == null) {
      return Optional.empty();
    }

    switch (idempotencyLookup) {
      case "hit":
        return Optional.of(state.toPreviouslyCreated());
      case "miss":
        return Optional.of(state.toCreated());
      default:
        return Optional.empty();
    }
  }

  private IdempotencyUtil() {
    throw new UnsupportedOperationException("Utility class");
  }
}
