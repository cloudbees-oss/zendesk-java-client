package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * @author nkharabaruk
 * @since 22/06/2023 22:00
 */
public class TimeZone {

  private String translatedName;
  private String name;
  private String ianaName;
  private int offset;
  private String formattedOffset;

  @JsonProperty("translated_name")
  public String getTranslatedName() {
    return translatedName;
  }

  public void setTranslatedName(String translatedName) {
    this.translatedName = translatedName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("iana_name")
  public String getIanaName() {
    return ianaName;
  }

  public void setIanaName(String ianaName) {
    this.ianaName = ianaName;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  @JsonProperty("formatted_offset")
  public String getFormattedOffset() {
    return formattedOffset;
  }

  public void setFormattedOffset(String formattedOffset) {
    this.formattedOffset = formattedOffset;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TimeZone timeZone = (TimeZone) o;
    return offset == timeZone.offset
        && Objects.equals(translatedName, timeZone.translatedName)
        && Objects.equals(name, timeZone.name)
        && Objects.equals(ianaName, timeZone.ianaName)
        && Objects.equals(formattedOffset, timeZone.formattedOffset);
  }

  @Override
  public int hashCode() {
    return Objects.hash(translatedName, name, ianaName, offset, formattedOffset);
  }
}
