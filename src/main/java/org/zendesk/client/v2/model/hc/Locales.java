package org.zendesk.client.v2.model.hc;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Locales {

  @JsonProperty("default_locale")
  private String defaultLocale;

  private List<String> locales;

  public String getDefaultLocale() {
    return defaultLocale;
  }

  public void setDefaultLocale(String defaultLocale) {
    this.defaultLocale = defaultLocale;
  }

  public List<String> getLocales() {
    return locales;
  }

  public void setLocales(List<String> locales) {
    this.locales = locales;
  }
}
