package org.zendesk.client.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Realm;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.asynchttpclient.request.body.multipart.FilePart;
import org.asynchttpclient.request.body.multipart.StringPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zendesk.client.v2.model.AgentRole;
import org.zendesk.client.v2.model.Attachment;
import org.zendesk.client.v2.model.Audit;
import org.zendesk.client.v2.model.Automation;
import org.zendesk.client.v2.model.Brand;
import org.zendesk.client.v2.model.Comment;
import org.zendesk.client.v2.model.ComplianceDeletionStatus;
import org.zendesk.client.v2.model.CustomTicketStatus;
import org.zendesk.client.v2.model.DeletedTicket;
import org.zendesk.client.v2.model.Field;
import org.zendesk.client.v2.model.Forum;
import org.zendesk.client.v2.model.Group;
import org.zendesk.client.v2.model.GroupMembership;
import org.zendesk.client.v2.model.Identity;
import org.zendesk.client.v2.model.JiraLink;
import org.zendesk.client.v2.model.JobStatus;
import org.zendesk.client.v2.model.Locale;
import org.zendesk.client.v2.model.Macro;
import org.zendesk.client.v2.model.Metric;
import org.zendesk.client.v2.model.Organization;
import org.zendesk.client.v2.model.OrganizationField;
import org.zendesk.client.v2.model.OrganizationMembership;
import org.zendesk.client.v2.model.SatisfactionRating;
import org.zendesk.client.v2.model.SearchResultEntity;
import org.zendesk.client.v2.model.SortOrder;
import org.zendesk.client.v2.model.Status;
import org.zendesk.client.v2.model.SuspendedTicket;
import org.zendesk.client.v2.model.Ticket;
import org.zendesk.client.v2.model.TicketCount;
import org.zendesk.client.v2.model.TicketForm;
import org.zendesk.client.v2.model.TicketImport;
import org.zendesk.client.v2.model.TicketPage;
import org.zendesk.client.v2.model.TicketResult;
import org.zendesk.client.v2.model.TimeZone;
import org.zendesk.client.v2.model.Topic;
import org.zendesk.client.v2.model.Trigger;
import org.zendesk.client.v2.model.TwitterMonitor;
import org.zendesk.client.v2.model.User;
import org.zendesk.client.v2.model.UserField;
import org.zendesk.client.v2.model.UserProfile;
import org.zendesk.client.v2.model.UserRelatedInfo;
import org.zendesk.client.v2.model.View;
import org.zendesk.client.v2.model.dynamic.DynamicContentItem;
import org.zendesk.client.v2.model.dynamic.DynamicContentItemVariant;
import org.zendesk.client.v2.model.hc.Article;
import org.zendesk.client.v2.model.hc.ArticleAttachments;
import org.zendesk.client.v2.model.hc.Category;
import org.zendesk.client.v2.model.hc.ContentTag;
import org.zendesk.client.v2.model.hc.Locales;
import org.zendesk.client.v2.model.hc.PermissionGroup;
import org.zendesk.client.v2.model.hc.Section;
import org.zendesk.client.v2.model.hc.Subscription;
import org.zendesk.client.v2.model.hc.Translation;
import org.zendesk.client.v2.model.hc.UserSegment;
import org.zendesk.client.v2.model.schedules.Holiday;
import org.zendesk.client.v2.model.schedules.Schedule;
import org.zendesk.client.v2.model.targets.BasecampTarget;
import org.zendesk.client.v2.model.targets.CampfireTarget;
import org.zendesk.client.v2.model.targets.EmailTarget;
import org.zendesk.client.v2.model.targets.PivotalTarget;
import org.zendesk.client.v2.model.targets.Target;
import org.zendesk.client.v2.model.targets.TwitterTarget;
import org.zendesk.client.v2.model.targets.UrlTarget;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author stephenc
 * @since 04/04/2013 13:08
 */
public class Zendesk implements Closeable {
  private static final String JSON = "application/json; charset=UTF-8";
  private static final String USER_AGENT_HEADER = "User-Agent";
  private static final DefaultAsyncHttpClientConfig DEFAULT_ASYNC_HTTP_CLIENT_CONFIG =
      new DefaultAsyncHttpClientConfig.Builder().setFollowRedirect(true).build();
  private final boolean closeClient;
  private final AsyncHttpClient client;
  private final Realm realm;
  private final String url;
  private final String oauthToken;
  private final Map<String, String> headers;
  private final int cbpPageSize;
  private final ObjectMapper mapper;
  private final Logger logger;
  private boolean closed = false;
  private static final Map<String, Class<? extends SearchResultEntity>> searchResultTypes =
      searchResultTypes();
  private static final Map<String, Class<? extends Target>> targetTypes = targetTypes();

  private static Map<String, Class<? extends SearchResultEntity>> searchResultTypes() {
    Map<String, Class<? extends SearchResultEntity>> result = new HashMap<>();
    result.put("ticket", Ticket.class);
    result.put("user", User.class);
    result.put("group", Group.class);
    result.put("organization", Organization.class);
    result.put("topic", Topic.class);
    result.put("article", Article.class);
    return Collections.unmodifiableMap(result);
  }

  private static Map<String, Class<? extends Target>> targetTypes() {
    Map<String, Class<? extends Target>> result = new HashMap<>();
    result.put("url_target", UrlTarget.class);
    result.put("email_target", EmailTarget.class);
    result.put("basecamp_target", BasecampTarget.class);
    result.put("campfire_target", CampfireTarget.class);
    result.put("pivotal_target", PivotalTarget.class);
    result.put("twitter_target", TwitterTarget.class);

    // TODO: Implement other Target types
    // result.put("clickatell_target", ClickatellTarget.class);
    // result.put("flowdock_target", FlowdockTarget.class);
    // result.put("get_satisfaction_target", GetSatisfactionTarget.class);
    // result.put("yammer_target", YammerTarget.class);

    return Collections.unmodifiableMap(result);
  }

  private Zendesk(
      AsyncHttpClient client,
      String url,
      String username,
      String password,
      Map<String, String> headers,
      int cbpPageSize) {
    this.logger = LoggerFactory.getLogger(Zendesk.class);
    this.closeClient = client == null;
    this.oauthToken = null;
    this.client =
        client == null ? new DefaultAsyncHttpClient(DEFAULT_ASYNC_HTTP_CLIENT_CONFIG) : client;
    this.url = url.endsWith("/") ? url + "api/v2" : url + "/api/v2";
    if (username != null) {
      this.realm =
          new Realm.Builder(username, password)
              .setScheme(Realm.AuthScheme.BASIC)
              .setUsePreemptiveAuth(true)
              .build();
    } else {
      if (password != null) {
        throw new IllegalStateException(
            "Cannot specify token or password without specifying username");
      }
      this.realm = null;
    }
    headers.putIfAbsent(USER_AGENT_HEADER, new DefaultUserAgent().toString());
    this.headers = Collections.unmodifiableMap(headers);
    this.cbpPageSize = cbpPageSize;
    this.mapper = createMapper();
  }

  private Zendesk(
      AsyncHttpClient client,
      String url,
      String oauthToken,
      Map<String, String> headers,
      int cbpPageSize) {
    this.logger = LoggerFactory.getLogger(Zendesk.class);
    this.closeClient = client == null;
    this.realm = null;
    this.client =
        client == null ? new DefaultAsyncHttpClient(DEFAULT_ASYNC_HTTP_CLIENT_CONFIG) : client;
    this.url = url.endsWith("/") ? url + "api/v2" : url + "/api/v2";
    if (oauthToken != null) {
      this.oauthToken = oauthToken;
    } else {
      throw new IllegalStateException(
          "Cannot specify token or password without specifying username");
    }
    headers.putIfAbsent(USER_AGENT_HEADER, new DefaultUserAgent().toString());
    this.headers = Collections.unmodifiableMap(headers);
    this.cbpPageSize = cbpPageSize;
    this.mapper = createMapper();
  }

  //////////////////////////////////////////////////////////////////////
  // Closeable interface methods
  //////////////////////////////////////////////////////////////////////

  public boolean isClosed() {
    return closed || client.isClosed();
  }

  public void close() {
    if (closeClient && !client.isClosed()) {
      try {
        client.close();
      } catch (IOException e) {
        logger.warn("Unexpected error on client close", e);
      }
    }
    closed = true;
  }

  //////////////////////////////////////////////////////////////////////
  // Action methods
  //////////////////////////////////////////////////////////////////////

  public JobStatus getJobStatus(JobStatus status) {
    return complete(getJobStatusAsync(status));
  }

  public ListenableFuture<JobStatus> getJobStatusAsync(JobStatus status) {
    return submit(
        req("GET", tmpl("/job_statuses/{id}.json").set("id", status.getId())), handleJobStatus());
  }

  public List<JobStatus> getJobStatuses(List<JobStatus> statuses) {
    return complete(getJobStatusesAsync(statuses));
  }

  public ListenableFuture<List<JobStatus>> getJobStatusesAsync(List<JobStatus> statuses) {
    List<String> ids = new ArrayList<>(statuses.size());
    for (JobStatus status : statuses) {
      ids.add(status.getId());
    }
    Class<JobStatus> clazz = (Class<JobStatus>) (Object) JobStatus.class;
    return submit(
        req("GET", tmpl("/job_statuses/show_many.json{?ids}").set("ids", ids)),
        handleList(clazz, "job_statuses"));
  }

  public Iterable<Brand> getBrands() {
    return new PagedIterable<>(cbp("/brands.json"), handleList(Brand.class, "brands"));
  }

  public TicketForm getTicketForm(long id) {
    return complete(
        submit(
            req("GET", tmpl("/ticket_forms/{id}.json").set("id", id)),
            handle(TicketForm.class, "ticket_form")));
  }

  public List<TicketForm> getTicketForms() {
    return complete(
        submit(
            req("GET", cnst("/ticket_forms.json")), handleList(TicketForm.class, "ticket_forms")));
  }

  public TicketForm createTicketForm(TicketForm ticketForm) {
    return complete(
        submit(
            req(
                "POST",
                cnst("/ticket_forms.json"),
                JSON,
                json(Collections.singletonMap("ticket_form", ticketForm))),
            handle(TicketForm.class, "ticket_form")));
  }

  public TicketForm updateTicketForm(TicketForm ticketForm) {
    checkHasId(ticketForm);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/ticket_forms/{id}.json").set("id", ticketForm.getId()),
                JSON,
                json(Collections.singletonMap("ticket_form", ticketForm))),
            handle(TicketForm.class, "ticket_form")));
  }

  public void deleteTicketForm(TicketForm ticketForm) {
    checkHasId(ticketForm);
    deleteTicketForm(ticketForm.getId());
  }

  public void deleteTicketForm(long id) {
    complete(submit(req("DELETE", tmpl("/ticket_forms/{id}.json").set("id", id)), handleStatus()));
  }

  public Ticket importTicket(TicketImport ticketImport) {
    return complete(
        submit(
            req(
                "POST",
                cnst("/imports/tickets.json"),
                JSON,
                json(Collections.singletonMap("ticket", ticketImport))),
            handle(Ticket.class, "ticket")));
  }

  public JobStatus importTickets(TicketImport... ticketImports) {
    return importTickets(Arrays.asList(ticketImports));
  }

  public JobStatus importTickets(List<TicketImport> ticketImports) {
    return complete(importTicketsAsync(ticketImports));
  }

  public ListenableFuture<JobStatus> importTicketsAsync(List<TicketImport> ticketImports) {
    return submit(
        req(
            "POST",
            cnst("/imports/tickets/create_many.json"),
            JSON,
            json(Collections.singletonMap("tickets", ticketImports))),
        handleJobStatus());
  }

  public TicketCount getTicketsCount() {
    return complete(
        submit(req("GET", cnst("/tickets/count.json")), handle(TicketCount.class, "count")));
  }

  public TicketCount getTicketsCountForOrganization(long id) {
    return complete(
        submit(
            req("GET", tmpl("/organizations/{id}/tickets/count.json").set("id", id)),
            handle(TicketCount.class, "count")));
  }

  public TicketCount getCcdTicketsCountForUser(long id) {
    return complete(
        submit(
            req("GET", tmpl("/users/{id}/tickets/ccd/count.json").set("id", id)),
            handle(TicketCount.class, "count")));
  }

  public TicketCount getAssignedTicketsCountForUser(long id) {
    return complete(
        submit(
            req("GET", tmpl("/users/{id}/tickets/assigned/count.json").set("id", id)),
            handle(TicketCount.class, "count")));
  }

  public Ticket getTicket(long id) {
    return complete(
        submit(
            req("GET", tmpl("/tickets/{id}.json").set("id", id)), handle(Ticket.class, "ticket")));
  }

  public Iterable<Ticket> getTicketIncidents(long id) {
    return new PagedIterable<>(
        cbp("/tickets/{id}/incidents.json").set("id", id), handleList(Ticket.class, "tickets"));
  }

  public List<User> getTicketCollaborators(long id) {
    return complete(
        submit(
            req("GET", tmpl("/tickets/{id}/collaborators.json").set("id", id)),
            handleList(User.class, "users")));
  }

  /** https://developer.zendesk.com/rest_api/docs/support/tickets#list-deleted-tickets */
  public Iterable<DeletedTicket> getDeletedTickets() {
    return new PagedIterable<>(
        cbp("/deleted_tickets.json"), handleList(DeletedTicket.class, "deleted_tickets"));
  }

  /** https://developer.zendesk.com/rest_api/docs/support/tickets#list-deleted-tickets */
  public Iterable<DeletedTicket> getDeletedTickets(String sortBy, SortOrder sortOrder) {
    return new PagedIterable<>(
        tmpl("/deleted_tickets.json?sort_by={sortBy}&sort_order={sortOrder}")
            .set("sortBy", sortBy)
            .set("sortOrder", sortOrder.getQueryParameter()),
        handleList(DeletedTicket.class, "deleted_tickets"));
  }

  public void deleteTicket(Ticket ticket) {
    checkHasId(ticket);
    deleteTicket(ticket.getId());
  }

  public void deleteTicket(long id) {
    complete(submit(req("DELETE", tmpl("/tickets/{id}.json").set("id", id)), handleStatus()));
  }

  public JobStatus permanentlyDeleteTicket(Ticket ticket) {
    checkHasId(ticket);
    return permanentlyDeleteTicket(ticket.getId());
  }

  public JobStatus permanentlyDeleteTicket(long id) {
    return complete(
        submit(req("DELETE", tmpl("/deleted_tickets/{id}.json").set("id", id)), handleJobStatus()));
  }

  public ListenableFuture<JobStatus> queueCreateTicketAsync(Ticket ticket) {
    return submit(
        req(
            "POST",
            cnst("/tickets.json?async=true"),
            JSON,
            json(Collections.singletonMap("ticket", ticket))),
        handleJobStatus());
  }

  public ListenableFuture<Ticket> createTicketAsync(Ticket ticket) {
    return submit(
        req("POST", cnst("/tickets.json"), JSON, json(Collections.singletonMap("ticket", ticket))),
        handle(Ticket.class, "ticket"));
  }

  public Ticket createTicket(Ticket ticket) {
    return complete(createTicketAsync(ticket));
  }

  public JobStatus createTickets(Ticket... tickets) {
    return createTickets(Arrays.asList(tickets));
  }

  public JobStatus createTickets(List<Ticket> tickets) {
    return complete(createTicketsAsync(tickets));
  }

  public ListenableFuture<JobStatus> createTicketsAsync(List<Ticket> tickets) {
    return submit(
        req(
            "POST",
            cnst("/tickets/create_many.json"),
            JSON,
            json(Collections.singletonMap("tickets", tickets))),
        handleJobStatus());
  }

  public Ticket updateTicket(Ticket ticket) {
    checkHasId(ticket);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/tickets/{id}.json").set("id", ticket.getId()),
                JSON,
                json(Collections.singletonMap("ticket", ticket))),
            handle(Ticket.class, "ticket")));
  }

  public JobStatus updateTickets(Ticket... tickets) {
    return updateTickets(Arrays.asList(tickets));
  }

  public JobStatus updateTickets(List<Ticket> tickets) {
    return complete(updateTicketsAsync(tickets));
  }

  public ListenableFuture<JobStatus> updateTicketsAsync(List<Ticket> tickets) {
    return submit(
        req(
            "PUT",
            cnst("/tickets/update_many.json"),
            JSON,
            json(Collections.singletonMap("tickets", tickets))),
        handleJobStatus());
  }

  public void markTicketAsSpam(Ticket ticket) {
    checkHasId(ticket);
    markTicketAsSpam(ticket.getId());
  }

  public void markTicketAsSpam(long id) {
    complete(
        submit(req("PUT", tmpl("/tickets/{id}/mark_as_spam.json").set("id", id)), handleStatus()));
  }

  public JobStatus deleteTickets(long id, long... ids) {
    return complete(
        submit(
            req("DELETE", tmpl("/tickets/destroy_many.json{?ids}").set("ids", idArray(id, ids))),
            handleJobStatus()));
  }

  public JobStatus permanentlyDeleteTickets(long id, long... ids) {
    return complete(
        submit(
            req(
                "DELETE",
                tmpl("/deleted_tickets/destroy_many.json{?ids}").set("ids", idArray(id, ids))),
            handleJobStatus()));
  }

  public Iterable<Ticket> getTickets() {
    return new PagedIterable<>(cbp("/tickets.json"), handleList(Ticket.class, "tickets"));
  }

  /**
   * @deprecated This API is no longer available from the vendor. Use the {@link
   *     #getTicketsFromSearch(String)} method instead
   * @param ticketStatus
   * @return
   */
  @Deprecated
  public Iterable<Ticket> getTicketsByStatus(Status... ticketStatus) {
    return new PagedIterable<>(
        tmpl("/tickets.json{?status}").set("status", statusArray(ticketStatus)),
        handleList(Ticket.class, "tickets"));
  }

  public Iterable<Ticket> getTicketsByExternalId(String externalId, boolean includeArchived) {
    Iterable<Ticket> results =
        new PagedIterable<>(
            tmpl("/tickets.json{?external_id}").set("external_id", externalId),
            handleList(Ticket.class, "tickets"));

    if (!includeArchived || results.iterator().hasNext()) {
      return results;
    }
    return new PagedIterable<>(
        tmpl("/search.json{?query}{&type}")
            .set("query", "external_id:" + externalId)
            .set("type", "ticket"),
        handleList(Ticket.class, "results"));
  }

  public Iterable<Ticket> getTicketsByExternalId(String externalId) {
    return getTicketsByExternalId(externalId, false);
  }

  public Iterable<Ticket> getTicketsFromSearch(String searchTerm) {
    return new PagedIterable<>(
        tmpl("/search.json{?query}").set("query", searchTerm + " type:ticket"),
        handleList(Ticket.class, "results"));
  }

  public Iterable<Article> getArticleFromSearch(String searchTerm) {
    return new PagedIterable<>(
        tmpl("/help_center/articles/search.json{?query}").set("query", searchTerm),
        handleList(Article.class, "results"));
  }

  public Iterable<Article> getArticleFromSearch(String searchTerm, Long sectionId) {
    return new PagedIterable<>(
        tmpl("/help_center/articles/search.json{?section,query}")
            .set("query", searchTerm)
            .set("section", sectionId),
        handleList(Article.class, "results"));
  }

  public Iterable<Article> getArticlesFromAnyLabels(List<String> labels) {
    return new PagedIterable<>(
        tmpl("/help_center/articles/search.json{?label_names}").set("label_names", labels),
        handleList(Article.class, "results"));
  }

  public Iterable<Article> getArticlesFromAllLabels(List<String> labels) {
    return new PagedIterable<>(
        tmpl("/help_center/en-us/articles.json{?label_names}").set("label_names", labels),
        handleList(Article.class, "articles"));
  }

  public List<ArticleAttachments> getAttachmentsFromArticle(Long articleID) {
    return complete(
        submit(
            req("GET", tmpl("/help_center/articles/{id}/attachments.json").set("id", articleID)),
            handleArticleAttachmentsList("article_attachments")));
  }

  public List<Ticket> getTickets(long id, long... ids) {
    return complete(
        submit(
            req("GET", tmpl("/tickets/show_many.json{?ids}").set("ids", idArray(id, ids))),
            handleList(Ticket.class, "tickets")));
  }

  public Iterable<Ticket> getRecentTickets() {
    return new PagedIterable<>(cnst("/tickets/recent.json"), handleList(Ticket.class, "tickets"));
  }

  /** https://developer.zendesk.com/rest_api/docs/support/incremental_export */
  public Iterable<Ticket> getTicketsIncrementally(Date startTime) {
    return new PagedIterable<>(
        tmpl("/incremental/tickets.json{?start_time}")
            .set("start_time", msToSeconds(startTime.getTime())),
        handleIncrementalList(Ticket.class, "tickets"));
  }

  /**
   * https://developer.zendesk.com/rest_api/docs/support/incremental_export
   *
   * @deprecated incremental export does not support an end_time parameter
   */
  @Deprecated
  public Iterable<Ticket> getTicketsIncrementally(Date startTime, Date endTime) {
    return new PagedIterable<>(
        tmpl("/incremental/tickets.json{?start_time,end_time}")
            .set("start_time", msToSeconds(startTime.getTime()))
            .set("end_time", msToSeconds(endTime.getTime())),
        handleIncrementalList(Ticket.class, "tickets"));
  }

  public Iterable<Ticket> getOrganizationTickets(long organizationId) {
    return new PagedIterable<>(
        tmpl("/organizations/{organizationId}/tickets.json").set("organizationId", organizationId),
        handleList(Ticket.class, "tickets"));
  }

  public Iterable<org.zendesk.client.v2.model.Request> getOrganizationRequests(
      long organizationId) {
    return new PagedIterable<>(
        tmpl("/organizations/{organizationId}/requests.json").set("organizationId", organizationId),
        handleList(org.zendesk.client.v2.model.Request.class, "requests"));
  }

  public Iterable<Ticket> getUserRequestedTickets(long userId) {
    return new PagedIterable<>(
        tmpl("/users/{userId}/tickets/requested.json").set("userId", userId),
        handleList(Ticket.class, "tickets"));
  }

  public Iterable<ComplianceDeletionStatus> getComplianceDeletionStatuses(long userId) {
    return new PagedIterable<>(
        tmpl("/users/{userId}/compliance_deletion_statuses.json").set("userId", userId),
        handleList(ComplianceDeletionStatus.class, "compliance_deletion_statuses"));
  }

  public Iterable<CustomTicketStatus> getCustomTicketStatuses() {
    return new PagedIterable<>(
        tmpl("/custom_statuses.json"), handleList(CustomTicketStatus.class, "custom_statuses"));
  }

  public Iterable<Ticket> getUserCCDTickets(long userId) {
    return new PagedIterable<>(
        tmpl("/users/{userId}/tickets/ccd.json").set("userId", userId),
        handleList(Ticket.class, "tickets"));
  }

  public UserRelatedInfo getUserRelatedInfo(long userId) {
    return complete(
        submit(
            req("GET", tmpl("/users/{userId}/related.json").set("userId", userId)),
            handle(UserRelatedInfo.class, "user_related")));
  }

  public Iterable<Metric> getTicketMetrics() {
    return new PagedIterable<>(
        cbp("/ticket_metrics.json"), handleList(Metric.class, "ticket_metrics"));
  }

  public Metric getTicketMetricByTicket(long id) {
    return complete(
        submit(
            req("GET", tmpl("/tickets/{ticketId}/metrics.json").set("ticketId", id)),
            handle(Metric.class, "ticket_metric")));
  }

  public Metric getTicketMetric(long id) {
    return complete(
        submit(
            req("GET", tmpl("/ticket_metrics/{ticketMetricId}.json").set("ticketMetricId", id)),
            handle(Metric.class, "ticket_metric")));
  }

  public Iterable<Audit> getTicketAudits(Ticket ticket) {
    checkHasId(ticket);
    return getTicketAudits(ticket.getId());
  }

  public Iterable<Audit> getTicketAudits(Long id) {
    return new PagedIterable<>(
        cbp("/tickets/{ticketId}/audits.json").set("ticketId", id),
        handleList(Audit.class, "audits"));
  }

  public Audit getTicketAudit(Ticket ticket, Audit audit) {
    checkHasId(audit);
    return getTicketAudit(ticket, audit.getId());
  }

  public Audit getTicketAudit(Ticket ticket, long id) {
    checkHasId(ticket);
    return getTicketAudit(ticket.getId(), id);
  }

  public Audit getTicketAudit(long ticketId, long auditId) {
    return complete(
        submit(
            req(
                "GET",
                tmpl("/tickets/{ticketId}/audits/{auditId}.json")
                    .set("ticketId", ticketId)
                    .set("auditId", auditId)),
            handle(Audit.class, "audit")));
  }

  public void trustTicketAudit(Ticket ticket, Audit audit) {
    checkHasId(audit);
    trustTicketAudit(ticket, audit.getId());
  }

  public void trustTicketAudit(Ticket ticket, long id) {
    checkHasId(ticket);
    trustTicketAudit(ticket.getId(), id);
  }

  public void trustTicketAudit(long ticketId, long auditId) {
    complete(
        submit(
            req(
                "PUT",
                tmpl("/tickets/{ticketId}/audits/{auditId}/trust.json")
                    .set("ticketId", ticketId)
                    .set("auditId", auditId)),
            handleStatus()));
  }

  public void makePrivateTicketAudit(Ticket ticket, Audit audit) {
    checkHasId(audit);
    makePrivateTicketAudit(ticket, audit.getId());
  }

  public void makePrivateTicketAudit(Ticket ticket, long id) {
    checkHasId(ticket);
    makePrivateTicketAudit(ticket.getId(), id);
  }

  public void makePrivateTicketAudit(long ticketId, long auditId) {
    complete(
        submit(
            req(
                "PUT",
                tmpl("/tickets/{ticketId}/audits/{auditId}/make_private.json")
                    .set("ticketId", ticketId)
                    .set("auditId", auditId)),
            handleStatus()));
  }

  public Iterable<Field> getTicketFields() {
    return new PagedIterable<>(
        cbp("/ticket_fields.json"), handleList(Field.class, "ticket_fields"));
  }

  public Field getTicketField(long id) {
    return complete(
        submit(
            req("GET", tmpl("/ticket_fields/{id}.json").set("id", id)),
            handle(Field.class, "ticket_field")));
  }

  public Field createTicketField(Field field) {
    return complete(
        submit(
            req(
                "POST",
                cnst("/ticket_fields.json"),
                JSON,
                json(Collections.singletonMap("ticket_field", field))),
            handle(Field.class, "ticket_field")));
  }

  public Field updateTicketField(Field field) {
    checkHasId(field);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/ticket_fields/{id}.json").set("id", field.getId()),
                JSON,
                json(Collections.singletonMap("ticket_field", field))),
            handle(Field.class, "ticket_field")));
  }

  public void deleteTicketField(Field field) {
    checkHasId(field);
    deleteTicket(field.getId());
  }

  public void deleteTicketField(long id) {
    complete(submit(req("DELETE", tmpl("/ticket_fields/{id}.json").set("id", id)), handleStatus()));
  }

  public Iterable<SuspendedTicket> getSuspendedTickets() {
    return new PagedIterable<>(
        cbp("/suspended_tickets.json"), handleList(SuspendedTicket.class, "suspended_tickets"));
  }

  /**
   * Recover Multiple Suspended Tickets. <a
   * href="https://developer.zendesk.com/rest_api/docs/support/suspended_tickets#recover-multiple-suspended-tickets">Accepts
   * up to 100 ticket ids.</a>
   *
   * @throws IllegalArgumentException when the number of tickets exceeds 100
   * @param tickets tickets to be recovered
   * @return recovered tickets
   */
  public Iterable<Ticket> recoverSuspendedTickets(List<SuspendedTicket> tickets) {
    if (100 < tickets.size()) {
      throw new IllegalArgumentException(
          "This endpoint accepts up to 100 tickets. Provided "
              + tickets.size()
              + " tickets.\n"
              + "https://developer.zendesk.com/rest_api/docs/support/suspended_tickets#recover-multiple-suspended-tickets");
    }
    List<Long> ids = new ArrayList<>();
    for (SuspendedTicket ticket : tickets) {
      ids.add(ticket.getId());
    }
    return complete(
        submit(
            req("PUT", tmpl("/suspended_tickets/recover_many.json{?ids}").set("ids", ids)),
            handleList(Ticket.class, "tickets")));
  }

  public void deleteSuspendedTicket(SuspendedTicket ticket) {
    checkHasId(ticket);
    deleteSuspendedTicket(ticket.getId());
  }

  public void deleteSuspendedTicket(long id) {
    complete(
        submit(req("DELETE", tmpl("/suspended_tickets/{id}.json").set("id", id)), handleStatus()));
  }

  public Attachment.Upload createUpload(String fileName, byte[] content) {
    return createUpload(null, fileName, "application/binary", content);
  }

  public Attachment.Upload createUpload(String fileName, String contentType, byte[] content) {
    return createUpload(null, fileName, contentType, content);
  }

  public Attachment.Upload createUpload(
      String token, String fileName, String contentType, byte[] content) {
    TemplateUri uri = tmpl("/uploads.json{?filename,token}").set("filename", fileName);
    if (token != null) {
      uri.set("token", token);
    }
    return complete(
        submit(req("POST", uri, contentType, content), handle(Attachment.Upload.class, "upload")));
  }

  public void associateAttachmentsToArticle(String idArticle, List<Attachment> attachments) {
    TemplateUri uri =
        tmpl("/help_center/articles/{article_id}/bulk_attachments.json")
            .set("article_id", idArticle);
    List<Long> attachmentsIds = new ArrayList<>();
    for (Attachment item : attachments) {
      attachmentsIds.add(item.getId());
    }
    complete(
        submit(
            req(
                "POST",
                uri,
                JSON,
                json(Collections.singletonMap("attachment_ids", attachmentsIds))),
            handleStatus()));
  }

  /** Create upload article with inline false */
  public ArticleAttachments createUploadArticle(long articleId, File file) throws IOException {
    return createUploadArticle(articleId, file, false);
  }

  public ArticleAttachments createUploadArticle(long articleId, File file, boolean inline)
      throws IOException {
    RequestBuilder builder =
        reqBuilder(
            "POST",
            tmpl("/help_center/articles/{id}/attachments.json").set("id", articleId).toString());
    builder.setHeader("Content-Type", "multipart/form-data");

    if (inline) builder.addBodyPart(new StringPart("inline", "true"));

    builder.addBodyPart(
        new FilePart(
            "file", file, "application/octet-stream", StandardCharsets.UTF_8, file.getName()));
    final Request req = builder.build();
    return complete(submit(req, handle(ArticleAttachments.class, "article_attachment")));
  }

  public void deleteUpload(Attachment.Upload upload) {
    checkHasToken(upload);
    deleteUpload(upload.getToken());
  }

  public void deleteUpload(String token) {
    complete(
        submit(req("DELETE", tmpl("/uploads/{token}.json").set("token", token)), handleStatus()));
  }

  public Attachment getAttachment(Attachment attachment) {
    checkHasId(attachment);
    return getAttachment(attachment.getId());
  }

  public Attachment getAttachment(long id) {
    return complete(
        submit(
            req("GET", tmpl("/attachments/{id}.json").set("id", id)),
            handle(Attachment.class, "attachment")));
  }

  public void deleteAttachment(Attachment attachment) {
    checkHasId(attachment);
    deleteAttachment(attachment.getId());
  }

  public void deleteAttachment(long id) {
    complete(submit(req("DELETE", tmpl("/attachments/{id}.json").set("id", id)), handleStatus()));
  }

  public Iterable<Target> getTargets() {
    return new PagedIterable<>(cnst("/targets.json"), handleTargetList("targets"));
  }

  public Target getTarget(long id) {
    return complete(
        submit(
            req("GET", tmpl("/targets/{id}.json").set("id", id)), handle(Target.class, "target")));
  }

  public Target createTarget(Target target) {
    return complete(
        submit(
            req(
                "POST",
                cnst("/targets.json"),
                JSON,
                json(Collections.singletonMap("target", target))),
            handle(Target.class, "target")));
  }

  public void deleteTarget(long targetId) {
    complete(submit(req("DELETE", tmpl("/targets/{id}.json").set("id", targetId)), handleStatus()));
  }

  public Iterable<Trigger> getTriggers() {
    return new PagedIterable<>(cbp("/triggers.json"), handleList(Trigger.class, "triggers"));
  }

  public Iterable<Trigger> getTriggers(
      String categoryId, boolean active, String sortBy, SortOrder sortOrder) {
    return new PagedIterable<>(
        tmpl("/triggers.json{?category_id,active,sort_by,sort_order}")
            .set("category_id", categoryId)
            .set("active", active)
            .set("sort_by", sortBy)
            .set("sort_order", sortOrder.getQueryParameter()),
        handleList(Trigger.class, "triggers"));
  }

  public Iterable<Trigger> getActiveTriggers() {
    return new PagedIterable<>(cbp("/triggers/active.json"), handleList(Trigger.class, "triggers"));
  }

  public Iterable<Trigger> searchTriggers(String query) {
    return new PagedIterable<>(
        tmpl("/triggers/search.json{?query}").set("query", query),
        handleList(Trigger.class, "triggers"));
  }

  public Iterable<Trigger> searchTriggers(
      String query, boolean active, String sortBy, SortOrder sortOrder) {
    return new PagedIterable<>(
        tmpl("/triggers/search.json{?query,active,sort_by,sort_order}")
            .set("query", query)
            .set("active", active)
            .set("sort_by", sortBy)
            .set("sort_order", sortOrder.getQueryParameter()),
        handleList(Trigger.class, "triggers"));
  }

  public Trigger getTrigger(long id) {
    return complete(
        submit(
            req("GET", tmpl("/triggers/{id}.json").set("id", id)),
            handle(Trigger.class, "trigger")));
  }

  public Trigger createTrigger(Trigger trigger) {
    return complete(
        submit(
            req(
                "POST",
                cnst("/triggers.json"),
                JSON,
                json(Collections.singletonMap("trigger", trigger))),
            handle(Trigger.class, "trigger")));
  }

  public Trigger updateTrigger(Long triggerId, Trigger trigger) {
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/triggers/{id}.json").set("id", triggerId),
                JSON,
                json(Collections.singletonMap("trigger", trigger))),
            handle(Trigger.class, "trigger")));
  }

  public void deleteTrigger(long triggerId) {
    complete(
        submit(req("DELETE", tmpl("/triggers/{id}.json").set("id", triggerId)), handleStatus()));
  }

  public Iterable<View> getViews() {
    return new PagedIterable<>(cbp("/views.json"), handleList(View.class, "views"));
  }

  public Iterable<Ticket> getView(long id) {
    return new PagedIterable<>(
        tmpl("/views/{id}/tickets.json").set("id", id), handleList(Ticket.class, "tickets"));
  }

  // Automations
  public Iterable<Automation> getAutomations() {
    return new PagedIterable<>(
        cbp("/automations.json"), handleList(Automation.class, "automations"));
  }

  public Automation getAutomation(long id) {
    return complete(
        submit(
            req("GET", tmpl("/automations/{id}.json").set("id", id)),
            handle(Automation.class, "automation")));
  }

  public Automation createAutomation(Automation automation) {
    return complete(
        submit(
            req(
                "POST",
                cnst("/automations.json"),
                JSON,
                json(Collections.singletonMap("automation", automation))),
            handle(Automation.class, "automation")));
  }

  public Automation updateAutomation(Long automationId, Automation automation) {
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/automations/{id}.json").set("id", automationId),
                JSON,
                json(Collections.singletonMap("automation", automation))),
            handle(Automation.class, "automation")));
  }

  public void deleteAutomation(long automationId) {
    complete(
        submit(
            req("DELETE", tmpl("/automations/{id}.json").set("id", automationId)), handleStatus()));
  }

  public Iterable<TwitterMonitor> getTwitterMonitors() {
    return new PagedIterable<>(
        cnst("/channels/twitter/monitored_twitter_handles.json"),
        handleList(TwitterMonitor.class, "monitored_twitter_handles"));
  }

  public Iterable<User> getUsers() {
    return new PagedIterable<>(cbp("/users.json"), handleList(User.class, "users"));
  }

  public Iterable<User> getUsersByRole(String role, String... roles) {
    // Going to have to build this URI manually, because the RFC6570 template spec doesn't support
    // variables like ?role[]=...role[]=..., which is what Zendesk requires.
    // See https://developer.zendesk.com/rest_api/docs/core/users#filters
    final StringBuilder uriBuilder =
        new StringBuilder("/users.json?page[size]=").append(cbpPageSize);
    if (roles.length == 0) {
      uriBuilder.append("&role=").append(encodeUrl(role));
    } else {
      uriBuilder.append("&role[]=").append(encodeUrl(role));
    }
    for (final String curRole : roles) {
      uriBuilder.append("&role[]=").append(encodeUrl(curRole));
    }
    return new PagedIterable<>(cnst(uriBuilder.toString()), handleList(User.class, "users"));
  }

  public List<User> getUsers(long id, long... ids) {
    return complete(
        submit(
            req("GET", tmpl("/users/show_many.json{?ids}").set("ids", idArray(id, ids))),
            handleList(User.class, "users")));
  }

  /**
   * @deprecated - User externalIds are Strings in Zendesk API, not longs. Use {@link
   *     #getUsersByExternalIds(String, String...)} instead
   */
  @Deprecated
  public List<User> getUsersByExternalIds(long externalId, long... externalIds) {
    return complete(
        submit(
            req(
                "GET",
                tmpl("/users/show_many.json{?external_ids}")
                    .set("external_ids", idArray(externalId, externalIds))),
            handleList(User.class, "users")));
  }

  public List<User> getUsersByExternalIds(String externalId, String... externalIds) {
    return complete(
        submit(
            req(
                "GET",
                tmpl("/users/show_many.json{?external_ids}")
                    .set("external_ids", idArray(externalId, externalIds))),
            handleList(User.class, "users")));
  }

  public Iterable<User> getUsersIncrementally(Date startTime) {
    return new PagedIterable<>(
        tmpl("/incremental/users.json{?start_time}")
            .set("start_time", msToSeconds(startTime.getTime())),
        handleIncrementalList(User.class, "users"));
  }

  public Iterable<User> getGroupUsers(long id) {
    return new PagedIterable<>(
        cbp("/groups/{id}/users.json").set("id", id), handleList(User.class, "users"));
  }

  public Iterable<User> getOrganizationUsers(long id) {
    return new PagedIterable<>(
        cbp("/organizations/{id}/users.json").set("id", id), handleList(User.class, "users"));
  }

  public User getUser(long id) {
    return complete(
        submit(req("GET", tmpl("/users/{id}.json").set("id", id)), handle(User.class, "user")));
  }

  public User getAuthenticatedUser() {
    return complete(submit(req("GET", cnst("/users/me.json")), handle(User.class, "user")));
  }

  public Iterable<UserField> getUserFields() {
    return complete(
        submit(req("GET", cnst("/user_fields.json")), handleList(UserField.class, "user_fields")));
  }

  public User createUser(User user) {
    return complete(
        submit(
            req("POST", cnst("/users.json"), JSON, json(Collections.singletonMap("user", user))),
            handle(User.class, "user")));
  }

  public User mergeUsers(long userIdThatWillRemain, long userIdThatWillBeMerged) {
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/users/{id}/merge.json").set("id", userIdThatWillBeMerged),
                JSON,
                json(
                    Collections.singletonMap(
                        "user", Collections.singletonMap("id", userIdThatWillRemain)))),
            handle(User.class, "user")));
  }

  public JobStatus createUsers(User... users) {
    return createUsers(Arrays.asList(users));
  }

  public JobStatus createUsers(List<User> users) {
    return complete(createUsersAsync(users));
  }

  public ListenableFuture<JobStatus> createUsersAsync(List<User> users) {
    return submit(
        req(
            "POST",
            cnst("/users/create_many.json"),
            JSON,
            json(Collections.singletonMap("users", users))),
        handleJobStatus());
  }

  public User createOrUpdateUser(User user) {
    return complete(
        submit(
            req(
                "POST",
                cnst("/users/create_or_update.json"),
                JSON,
                json(Collections.singletonMap("user", user))),
            handle(User.class, "user")));
  }

  public JobStatus createOrUpdateUsers(User... users) {
    return createOrUpdateUsers(Arrays.asList(users));
  }

  public JobStatus createOrUpdateUsers(List<User> users) {
    return complete(createOrUpdateUsersAsync(users));
  }

  public ListenableFuture<JobStatus> createOrUpdateUsersAsync(List<User> users) {
    return submit(
        req(
            "POST",
            cnst("/users/create_or_update_many.json"),
            JSON,
            json(Collections.singletonMap("users", users))),
        handleJobStatus());
  }

  public User updateUser(User user) {
    checkHasId(user);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/users/{id}.json").set("id", user.getId()),
                JSON,
                json(Collections.singletonMap("user", user))),
            handle(User.class, "user")));
  }

  public JobStatus updateUsers(User... users) {
    return updateUsers(Arrays.asList(users));
  }

  public JobStatus updateUsers(List<User> users) {
    return complete(updateUsersAsync(users));
  }

  public ListenableFuture<JobStatus> updateUsersAsync(List<User> users) {
    return submit(
        req(
            "PUT",
            cnst("/users/update_many.json"),
            JSON,
            json(Collections.singletonMap("users", users))),
        handleJobStatus());
  }

  public void deleteUser(User user) {
    checkHasId(user);
    deleteUser(user.getId());
  }

  public void deleteUser(long id) {
    complete(submit(req("DELETE", tmpl("/users/{id}.json").set("id", id)), handleStatus()));
  }

  public JobStatus deleteUsers(long... ids) {
    return complete(
        submit(
            req("DELETE", tmpl("/users/destroy_many.json{?ids}").set("ids", ids)),
            handleJobStatus()));
  }

  public User permanentlyDeleteUser(User user) {
    checkHasId(user);
    return permanentlyDeleteUser(user.getId());
  }

  public User permanentlyDeleteUser(long id) {
    deleteUser(id);
    return complete(
        submit(req("DELETE", tmpl("/deleted_users/{id}.json").set("id", id)), handle(User.class)));
  }

  public User suspendUser(long id) {
    User user = new User();
    user.setId(id);
    user.setSuspended(true);
    return updateUser(user);
  }

  public User unsuspendUser(long id) {
    User user = new User();
    user.setId(id);
    user.setSuspended(false);
    return updateUser(user);
  }

  public Iterable<User> lookupUserByEmail(String email) {
    return new PagedIterable<>(
        tmpl("/users/search.json{?query}").set("query", email), handleList(User.class, "users"));
  }

  public Iterable<User> lookupUserByExternalId(String externalId) {
    return new PagedIterable<>(
        tmpl("/users/search.json{?external_id}").set("external_id", externalId),
        handleList(User.class, "users"));
  }

  public User getCurrentUser() {
    return complete(submit(req("GET", cnst("/users/me.json")), handle(User.class, "user")));
  }

  public void resetUserPassword(User user, String password) {
    checkHasId(user);
    resetUserPassword(user.getId(), password);
  }

  public void resetUserPassword(long id, String password) {
    complete(
        submit(
            req(
                "POST",
                tmpl("/users/{id}/password.json").set("id", id),
                JSON,
                json(Collections.singletonMap("password", password))),
            handleStatus()));
  }

  public void changeUserPassword(User user, String oldPassword, String newPassword) {
    checkHasId(user);
    Map<String, String> req = new HashMap<>();
    req.put("previous_password", oldPassword);
    req.put("password", newPassword);
    complete(
        submit(
            req("PUT", tmpl("/users/{id}/password.json").set("id", user.getId()), JSON, json(req)),
            handleStatus()));
  }

  public List<UserProfile> getUserProfilesForUser(User user) {
    return getUserProfilesForUser(user.getId());
  }

  public List<UserProfile> getUserProfilesForUser(long userId) {
    return complete(
        submit(
            req(
                "GET",
                tmpl("/users/{user_id}/profiles")
                    .set("user_id", userId)),
            handleList(UserProfile.class, "profiles")));
  }

  public UserProfile getUserProfile(UserProfile userProfile) {
    return getUserProfile(userProfile.getId());
  }

  public UserProfile getUserProfile(String userProfileId) {
    return complete(
        submit(
            req(
                "GET",
                tmpl("/user_profiles/{profile_id}")
                    .set("profile_id", userProfileId)),
            handle(UserProfile.class, "profile")));
  }

  public UserProfile getUserProfilebyIdentifier(String identifier) {
    return complete(
        submit(
            req(
                "GET",
                tmpl("/user_profiles?identifier={identifier}")
                    .set("identifier", identifier)),
            handle(UserProfile.class, "profile")));
  }

  public UserProfile createOrUpdateUserProfile(UserProfile userProfile) {
    return createOrUpdateUserProfile(userProfile,
                                     userProfile.getIdentifiers().get(0).getType(),
                                     userProfile.getIdentifiers().get(0).getValue());
  }

  public UserProfile createOrUpdateUserProfile(UserProfile userProfile, String identifierType, String identifierValue) {
    String identifier = String.format(
        "%s:%s:%s:%s",
        userProfile.getSource(),
        userProfile.getType(),
        identifierType,
        identifierValue);

    return complete(
        submit(
            req(
                "PUT",
                tmpl("/user_profiles?identifier={identifier}")
                    .set("identifier", identifier),
                JSON,
                json(Collections.singletonMap("profile", userProfile))),
            handle(UserProfile.class, "profile")));
  }

  public UserProfile updateUserProfile(UserProfile userProfile) {

    return complete(
        submit(
            req(
                "PUT",
                tmpl("/user_profiles/{profile_id}")
                    .set("profile_id", userProfile.getId()),
                JSON,
                json(Collections.singletonMap("profile", userProfile))),
            handle(UserProfile.class, "profile")));
  }

  public void deleteUserProfile(UserProfile userProfile) {
    deleteUserProfile(userProfile.getId());
  }

  public void deleteUserProfile(String userProfileId) {
    complete(
        submit(
            req(
                "DELETE",
                tmpl("/user_profiles/{profile_id}")
                    .set("profile_id", userProfileId)),
            handleStatus()));
  }

  public List<Identity> getUserIdentities(User user) {
    checkHasId(user);
    return getUserIdentities(user.getId());
  }

  public List<Identity> getUserIdentities(long userId) {
    return complete(
        submit(
            req("GET", tmpl("/users/{id}/identities.json").set("id", userId)),
            handleList(Identity.class, "identities")));
  }

  public Identity getUserIdentity(User user, Identity identity) {
    checkHasId(identity);
    return getUserIdentity(user, identity.getId());
  }

  public Identity getUserIdentity(User user, long identityId) {
    checkHasId(user);
    return getUserIdentity(user.getId(), identityId);
  }

  public Identity getUserIdentity(long userId, long identityId) {
    return complete(
        submit(
            req(
                "GET",
                tmpl("/users/{userId}/identities/{identityId}.json")
                    .set("userId", userId)
                    .set("identityId", identityId)),
            handle(Identity.class, "identity")));
  }

  public List<Identity> setUserPrimaryIdentity(User user, Identity identity) {
    checkHasId(identity);
    return setUserPrimaryIdentity(user, identity.getId());
  }

  public List<Identity> setUserPrimaryIdentity(User user, long identityId) {
    checkHasId(user);
    return setUserPrimaryIdentity(user.getId(), identityId);
  }

  public List<Identity> setUserPrimaryIdentity(long userId, long identityId) {
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/users/{userId}/identities/{identityId}/make_primary.json")
                    .set("userId", userId)
                    .set("identityId", identityId),
                JSON,
                null),
            handleList(Identity.class, "identities")));
  }

  public Identity verifyUserIdentity(User user, Identity identity) {
    checkHasId(identity);
    return verifyUserIdentity(user, identity.getId());
  }

  public Identity verifyUserIdentity(User user, long identityId) {
    checkHasId(user);
    return verifyUserIdentity(user.getId(), identityId);
  }

  public Identity verifyUserIdentity(long userId, long identityId) {
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/users/{userId}/identities/{identityId}/verify.json")
                    .set("userId", userId)
                    .set("identityId", identityId),
                JSON,
                null),
            handle(Identity.class, "identity")));
  }

  public Identity requestVerifyUserIdentity(User user, Identity identity) {
    checkHasId(identity);
    return requestVerifyUserIdentity(user, identity.getId());
  }

  public Identity requestVerifyUserIdentity(User user, long identityId) {
    checkHasId(user);
    return requestVerifyUserIdentity(user.getId(), identityId);
  }

  public Identity requestVerifyUserIdentity(long userId, long identityId) {
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/users/{userId}/identities/{identityId}/request_verification.json")
                    .set("userId", userId)
                    .set("identityId", identityId),
                JSON,
                null),
            handle(Identity.class, "identity")));
  }

  public Identity updateUserIdentity(long userId, Identity identity) {
    checkHasId(identity);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/users/{userId}/identities/{identityId}.json")
                    .set("userId", userId)
                    .set("identityId", identity.getId()),
                JSON,
                json(Collections.singletonMap("identity", identity))),
            handle(Identity.class, "identity")));
  }

  public Identity updateUserIdentity(User user, Identity identity) {
    checkHasId(user);
    return updateUserIdentity(user.getId(), identity);
  }

  public void deleteUserIdentity(User user, Identity identity) {
    checkHasId(identity);
    deleteUserIdentity(user, identity.getId());
  }

  public void deleteUserIdentity(User user, long identityId) {
    checkHasId(user);
    deleteUserIdentity(user.getId(), identityId);
  }

  public void deleteUserIdentity(long userId, long identityId) {
    complete(
        submit(
            req(
                "DELETE",
                tmpl("/users/{userId}/identities/{identityId}.json")
                    .set("userId", userId)
                    .set("identityId", identityId)),
            handleStatus()));
  }

  public Identity createUserIdentity(long userId, Identity identity) {
    return complete(
        submit(
            req(
                "POST",
                tmpl("/users/{userId}/identities.json").set("userId", userId),
                JSON,
                json(Collections.singletonMap("identity", identity))),
            handle(Identity.class, "identity")));
  }

  public Identity createUserIdentity(User user, Identity identity) {
    return complete(
        submit(
            req(
                "POST",
                tmpl("/users/{userId}/identities.json").set("userId", user.getId()),
                JSON,
                json(Collections.singletonMap("identity", identity))),
            handle(Identity.class, "identity")));
  }

  public Iterable<AgentRole> getCustomAgentRoles() {
    return new PagedIterable<>(
        cnst("/custom_roles.json"), handleList(AgentRole.class, "custom_roles"));
  }

  public Iterable<org.zendesk.client.v2.model.Request> getRequests() {
    return new PagedIterable<>(
        cbp("/requests.json"), handleList(org.zendesk.client.v2.model.Request.class, "requests"));
  }

  public Iterable<org.zendesk.client.v2.model.Request> getOpenRequests() {
    return new PagedIterable<>(
        cbp("/requests/open.json"),
        handleList(org.zendesk.client.v2.model.Request.class, "requests"));
  }

  public Iterable<org.zendesk.client.v2.model.Request> getSolvedRequests() {
    return new PagedIterable<>(
        cbp("/requests/solved.json"),
        handleList(org.zendesk.client.v2.model.Request.class, "requests"));
  }

  public Iterable<org.zendesk.client.v2.model.Request> getCCRequests() {
    return new PagedIterable<>(
        cnst("/requests/ccd.json"),
        handleList(org.zendesk.client.v2.model.Request.class, "requests"));
  }

  public Iterable<org.zendesk.client.v2.model.Request> getUserRequests(User user) {
    checkHasId(user);
    return getUserRequests(user.getId());
  }

  public Iterable<org.zendesk.client.v2.model.Request> getUserRequests(long id) {
    return new PagedIterable<>(
        tmpl("/users/{id}/requests.json").set("id", id),
        handleList(org.zendesk.client.v2.model.Request.class, "requests"));
  }

  public org.zendesk.client.v2.model.Request getRequest(long id) {
    return complete(
        submit(
            req("GET", tmpl("/requests/{id}.json").set("id", id)),
            handle(org.zendesk.client.v2.model.Request.class, "request")));
  }

  public org.zendesk.client.v2.model.Request createRequest(
      org.zendesk.client.v2.model.Request request) {
    return complete(
        submit(
            req(
                "POST",
                cnst("/requests.json"),
                JSON,
                json(Collections.singletonMap("request", request))),
            handle(org.zendesk.client.v2.model.Request.class, "request")));
  }

  public org.zendesk.client.v2.model.Request updateRequest(
      org.zendesk.client.v2.model.Request request) {
    checkHasId(request);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/requests/{id}.json").set("id", request.getId()),
                JSON,
                json(Collections.singletonMap("request", request))),
            handle(org.zendesk.client.v2.model.Request.class, "request")));
  }

  public Iterable<Comment> getRequestComments(org.zendesk.client.v2.model.Request request) {
    checkHasId(request);
    return getRequestComments(request.getId());
  }

  public Iterable<Comment> getRequestComments(long id) {
    return new PagedIterable<>(
        cbp("/requests/{id}/comments.json").set("id", id), handleList(Comment.class, "comments"));
  }

  public Iterable<Comment> getTicketComments(long id) {
    return getTicketComments(id, SortOrder.ASCENDING);
  }

  public Iterable<Comment> getTicketComments(long id, SortOrder order) {
    return new PagedIterable<>(
        tmpl("/tickets/{id}/comments.json?sort_order={order}")
            .set("id", id)
            .set("order", order.getQueryParameter()),
        handleList(Comment.class, "comments"));
  }

  public Comment getRequestComment(org.zendesk.client.v2.model.Request request, Comment comment) {
    checkHasId(comment);
    return getRequestComment(request, comment.getId());
  }

  public Comment getRequestComment(org.zendesk.client.v2.model.Request request, long commentId) {
    checkHasId(request);
    return getRequestComment(request.getId(), commentId);
  }

  public Comment getRequestComment(long requestId, long commentId) {
    return complete(
        submit(
            req(
                "GET",
                tmpl("/requests/{requestId}/comments/{commentId}.json")
                    .set("requestId", requestId)
                    .set("commentId", commentId)),
            handle(Comment.class, "comment")));
  }

  public Ticket createComment(long ticketId, Comment comment) {
    Ticket ticket = new Ticket();
    ticket.setComment(comment);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/tickets/{id}.json").set("id", ticketId),
                JSON,
                json(Collections.singletonMap("ticket", ticket))),
            handle(Ticket.class, "ticket")));
  }

  public Ticket createTicketFromTweet(long tweetId, long monitorId) {
    Map<String, Object> map = new HashMap<>();
    map.put("twitter_status_message_id", tweetId);
    map.put("monitored_twitter_handle_id", monitorId);

    return complete(
        submit(
            req(
                "POST",
                cnst("/channels/twitter/tickets.json"),
                JSON,
                json(Collections.singletonMap("ticket", map))),
            handle(Ticket.class, "ticket")));
  }

  public Iterable<Organization> getOrganizations() {
    return new PagedIterable<>(
        cbp("/organizations.json"), handleList(Organization.class, "organizations"));
  }

  public List<Organization> getOrganizations(long id, long... ids) {
    return complete(
        submit(
            req("GET", tmpl("/organizations/show_many.json{?ids}").set("ids", idArray(id, ids))),
            handleList(Organization.class, "organizations")));
  }

  public Iterable<Organization> getOrganizationsIncrementally(Date startTime) {
    return new PagedIterable<>(
        tmpl("/incremental/organizations.json{?start_time}")
            .set("start_time", msToSeconds(startTime.getTime())),
        handleIncrementalList(Organization.class, "organizations"));
  }

  public Iterable<OrganizationField> getOrganizationFields() {
    // The organization_fields api doesn't seem to support paging
    return complete(
        submit(
            req("GET", cnst("/organization_fields.json")),
            handleList(OrganizationField.class, "organization_fields")));
  }

  public Iterable<Organization> getAutoCompleteOrganizations(String name) {
    if (name == null || name.length() < 2) {
      throw new IllegalArgumentException("Name must be at least 2 characters long");
    }
    return new PagedIterable<>(
        tmpl("/organizations/autocomplete.json{?name}").set("name", name),
        handleList(Organization.class, "organizations"));
  }

  // TODO getOrganizationRelatedInformation

  public Organization getOrganization(long id) {
    return complete(
        submit(
            req("GET", tmpl("/organizations/{id}.json").set("id", id)),
            handle(Organization.class, "organization")));
  }

  public Organization createOrganization(Organization organization) {
    return complete(
        submit(
            req(
                "POST",
                cnst("/organizations.json"),
                JSON,
                json(Collections.singletonMap("organization", organization))),
            handle(Organization.class, "organization")));
  }

  public JobStatus createOrganizations(Organization... organizations) {
    return createOrganizations(Arrays.asList(organizations));
  }

  public JobStatus createOrganizations(List<Organization> organizations) {
    return complete(createOrganizationsAsync(organizations));
  }

  public ListenableFuture<JobStatus> createOrganizationsAsync(List<Organization> organizations) {
    return submit(
        req(
            "POST",
            cnst("/organizations/create_many.json"),
            JSON,
            json(Collections.singletonMap("organizations", organizations))),
        handleJobStatus());
  }

  public Organization createOrUpdateOrganization(Organization organization) {
    return complete(
        submit(
            req(
                "POST",
                cnst("/organizations/create_or_update.json"),
                JSON,
                json(Collections.singletonMap("organization", organization))),
            handle(Organization.class, "organization")));
  }

  public Organization updateOrganization(Organization organization) {
    checkHasId(organization);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/organizations/{id}.json").set("id", organization.getId()),
                JSON,
                json(Collections.singletonMap("organization", organization))),
            handle(Organization.class, "organization")));
  }

  public JobStatus updateOrganizations(Organization... organizations) {
    return updateOrganizations(Arrays.asList(organizations));
  }

  public JobStatus updateOrganizations(List<Organization> organizations) {
    return complete(updateOrganizationsAsync(organizations));
  }

  public ListenableFuture<JobStatus> updateOrganizationsAsync(List<Organization> organizations) {
    return submit(
        req(
            "PUT",
            cnst("/organizations/update_many.json"),
            JSON,
            json(Collections.singletonMap("organizations", organizations))),
        handleJobStatus());
  }

  public void deleteOrganization(Organization organization) {
    checkHasId(organization);
    deleteOrganization(organization.getId());
  }

  public void deleteOrganization(long id) {
    complete(submit(req("DELETE", tmpl("/organizations/{id}.json").set("id", id)), handleStatus()));
  }

  public JobStatus deleteOrganizations(long... ids) {
    return complete(
        submit(
            req("DELETE", tmpl("/organizations/destroy_many.json{?ids}").set("ids", ids)),
            handleJobStatus()));
  }

  public Iterable<Organization> lookupOrganizationsByExternalId(String externalId) {
    if (externalId == null || externalId.length() == 0) {
      throw new IllegalArgumentException("External ID must not be null or length 0");
    }
    return new PagedIterable<>(
        tmpl("/organizations/search.json{?external_id}").set("external_id", externalId),
        handleList(Organization.class, "organizations"));
  }

  public Iterable<OrganizationMembership> getOrganizationMemberships() {
    return new PagedIterable<>(
        cbp("/organization_memberships.json"),
        handleList(OrganizationMembership.class, "organization_memberships"));
  }

  public Iterable<OrganizationMembership> getOrganizationMembershipsForOrg(long organization_id) {
    return new PagedIterable<>(
        cbp("/organizations/{organization_id}/organization_memberships.json")
            .set("organization_id", organization_id),
        handleList(OrganizationMembership.class, "organization_memberships"));
  }

  public Iterable<OrganizationMembership> getOrganizationMembershipsForUser(long user_id) {
    return new PagedIterable<>(
        cbp("/users/{user_id}/organization_memberships.json").set("user_id", user_id),
        handleList(OrganizationMembership.class, "organization_memberships"));
  }

  public OrganizationMembership getOrganizationMembershipForUser(long user_id, long id) {
    return complete(
        submit(
            req(
                "GET",
                tmpl("/users/{user_id}/organization_memberships/{id}.json")
                    .set("user_id", user_id)
                    .set("id", id)),
            handle(OrganizationMembership.class, "organization_membership")));
  }

  public OrganizationMembership getOrganizationMembership(long id) {
    return complete(
        submit(
            req("GET", tmpl("/organization_memberships/{id}.json").set("id", id)),
            handle(OrganizationMembership.class, "organization_membership")));
  }

  public OrganizationMembership createOrganizationMembership(
      OrganizationMembership organizationMembership) {
    return complete(
        submit(
            req(
                "POST",
                cnst("/organization_memberships.json"),
                JSON,
                json(Collections.singletonMap("organization_membership", organizationMembership))),
            handle(OrganizationMembership.class, "organization_membership")));
  }

  /**
   * https://developer.zendesk.com/rest_api/docs/support/organization_memberships#create-many-memberships
   */
  public JobStatus createOrganizationMemberships(
      OrganizationMembership... organizationMemberships) {
    return createOrganizationMemberships(Arrays.asList(organizationMemberships));
  }

  /**
   * https://developer.zendesk.com/rest_api/docs/support/organization_memberships#create-many-memberships
   */
  public JobStatus createOrganizationMemberships(
      List<OrganizationMembership> organizationMemberships) {
    return complete(createOrganizationMembershipsAsync(organizationMemberships));
  }

  /**
   * https://developer.zendesk.com/rest_api/docs/support/organization_memberships#create-many-memberships
   */
  public ListenableFuture<JobStatus> createOrganizationMembershipsAsync(
      List<OrganizationMembership> organizationMemberships) {
    return submit(
        req(
            "POST",
            cnst("/organization_memberships/create_many.json"),
            JSON,
            json(Collections.singletonMap("organization_memberships", organizationMemberships))),
        handleJobStatus());
  }

  public void deleteOrganizationMembership(long id) {
    complete(
        submit(
            req("DELETE", tmpl("/organization_memberships/{id}.json").set("id", id)),
            handleStatus()));
  }

  /**
   * https://developer.zendesk.com/rest_api/docs/support/organization_memberships#bulk-delete-memberships
   */
  public void deleteOrganizationMemberships(long id, long... ids) {
    complete(
        submit(
            req(
                "DELETE",
                tmpl("/organization_memberships/destroy_many.json{?ids}")
                    .set("ids", idArray(id, ids))),
            handleStatus()));
  }

  public Iterable<Group> getGroups() {
    return new PagedIterable<>(cbp("/groups.json"), handleList(Group.class, "groups"));
  }

  public Iterable<Group> getAssignableGroups() {
    return new PagedIterable<>(cbp("/groups/assignable.json"), handleList(Group.class, "groups"));
  }

  public Group getGroup(long id) {
    return complete(
        submit(req("GET", tmpl("/groups/{id}.json").set("id", id)), handle(Group.class, "group")));
  }

  public Group createGroup(Group group) {
    return complete(
        submit(
            req("POST", cnst("/groups.json"), JSON, json(Collections.singletonMap("group", group))),
            handle(Group.class, "group")));
  }

  /**
   * This API will be removed in a future release. The API endpoint does not exist. Instead, the
   * {@link #createGroup(Group) createGroup} method should be called for each Group
   *
   * @see <a href="https://github.com/cloudbees/zendesk-java-client/issues/111">Zendesk Java Client
   *     Issue #111</a>
   */
  @Deprecated
  public List<Group> createGroups(Group... groups) {
    return createGroups(Arrays.asList(groups));
  }

  /**
   * This API will be removed in a future release. The API endpoint does not exist. Instead, the
   * {@link #createGroup(Group) createGroup} method should be called for each Group
   *
   * @see <a href="https://github.com/cloudbees/zendesk-java-client/issues/111">Zendesk Java Client
   *     Issue #111</a>
   */
  @Deprecated
  public List<Group> createGroups(List<Group> groups) {
    throw new ZendeskException("API Endpoint for createGroups does not exist.");
  }

  public Group updateGroup(Group group) {
    checkHasId(group);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/groups/{id}.json").set("id", group.getId()),
                JSON,
                json(Collections.singletonMap("group", group))),
            handle(Group.class, "group")));
  }

  public void deleteGroup(Group group) {
    checkHasId(group);
    deleteGroup(group.getId());
  }

  public void deleteGroup(long id) {
    complete(submit(req("DELETE", tmpl("/groups/{id}.json").set("id", id)), handleStatus()));
  }

  public Iterable<Macro> getMacros() {
    return new PagedIterable<>(cbp("/macros.json"), handleList(Macro.class, "macros"));
  }

  public Macro getMacro(long macroId) {

    return complete(
        submit(
            req("GET", tmpl("/macros/{id}.json").set("id", macroId)),
            handle(Macro.class, "macro")));
  }

  public Macro createMacro(Macro macro) {
    return complete(
        submit(
            req("POST", cnst("/macros.json"), JSON, json(Collections.singletonMap("macro", macro))),
            handle(Macro.class, "macro")));
  }

  public Macro updateMacro(Long macroId, Macro macro) {
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/macros/{id}.json").set("id", macroId),
                JSON,
                json(Collections.singletonMap("macro", macro))),
            handle(Macro.class, "macro")));
  }

  public Ticket macrosShowChangesToTicket(long macroId) {
    return complete(
            submit(
                req("GET", tmpl("/macros/{id}/apply.json").set("id", macroId)),
                handle(TicketResult.class, "result")))
        .getTicket();
  }

  public Ticket macrosShowTicketAfterChanges(long ticketId, long macroId) {
    return complete(
            submit(
                req(
                    "GET",
                    tmpl("/tickets/{ticket_id}/macros/{id}/apply.json")
                        .set("ticket_id", ticketId)
                        .set("id", macroId)),
                handle(TicketResult.class, "result")))
        .getTicket();
  }

  public List<String> addTagToTicket(long id, String... tags) {
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/tickets/{id}/tags.json").set("id", id),
                JSON,
                json(Collections.singletonMap("tags", tags))),
            handle(List.class, "tags")));
  }

  public List<String> addTagToTopics(long id, String... tags) {
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/topics/{id}/tags.json").set("id", id),
                JSON,
                json(Collections.singletonMap("tags", tags))),
            handle(List.class, "tags")));
  }

  public List<String> addTagToOrganisations(long id, String... tags) {
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/organizations/{id}/tags.json").set("id", id),
                JSON,
                json(Collections.singletonMap("tags", tags))),
            handle(List.class, "tags")));
  }

  public List<String> setTagOnTicket(long id, String... tags) {
    return complete(
        submit(
            req(
                "POST",
                tmpl("/tickets/{id}/tags.json").set("id", id),
                JSON,
                json(Collections.singletonMap("tags", tags))),
            handle(List.class, "tags")));
  }

  public List<String> setTagOnTopics(long id, String... tags) {
    return complete(
        submit(
            req(
                "POST",
                tmpl("/topics/{id}/tags.json").set("id", id),
                JSON,
                json(Collections.singletonMap("tags", tags))),
            handle(List.class, "tags")));
  }

  public List<String> setTagOnOrganisations(long id, String... tags) {
    return complete(
        submit(
            req(
                "POST",
                tmpl("/organizations/{id}/tags.json").set("id", id),
                JSON,
                json(Collections.singletonMap("tags", tags))),
            handle(List.class, "tags")));
  }

  public List<String> removeTagFromTicket(long id, String... tags) {
    return complete(
        submit(
            req(
                "DELETE",
                tmpl("/tickets/{id}/tags.json").set("id", id),
                JSON,
                json(Collections.singletonMap("tags", tags))),
            handle(List.class, "tags")));
  }

  public List<String> removeTagFromTopics(long id, String... tags) {
    return complete(
        submit(
            req(
                "DELETE",
                tmpl("/topics/{id}/tags.json").set("id", id),
                JSON,
                json(Collections.singletonMap("tags", tags))),
            handle(List.class, "tags")));
  }

  public List<String> removeTagFromOrganisations(long id, String... tags) {
    return complete(
        submit(
            req(
                "DELETE",
                tmpl("/organizations/{id}/tags.json").set("id", id),
                JSON,
                json(Collections.singletonMap("tags", tags))),
            handle(List.class, "tags")));
  }

  public Map getIncrementalTicketsResult(long unixEpochTime) {
    return complete(
        submit(
            req("GET", tmpl("/exports/tickets.json?start_time={time}").set("time", unixEpochTime)),
            handle(Map.class)));
  }

  public Iterable<GroupMembership> getGroupMemberships() {
    return new PagedIterable<>(
        cbp("/group_memberships.json"), handleList(GroupMembership.class, "group_memberships"));
  }

  public Iterable<GroupMembership> getGroupMembershipByUser(long user_id) {
    return new PagedIterable<>(
        cbp("/users/{user_id}/group_memberships.json").set("user_id", user_id),
        handleList(GroupMembership.class, "group_memberships"));
  }

  public Iterable<GroupMembership> getGroupMemberships(long group_id) {
    return new PagedIterable<>(
        cbp("/groups/{group_id}/memberships.json").set("group_id", group_id),
        handleList(GroupMembership.class, "group_memberships"));
  }

  public Iterable<GroupMembership> getAssignableGroupMemberships() {
    return new PagedIterable<>(
        cbp("/group_memberships/assignable.json"),
        handleList(GroupMembership.class, "group_memberships"));
  }

  public Iterable<GroupMembership> getAssignableGroupMemberships(long group_id) {
    return new PagedIterable<>(
        cbp("/groups/{group_id}/memberships/assignable.json").set("group_id", group_id),
        handleList(GroupMembership.class, "group_memberships"));
  }

  public GroupMembership getGroupMembership(long id) {
    return complete(
        submit(
            req("GET", tmpl("/group_memberships/{id}.json").set("id", id)),
            handle(GroupMembership.class, "group_membership")));
  }

  public GroupMembership getGroupMembership(long user_id, long group_membership_id) {
    return complete(
        submit(
            req(
                "GET",
                tmpl("/users/{uid}/group_memberships/{gmid}.json")
                    .set("uid", user_id)
                    .set("gmid", group_membership_id)),
            handle(GroupMembership.class, "group_membership")));
  }

  public GroupMembership createGroupMembership(GroupMembership groupMembership) {
    return complete(
        submit(
            req(
                "POST",
                cnst("/group_memberships.json"),
                JSON,
                json(Collections.singletonMap("group_membership", groupMembership))),
            handle(GroupMembership.class, "group_membership")));
  }

  public GroupMembership createGroupMembership(long user_id, GroupMembership groupMembership) {
    return complete(
        submit(
            req(
                "POST",
                tmpl("/users/{id}/group_memberships.json").set("id", user_id),
                JSON,
                json(Collections.singletonMap("group_membership", groupMembership))),
            handle(GroupMembership.class, "group_membership")));
  }

  public void deleteGroupMembership(GroupMembership groupMembership) {
    checkHasId(groupMembership);
    deleteGroupMembership(groupMembership.getId());
  }

  public void deleteGroupMembership(long id) {
    complete(
        submit(req("DELETE", tmpl("/group_memberships/{id}.json").set("id", id)), handleStatus()));
  }

  public void deleteGroupMembership(long user_id, GroupMembership groupMembership) {
    checkHasId(groupMembership);
    deleteGroupMembership(user_id, groupMembership.getId());
  }

  public void deleteGroupMembership(long user_id, long group_membership_id) {
    complete(
        submit(
            req(
                "DELETE",
                tmpl("/users/{uid}/group_memberships/{gmid}.json")
                    .set("uid", user_id)
                    .set("gmid", group_membership_id)),
            handleStatus()));
  }

  public List<GroupMembership> setGroupMembershipAsDefault(
      long user_id, GroupMembership groupMembership) {
    checkHasId(groupMembership);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/users/{uid}/group_memberships/{gmid}/make_default.json")
                    .set("uid", user_id)
                    .set("gmid", groupMembership.getId()),
                JSON,
                json(Collections.singletonMap("group_memberships", groupMembership))),
            handleList(GroupMembership.class, "results")));
  }

  public Iterable<Forum> getForums() {
    return new PagedIterable<>(cnst("/forums.json"), handleList(Forum.class, "forums"));
  }

  public List<Forum> getForums(long category_id) {
    return complete(
        submit(
            req("GET", tmpl("/categories/{id}/forums.json").set("id", category_id)),
            handleList(Forum.class, "forums")));
  }

  public Forum getForum(long id) {
    return complete(
        submit(req("GET", tmpl("/forums/{id}.json").set("id", id)), handle(Forum.class, "forum")));
  }

  public Forum createForum(Forum forum) {
    return complete(
        submit(
            req("POST", cnst("/forums.json"), JSON, json(Collections.singletonMap("forum", forum))),
            handle(Forum.class, "forum")));
  }

  public Forum updateForum(Forum forum) {
    checkHasId(forum);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/forums/{id}.json").set("id", forum.getId()),
                JSON,
                json(Collections.singletonMap("forum", forum))),
            handle(Forum.class, "forum")));
  }

  public void deleteForum(Forum forum) {
    checkHasId(forum);
    complete(
        submit(req("DELETE", tmpl("/forums/{id}.json").set("id", forum.getId())), handleStatus()));
  }

  public Iterable<Topic> getTopics() {
    return new PagedIterable<>(cnst("/topics.json"), handleList(Topic.class, "topics"));
  }

  public List<Topic> getTopics(long forum_id) {
    return complete(
        submit(
            req("GET", tmpl("/forums/{id}/topics.json").set("id", forum_id)),
            handleList(Topic.class, "topics")));
  }

  public List<Topic> getTopicsByUser(long user_id) {
    return complete(
        submit(
            req("GET", tmpl("/users/{id}/topics.json").set("id", user_id)),
            handleList(Topic.class, "topics")));
  }

  public Topic getTopic(long id) {
    return complete(
        submit(req("GET", tmpl("/topics/{id}.json").set("id", id)), handle(Topic.class, "topic")));
  }

  public Topic createTopic(Topic topic) {
    checkHasId(topic);
    return complete(
        submit(
            req("POST", cnst("/topics.json"), JSON, json(Collections.singletonMap("topic", topic))),
            handle(Topic.class, "topic")));
  }

  public Topic importTopic(Topic topic) {
    checkHasId(topic);
    return complete(
        submit(
            req(
                "POST",
                cnst("/import/topics.json"),
                JSON,
                json(Collections.singletonMap("topic", topic))),
            handle(Topic.class, "topic")));
  }

  public List<Topic> getTopics(long id, long... ids) {
    return complete(
        submit(
            req("POST", tmpl("/topics/show_many.json{?ids}").set("ids", idArray(id, ids))),
            handleList(Topic.class, "topics")));
  }

  public Topic updateTopic(Topic topic) {
    checkHasId(topic);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/topics/{id}.json").set("id", topic.getId()),
                JSON,
                json(Collections.singletonMap("topic", topic))),
            handle(Topic.class, "topic")));
  }

  public void deleteTopic(Topic topic) {
    checkHasId(topic);
    complete(
        submit(req("DELETE", tmpl("/topics/{id}.json").set("id", topic.getId())), handleStatus()));
  }

  // https://support.zendesk.com/hc/communities/public/posts/203464106-Managing-Organization-Memberships-via-the-Zendesk-API
  public Iterable<OrganizationMembership> getOrganizationMembershipByUser(long user_id) {
    return getOrganizationMembershipsForUser(user_id);
  }

  public OrganizationMembership getGroupOrganization(
      long user_id, long organization_membership_id) {
    return complete(
        submit(
            req(
                "GET",
                tmpl("/users/{uid}/organization_memberships/{oid}.json")
                    .set("uid", user_id)
                    .set("oid", organization_membership_id)),
            handle(OrganizationMembership.class, "organization_membership")));
  }

  public OrganizationMembership createOrganizationMembership(
      long user_id, OrganizationMembership organizationMembership) {
    return complete(
        submit(
            req(
                "POST",
                tmpl("/users/{id}/organization_memberships.json").set("id", user_id),
                JSON,
                json(Collections.singletonMap("organization_membership", organizationMembership))),
            handle(OrganizationMembership.class, "organization_membership")));
  }

  public void deleteOrganizationMembership(
      long user_id, OrganizationMembership organizationMembership) {
    checkHasId(organizationMembership);
    deleteOrganizationMembership(user_id, organizationMembership.getId());
  }

  public void deleteOrganizationMembership(long user_id, long organization_membership_id) {
    complete(
        submit(
            req(
                "DELETE",
                tmpl("/users/{uid}/organization_memberships/{oid}.json")
                    .set("uid", user_id)
                    .set("oid", organization_membership_id)),
            handleStatus()));
  }

  public List<OrganizationMembership> setOrganizationMembershipAsDefault(
      long user_id, OrganizationMembership organizationMembership) {
    checkHasId(organizationMembership);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/users/{uid}/organization_memberships/{omid}/make_default.json")
                    .set("uid", user_id)
                    .set("omid", organizationMembership.getId()),
                JSON,
                json(Collections.singletonMap("organization_memberships", organizationMembership))),
            handleList(OrganizationMembership.class, "results")));
  }

  // -- END BETA

  public Iterable<SearchResultEntity> getSearchResults(String query) {
    return new PagedIterable<>(
        tmpl("/search.json{?query}").set("query", query), handleSearchList("results"));
  }

  public <T extends SearchResultEntity> Iterable<T> getSearchResults(Class<T> type, String query) {
    return getSearchResults(type, query, Collections.emptyMap());
  }

  /**
   * @deprecated Use {@link #getSearchResults(Class, String, Map)} instead.
   */
  @Deprecated
  public <T extends SearchResultEntity> Iterable<T> getSearchResults(
      Class<T> type, String query, String params) {
    /*
       preserving backwards compatabile logic, this method will continue to do what it did before, which is "wrong"
       in that you can't really specify things like sort order via this method
    */

    Map<String, Object> paramsMap = new HashMap<>(1);
    paramsMap.put("params", params);

    return getSearchResults(type, query, paramsMap);
  }

  public <T extends SearchResultEntity> Iterable<T> getSearchResults(
      Class<T> type, String query, String sortBy, SortOrder sortOrder) {
    Map<String, Object> paramsMap = new HashMap<>(2);
    paramsMap.put("sort_by", sortBy);
    paramsMap.put("sort_order", sortOrder.getQueryParameter());

    return getSearchResults(type, query, paramsMap);
  }

  public <T extends SearchResultEntity> Iterable<T> getSearchResults(
      Class<T> type, String query, Map<String, Object> params) {
    String typeName = getTypeName(type);

    if (typeName == null) {
      return Collections.emptyList();
    }

    TemplateUri templateUri = getSearchUri(params, query, typeName);

    return new PagedIterable<>(templateUri, handleList(type, "results"));
  }

  /**
   * Search API implementation with pagination support.
   *
   * @param searchType type of search entity like Ticket, User etc
   * @param pageType page return type to which the search result will be deserialized
   * @param query string used filter a type given by searchType
   * @param queryParams additional parameters other than filter string like per_page, page etc
   * @param sortBy name of any field of the searchType
   * @param sortOrder sort order
   */
  public <T> Optional<T> getSearchResults(
      final Class<?> searchType,
      final Class<T> pageType,
      final String query,
      final Map<String, Object> queryParams,
      final String sortBy,
      final SortOrder sortOrder) {

    String typeName = getTypeName(searchType);

    if (typeName == null) {
      return Optional.empty();
    }

    final Map<String, Object> paramsMap = new HashMap<>();

    if (queryParams != null) {
      paramsMap.putAll(queryParams);
    }

    if (sortBy != null && sortOrder != null) {
      paramsMap.put("sort_by", sortBy);
      paramsMap.put("sort_order", sortOrder.getQueryParameter());
    }

    final TemplateUri templateUri = getSearchUri(paramsMap, query, typeName);

    return Optional.of(complete(submit(req("GET", templateUri.toString()), handle(pageType))));
  }

  /**
   * Ticket Search API implementation with pagination support.
   *
   * @param query string used filter a type given by searchType
   * @param queryParams additional parameters other than filter string like per_page, page etc
   * @param sortBy name of any field of the searchType
   * @param sortOrder sort order
   */
  public Optional<TicketPage> getSearchTicketResults(
      final String query,
      final Map<String, Object> queryParams,
      final String sortBy,
      final SortOrder sortOrder) {

    return getSearchResults(Ticket.class, TicketPage.class, query, queryParams, sortBy, sortOrder);
  }

  public void notifyApp(String json) {
    complete(submit(req("POST", cnst("/apps/notify.json"), JSON, json.getBytes()), handleStatus()));
  }

  public void updateInstallation(int id, String json) {
    complete(
        submit(
            req("PUT", tmpl("/apps/installations/{id}.json").set("id", id), JSON, json.getBytes()),
            handleStatus()));
  }

  public Iterable<SatisfactionRating> getSatisfactionRatings() {
    return new PagedIterable<>(
        cbp("/satisfaction_ratings.json"),
        handleList(SatisfactionRating.class, "satisfaction_ratings"));
  }

  public SatisfactionRating getSatisfactionRating(long id) {
    return complete(
        submit(
            req("GET", tmpl("/satisfaction_ratings/{id}.json").set("id", id)),
            handle(SatisfactionRating.class, "satisfaction_rating")));
  }

  public SatisfactionRating createSatisfactionRating(
      long ticketId, SatisfactionRating satisfactionRating) {
    return complete(
        submit(
            req(
                "POST",
                tmpl("/tickets/{ticketId}/satisfaction_rating.json").set("ticketId", ticketId),
                JSON,
                json(Collections.singletonMap("satisfaction_rating", satisfactionRating))),
            handle(SatisfactionRating.class, "satisfaction_rating")));
  }

  public SatisfactionRating createSatisfactionRating(
      Ticket ticket, SatisfactionRating satisfactionRating) {
    return createSatisfactionRating(ticket.getId(), satisfactionRating);
  }

  //////////////////////////////////////////////////////////////////////
  // Action methods for Dynamic Content - Items and Variants
  //////////////////////////////////////////////////////////////////////

  public Iterable<DynamicContentItem> getDynamicContentItems() {
    return new PagedIterable<>(
        cbp("/dynamic_content/items.json"), handleList(DynamicContentItem.class, "items"));
  }

  public DynamicContentItem getDynamicContentItem(long id) {
    return complete(
        submit(
            req("GET", tmpl("/dynamic_content/items/{id}.json").set("id", id)),
            handle(DynamicContentItem.class, "item")));
  }

  public DynamicContentItem createDynamicContentItem(DynamicContentItem item) {
    return complete(
        submit(
            req(
                "POST",
                cnst("/dynamic_content/items.json"),
                JSON,
                json(Collections.singletonMap("item", item))),
            handle(DynamicContentItem.class, "item")));
  }

  public DynamicContentItem updateDynamicContentItem(DynamicContentItem item) {
    checkHasId(item);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/dynamic_content/items/{id}.json").set("id", item.getId()),
                JSON,
                json(Collections.singletonMap("item", item))),
            handle(DynamicContentItem.class, "item")));
  }

  public void deleteDynamicContentItem(DynamicContentItem item) {
    checkHasId(item);
    complete(
        submit(
            req("DELETE", tmpl("/dynamic_content/items/{id}.json").set("id", item.getId())),
            handleStatus()));
  }

  /** VARIANTS */
  public Iterable<DynamicContentItemVariant> getDynamicContentItemVariants(
      DynamicContentItem item) {
    checkHasId(item);
    return new PagedIterable<>(
        cbp("/dynamic_content/items/{id}/variants.json").set("id", item.getId()),
        handleList(DynamicContentItemVariant.class, "variants"));
  }

  public DynamicContentItemVariant getDynamicContentItemVariant(Long itemId, long id) {
    return complete(
        submit(
            req(
                "GET",
                tmpl("/dynamic_content/items/{itemId}/variants/{id}.json")
                    .set("itemId", itemId)
                    .set("id", id)),
            handle(DynamicContentItemVariant.class, "variant")));
  }

  public DynamicContentItemVariant createDynamicContentItemVariant(
      Long itemId, DynamicContentItemVariant variant) {
    checkHasItemId(itemId);
    return complete(
        submit(
            req(
                "POST",
                tmpl("/dynamic_content/items/{id}/variants.json").set("id", itemId),
                JSON,
                json(Collections.singletonMap("variant", variant))),
            handle(DynamicContentItemVariant.class, "variant")));
  }

  public DynamicContentItemVariant updateDynamicContentItemVariant(
      Long itemId, DynamicContentItemVariant variant) {
    checkHasItemId(itemId);
    checkHasId(variant);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/dynamic_content/items/{itemId}/variants/{id}.json")
                    .set("itemId", itemId)
                    .set("id", variant.getId()),
                JSON,
                json(Collections.singletonMap("variant", variant))),
            handle(DynamicContentItemVariant.class, "variant")));
  }

  public void deleteDynamicContentItemVariant(Long itemId, DynamicContentItemVariant variant) {
    checkHasItemId(itemId);
    checkHasId(variant);
    complete(
        submit(
            req(
                "DELETE",
                tmpl("/dynamic_content/items/{itemId}/variants/{id}.json")
                    .set("itemId", itemId)
                    .set("id", variant.getId())),
            handleStatus()));
  }

  //////////////////////////////////////////////////////////////////////
  // Action methods for Locales
  //////////////////////////////////////////////////////////////////////

  /**
   * https://developer.zendesk.com/api-reference/ticketing/account-configuration/locales/#list-locales
   *
   * @return the translation locales available for the account.
   * @since FIXME
   */
  public Iterable<Locale> getLocales() {
    return new PagedIterable<>(cnst("/locales.json"), handleList(Locale.class, "locales"));
  }

  // TODO search with query building API

  //////////////////////////////////////////////////////////////////////
  // Action methods for Help Center
  //////////////////////////////////////////////////////////////////////
  /**
   * Get all permission groups
   *
   * @return List of Permission Groups
   */
  public Iterable<PermissionGroup> getPermissionGroups() {
    return new PagedIterable<>(
        cnst("/guide/permission_groups.json"),
        handleList(PermissionGroup.class, "permission_groups"));
  }

  /**
   * Get permission group by id
   *
   * @param id
   */
  public PermissionGroup getPermissionGroup(long id) {
    return complete(
        submit(
            req("GET", tmpl("/guide/permission_groups/{id}.json").set("id", id)),
            handle(PermissionGroup.class, "permission_group")));
  }

  /**
   * Create permission group
   *
   * @param permissionGroup
   */
  public PermissionGroup createPermissionGroup(PermissionGroup permissionGroup) {
    return complete(
        submit(
            req(
                "POST",
                tmpl("/guide/permission_groups.json"),
                JSON,
                json(Collections.singletonMap("permission_group", permissionGroup))),
            handle(PermissionGroup.class, "permission_group")));
  }

  /**
   * Update permission group
   *
   * @param permissionGroup
   */
  public PermissionGroup updatePermissionGroup(PermissionGroup permissionGroup) {
    checkHasId(permissionGroup);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/guide/permission_groups/{id}.json").set("id", permissionGroup.getId()),
                JSON,
                json(Collections.singletonMap("permission_group", permissionGroup))),
            handle(PermissionGroup.class, "permission_group")));
  }

  /**
   * Delete permission group
   *
   * @param permissionGroup
   */
  public void deletePermissionGroup(PermissionGroup permissionGroup) {
    checkHasId(permissionGroup);
    deletePermissionGroup(permissionGroup.getId());
  }

  /**
   * Delete permission group
   *
   * @param id
   */
  public void deletePermissionGroup(long id) {
    complete(
        submit(
            req("DELETE", tmpl("/guide/permission_groups/{id}.json").set("id", id)),
            handleStatus()));
  }

  /**
   * Get user segments
   *
   * @return List of User Segments
   */
  public Iterable<UserSegment> getUserSegments() {
    return new PagedIterable<>(
        cnst("/help_center/user_segments.json"), handleList(UserSegment.class, "user_segments"));
  }

  /**
   * Returns the list of user segments that a particular user belongs to
   *
   * @return List of User Segments
   */
  public Iterable<UserSegment> getUserSegments(long id) {
    return new PagedIterable<>(
        tmpl("/help_center/users/{id}/user_segments.json").set("id", id),
        handleList(UserSegment.class, "user_segments"));
  }

  /**
   * Request only user segments applicable on the account's current Guide plan
   *
   * @return List of User Segments
   */
  public Iterable<UserSegment> getUserSegmentsApplicable() {
    return new PagedIterable<>(
        cnst("/help_center/user_segments/applicable.json"),
        handleList(UserSegment.class, "user_segments"));
  }

  /**
   * Get user segment by id
   *
   * @param id
   */
  public UserSegment getUserSegment(long id) {
    return complete(
        submit(
            req("GET", tmpl("/help_center/user_segments/{id}.json").set("id", id)),
            handle(UserSegment.class, "user_segment")));
  }

  /**
   * List Sections using a User Segment
   *
   * @param userSegment
   * @return List of Sections
   */
  public Iterable<Section> getSections(UserSegment userSegment) {
    checkHasId(userSegment);
    return new PagedIterable<>(
        tmpl("/help_center/user_segments/{id}/sections.json").set("id", userSegment.getId()),
        handleList(Section.class, "sections"));
  }

  /**
   * List Topics using a User Segment
   *
   * @param userSegment
   * @return List of Topics
   */
  public Iterable<Topic> getTopics(UserSegment userSegment) {
    checkHasId(userSegment);
    return new PagedIterable<>(
        tmpl("/help_center/user_segments/{id}/topics.json").set("id", userSegment.getId()),
        handleList(Topic.class, "topics"));
  }

  /**
   * Create User Segment
   *
   * @param userSegment
   */
  public UserSegment createUserSegment(UserSegment userSegment) {
    return complete(
        submit(
            req(
                "POST",
                tmpl("/help_center/user_segments.json"),
                JSON,
                json(Collections.singletonMap("user_segment", userSegment))),
            handle(UserSegment.class, "user_segment")));
  }

  /**
   * Update User Segment
   *
   * @param userSegment
   */
  public UserSegment updateUserSegment(UserSegment userSegment) {
    checkHasId(userSegment);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/help_center/user_segments/{id}.json").set("id", userSegment.getId()),
                JSON,
                json(Collections.singletonMap("user_segment", userSegment))),
            handle(UserSegment.class, "user_segment")));
  }

  /**
   * Delete User Segment
   *
   * @param userSegment
   */
  public void deleteUserSegment(UserSegment userSegment) {
    checkHasId(userSegment);
    deleteUserSegment(userSegment.getId());
  }

  /**
   * Delete User Segment
   *
   * @param id
   */
  public void deleteUserSegment(long id) {
    complete(
        submit(
            req("DELETE", tmpl("/help_center/user_segments/{id}.json").set("id", id)),
            handleStatus()));
  }

  public Locales listHelpCenterLocales() {
    return complete(submit(req("GET", cnst("/help_center/locales.json")), handle(Locales.class)));
  }

  /**
   * @deprecated Use {@link Zendesk#listHelpCenterLocales()} instead
   */
  @Deprecated
  public List<String> getHelpCenterLocales() {
    return listHelpCenterLocales().getLocales();
  }

  /**
   * Get all articles from help center.
   *
   * @return List of Articles.
   */
  public Iterable<Article> getArticles() {
    return new PagedIterable<>(
        cnst("/help_center/articles.json"), handleList(Article.class, "articles"));
  }

  public Iterable<Article> getArticles(String locale) {
    return new PagedIterable<>(
        tmpl("/help_center/{locale}/articles.json").set("locale", locale),
        handleList(Article.class, "articles"));
  }

  public Iterable<Article> getArticles(Category category) {
    checkHasId(category);
    return new PagedIterable<>(
        tmpl("/help_center/categories/{id}/articles.json").set("id", category.getId()),
        handleList(Article.class, "articles"));
  }

  public Iterable<Article> getArticles(Category category, String locale) {
    checkHasId(category);
    return new PagedIterable<>(
        tmpl("/help_center/{locale}/categories/{id}/articles.json")
            .set("id", category.getId())
            .set("locale", locale),
        handleList(Article.class, "articles"));
  }

  public Iterable<Article> getArticles(Section section) {
    checkHasId(section);
    return new PagedIterable<>(
        tmpl("/help_center/sections/{id}/articles.json").set("id", section.getId()),
        handleList(Article.class, "articles"));
  }

  public Iterable<Article> getArticles(Section section, String locale) {
    checkHasId(section);
    return new PagedIterable<>(
        tmpl("/help_center/{locale}/sections/{id}/articles.json")
            .set("id", section.getId())
            .set("locale", locale),
        handleList(Article.class, "articles"));
  }

  public Iterable<Article> getArticlesIncrementally(Date startTime) {
    return new PagedIterable<>(
        tmpl("/help_center/incremental/articles.json{?start_time}")
            .set("start_time", msToSeconds(startTime.getTime())),
        handleIncrementalList(Article.class, "articles"));
  }

  public List<Article> getArticlesFromPage(int page) {
    return complete(
        submit(
            req("GET", tmpl("/help_center/articles.json?page={page}").set("page", page)),
            handleList(Article.class, "articles")));
  }

  public Article getArticle(long id) {
    return complete(
        submit(
            req("GET", tmpl("/help_center/articles/{id}.json").set("id", id)),
            handle(Article.class, "article")));
  }

  public Iterable<Translation> getArticleTranslations(Long articleId) {
    return new PagedIterable<>(
        tmpl("/help_center/articles/{articleId}/translations.json").set("articleId", articleId),
        handleList(Translation.class, "translations"));
  }

  public Translation showArticleTranslation(long articleId, String locale) {
    return complete(
        submit(
            req(
                "GET",
                tmpl("/help_center/articles/{articleId}/translations/{locale}.json")
                    .set("articleId", articleId)
                    .set("locale", locale)),
            handle(Translation.class, "translation")));
  }

  public Article createArticle(Article article) {
    checkHasSectionId(article);
    return complete(
        submit(
            req(
                "POST",
                tmpl("/help_center/sections/{id}/articles.json").set("id", article.getSectionId()),
                JSON,
                json(Collections.singletonMap("article", article))),
            handle(Article.class, "article")));
  }

  public Article createArticle(Article article, boolean notifySubscribers) {
    checkHasSectionId(article);

    Map map = new HashMap<String, Object>();
    map.put("article", article);
    map.put(
        "notify_subscribers", notifySubscribers ? String.valueOf("true") : String.valueOf("false"));

    return complete(
        submit(
            req(
                "POST",
                tmpl("/help_center/sections/{id}/articles.json").set("id", article.getSectionId()),
                JSON,
                json(Collections.unmodifiableMap(map))),
            handle(Article.class, "article")));
  }

  public Article updateArticle(Article article) {
    checkHasId(article);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/help_center/articles/{id}.json").set("id", article.getId()),
                JSON,
                json(Collections.singletonMap("article", article))),
            handle(Article.class, "article")));
  }

  public Translation createArticleTranslation(Long articleId, Translation translation) {
    checkHasArticleId(articleId);
    return complete(
        submit(
            req(
                "POST",
                tmpl("/help_center/articles/{id}/translations.json").set("id", articleId),
                JSON,
                json(Collections.singletonMap("translation", translation))),
            handle(Translation.class, "translation")));
  }

  public Translation updateArticleTranslation(
      Long articleId, String locale, Translation translation) {
    checkHasId(translation);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/help_center/articles/{id}/translations/{locale}.json")
                    .set("id", articleId)
                    .set("locale", locale),
                JSON,
                json(Collections.singletonMap("translation", translation))),
            handle(Translation.class, "translation")));
  }

  public void deleteArticle(Article article) {
    checkHasId(article);
    complete(
        submit(
            req("DELETE", tmpl("/help_center/articles/{id}.json").set("id", article.getId())),
            handleStatus()));
  }

  /**
   * Delete translation.
   *
   * @param translation
   */
  public void deleteTranslation(Translation translation) {
    checkHasId(translation);
    deleteTranslation(translation.getId());
  }

  /**
   * Delete translation.
   *
   * @param translationId
   */
  public void deleteTranslation(Long translationId) {
    complete(
        submit(
            req("DELETE", tmpl("/help_center/translations/{id}.json").set("id", translationId)),
            handleStatus()));
  }

  /**
   * Delete attachment from article.
   *
   * @param attachment
   */
  public void deleteArticleAttachment(ArticleAttachments attachment) {
    checkHasId(attachment);
    deleteArticleAttachment(attachment.getId());
  }

  /**
   * Delete attachment from article.
   *
   * @param id attachment identifier.
   */
  public void deleteArticleAttachment(long id) {
    complete(
        submit(
            req("DELETE", tmpl("/help_center/articles/attachments/{id}.json").set("id", id)),
            handleStatus()));
  }

  public Iterable<Category> getCategories() {
    return new PagedIterable<>(
        cnst("/help_center/categories.json"), handleList(Category.class, "categories"));
  }

  public Category getCategory(long id) {
    return complete(
        submit(
            req("GET", tmpl("/help_center/categories/{id}.json").set("id", id)),
            handle(Category.class, "category")));
  }

  public Iterable<Translation> getCategoryTranslations(Long categoryId) {
    return new PagedIterable<>(
        tmpl("/help_center/categories/{categoryId}/translations.json")
            .set("categoryId", categoryId),
        handleList(Translation.class, "translations"));
  }

  public Translation showCategoryTranslation(long categoryId, String locale) {
    return complete(
        submit(
            req(
                "GET",
                tmpl("/help_center/categories/{categoryId}/translations/{locale}.json")
                    .set("categoryId", categoryId)
                    .set("locale", locale)),
            handle(Translation.class, "translation")));
  }

  public Category createCategory(Category category) {
    return complete(
        submit(
            req(
                "POST",
                cnst("/help_center/categories.json"),
                JSON,
                json(Collections.singletonMap("category", category))),
            handle(Category.class, "category")));
  }

  public Category updateCategory(Category category) {
    checkHasId(category);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/help_center/categories/{id}.json").set("id", category.getId()),
                JSON,
                json(Collections.singletonMap("category", category))),
            handle(Category.class, "category")));
  }

  public Translation createCategoryTranslation(Long categoryId, Translation translation) {
    checkHasCategoryId(categoryId);
    return complete(
        submit(
            req(
                "POST",
                tmpl("/help_center/categories/{id}/translations.json").set("id", categoryId),
                JSON,
                json(Collections.singletonMap("translation", translation))),
            handle(Translation.class, "translation")));
  }

  public Translation updateCategoryTranslation(
      Long categoryId, String locale, Translation translation) {
    checkHasId(translation);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/help_center/categories/{id}/translations/{locale}.json")
                    .set("id", categoryId)
                    .set("locale", locale),
                JSON,
                json(Collections.singletonMap("translation", translation))),
            handle(Translation.class, "translation")));
  }

  public void deleteCategory(Category category) {
    checkHasId(category);
    complete(
        submit(
            req("DELETE", tmpl("/help_center/categories/{id}.json").set("id", category.getId())),
            handleStatus()));
  }

  public Iterable<Section> getSections() {
    return new PagedIterable<>(
        cnst("/help_center/sections.json"), handleList(Section.class, "sections"));
  }

  public Iterable<Section> getSections(Category category) {
    checkHasId(category);
    return new PagedIterable<>(
        tmpl("/help_center/categories/{id}/sections.json").set("id", category.getId()),
        handleList(Section.class, "sections"));
  }

  public Section getSection(long id) {
    return complete(
        submit(
            req("GET", tmpl("/help_center/sections/{id}.json").set("id", id)),
            handle(Section.class, "section")));
  }

  public Iterable<Translation> getSectionTranslations(Long sectionId) {
    return new PagedIterable<>(
        tmpl("/help_center/sections/{sectionId}/translations.json").set("sectionId", sectionId),
        handleList(Translation.class, "translations"));
  }

  public Translation showSectionTranslation(long sectionId, String locale) {
    return complete(
        submit(
            req(
                "GET",
                tmpl("/help_center/sections/{sectionId}/translations/{locale}.json")
                    .set("sectionId", sectionId)
                    .set("locale", locale)),
            handle(Translation.class, "translation")));
  }

  public Section createSection(Section section) {
    checkHasCategoryId(section);
    return complete(
        submit(
            req(
                "POST",
                tmpl("/help_center/categories/{id}/sections.json")
                    .set("id", section.getCategoryId()),
                JSON,
                json(Collections.singletonMap("section", section))),
            handle(Section.class, "section")));
  }

  public Section updateSection(Section section) {
    checkHasId(section);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/help_center/sections/{id}.json").set("id", section.getId()),
                JSON,
                json(Collections.singletonMap("section", section))),
            handle(Section.class, "section")));
  }

  public Translation createSectionTranslation(Long sectionId, Translation translation) {
    checkHasSectionId(sectionId);
    return complete(
        submit(
            req(
                "POST",
                tmpl("/help_center/sections/{id}/translations.json").set("id", sectionId),
                JSON,
                json(Collections.singletonMap("translation", translation))),
            handle(Translation.class, "translation")));
  }

  public Translation updateSectionTranslation(
      Long sectionId, String locale, Translation translation) {
    checkHasId(translation);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/help_center/sections/{id}/translations/{locale}.json")
                    .set("id", sectionId)
                    .set("locale", locale),
                JSON,
                json(Collections.singletonMap("translation", translation))),
            handle(Translation.class, "translation")));
  }

  public void deleteSection(Section section) {
    checkHasId(section);
    complete(
        submit(
            req("DELETE", tmpl("/help_center/sections/{id}.json").set("id", section.getId())),
            handleStatus()));
  }

  public Iterable<Subscription> getUserSubscriptions(User user) {
    checkHasId(user);
    return getUserSubscriptions(user.getId());
  }

  public Iterable<Subscription> getUserSubscriptions(Long userId) {
    return new PagedIterable<>(
        tmpl("/help_center/users/{userId}/subscriptions.json").set("userId", userId),
        handleList(Subscription.class, "subscriptions"));
  }

  public Iterable<Subscription> getArticleSubscriptions(Long articleId) {
    return getArticleSubscriptions(articleId, null);
  }

  public Iterable<Subscription> getArticleSubscriptions(Long articleId, String locale) {
    return new PagedIterable<>(
        tmpl("/help_center{/locale}/articles/{articleId}/subscriptions.json")
            .set("locale", locale)
            .set("articleId", articleId),
        handleList(Subscription.class, "subscriptions"));
  }

  public Iterable<Subscription> getSectionSubscriptions(Long sectionId) {
    return getSectionSubscriptions(sectionId, null);
  }

  public Iterable<Subscription> getSectionSubscriptions(Long sectionId, String locale) {
    return new PagedIterable<>(
        tmpl("/help_center{/locale}/sections/{sectionId}/subscriptions.json")
            .set("locale", locale)
            .set("sectionId", sectionId),
        handleList(Subscription.class, "subscriptions"));
  }

  /**
   * Get a list of the current business hours schedules
   *
   * @return A List of Schedules
   */
  public Iterable<Schedule> getSchedules() {
    return complete(
        submit(
            req("GET", cnst("/business_hours/schedules.json")),
            handleList(Schedule.class, "schedules")));
  }

  public Schedule getSchedule(Schedule schedule) {
    checkHasId(schedule);
    return getSchedule(schedule.getId());
  }

  public Schedule getSchedule(Long scheduleId) {
    return complete(
        submit(
            req("GET", tmpl("/business_hours/schedules/{id}.json").set("id", scheduleId)),
            handle(Schedule.class, "schedule")));
  }

  public Iterable<Holiday> getHolidaysForSchedule(Schedule schedule) {
    checkHasId(schedule);
    return getHolidaysForSchedule(schedule.getId());
  }

  public Iterable<Holiday> getHolidaysForSchedule(Long scheduleId) {
    return complete(
        submit(
            req("GET", tmpl("/business_hours/schedules/{id}/holidays.json").set("id", scheduleId)),
            handleList(Holiday.class, "holidays")));
  }

  public ContentTag getContentTag(String contentTagId) {
    return complete(
        submit(
            req("GET", tmpl("/guide/content_tags/{id}").set("id", contentTagId)),
            handle(ContentTag.class, "content_tag")));
  }

  public ContentTag createContentTag(ContentTag contentTag) {
    checkHasName(contentTag);
    return complete(
        submit(
            req(
                "POST",
                cnst("/guide/content_tags"),
                JSON,
                json(Collections.singletonMap("content_tag", contentTag))),
            handle(ContentTag.class, "content_tag")));
  }

  public ContentTag updateContentTag(ContentTag contentTag) {
    checkHasId(contentTag);
    checkHasName(contentTag);
    return complete(
        submit(
            req(
                "PUT",
                tmpl("/guide/content_tags/{id}").set("id", contentTag.getId()),
                JSON,
                json(Collections.singletonMap("content_tag", contentTag))),
            handle(ContentTag.class, "content_tag")));
  }

  public void deleteContentTag(ContentTag contentTag) {
    checkHasId(contentTag);
    complete(
        submit(
            req("DELETE", tmpl("/guide/content_tags/{id}").set("id", contentTag.getId())),
            handleStatus()));
  }

  public Iterable<ContentTag> getContentTags() {
    int defaultPageSize = 10;
    return getContentTags(defaultPageSize, null);
  }

  public Iterable<ContentTag> getContentTags(int pageSize) {
    return getContentTags(pageSize, null);
  }

  public Iterable<ContentTag> getContentTags(int pageSize, String namePrefix) {
    Function<String, Uri> afterCursorUriBuilder =
        (String afterCursor) -> buildContentTagsSearchUrl(pageSize, namePrefix, afterCursor);
    return new PagedIterable<>(
        afterCursorUriBuilder.apply(null),
        handleListWithAfterCursorButNoLinks(ContentTag.class, afterCursorUriBuilder, "records"));
  }

  public Iterable<JiraLink> getJiraLinks() {
    return new PagedIterable<>(cnst("/jira/links"), handleList(JiraLink.class, "links"));
  }

  private Uri buildContentTagsSearchUrl(int pageSize, String namePrefixFilter, String afterCursor) {
    final StringBuilder uriBuilder =
        new StringBuilder("/guide/content_tags?page[size]=").append(pageSize);

    if (namePrefixFilter != null) {
      uriBuilder.append("&filter[name_prefix]=").append(encodeUrl(namePrefixFilter));
    }
    if (afterCursor != null) {
      uriBuilder.append("&page[after]=").append(encodeUrl(afterCursor));
    }
    return cnst(uriBuilder.toString());
  }

  public List<TimeZone> getTimeZones() {
    return complete(
        submit(req("GET", cnst("/time_zones.json")), handleList(TimeZone.class, "time_zones")));
  }

  //////////////////////////////////////////////////////////////////////
  // Helper methods
  //////////////////////////////////////////////////////////////////////

  private byte[] json(Object object) {
    try {
      return mapper.writeValueAsBytes(object);
    } catch (JsonProcessingException e) {
      throw new ZendeskException(e.getMessage(), e);
    }
  }

  private <T> ListenableFuture<T> submit(
      Request request, ZendeskAsyncCompletionHandler<T> handler) {
    if (logger.isDebugEnabled()) {
      if (request.getStringData() != null) {
        logger.debug(
            "Request {} {}\n{}", request.getMethod(), request.getUrl(), request.getStringData());
      } else if (request.getByteData() != null) {
        logger.debug(
            "Request {} {} {} {} bytes",
            request.getMethod(),
            request.getUrl(),
            request.getHeaders().get("Content-type"),
            request.getByteData().length);
      } else {
        logger.debug("Request {} {}", request.getMethod(), request.getUrl());
      }
    }
    return client.executeRequest(request, handler);
  }

  private abstract static class ZendeskAsyncCompletionHandler<T> extends AsyncCompletionHandler<T> {
    @Override
    public void onThrowable(Throwable t) {
      if (t instanceof IOException) {
        throw new ZendeskException(t);
      } else {
        super.onThrowable(t);
      }
    }
  }

  private Request req(String method, Uri template) {
    return req(method, template.toString());
  }

  private Request req(String method, String url) {
    return reqBuilder(method, url).build();
  }

  private Request req(String method, Uri template, String contentType, byte[] body) {
    RequestBuilder builder = reqBuilder(method, template.toString());
    builder.addHeader("Content-type", contentType);
    builder.setBody(body);
    return builder.build();
  }

  private RequestBuilder reqBuilder(String method, String url) {
    RequestBuilder builder = new RequestBuilder(method);
    if (realm != null) {
      builder.setRealm(realm);
    } else {
      builder.addHeader("Authorization", "Bearer " + oauthToken);
    }
    headers.forEach(builder::setHeader);
    return builder.setUrl(url);
  }

  protected ZendeskAsyncCompletionHandler<Void> handleStatus() {
    return new ZendeskAsyncCompletionHandler<Void>() {
      @Override
      public Void onCompleted(Response response) throws Exception {
        logResponse(response);
        if (isStatus2xx(response)) {
          return null;
        } else if (isRateLimitResponse(response)) {
          throw new ZendeskResponseRateLimitException(response);
        }
        throw new ZendeskResponseException(response);
      }
    };
  }

  @SuppressWarnings("unchecked")
  protected <T> ZendeskAsyncCompletionHandler<T> handle(final Class<T> clazz) {
    return new ZendeskAsyncCompletionHandler<T>() {
      @Override
      public T onCompleted(Response response) throws Exception {
        logResponse(response);
        if (isStatus2xx(response)) {
          return (T) mapper.readerFor(clazz).readValue(response.getResponseBodyAsStream());
        } else if (isRateLimitResponse(response)) {
          throw new ZendeskResponseRateLimitException(response);
        }
        if (response.getStatusCode() == 404) {
          return null;
        }
        throw new ZendeskResponseException(response);
      }
    };
  }

  private class BasicAsyncCompletionHandler<T> extends ZendeskAsyncCompletionHandler<T> {
    private final Class<T> clazz;
    private final String name;
    private final Class[] typeParams;

    public BasicAsyncCompletionHandler(Class clazz, String name, Class... typeParams) {
      this.clazz = clazz;
      this.name = name;
      this.typeParams = typeParams;
    }

    @Override
    public T onCompleted(Response response) throws Exception {
      logResponse(response);
      if (isStatus2xx(response)) {
        if (typeParams.length > 0) {
          JavaType type = mapper.getTypeFactory().constructParametricType(clazz, typeParams);
          return mapper.convertValue(
              mapper.readTree(response.getResponseBodyAsStream()).get(name), type);
        }
        return mapper.convertValue(
            mapper.readTree(response.getResponseBodyAsStream()).get(name), clazz);
      } else if (isRateLimitResponse(response)) {
        throw new ZendeskResponseRateLimitException(response);
      }
      if (response.getStatusCode() == 404) {
        return null;
      }
      throw new ZendeskResponseException(response);
    }
  }

  protected <T> ZendeskAsyncCompletionHandler<T> handle(
      final Class<T> clazz, final String name, final Class... typeParams) {
    return new BasicAsyncCompletionHandler<>(clazz, name, typeParams);
  }

  protected ZendeskAsyncCompletionHandler<JobStatus> handleJobStatus() {
    return new BasicAsyncCompletionHandler<JobStatus>(JobStatus.class, "job_status") {
      @Override
      public JobStatus onCompleted(Response response) throws Exception {
        JobStatus result = super.onCompleted(response);
        if (result == null) {
          // null is when we receive a 404 response.
          // For an async job we trigger an error
          throw new ZendeskResponseException(response);
        }
        return result;
      }
    };
  }

  private static final String CURSOR_LINKS = "links";
  private static final String CURSOR_NEXT_PAGE = "next";
  private static final String NEXT_PAGE = "next_page";
  private static final String END_TIME = "end_time";
  private static final String COUNT = "count";
  private static final int INCREMENTAL_EXPORT_MAX_COUNT_BY_REQUEST = 1000;

  private abstract class PagedAsyncCompletionHandler<T> extends ZendeskAsyncCompletionHandler<T> {
    private String nextPage;

    public void setPagedProperties(JsonNode responseNode, Class<?> clazz) {
      JsonNode node = responseNode.get(CURSOR_LINKS);

      // Attempt to use cursor pagination if possible
      if (node != null) {
        node = node.get(CURSOR_NEXT_PAGE);
      } else {
        node = responseNode.get(NEXT_PAGE);
      }

      if (node == null) {
        this.nextPage = null;
        if (logger.isDebugEnabled()) {
          logger.debug(
              NEXT_PAGE
                  + " property not found, pagination not supported"
                  + (clazz != null ? " for " + clazz.getName() : ""));
        }
      } else {
        this.nextPage = node.asText();
      }
    }

    public String getNextPage() {
      return nextPage;
    }

    public void setNextPage(String nextPage) {
      this.nextPage = nextPage;
    }
  }

  private class PagedAsyncListCompletionHandler<T> extends PagedAsyncCompletionHandler<List<T>> {
    private final Class<T> clazz;
    private final String name;

    public PagedAsyncListCompletionHandler(Class<T> clazz, String name) {
      this.clazz = clazz;
      this.name = name;
    }

    @Override
    public List<T> onCompleted(Response response) throws Exception {
      logResponse(response);
      if (isStatus2xx(response)) {
        JsonNode responseNode = mapper.readTree(response.getResponseBodyAsBytes());
        setPagedProperties(responseNode, clazz);
        List<T> values = new ArrayList<>();
        for (JsonNode node : responseNode.get(name)) {
          values.add(mapper.convertValue(node, clazz));
        }
        return values;
      } else if (isRateLimitResponse(response)) {
        throw new ZendeskResponseRateLimitException(response);
      }
      throw new ZendeskResponseException(response);
    }
  }

  protected <T> PagedAsyncCompletionHandler<List<T>> handleList(
      final Class<T> clazz, final String name) {
    return new PagedAsyncListCompletionHandler<>(clazz, name);
  }

  private static final long FIVE_MINUTES = TimeUnit.MINUTES.toMillis(5);

  protected <T> PagedAsyncCompletionHandler<List<T>> handleIncrementalList(
      final Class<T> clazz, final String name) {
    return new PagedAsyncListCompletionHandler<T>(clazz, name) {
      @Override
      public void setPagedProperties(JsonNode responseNode, Class<?> clazz) {
        JsonNode node = responseNode.get(NEXT_PAGE);
        if (node == null) {
          if (logger.isDebugEnabled()) {
            logger.debug(
                NEXT_PAGE
                    + " property not found, pagination not supported"
                    + (clazz != null ? " for " + clazz.getName() : ""));
          }
          setNextPage(null);
          return;
        }
        JsonNode endTimeNode = responseNode.get(END_TIME);
        if (endTimeNode == null || endTimeNode.asLong() == 0) {
          if (logger.isDebugEnabled()) {
            logger.debug(
                END_TIME
                    + " property not found, incremental export pagination not supported"
                    + (clazz != null ? " for " + clazz.getName() : ""));
          }
          setNextPage(null);
          return;
        }
        /*
         A request after five minutes ago will result in a 422 responds from Zendesk.
         Therefore, we stop pagination.
        */
        if (TimeUnit.SECONDS.toMillis(endTimeNode.asLong())
            > System.currentTimeMillis() - FIVE_MINUTES) {
          setNextPage(null);
        } else {
          // Taking into account documentation found at
          // https://developer.zendesk.com/rest_api/docs/core/incremental_export#polling-strategy
          JsonNode countNode = responseNode.get(COUNT);
          if (countNode == null) {
            if (logger.isDebugEnabled()) {
              logger.debug(
                  COUNT
                      + " property not found, incremental export pagination not supported"
                      + (clazz != null ? " for " + clazz.getName() : ""));
            }
            setNextPage(null);
            return;
          }

          if (countNode.asInt() < INCREMENTAL_EXPORT_MAX_COUNT_BY_REQUEST) {
            setNextPage(null);
          } else {
            setNextPage(node.asText());
          }
        }
      }
    };
  }

  protected PagedAsyncCompletionHandler<List<SearchResultEntity>> handleSearchList(
      final String name) {
    return new PagedAsyncCompletionHandler<List<SearchResultEntity>>() {
      @Override
      public List<SearchResultEntity> onCompleted(Response response) throws Exception {
        logResponse(response);
        if (isStatus2xx(response)) {
          JsonNode responseNode = mapper.readTree(response.getResponseBodyAsStream()).get(name);
          setPagedProperties(responseNode, null);
          List<SearchResultEntity> values = new ArrayList<>();
          for (JsonNode node : responseNode) {
            Class<? extends SearchResultEntity> clazz =
                searchResultTypes.get(node.get("result_type").asText());
            if (clazz != null) {
              values.add(mapper.convertValue(node, clazz));
            }
          }
          return values;
        } else if (isRateLimitResponse(response)) {
          throw new ZendeskResponseRateLimitException(response);
        }
        throw new ZendeskResponseException(response);
      }
    };
  }

  protected PagedAsyncCompletionHandler<List<Target>> handleTargetList(final String name) {
    return new PagedAsyncCompletionHandler<List<Target>>() {
      @Override
      public List<Target> onCompleted(Response response) throws Exception {
        logResponse(response);
        if (isStatus2xx(response)) {
          JsonNode responseNode = mapper.readTree(response.getResponseBodyAsBytes());
          setPagedProperties(responseNode, null);
          List<Target> values = new ArrayList<>();
          for (JsonNode node : responseNode.get(name)) {
            Class<? extends Target> clazz = targetTypes.get(node.get("type").asText());
            if (clazz != null) {
              values.add(mapper.convertValue(node, clazz));
            }
          }
          return values;
        } else if (isRateLimitResponse(response)) {
          throw new ZendeskResponseRateLimitException(response);
        }
        throw new ZendeskResponseException(response);
      }
    };
  }

  protected PagedAsyncCompletionHandler<List<ArticleAttachments>> handleArticleAttachmentsList(
      final String name) {
    return new PagedAsyncCompletionHandler<List<ArticleAttachments>>() {
      @Override
      public List<ArticleAttachments> onCompleted(Response response) throws Exception {
        logResponse(response);
        if (isStatus2xx(response)) {
          JsonNode responseNode = mapper.readTree(response.getResponseBodyAsBytes());
          List<ArticleAttachments> values = new ArrayList<>();
          for (JsonNode node : responseNode.get(name)) {
            values.add(mapper.convertValue(node, ArticleAttachments.class));
          }
          return values;
        } else if (isRateLimitResponse(response)) {
          throw new ZendeskResponseRateLimitException(response);
        }
        throw new ZendeskResponseException(response);
      }
    };
  }

  /**
   * For a resource (e.g. ContentTag) which supports cursor based pagination for multiple results,
   * but where the response does not have a `links.next` node (which would hold the URL of the next
   * page) So we need to build the next page URL from the original URL and the meta.after_cursor
   * node value
   *
   * @param <T> The class of the resource
   * @param afterCursorUriBuilder a function to build the URL for the next page
   *     `fn(after_cursor_value) => URL_of_next_page`
   * @param name the name of the Json node that contains the resources entities (e.g. 'records' for
   *     ContentTag)
   */
  private <T> PagedAsyncCompletionHandler<List<T>> handleListWithAfterCursorButNoLinks(
      Class<T> clazz, Function<String, Uri> afterCursorUriBuilder, String name) {

    return new PagedAsyncListCompletionHandler<T>(clazz, name) {
      @Override
      public void setPagedProperties(JsonNode responseNode, Class<?> clazz) {
        JsonNode metaNode = responseNode.get("meta");
        String nextPage = null;
        if (metaNode == null) {
          if (logger.isDebugEnabled()) {
            logger.debug(
                "meta"
                    + " property not found, pagination not supported"
                    + (clazz != null ? " for " + clazz.getName() : ""));
          }
        } else {
          JsonNode afterCursorNode = metaNode.get("after_cursor");
          if (afterCursorNode != null) {
            JsonNode hasMoreNode = metaNode.get("has_more");
            if (hasMoreNode != null && hasMoreNode.asBoolean()) {
              nextPage = afterCursorUriBuilder.apply(afterCursorNode.asText()).toString();
            }
          }
        }
        setNextPage(nextPage);
      }
    };
  }

  private TemplateUri tmpl(String template) {
    return new TemplateUri(url + template);
  }

  private TemplateUri cbp(String path) {
    Objects.requireNonNull(path, "Path cannot be null");
    if (path.indexOf('?') != -1) {
      throw new IllegalArgumentException("Path cannot contain a query string");
    }
    return new TemplateUri(url + path + "?page[size]={pageSize}").set("pageSize", cbpPageSize);
  }

  private Uri cnst(String template) {
    return new FixedUri(url + template);
  }

  private void logResponse(Response response) throws IOException {
    if (logger.isDebugEnabled()) {
      logger.debug(
          "Response HTTP/{} {}\n{}",
          response.getStatusCode(),
          response.getStatusText(),
          response.getResponseBody());
    }
    if (logger.isTraceEnabled()) {
      logger.trace("Response headers {}", response.getHeaders());
    }
  }

  private static final String UTF_8 = "UTF-8";

  private static String encodeUrl(String input) {
    try {
      return URLEncoder.encode(input, UTF_8);
    } catch (UnsupportedEncodingException impossible) {
      return input;
    }
  }

  private static long msToSeconds(long millis) {
    return TimeUnit.MILLISECONDS.toSeconds(millis);
  }

  private boolean isStatus2xx(Response response) {
    return response.getStatusCode() / 100 == 2;
  }

  private boolean isRateLimitResponse(Response response) {
    return response.getStatusCode() == 429;
  }

  //////////////////////////////////////////////////////////////////////
  // Static helper methods
  //////////////////////////////////////////////////////////////////////

  private static <T> T complete(ListenableFuture<T> future) {
    try {
      return future.get();
    } catch (InterruptedException e) {
      throw new ZendeskException(e.getMessage(), e);
    } catch (ExecutionException e) {
      if (e.getCause() instanceof ZendeskException) {
        if (e.getCause() instanceof ZendeskResponseRateLimitException) {
          throw new ZendeskResponseRateLimitException(
              (ZendeskResponseRateLimitException) e.getCause());
        }
        if (e.getCause() instanceof ZendeskResponseException) {
          throw new ZendeskResponseException((ZendeskResponseException) e.getCause());
        }
        throw new ZendeskException(e.getCause());
      }
      throw new ZendeskException(e.getMessage(), e);
    }
  }

  private static void checkHasId(Ticket ticket) {
    if (ticket.getId() == null) {
      throw new IllegalArgumentException("Ticket requires id");
    }
  }

  private static void checkHasId(TicketForm ticketForm) {
    if (ticketForm.getId() == null) {
      throw new IllegalArgumentException("TicketForm requires id");
    }
  }

  private static void checkHasId(org.zendesk.client.v2.model.Request request) {
    if (request.getId() == null) {
      throw new IllegalArgumentException("Request requires id");
    }
  }

  private static void checkHasId(Audit audit) {
    if (audit.getId() == null) {
      throw new IllegalArgumentException("Audit requires id");
    }
  }

  private static void checkHasId(Comment comment) {
    if (comment.getId() == null) {
      throw new IllegalArgumentException("Comment requires id");
    }
  }

  private static void checkHasId(Field field) {
    if (field.getId() == null) {
      throw new IllegalArgumentException("Field requires id");
    }
  }

  private static void checkHasId(Attachment attachment) {
    if (attachment.getId() == null) {
      throw new IllegalArgumentException("Attachment requires id");
    }
  }

  private static void checkHasId(ArticleAttachments attachments) {
    if (attachments.getId() == null) {
      throw new IllegalArgumentException("Attachment requires id");
    }
  }

  private static void checkHasId(User user) {
    if (user.getId() == null) {
      throw new IllegalArgumentException("User requires id");
    }
  }

  private static void checkHasId(Identity identity) {
    if (identity.getId() == null) {
      throw new IllegalArgumentException("Identity requires id");
    }
  }

  private static void checkHasId(Organization organization) {
    if (organization.getId() == null) {
      throw new IllegalArgumentException("Organization requires id");
    }
  }

  private static void checkHasId(Group group) {
    if (group.getId() == null) {
      throw new IllegalArgumentException("Group requires id");
    }
  }

  private static void checkHasId(GroupMembership groupMembership) {
    if (groupMembership.getId() == null) {
      throw new IllegalArgumentException("GroupMembership requires id");
    }
  }

  private void checkHasId(Forum forum) {
    if (forum.getId() == null) {
      throw new IllegalArgumentException("Forum requires id");
    }
  }

  private void checkHasId(Topic topic) {
    if (topic.getId() == null) {
      throw new IllegalArgumentException("Topic requires id");
    }
  }

  private static void checkHasId(OrganizationMembership organizationMembership) {
    if (organizationMembership.getId() == null) {
      throw new IllegalArgumentException("OrganizationMembership requires id");
    }
  }

  private static void checkHasId(Article article) {
    if (article.getId() == null) {
      throw new IllegalArgumentException("Article requires id");
    }
  }

  private static void checkHasId(DynamicContentItem item) {
    if (item.getId() == null) {
      throw new IllegalArgumentException("Item requires id");
    }
  }

  private static void checkHasId(DynamicContentItemVariant variant) {
    if (variant.getId() == null) {
      throw new IllegalArgumentException("Variant requires id");
    }
  }

  private static void checkHasItemId(Long itemId) {
    if (itemId == null) {
      throw new IllegalArgumentException("Variant requires item id");
    }
  }

  private static void checkHasSectionId(Article article) {
    if (article.getSectionId() == null) {
      throw new IllegalArgumentException("Article requires section id");
    }
  }

  private static void checkHasArticleId(Long articleId) {
    if (articleId == null) {
      throw new IllegalArgumentException("Translation requires article id");
    }
  }

  private static void checkHasSectionId(Long articleId) {
    if (articleId == null) {
      throw new IllegalArgumentException("Translation requires section id");
    }
  }

  private static void checkHasCategoryId(Long articleId) {
    if (articleId == null) {
      throw new IllegalArgumentException("Translation requires category id");
    }
  }

  private static void checkHasCategoryId(Section section) {
    if (section.getCategoryId() == null) {
      throw new IllegalArgumentException("Section requires category id");
    }
  }

  private static void checkHasId(Category category) {
    if (category.getId() == null) {
      throw new IllegalArgumentException("Category requires id");
    }
  }

  private static void checkHasId(Section section) {
    if (section.getId() == null) {
      throw new IllegalArgumentException("Section requires id");
    }
  }

  private static void checkHasId(SuspendedTicket ticket) {
    if (ticket == null || ticket.getId() == null) {
      throw new IllegalArgumentException("SuspendedTicket requires id");
    }
  }

  private static void checkHasId(Translation translation) {
    if (translation.getId() == null) {
      throw new IllegalArgumentException("Translation requires id");
    }
  }

  private static void checkHasId(Schedule schedule) {
    if (schedule == null || schedule.getId() == null) {
      throw new IllegalArgumentException("Schedule requires id");
    }
  }

  private static void checkHasId(Holiday holiday) {
    if (holiday == null || holiday.getId() == null) {
      throw new IllegalArgumentException("Holiday requires id");
    }
  }

  private static void checkHasId(PermissionGroup permissionGroup) {
    if (permissionGroup.getId() == null) {
      throw new IllegalArgumentException("PermissionGroup requires id");
    }
  }

  private static void checkHasId(UserSegment userSegment) {
    if (userSegment.getId() == null) {
      throw new IllegalArgumentException("UserSegment requires id");
    }
  }

  private static void checkHasId(ContentTag contentTag) {
    if (contentTag.getId() == null) {
      throw new IllegalArgumentException("Content Tag requires id");
    }
  }

  private static void checkHasName(ContentTag contentTag) {
    if (contentTag.getName() == null || contentTag.getName().trim().isEmpty()) {
      throw new IllegalArgumentException("Content Tag requires name");
    }
  }

  private static void checkHasToken(Attachment.Upload upload) {
    if (upload.getToken() == null) {
      throw new IllegalArgumentException("Upload requires token");
    }
  }

  private static List<Long> idArray(long id, long... ids) {
    List<Long> result = new ArrayList<>(ids.length + 1);
    result.add(id);
    for (long i : ids) {
      result.add(i);
    }
    return result;
  }

  private static List<String> idArray(String id, String... ids) {
    List<String> result = new ArrayList<>(ids.length + 1);
    result.add(id);
    for (String i : ids) {
      result.add(i);
    }
    return result;
  }

  private static List<String> statusArray(Status... statuses) {
    List<String> result = new ArrayList<>(statuses.length);
    for (Status s : statuses) {
      result.add(s.toString());
    }
    return result;
  }

  private static String getTypeName(final Class<?> type) {
    String typeName = null;
    for (final Map.Entry<String, Class<? extends SearchResultEntity>> entry :
        searchResultTypes.entrySet()) {
      if (type.equals(entry.getValue())) {
        typeName = entry.getKey();
        break;
      }
    }
    return typeName;
  }

  private TemplateUri getSearchUri(Map<String, Object> params, String query, String typeName) {

    StringBuilder uriTemplate =
        new StringBuilder("/search.json{?query"); // leave off ending curly brace

    // we have to add each param name to the template so that when we call set() with a map, the
    // entries get put in the uri
    for (String paramName : params.keySet()) {
      uriTemplate.append(",").append(paramName);
    }

    uriTemplate.append("}");

    TemplateUri templateUri =
        tmpl(uriTemplate.toString()).set("query", query + " type:" + typeName);

    if (params != null) {
      templateUri.set(params);
    }

    return templateUri;
  }

  public static ObjectMapper createMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.setDateFormat(new StdDateFormat());
    mapper.enable(DeserializationFeature.USE_LONG_FOR_INTS);
    return mapper;
  }

  //////////////////////////////////////////////////////////////////////
  // Helper classes
  //////////////////////////////////////////////////////////////////////

  private class PagedIterable<T> implements Iterable<T> {

    private final Uri url;
    private final PagedAsyncCompletionHandler<List<T>> handler;

    private PagedIterable(Uri url, PagedAsyncCompletionHandler<List<T>> handler) {
      this.handler = handler;
      this.url = url;
    }

    public Iterator<T> iterator() {
      return new PagedIterator(url);
    }

    private class PagedIterator implements Iterator<T> {

      private Iterator<T> current;
      private String nextPage;

      public PagedIterator(Uri url) {
        this.nextPage = url.toString();
      }

      public boolean hasNext() {
        if (current == null || !current.hasNext()) {
          if (nextPage == null || nextPage.equalsIgnoreCase("null")) {
            return false;
          }
          List<T> values = complete(submit(req("GET", nextPage), handler));
          nextPage = handler.getNextPage();
          current = values.iterator();
        }
        return current.hasNext();
      }

      public T next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        return current.next();
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    }
  }

  public static class Builder {
    private static final Integer DEFAULT_CBP_PAGE_SIZE = 100;
    private AsyncHttpClient client = null;
    private final String url;
    private String username = null;
    private String password = null;
    private String token = null;
    private String oauthToken = null;
    private int cbpPageSize = DEFAULT_CBP_PAGE_SIZE;
    private final Map<String, String> headers;

    public Builder(String url) {
      this.url = url;
      this.headers = new HashMap<>();
    }

    public Builder setClient(AsyncHttpClient client) {
      this.client = client;
      return this;
    }

    public Builder setUsername(String username) {
      this.username = username;
      return this;
    }

    public Builder setPassword(String password) {
      this.password = password;
      if (password != null) {
        this.token = null;
        this.oauthToken = null;
      }
      return this;
    }

    public Builder setToken(String token) {
      this.token = token;
      if (token != null) {
        this.password = null;
        this.oauthToken = null;
      }
      return this;
    }

    public Builder setOauthToken(String oauthToken) {
      this.oauthToken = oauthToken;
      if (oauthToken != null) {
        this.password = null;
        this.token = null;
      }
      return this;
    }

    public Builder setRetry(boolean retry) {
      return this;
    }

    public Builder addHeader(String name, String value) {
      Objects.requireNonNull(name, "Header name cannot be null");
      Objects.requireNonNull(value, "Header value cannot be null");
      headers.put(name, value);
      return this;
    }

    public Builder setCbpPageSize(int cbpPageSize) {
      this.cbpPageSize = cbpPageSize;
      return this;
    }

    public Zendesk build() {
      if (token != null) {
        return new Zendesk(client, url, username + "/token", token, headers, cbpPageSize);
      } else if (oauthToken != null) {
        return new Zendesk(client, url, oauthToken, headers, cbpPageSize);
      }
      return new Zendesk(client, url, username, password, headers, cbpPageSize);
    }
  }
}
