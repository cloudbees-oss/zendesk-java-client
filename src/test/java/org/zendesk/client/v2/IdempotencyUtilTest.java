package org.zendesk.client.v2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.junit.Test;
import org.zendesk.client.v2.model.IdempotencyState;
import org.zendesk.client.v2.model.IdempotentEntity;

public class IdempotencyUtilTest {

  private static final Request REQUEST = new RequestBuilder("POST")
      .setUrl("https://example.com")
      .build();
  private static final String KEY = "test-key-123";

  @Test
  public void addIdempotencyState_withNullState_returnsOriginalRequest() {
    Request result = IdempotencyUtil.addIdempotencyState(REQUEST, null);

    assertThat(result).isSameAs(REQUEST);
    assertThat(result.getHeaders().get(IdempotencyUtil.IDEMPOTENCY_KEY_HEADER)).isNull();
  }

  @Test
  public void addIdempotencyState_withPendingState_addsIdempotencyKeyHeader() {
    IdempotentEntity entity = createMockEntity();
    IdempotencyState state = IdempotencyState.of(entity).orElseThrow();

    Request result = IdempotencyUtil.addIdempotencyState(REQUEST, state);

    assertThat(result).isNotSameAs(REQUEST);
    assertThat(result.getHeaders().get(IdempotencyUtil.IDEMPOTENCY_KEY_HEADER)).isEqualTo(KEY);
  }

  @Test
  public void addIdempotencyState_withCreatedState_throwsIllegalArgumentException() {
    IdempotentEntity entity = createMockEntity();
    IdempotencyState state = IdempotencyState.of(entity).orElseThrow().toCreated();

    assertThatThrownBy(() -> IdempotencyUtil.addIdempotencyState(REQUEST, state))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void addIdempotencyState_withPreviouslyCreatedState_throwsIllegalArgumentException() {
    IdempotentEntity entity = createMockEntity();
    IdempotencyState state = IdempotencyState.of(entity).orElseThrow().toPreviouslyCreated();

    assertThatThrownBy(() -> IdempotencyUtil.addIdempotencyState(REQUEST, state))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void wrapHandler_withNullState_returnsOriginalHandler() {
    @SuppressWarnings("unchecked")
    AsyncCompletionHandler<IdempotentEntity> handler = mock(AsyncCompletionHandler.class);

    AsyncCompletionHandler<IdempotentEntity> result =
        IdempotencyUtil.wrapHandler(handler, null);

    assertThat(result).isSameAs(handler);
  }

  @Test
  public void wrapHandler_withMissHeader_setsIsIdempotencyHitToFalse() throws Exception {
    testWrapHandler(false);
  }

  @Test
  public void wrapHandler_withHitHeader_setsIsIdempotencyHitToTrue() throws Exception {
    testWrapHandler(true);
  }

  @Test
  public void wrapHandler_withMissingHeader_doesNotSetIdempotencyFields() throws Exception {
    testWrapHandler(null);
  }

  @Test
  public void wrapHandler_propagatesOnThrowable() {
    IdempotentEntity entity = createMockEntity();
    IdempotencyState state = IdempotencyState.of(entity).orElseThrow();

    @SuppressWarnings("unchecked")
    AsyncCompletionHandler<IdempotentEntity> originalHandler = mock(AsyncCompletionHandler.class);
    Throwable throwable = new RuntimeException("test exception");

    AsyncCompletionHandler<IdempotentEntity> wrappedHandler =
        IdempotencyUtil.wrapHandler(originalHandler, state);
    wrappedHandler.onThrowable(throwable);

    verify(originalHandler).onThrowable(throwable);
  }

  private IdempotentEntity createMockEntity() {
    IdempotentEntity entity = mock(IdempotentEntity.class);
    when(entity.getIdempotencyKey()).thenReturn(KEY);
    return entity;
  }

  private void testWrapHandler(Boolean isHit) throws Exception {
    IdempotentEntity entity = createMockEntity();
    IdempotencyState state = IdempotencyState.of(entity).orElseThrow();

    @SuppressWarnings("unchecked")
    AsyncCompletionHandler<IdempotentEntity> originalHandler = mock(AsyncCompletionHandler.class);
    Response response = mock(Response.class);

    String headerValue = Optional.ofNullable(isHit)
        .map(hit -> hit
            ? IdempotencyUtil.IDEMPOTENCY_LOOKUP_HIT
            : IdempotencyUtil.IDEMPOTENCY_LOOKUP_MISS)
        .orElse(null);
    when(response.getHeader(IdempotencyUtil.IDEMPOTENCY_LOOKUP_HEADER)).thenReturn(headerValue);
    when(originalHandler.onCompleted(response)).thenReturn(entity);

    AsyncCompletionHandler<IdempotentEntity> wrappedHandler =
        IdempotencyUtil.wrapHandler(originalHandler, state);

    IdempotentEntity result = wrappedHandler.onCompleted(response);
    assertThat(result).isSameAs(entity);

    int numExpectedInvocations = isHit == null ? 0 : 1;
    verify(entity, times(numExpectedInvocations)).setIdempotencyKey(KEY);
    verify(entity, times(numExpectedInvocations)).setIsIdempotencyHit(isHit);
  }
}
