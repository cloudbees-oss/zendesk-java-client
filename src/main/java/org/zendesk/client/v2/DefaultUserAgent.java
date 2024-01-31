package org.zendesk.client.v2;

import java.util.regex.Pattern;

public class DefaultUserAgent {
  private static final Pattern VERSION_PATTERN = Pattern.compile("[\\w-.]+");
  private final String userAgent;

  public DefaultUserAgent() {
    this(DefaultUserAgent.class.getPackage().getImplementationVersion());
  }

  public DefaultUserAgent(String version) {
    StringBuilder sb = new StringBuilder("zendesk-java-client");
    if (version != null && VERSION_PATTERN.matcher(version).matches()) {
      sb.append('/').append(version);
    }
    this.userAgent = sb.toString();
  }

  public String toString() {
    return this.userAgent;
  }
}
