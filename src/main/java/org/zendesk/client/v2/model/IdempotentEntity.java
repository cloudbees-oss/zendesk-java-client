package org.zendesk.client.v2.model;

/**
 * Interface for entities that support idempotent operations.
 *
 * <p>Entities implementing this interface can be created with an idempotency key to prevent
 * duplicate resource creation. The Zendesk API uses idempotency keys to safely handle retries of
 * create operations.
 *
 * <p>Usage example:
 *
 * <pre>{@code
 * Ticket ticket = new Ticket();
 * ticket.setSubject("Help needed");
 * ticket.setIdempotencyKey("unique-key-123"); // Prevents duplicate ticket creation
 * Ticket created = zendesk.createTicket(ticket);
 * if (Boolean.TRUE.equals(created.getIsIdempotencyHit())) {
 *   // This ticket was already created with this key
 * }
 * }</pre>
 *
 * @see <a href="https://developer.zendesk.com/api-reference/ticketing/introduction/#idempotency">
 *     Zendesk API Idempotency</a>
 * @since 1.5.0
 */
public interface IdempotentEntity {

  /**
   * Gets the idempotency key for this entity.
   *
   * @return the idempotency key, or null if not set
   */
  String getIdempotencyKey();

  /**
   * Sets the idempotency key for this entity.
   *
   * <p>The idempotency key should be a unique string (e.g., a UUID) that identifies this specific
   * create operation. If a request with the same key is retried, the API will return the
   * previously created resource instead of creating a duplicate.
   *
   * @param idempotencyKey the idempotency key to use, or null to disable idempotency
   */
  void setIdempotencyKey(String idempotencyKey);

  /**
   * Indicates whether this entity was retrieved from a previous idempotent request.
   *
   * <p>After a successful create operation, this field will be:
   *
   * <ul>
   *   <li>{@code false} if the resource was newly created (idempotency key miss)
   *   <li>{@code true} if the resource was previously created (idempotency key hit)
   *   <li>{@code null} if idempotency was not used or the API didn't return the header
   * </ul>
   *
   * @return true if this is a duplicate request, false if newly created, null if unknown
   */
  Boolean getIsIdempotencyHit();

  /**
   * Sets whether this entity was retrieved from a previous idempotent request.
   *
   * <p>This is typically set automatically by the SDK based on response headers.
   *
   * @param isIdempotencyHit true if this is a duplicate request, false if newly created, null if
   *     unknown
   */
  void setIsIdempotencyHit(Boolean isIdempotencyHit);
}
