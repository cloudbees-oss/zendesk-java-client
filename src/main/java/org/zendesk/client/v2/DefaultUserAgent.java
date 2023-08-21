package org.zendesk.client.v2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;

public class DefaultUserAgent {
  private static final Properties ZDJ_PROPERTIES = loadProperties();
  private static final Pattern VERSION_PATTERN = Pattern.compile("[\\w-.]+");
  private final String userAgent;

  private static Properties loadProperties() {
    try (InputStream is =
        DefaultUserAgent.class.getResourceAsStream("/zendesk-java-client.properties")) {
      Properties properties = new Properties();
      properties.load(is);
      return properties;
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  public DefaultUserAgent() {
    this(ZDJ_PROPERTIES.getProperty("version"));
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
