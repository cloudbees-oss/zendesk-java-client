package org.zendesk.client.v2.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.Test;
import org.zendesk.client.v2.model.IdempotencyState.Status;

public class IdempotencyStateTest {

  private static final String KEY = "test-key-123";
  private static final String OTHER_KEY = "test-key-456";

  @Test
  public void of_withIdempotencyKey_createsPendingState() {
    IdempotentEntity entity = createMockEntity(KEY);

    Optional<IdempotencyState> result = IdempotencyState.of(entity);

    assertThat(result).isPresent();
    IdempotencyState state = result.get();
    assertThat(state.getIdempotencyKey()).isEqualTo(KEY);
    assertThat(state.getStatus()).isEqualTo(Status.PENDING);
  }

  @Test
  public void of_withNullIdempotencyKey_returnsEmptyOptional() {
    IdempotentEntity entity = createMockEntity(null);

    Optional<IdempotencyState> result = IdempotencyState.of(entity);

    assertThat(result).isEmpty();
  }

  @Test
  public void apply_withCreatedState_setsEntityFieldsCorrectly() {
    IdempotentEntity entity = createMockEntity(KEY);
    IdempotencyState state = IdempotencyState.of(entity).orElseThrow().toCreated();

    state.apply(entity);

    verify(entity).setIdempotencyKey(KEY);
    verify(entity).setIsIdempotencyHit(false);
  }

  @Test
  public void apply_withPreviouslyCreatedState_setsEntityFieldsCorrectly() {
    IdempotentEntity entity = createMockEntity(KEY);
    IdempotencyState state = IdempotencyState.of(entity).orElseThrow().toPreviouslyCreated();

    state.apply(entity);

    verify(entity).setIdempotencyKey(KEY);
    verify(entity).setIsIdempotencyHit(true);
  }

  @Test
  public void apply_withPendingState_throwsIllegalStateException() {
    IdempotentEntity entity = createMockEntity(KEY);
    IdempotencyState state = IdempotencyState.of(entity).orElseThrow();

    assertThatThrownBy(() -> state.apply(entity)).isInstanceOf(IllegalStateException.class);
  }

  @Test
  public void apply_withMismatchedKey_throwsIllegalArgumentException() {
    assertThat(OTHER_KEY).isNotEqualTo(KEY);
    IdempotentEntity entity = createMockEntity(OTHER_KEY);
    IdempotencyState state = IdempotencyState.of(createMockEntity(KEY)).orElseThrow().toCreated();

    assertThatThrownBy(() -> state.apply(entity)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void apply_withNullEntity_doesNotThrow() {
    IdempotentEntity entity = createMockEntity(KEY);
    IdempotencyState state = IdempotencyState.of(entity).orElseThrow().toCreated();

    assertThatNoException().isThrownBy(() -> state.apply(null));
  }

  @Test
  public void toCreated_transitionsCorrectly() {
    IdempotentEntity entity = createMockEntity(KEY);
    IdempotencyState pendingState = IdempotencyState.of(entity).orElseThrow();

    IdempotencyState createdState = pendingState.toCreated();

    assertThat(createdState.getIdempotencyKey()).isEqualTo(KEY);
    assertThat(createdState.getStatus()).isEqualTo(Status.CREATED);
    // Original state should not change
    assertThat(pendingState.getStatus()).isEqualTo(Status.PENDING);
  }

  @Test
  public void toPreviouslyCreated_transitionsCorrectly() {
    IdempotentEntity entity = createMockEntity(KEY);
    IdempotencyState pendingState = IdempotencyState.of(entity).orElseThrow();

    IdempotencyState previouslyCreatedState = pendingState.toPreviouslyCreated();

    assertThat(previouslyCreatedState.getIdempotencyKey()).isEqualTo(KEY);
    assertThat(previouslyCreatedState.getStatus()).isEqualTo(Status.PREVIOUSLY_CREATED);
    // Original state should not change
    assertThat(pendingState.getStatus()).isEqualTo(Status.PENDING);
  }

  @Test
  public void equals_withSameState_returnsTrue() {
    IdempotentEntity entity1 = createMockEntity(KEY);
    IdempotentEntity entity2 = createMockEntity(KEY);

    IdempotencyState state1 = IdempotencyState.of(entity1).orElseThrow();
    IdempotencyState state2 = IdempotencyState.of(entity2).orElseThrow();

    assertThat(state1).isEqualTo(state2);
  }

  @Test
  public void equals_withDifferentKey_returnsFalse() {
    IdempotentEntity entity1 = createMockEntity(KEY);
    IdempotentEntity entity2 = createMockEntity(OTHER_KEY);

    IdempotencyState state1 = IdempotencyState.of(entity1).orElseThrow();
    IdempotencyState state2 = IdempotencyState.of(entity2).orElseThrow();

    assertThat(state1).isNotEqualTo(state2);
  }

  @Test
  public void equals_withDifferentStatus_returnsFalse() {
    IdempotentEntity entity = createMockEntity(KEY);
    IdempotencyState pendingState = IdempotencyState.of(entity).orElseThrow();

    assertThat(pendingState)
        .isNotEqualTo(pendingState.toCreated())
        .isNotEqualTo(pendingState.toPreviouslyCreated());
    assertThat(pendingState.toCreated()).isNotEqualTo(pendingState.toPreviouslyCreated());
  }

  @Test
  public void equals_withItself_returnsTrue() {
    IdempotentEntity entity = createMockEntity(KEY);
    IdempotencyState state = IdempotencyState.of(entity).orElseThrow();

    assertThat(state).isEqualTo(state);
  }

  @Test
  public void equals_withNull_returnsFalse() {
    IdempotentEntity entity = createMockEntity("test-key-123");
    IdempotencyState state = IdempotencyState.of(entity).orElseThrow();

    assertThat(state).isNotEqualTo(null);
  }

  @Test
  public void equals_withDifferentType_returnsFalse() {
    IdempotentEntity entity = createMockEntity("test-key-123");
    IdempotencyState state = IdempotencyState.of(entity).orElseThrow();

    assertThat(state).isNotEqualTo("not an IdempotencyState");
  }

  @Test
  public void hashCode_withSameState_returnsSameHashCode() {
    IdempotentEntity entity1 = createMockEntity(KEY);
    IdempotentEntity entity2 = createMockEntity(KEY);

    IdempotencyState state1 = IdempotencyState.of(entity1).orElseThrow();
    IdempotencyState state2 = IdempotencyState.of(entity2).orElseThrow();

    assertThat(state1).hasSameHashCodeAs(state2);
    assertThat(state1.toCreated()).hasSameHashCodeAs(state2.toCreated());
    assertThat(state1.toPreviouslyCreated()).hasSameHashCodeAs(state2.toPreviouslyCreated());
  }

  @Test
  public void toString_returnsFormattedString() {
    IdempotentEntity entity = createMockEntity("test-key-123");
    IdempotencyState state = IdempotencyState.of(entity).orElseThrow();

    String result = state.toString();

    assertThat(result)
        .contains("IdempotencyState")
        .contains("test-key-123")
        .contains("PENDING");
  }

  private IdempotentEntity createMockEntity(String idempotencyKey) {
    IdempotentEntity entity = mock(IdempotentEntity.class);
    when(entity.getIdempotencyKey()).thenReturn(idempotencyKey);
    return entity;
  }
}
