package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.Objects;

public class RecipientAddress {

  private String url;

  private Long id;

  @JsonProperty("brand_id")
  private Long brandId;

  @JsonProperty("default")
  private boolean isDefault;

  private String name;

  private String email;

  @JsonProperty("forwarding_status")
  private String forwardingStatus;

  @JsonProperty("cname_status")
  private String cnameStatus;

  @JsonProperty("domain_verification_status")
  private String domainVerificationStatus;

  @JsonProperty("domain_verification_code")
  private String domainVerificationCode;

  @JsonProperty("created_at")
  private Date createdAt;

  @JsonProperty("updated_at")
  private Date updatedAt;

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Long getBrandId() {
    return brandId;
  }

  public void setBrandId(Long brandId) {
    this.brandId = brandId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getForwardingStatus() {
    return forwardingStatus;
  }

  public void setForwardingStatus(String forwardingStatus) {
    this.forwardingStatus = forwardingStatus;
  }

  public String getCnameStatus() {
    return cnameStatus;
  }

  public void setCnameStatus(String cnameStatus) {
    this.cnameStatus = cnameStatus;
  }

  public String getDomainVerificationStatus() {
    return domainVerificationStatus;
  }

  public void setDomainVerificationStatus(String domainVerificationStatus) {
    this.domainVerificationStatus = domainVerificationStatus;
  }

  public String getDomainVerificationCode() {
    return domainVerificationCode;
  }

  public void setDomainVerificationCode(String domainVerificationCode) {
    this.domainVerificationCode = domainVerificationCode;
  }

  public boolean isDefault() {
    return isDefault;
  }

  public void setDefault(boolean isDefault) {
    this.isDefault = isDefault;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RecipientAddress that = (RecipientAddress) o;
    return isDefault == that.isDefault
        && Objects.equals(url, that.url)
        && Objects.equals(id, that.id)
        && Objects.equals(brandId, that.brandId)
        && Objects.equals(name, that.name)
        && Objects.equals(email, that.email)
        && Objects.equals(forwardingStatus, that.forwardingStatus)
        && Objects.equals(cnameStatus, that.cnameStatus)
        && Objects.equals(domainVerificationStatus, that.domainVerificationStatus)
        && Objects.equals(domainVerificationCode, that.domainVerificationCode)
        && Objects.equals(createdAt, that.createdAt)
        && Objects.equals(updatedAt, that.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        url,
        id,
        brandId,
        isDefault,
        name,
        email,
        forwardingStatus,
        cnameStatus,
        domainVerificationStatus,
        domainVerificationCode,
        createdAt,
        updatedAt);
  }

  @Override
  public String toString() {
    return "RecipientAddress{"
        + "url='"
        + url
        + '\''
        + ", id="
        + id
        + ", brandId="
        + brandId
        + ", isDefault="
        + isDefault
        + ", name='"
        + name
        + '\''
        + ", email='"
        + email
        + '\''
        + ", forwardingStatus='"
        + forwardingStatus
        + '\''
        + ", cnameStatus='"
        + cnameStatus
        + '\''
        + ", domainVerificationStatus='"
        + domainVerificationStatus
        + '\''
        + ", domainVerificationCode='"
        + domainVerificationCode
        + '\''
        + ", createdAt="
        + createdAt
        + ", updatedAt="
        + updatedAt
        + '}';
  }
}
