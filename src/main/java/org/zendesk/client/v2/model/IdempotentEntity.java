package org.zendesk.client.v2.model;

public interface IdempotentEntity {

  String getIdempotencyKey();

  void setIdempotencyKey(String idempotencyKey);

  Boolean getIsIdempotencyHit();

  void setIsIdempotencyHit(Boolean isIdempotencyHit);
}
