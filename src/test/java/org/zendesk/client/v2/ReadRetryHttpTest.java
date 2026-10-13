package org.zendesk.client.v2;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.*;

import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.zendesk.client.v2.model.JobStatus;
import org.zendesk.client.v2.model.Ticket;
import org.zendesk.client.v2.model.TicketImport;

/**
 * Tests retry behavior against local HTTP endpoints.
 *
 * @since 1.7.0
 */
public class ReadRetryHttpTest {
  /** Local HTTP endpoint used by these tests. */
  @Rule public WireMockRule api = new WireMockRule(options().dynamicPort());

  /** Returns the URL of the local HTTP endpoint. */
  String base() {
    return "http://localhost:" + api.port();
  }

  /** Configured transport instance closed after each test. */
  private org.asynchttpclient.DefaultAsyncHttpClient http;

  /** Creates a client with retries enabled at the Zendesk read boundary. */
  Zendesk client() {
    if (http != null) http.close();
    http =
        new org.asynchttpclient.DefaultAsyncHttpClient(
            new org.asynchttpclient.DefaultAsyncHttpClientConfig.Builder()
                .setMaxRequestRetry(0)
                .setRequestTimeout(java.time.Duration.ofSeconds(2))
                .build());
    return new Zendesk.Builder(base()).setClient(http).setOauthToken("retry-test-token").build();
  }

  @org.junit.After
  /** {@inheritDoc} */
  public void closeTransport() throws Exception {
    if (http != null) http.close();
  }

  @Test
  /** Verifies retries resume at the failed page without duplicating earlier results. */
  public void retriesOnlyTheFailedCursorPageWithoutDuplicatingResults() {
    api.stubFor(
        get(urlEqualTo("/api/v2/search/export?page%5Bafter%5D=second"))
            .inScenario("page")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(aResponse().withStatus(503))
            .willSetStateTo("ready"));
    api.stubFor(
        get(urlEqualTo("/api/v2/search/export?page%5Bafter%5D=second"))
            .inScenario("page")
            .whenScenarioStateIs("ready")
            .willReturn(okJson("{\"results\":[{\"id\":2}],\"links\":{\"next\":null}}")));
    api.stubFor(
        get(urlPathEqualTo("/api/v2/search/export"))
            .withQueryParam("query", equalTo("status:solved"))
            .willReturn(
                okJson(
                    "{\"results\":[{\"id\":1}],\"links\":{\"next\":\""
                        + base()
                        + "/api/v2/search/export?page%5Bafter%5D=second\"}}")));
    try (Zendesk zd = client()) {
      List<Long> ids = new ArrayList<>();
      zd.getTicketFromSearchWithExport("status:solved", 50).forEach(t -> ids.add(t.getId()));
      assertEquals(Arrays.asList(1L, 2L), ids);
    }
    api.verify(3, getRequestedFor(urlPathEqualTo("/api/v2/search/export")));
  }

  @Test
  /** Verifies rate-limit delays are honored within the configured bound. */
  public void rateLimitBackoffUsesServerSecondsAndRefusesExcessiveWait() {
    for (int seconds : new int[] {0, 2, 61}) {
      api.resetRequests();
      api.stubFor(
          get(urlEqualTo("/api/v2/users/me.json"))
              .willReturn(aResponse().withStatus(429).withHeader("Retry-After", "" + seconds)));
      List<Long> waits = new ArrayList<>();
      try (Zendesk zd = client()) {
        // getCurrentUser itself is outside the retry allowlist: isolate backoff without sleeping.
        ZendeskResponseRateLimitException error =
            assertThrows(
                ZendeskResponseRateLimitException.class,
                () -> ReadRetry.execute(zd::getCurrentUser, waits::add));
        assertEquals(Long.valueOf(seconds), error.getRetryAfter());
      }
      assertEquals(seconds > 60 ? 0 : 2, waits.size());
      if (!waits.isEmpty()) assertEquals(Arrays.asList(seconds * 1000L, seconds * 1000L), waits);
      api.verify(seconds > 60 ? 1 : 3, getRequestedFor(urlEqualTo("/api/v2/users/me.json")));
    }
  }

  @Test
  /** Verifies date and malformed Retry-After headers never cause an early retry. */
  public void retryAfterDateOrMalformedHeaderDoesNotTriggerAnEarlyRetry() {
    String future =
        java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)
            .plusMinutes(5)
            .format(java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME);
    java.time.ZonedDateTime later =
        java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC).plusMinutes(5);
    for (String value :
        Arrays.asList(
            future,
            later.format(
                java.time.format.DateTimeFormatter.ofPattern(
                    "EEEE, dd-MMM-yy HH:mm:ss 'GMT'", java.util.Locale.US)),
            later.format(
                java.time.format.DateTimeFormatter.ofPattern(
                    "EEE MMM dd HH:mm:ss yyyy", java.util.Locale.US)),
            "invalid",
            "Sunday, 31-Feb-26 08:49:37 GMT",
            "Sun Nov  6 08:49:37 1994 trailing",
            "999999999999999999999999")) {
      api.resetRequests();
      api.stubFor(
          get(urlEqualTo("/api/v2/users/me.json"))
              .willReturn(aResponse().withStatus(429).withHeader("Retry-After", value)));
      try (Zendesk zd = client()) {
        assertThrows(
            ZendeskResponseRateLimitException.class,
            () -> ReadRetry.execute(zd::getCurrentUser, millis -> fail("Must not retry early")));
      }
      api.verify(1, getRequestedFor(urlEqualTo("/api/v2/users/me.json")));
    }
  }

  @Test
  /** Verifies every HTTP-date format is honored within the retry bound. */
  public void acceptsAllRetryAfterHttpDateFormats() {
    java.time.ZonedDateTime future =
        java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC).plusSeconds(30).withNano(0);
    for (String pattern :
        new String[] {
          "EEE, dd MMM yyyy HH:mm:ss 'GMT'",
          "EEEE, dd-MMM-yy HH:mm:ss 'GMT'",
          "EEE MMM dd HH:mm:ss yyyy"
        }) {
      String value =
          future.format(java.time.format.DateTimeFormatter.ofPattern(pattern, java.util.Locale.US));
      api.resetRequests();
      api.stubFor(
          get(urlEqualTo("/api/v2/users/me.json"))
              .willReturn(aResponse().withStatus(429).withHeader("Retry-After", value)));
      List<Long> waits = new ArrayList<>();
      try (Zendesk zd = client()) {
        ZendeskResponseRateLimitException error =
            assertThrows(
                ZendeskResponseRateLimitException.class,
                () -> ReadRetry.execute(zd::getCurrentUser, waits::add));
        long remaining = error.getRetryAfter();
        assertTrue(value, remaining > 0 && remaining <= 30);
        assertEquals(2, waits.size());
        for (long wait : waits) assertTrue(value, wait >= remaining * 1000L && wait <= 30000L);
      }
      api.verify(3, getRequestedFor(urlEqualTo("/api/v2/users/me.json")));
    }
  }

  @Test
  /** Verifies expired dates, including space-padded asctime days, permit an immediate retry. */
  public void expiredRetryAfterDatesPermitImmediateRetry() {
    for (String value :
        Arrays.asList(
            "Sun, 06 Nov 1994 08:49:37 GMT",
            "Sunday, 06-Nov-94 08:49:37 GMT",
            "Sun Nov  6 08:49:37 1994")) {
      api.stubFor(
          get(urlEqualTo("/api/v2/users/me.json"))
              .willReturn(aResponse().withStatus(429).withHeader("Retry-After", value)));
      try (Zendesk zd = client()) {
        ZendeskResponseRateLimitException error =
            assertThrows(ZendeskResponseRateLimitException.class, zd::getCurrentUser);
        assertEquals(value, Long.valueOf(0), error.getRetryAfter());
      }
    }
  }

  @Test
  /** Verifies RFC 850 two-digit years use the HTTP fifty-year window. */
  public void rfc850TwoDigitYearsUseTheHttpCenturyWindow() {
    java.time.ZonedDateTime now = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC);
    for (java.time.ZonedDateTime date : Arrays.asList(now.plusYears(49), now.minusYears(49))) {
      String value =
          date.format(
              java.time.format.DateTimeFormatter.ofPattern(
                  "EEEE, dd-MMM-yy HH:mm:ss 'GMT'", java.util.Locale.US));
      api.stubFor(
          get(urlEqualTo("/api/v2/users/me.json"))
              .willReturn(aResponse().withStatus(429).withHeader("Retry-After", value)));
      try (Zendesk zd = client()) {
        ZendeskResponseRateLimitException error =
            assertThrows(ZendeskResponseRateLimitException.class, zd::getCurrentUser);
        if (date.isAfter(now)) {
          long expected = java.time.Duration.between(now, date).getSeconds();
          assertTrue(value, Math.abs(expected - error.getRetryAfter()) <= 5);
        } else {
          assertEquals(value, Long.valueOf(0), error.getRetryAfter());
        }
      }
    }
  }

  @Test
  /** Verifies connection resets are retried only up to the attempt limit. */
  public void readConnectionResetIsRetriedButBounded() {
    api.stubFor(
        get(urlEqualTo("/api/v2/job_statuses/job.json"))
            .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));
    JobStatus job = new JobStatus();
    job.setId("job");
    try (Zendesk zd = client()) {
      assertThrows(ZendeskException.class, () -> zd.getJobStatus(job));
    }
    api.verify(3, getRequestedFor(urlEqualTo("/api/v2/job_statuses/job.json")));
  }

  @Test
  /** Verifies job status and attachment metadata use bounded retries. */
  public void jobAndMetadataUseBoundedReadRetries() {
    api.stubFor(
        get(urlEqualTo("/api/v2/job_statuses/job.json"))
            .willReturn(aResponse().withStatus(429).withHeader("Retry-After", "0")));
    api.stubFor(
        get(urlEqualTo("/api/v2/attachments/7.json"))
            .willReturn(aResponse().withStatus(429).withHeader("Retry-After", "0")));
    JobStatus job = new JobStatus();
    job.setId("job");
    try (Zendesk zd = client()) {
      assertThrows(ZendeskResponseRateLimitException.class, () -> zd.getJobStatus(job));
      assertThrows(ZendeskResponseRateLimitException.class, () -> zd.getAttachment(7));
    }
    api.verify(3, getRequestedFor(urlEqualTo("/api/v2/job_statuses/job.json")));
    api.verify(3, getRequestedFor(urlEqualTo("/api/v2/attachments/7.json")));
  }

  @Test
  /** Verifies write operations are not replayed after HTTP or transport failures. */
  public void importCreateAndUpdateAreNeverReplayedOnHttpFailureOrConnectionReset() {
    for (boolean reset : new boolean[] {false, true}) {
      api.resetRequests();
      api.stubFor(
          any(anyUrl())
              .willReturn(
                  reset
                      ? aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)
                      : aResponse().withStatus(503)));
      try (Zendesk zd = client()) {
        assertThrows(
            ZendeskException.class, () -> zd.importTickets(Arrays.asList(new TicketImport())));
        assertThrows(ZendeskException.class, () -> zd.createTickets(Arrays.asList(new Ticket())));
        Ticket ticket = new Ticket();
        ticket.setId(7L);
        assertThrows(ZendeskException.class, () -> zd.updateTickets(Arrays.asList(ticket)));
      }
      api.verify(1, postRequestedFor(urlEqualTo("/api/v2/imports/tickets/create_many.json")));
      api.verify(1, postRequestedFor(urlEqualTo("/api/v2/tickets/create_many.json")));
      api.verify(1, putRequestedFor(urlEqualTo("/api/v2/tickets/update_many.json")));
    }
  }
}
