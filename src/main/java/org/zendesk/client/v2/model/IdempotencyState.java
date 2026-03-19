package org.zendesk.client.v2.model;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents the state of an idempotent operation in the Zendesk API.
 *
 * <p>This immutable class tracks the lifecycle of an idempotent request:
 *
 * <ul>
 *   <li><b>PENDING</b> - Initial state before the request is sent
 *   <li><b>CREATED</b> - The resource was newly created (idempotency key miss)
 *   <li><b>PREVIOUSLY_CREATED</b> - The resource was previously created (idempotency key hit)
 * </ul>
 *
 * @since 1.5.0
 */
public class IdempotencyState {

  /**
   * The status of an idempotent operation.
   */
  public enum Status {
    /** Initial state, ready to be sent with a request. */
    PENDING,
    /** The resource was newly created (first request with this idempotency key). */
    CREATED,
    /** The resource was previously created (duplicate request with this idempotency key). */
    PREVIOUSLY_CREATED
  }

  private final String idempotencyKey;
  private final Status status;

  /**
   * Creates an IdempotencyState from an entity if it has an idempotency key.
   *
   * @param entity the entity to extract the idempotency key from
   * @return an Optional containing a PENDING IdempotencyState if the entity has an idempotency
   *     key, or an empty Optional if the key is null
   */
  public static Optional<IdempotencyState> of(IdempotentEntity entity) {
    return Optional.ofNullable(entity.getIdempotencyKey())
        .map(key -> new IdempotencyState(key, Status.PENDING));
  }

  /**
   * Applies this state to the given entity, setting its idempotency fields.
   *
   * <p>This method updates the entity's idempotency key and hit status based on the current state.
   * The state must not be PENDING when calling this method.
   *
   * @param entity the entity to update, or null to skip the operation
   * @throws IllegalStateException if this state is PENDING
   * @throws IllegalArgumentException if the entity's idempotency key doesn't match this state's
   *     key
   */
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

  /**
   * Creates a new state with status CREATED, indicating the resource was newly created.
   *
   * @return a new IdempotencyState with the same key and CREATED status
   */
  public IdempotencyState toCreated() {
    return new IdempotencyState(idempotencyKey, Status.CREATED);
  }

  /**
   * Creates a new state with status PREVIOUSLY_CREATED, indicating a duplicate request.
   *
   * @return a new IdempotencyState with the same key and PREVIOUSLY_CREATED status
   */
  public IdempotencyState toPreviouslyCreated() {
    return new IdempotencyState(idempotencyKey, Status.PREVIOUSLY_CREATED);
  }

  private IdempotencyState(String idempotencyKey, Status status) {
    this.idempotencyKey = idempotencyKey;
    this.status = status;
  }
}
