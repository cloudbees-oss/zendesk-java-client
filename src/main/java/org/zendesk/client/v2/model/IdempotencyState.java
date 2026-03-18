package org.zendesk.client.v2.model;

import java.util.Objects;
import java.util.Optional;

public class IdempotencyState {

  public enum Status {
    PENDING,
    CREATED,
    PREVIOUSLY_CREATED
  }

  private final String idempotencyKey;
  private final Status status;

  public static Optional<IdempotencyState> of(IdempotentEntity entity) {
    return Optional.ofNullable(entity.getIdempotencyKey())
        .map(key -> new IdempotencyState(key, Status.PENDING));
  }

  public void apply(IdempotentEntity entity) {
    if (entity == null) {
      return;
    }

    String entityKey = entity.getIdempotencyKey();
    if (entityKey != null && !entityKey.equals(idempotencyKey)) {
      throw new IllegalArgumentException(
          String.format(
              "Idempotency key mismatch: entity key = %s, state key = %s",
              entityKey,
              idempotencyKey));
    }

    if (status == Status.PENDING) {
      throw new IllegalStateException(
          String.format("Cannot apply idempotency state: %s", this));
    }

    entity.setIdempotencyKey(idempotencyKey);
    entity.setIsIdempotencyHit(status == Status.PREVIOUSLY_CREATED);
  }

  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  public Status getStatus() {
    return status;
  }

  @Override
  public String toString() {
    return String.format(
        "IdempotencyState{idempotencyKey=%s, status=%s}",
        idempotencyKey,
        status.name());
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (other instanceof IdempotencyState) {
      IdempotencyState otherState = (IdempotencyState) other;
      return Objects.equals(idempotencyKey, otherState.idempotencyKey)
          && Objects.equals(status, otherState.status);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(idempotencyKey, status);
  }

  public IdempotencyState toCreated() {
    return new IdempotencyState(idempotencyKey, Status.CREATED);
  }

  public IdempotencyState toPreviouslyCreated() {
    return new IdempotencyState(idempotencyKey, Status.PREVIOUSLY_CREATED);
  }

  private IdempotencyState(String idempotencyKey, Status status) {
    this.idempotencyKey = idempotencyKey;
    this.status = status;
  }
}
