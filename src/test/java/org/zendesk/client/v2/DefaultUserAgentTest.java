package org.zendesk.client.v2;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class DefaultUserAgentTest {
  @Test
  public void testDefaultVersion() {
    assertThat(new DefaultUserAgent().toString()).startsWith("zendesk-java-client/");
  }

  @Test
  public void testNullVersion() {
    assertThat(new DefaultUserAgent(null).toString()).isEqualTo("zendesk-java-client");
  }

  @Test
  public void testInvalidVersion() {
    assertThat(new DefaultUserAgent("???").toString()).isEqualTo("zendesk-java-client");
  }

  @Test
  public void testSnapshotVersion() {
    assertThat(new DefaultUserAgent("0.1.2-SNAPSHOT").toString())
        .isEqualTo("zendesk-java-client/0.1.2-SNAPSHOT");
  }

  @Test
  public void testReleaseVersion() {
    assertThat(new DefaultUserAgent("0.1.2").toString()).isEqualTo("zendesk-java-client/0.1.2");
  }
}
