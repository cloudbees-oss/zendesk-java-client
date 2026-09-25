package org.zendesk.client.v2;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.*;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.nio.file.Files;
import java.nio.file.Path;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.zendesk.client.v2.model.Attachment;

public class AttachmentDownloadTest {
  @Rule public WireMockRule api = new WireMockRule(options().dynamicPort());
  @Rule public TemporaryFolder files = new TemporaryFolder();
  private static final byte[] BINARY = {0, -1, -128, 13, 10, 1, 65};
  private static final String TOKEN = "attachment-test-access-token";

  private String base() {
    return "http://localhost:" + api.port();
  }

  private Zendesk oauth() {
    return new Zendesk.Builder(base())
        .setOauthClientCredentials("test-client", "test-secret", "read")
        .build();
  }

  private void token(String token, int expires) {
    api.stubFor(
        post(urlEqualTo("/oauth/tokens"))
            .willReturn(
                okJson(
                    "{\"access_token\":\""
                        + token
                        + "\",\"token_type\":\"bearer\",\"expires_in\":"
                        + expires
                        + "}")));
  }

  private void metadata(String url) {
    api.stubFor(
        get(urlEqualTo("/api/v2/attachments/7.json"))
            .willReturn(
                okJson(
                    "{\"attachment\":{\"id\":7,\"file_name\":\"binary.dat\",\"content_url\":\""
                        + url
                        + "\"}}")));
  }

  private void content(int status) {
    api.stubFor(
        get(urlEqualTo("/binary"))
            .willReturn(
                aResponse()
                    .withStatus(status)
                    .withHeader("Retry-After", "0")
                    .withHeader("Content-Type", "application/octet-stream")
                    .withBody(status == 200 ? BINARY : new byte[] {69, 114, 114})));
  }

  @Test
  public void refreshesMetadataCopiesBytesCreatesParentsOverwritesAndReusesToken()
      throws Exception {
    token(TOKEN, 3600);
    metadata(base() + "/binary");
    content(200);
    Attachment stale = new Attachment();
    stale.setId(7L);
    stale.setContentUrl(base() + "/stale-url");
    Path target = files.getRoot().toPath().resolve("nested/binary.dat");
    try (Zendesk zd = oauth()) {
      assertSame(target, zd.downloadAttachment(stale, target));
      assertArrayEquals(BINARY, Files.readAllBytes(target));
      Files.write(target, new byte[] {9, 9});
      assertSame(target, zd.downloadAttachment(7L, target));
      assertArrayEquals(BINARY, Files.readAllBytes(target));
    }
    api.verify(
        1,
        postRequestedFor(urlEqualTo("/oauth/tokens"))
            .withRequestBody(matchingJsonPath("$.grant_type", equalTo("client_credentials"))));
    api.verify(
        2,
        getRequestedFor(urlEqualTo("/api/v2/attachments/7.json"))
            .withHeader("Authorization", equalTo("Bearer " + TOKEN)));
    api.verify(
        2,
        getRequestedFor(urlEqualTo("/binary"))
            .withHeader("Authorization", equalTo("Bearer " + TOKEN)));
    api.verify(0, getRequestedFor(urlEqualTo("/stale-url")));
  }

  @Test
  public void renewsExpiredTokenForMetadataAndDownload() throws Exception {
    token(TOKEN, 1);
    metadata(base() + "/binary");
    content(200);
    Path target = files.getRoot().toPath().resolve("renew.dat");
    try (Zendesk zd = oauth()) {
      zd.downloadAttachment(7L, target);
      Thread.sleep(1100);
      token("renewed-test-token", 3600);
      zd.downloadAttachment(7L, target);
    }
    api.verify(2, postRequestedFor(urlEqualTo("/oauth/tokens")));
    api.verify(
        getRequestedFor(urlEqualTo("/api/v2/attachments/7.json"))
            .withHeader("Authorization", equalTo("Bearer renewed-test-token")));
    api.verify(
        getRequestedFor(urlEqualTo("/binary"))
            .withHeader("Authorization", equalTo("Bearer renewed-test-token")));
  }

  @Test
  public void rejectsHttpErrorsWithoutTouchingExistingFile() throws Exception {
    token(TOKEN, 3600);
    metadata(base() + "/binary");
    Path target = files.newFile("existing").toPath();
    byte[] original = {5, 6};
    Files.write(target, original);
    try (Zendesk zd = oauth()) {
      for (int status : new int[] {401, 403, 404, 429, 500}) {
        content(status);
        ZendeskResponseException failure =
            assertThrows(ZendeskResponseException.class, () -> zd.downloadAttachment(7L, target));
        assertEquals(status, failure.getStatusCode());
        assertFalse(failure.toString().contains(TOKEN));
        assertFalse(failure.toString().contains("test-secret"));
        if (status == 429) assertTrue(failure instanceof ZendeskResponseRateLimitException);
        assertArrayEquals(original, Files.readAllBytes(target));
      }
    }
  }

  @Test
  public void rejectsInvalidArgumentsBeforeNetworkIo() throws Exception {
    try (Zendesk zd = oauth()) {
      Path target = files.getRoot().toPath().resolve("invalid");
      assertThrows(
          NullPointerException.class, () -> zd.downloadAttachment((Attachment) null, target));
      assertThrows(
          IllegalArgumentException.class, () -> zd.downloadAttachment(new Attachment(), target));
      assertThrows(IllegalArgumentException.class, () -> zd.downloadAttachment(0L, target));
      assertThrows(IllegalArgumentException.class, () -> zd.downloadAttachment(-1L, target));
      assertThrows(NullPointerException.class, () -> zd.downloadAttachment(7L, null));
    }
    api.verify(0, postRequestedFor(urlEqualTo("/oauth/tokens")));
  }

  @Test
  public void rejectsMissingOrBlankContentUrl() throws Exception {
    token(TOKEN, 3600);
    try (Zendesk zd = oauth()) {
      for (String body :
          new String[] {
            "{\"attachment\":{\"id\":7}}", "{\"attachment\":{\"id\":7,\"content_url\":\"   \"}}"
          }) {
        api.stubFor(get(urlEqualTo("/api/v2/attachments/7.json")).willReturn(okJson(body)));
        Path target = files.getRoot().toPath().resolve("absent/file");
        assertThrows(IllegalArgumentException.class, () -> zd.downloadAttachment(7L, target));
        assertFalse(Files.exists(target.getParent()));
      }
    }
  }

  @Test
  public void missingMetadataIsNotASuccessfulDownload() throws Exception {
    token(TOKEN, 3600);
    api.stubFor(
        get(urlEqualTo("/api/v2/attachments/7.json")).willReturn(aResponse().withStatus(404)));
    try (Zendesk zd = oauth()) {
      Path target = files.getRoot().toPath().resolve("missing");
      assertEquals(
          404,
          assertThrows(ZendeskResponseException.class, () -> zd.downloadAttachment(7L, target))
              .getStatusCode());
      assertFalse(Files.exists(target));
    }
  }

  @Test
  public void followsRedirectsWithDefaultClient() throws Exception {
    token(TOKEN, 3600);
    metadata(base() + "/redirect");
    api.stubFor(
        get(urlEqualTo("/redirect"))
            .willReturn(aResponse().withStatus(302).withHeader("Location", base() + "/binary")));
    content(200);
    try (Zendesk zd = oauth()) {
      Path target = files.getRoot().toPath().resolve("redirect");
      zd.downloadAttachment(7L, target);
      assertArrayEquals(BINARY, Files.readAllBytes(target));
    }
    api.verify(
        getRequestedFor(urlEqualTo("/binary"))
            .withHeader("Authorization", equalTo("Bearer " + TOKEN)));
  }

  @Test
  public void followsRedirectToAnotherHostUsingTheSameClient() throws Exception {
    token(TOKEN, 3600);
    metadata(base() + "/redirect");
    api.stubFor(
        get(urlEqualTo("/redirect"))
            .willReturn(
                aResponse()
                    .withStatus(302)
                    .withHeader("Location", "http://127.0.0.1:" + api.port() + "/binary")));
    content(200);
    try (Zendesk zd = oauth()) {
      Path target = files.getRoot().toPath().resolve("other-host");
      zd.downloadAttachment(7L, target);
      assertArrayEquals(BINARY, Files.readAllBytes(target));
    }
    api.verify(
        getRequestedFor(urlEqualTo("/binary"))
            .withHeader("Host", equalTo("127.0.0.1:" + api.port()))
            .withHeader("Authorization", absent()));
  }

  @Test
  public void sameHostDifferentPortDoesNotReceiveAuthorization() throws Exception {
    token(TOKEN, 3600);
    com.github.tomakehurst.wiremock.WireMockServer cdn =
        new com.github.tomakehurst.wiremock.WireMockServer(options().dynamicPort());
    cdn.start();
    try {
      metadata(base() + "/redirect");
      api.stubFor(
          get(urlEqualTo("/redirect"))
              .willReturn(
                  aResponse()
                      .withStatus(302)
                      .withHeader("Location", "http://localhost:" + cdn.port() + "/binary")));
      cdn.stubFor(get(urlEqualTo("/binary")).willReturn(ok().withBody(BINARY)));
      try (Zendesk zd = oauth()) {
        Path target = files.getRoot().toPath().resolve("port-change");
        zd.downloadAttachment(7L, target);
        assertArrayEquals(BINARY, Files.readAllBytes(target));
      }
      api.verify(
          getRequestedFor(urlEqualTo("/redirect"))
              .withHeader("Authorization", equalTo("Bearer " + TOKEN)));
      cdn.verify(getRequestedFor(urlEqualTo("/binary")).withHeader("Authorization", absent()));
    } finally {
      cdn.stop();
    }
  }

  @Test
  public void directExternalContentUrlDoesNotReceiveZendeskCredentials() throws Exception {
    token(TOKEN, 3600);
    metadata("http://127.0.0.1:" + api.port() + "/binary");
    content(200);
    try (Zendesk zd = oauth()) {
      zd.downloadAttachment(7L, files.getRoot().toPath().resolve("cdn"));
    }
    api.verify(getRequestedFor(urlEqualTo("/binary")).withHeader("Authorization", absent()));
    api.verify(
        getRequestedFor(urlEqualTo("/api/v2/attachments/7.json"))
            .withHeader("Authorization", equalTo("Bearer " + TOKEN)));
  }

  @Test
  public void schemeChangeAndTlsDowngradeNeverForwardAuthorization() throws Exception {
    com.github.tomakehurst.wiremock.WireMockServer tls =
        new com.github.tomakehurst.wiremock.WireMockServer(
            options().dynamicPort().dynamicHttpsPort());
    tls.start();
    String secure = "https://localhost:" + tls.httpsPort();
    try (DefaultAsyncHttpClient http =
        new DefaultAsyncHttpClient(
            new DefaultAsyncHttpClientConfig.Builder()
                .setFollowRedirect(true)
                .setMaxRequestRetry(0)
                .setUseInsecureTrustManager(true)
                .build())) {
      // Trust the local WireMock test certificate only, never a production setting.
      metadata(base() + "/redirect");
      api.stubFor(
          get(urlEqualTo("/redirect"))
              .willReturn(aResponse().withStatus(302).withHeader("Location", secure + "/binary")));
      tls.stubFor(get(urlEqualTo("/binary")).willReturn(ok().withBody(BINARY)));
      try (Zendesk zd = new Zendesk.Builder(base()).setClient(http).setOauthToken(TOKEN).build()) {
        zd.downloadAttachment(7, files.getRoot().toPath().resolve("upgrade"));
      }
      api.verify(
          getRequestedFor(urlEqualTo("/redirect"))
              .withHeader("Authorization", equalTo("Bearer " + TOKEN)));
      tls.verify(getRequestedFor(urlEqualTo("/binary")).withHeader("Authorization", absent()));
      tls.stubFor(
          get(urlEqualTo("/api/v2/attachments/7.json"))
              .willReturn(
                  okJson(
                      "{\"attachment\":{\"id\":7,\"content_url\":\"" + secure + "/redirect\"}}")));
      tls.stubFor(
          get(urlEqualTo("/redirect"))
              .willReturn(aResponse().withStatus(302).withHeader("Location", base() + "/binary")));
      content(200);
      try (Zendesk zd = new Zendesk.Builder(secure).setClient(http).setOauthToken(TOKEN).build()) {
        zd.downloadAttachment(7, files.getRoot().toPath().resolve("downgrade"));
      }
      tls.verify(
          getRequestedFor(urlEqualTo("/redirect"))
              .withHeader("Authorization", equalTo("Bearer " + TOKEN)));
      api.verify(getRequestedFor(urlEqualTo("/binary")).withHeader("Authorization", absent()));
    } finally {
      tls.stop();
    }
  }

  @Test
  public void downloadDoesNotLogCredentialsOrBinaryBody() throws Exception {
    token(TOKEN, 3600);
    metadata(base() + "/binary");
    content(200);
    java.io.ByteArrayOutputStream captured = new java.io.ByteArrayOutputStream();
    java.io.PrintStream previous = System.err;
    try (java.io.PrintStream capture = new java.io.PrintStream(captured, true, "UTF-8")) {
      System.setErr(capture);
      try (Zendesk zd = oauth()) {
        zd.downloadAttachment(7L, files.getRoot().toPath().resolve("logging"));
      } finally {
        System.setErr(previous);
      }
    }
    String log = captured.toString("UTF-8");
    assertFalse(log.contains(TOKEN));
    assertFalse(log.contains("test-secret"));
    assertFalse(log.contains("Authorization: Bearer"));
  }

  @Test
  public void fileWriteFailuresArePropagated() throws Exception {
    token(TOKEN, 3600);
    metadata(base() + "/binary");
    content(200);
    Path parentFile = files.newFile("not-a-directory").toPath();
    try (Zendesk zd = oauth()) {
      assertThrows(
          java.io.IOException.class, () -> zd.downloadAttachment(7L, parentFile.resolve("child")));
    }
  }

  @Test
  public void respectsInjectedClientRedirectPolicyAndConfiguredHeaders() throws Exception {
    metadata(base() + "/redirect");
    api.stubFor(
        get(urlEqualTo("/redirect"))
            .willReturn(aResponse().withStatus(302).withHeader("Location", base() + "/binary")));
    try (DefaultAsyncHttpClient http =
            new DefaultAsyncHttpClient(
                new DefaultAsyncHttpClientConfig.Builder().setFollowRedirect(false).build());
        Zendesk zd =
            new Zendesk.Builder(base())
                .setClient(http)
                .setOauthToken(TOKEN)
                .addHeader("X-Test", "shared")
                .build()) {
      Path target = files.getRoot().toPath().resolve("redirect");
      assertEquals(
          302,
          assertThrows(ZendeskResponseException.class, () -> zd.downloadAttachment(7L, target))
              .getStatusCode());
      assertFalse(Files.exists(target));
    }
    api.verify(getRequestedFor(urlEqualTo("/redirect")).withHeader("X-Test", equalTo("shared")));
    api.verify(0, getRequestedFor(urlEqualTo("/binary")));
  }
}
