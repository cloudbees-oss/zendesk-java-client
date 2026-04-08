package org.zendesk.client.v2.model;

/**
 * Result wrapper for idempotent API operations.
 *
 * <p>Contains the response entity and a flag indicating whether the request was a duplicate. When
 * using idempotency keys, the Zendesk API may return a cached response from a previous identical
 * request rather than creating a new resource.
 *
 * @param <T> the type of the result entity (e.g., {@link Ticket})
 * @see <a href="https://developer.zendesk.com/api-reference/ticketing/introduction/#idempotency">
 *     Zendesk API Idempotency</a>
 * @since 1.5.0
 */
public class IdempotentResult<T> {

  private final T result;
  private final boolean duplicateRequest;

  /**
   * Creates a new idempotent result.
   *
   * @param result the response entity returned by the API
   * @param duplicateRequest {@code true} if this was a duplicate request (idempotency cache hit),
   *     {@code false} if this was a new request (idempotency cache miss)
   */
  public IdempotentResult(T result, boolean duplicateRequest) {
    this.result = result;
    this.duplicateRequest = duplicateRequest;
  }

  /**
   * Returns the result entity from the API response.
   *
   * <p>This entity is returned regardless of whether the request was a duplicate or not. For
   * duplicate requests, this represents the cached response from the original request.
   *
   * @return the response entity
   */
  public T get() {
    return result;
  }

  /**
   * Returns whether this request was identified as a duplicate.
   *
   * <p>Returns {@code true} if the Zendesk API returned a cached response (indicated by the {@code
   * x-idempotency-lookup: hit} header), meaning this idempotency key was previously used and no new
   * resource was created. Returns {@code false} if this was a new request that created a new
   * resource (indicated by the {@code x-idempotency-lookup: miss} header).
   *
   * <p><b>Note:</b> If the same idempotency key is reused with different request parameters, the
   * Zendesk API will return a 400 error and a {@link
   * org.zendesk.client.v2.ZendeskResponseIdempotencyConflictException} will be thrown instead of
   * returning an {@code IdempotentResult}.
   *
   * @return {@code true} if this was a duplicate request, {@code false} otherwise
   */
  public boolean isDuplicateRequest() {
    return duplicateRequest;
  }
}
