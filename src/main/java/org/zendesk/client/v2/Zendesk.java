package org.zendesk.client.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.DefaultAsyncHttpClient;
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
import org.zendesk.client.v2.model.Field;
import org.zendesk.client.v2.model.Forum;
import org.zendesk.client.v2.model.Group;
import org.zendesk.client.v2.model.GroupMembership;
import org.zendesk.client.v2.model.Identity;
import org.zendesk.client.v2.model.JobStatus;
import org.zendesk.client.v2.model.Macro;
import org.zendesk.client.v2.model.Metric;
import org.zendesk.client.v2.model.Organization;
import org.zendesk.client.v2.model.OrganizationField;
import org.zendesk.client.v2.model.OrganizationMembership;
import org.zendesk.client.v2.model.SatisfactionRating;
import org.zendesk.client.v2.model.SearchResultEntity;
import org.zendesk.client.v2.model.Status;
import org.zendesk.client.v2.model.SuspendedTicket;
import org.zendesk.client.v2.model.Ticket;
import org.zendesk.client.v2.model.TicketForm;
import org.zendesk.client.v2.model.TicketImport;
import org.zendesk.client.v2.model.TicketResult;
import org.zendesk.client.v2.model.Topic;
import org.zendesk.client.v2.model.Trigger;
import org.zendesk.client.v2.model.TwitterMonitor;
import org.zendesk.client.v2.model.User;
import org.zendesk.client.v2.model.UserField;
import org.zendesk.client.v2.model.UserRelatedInfo;
import org.zendesk.client.v2.model.hc.Article;
import org.zendesk.client.v2.model.hc.ArticleAttachments;
import org.zendesk.client.v2.model.hc.Category;
import org.zendesk.client.v2.model.hc.Section;
import org.zendesk.client.v2.model.hc.Subscription;
import org.zendesk.client.v2.model.hc.Translation;
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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author stephenc
 * @since 04/04/2013 13:08
 */
public class Zendesk implements Closeable {
    private static final String JSON = "application/json; charset=UTF-8";
    private final boolean closeClient;
    private final AsyncHttpClient client;
    private final Realm realm;
    private final String url;
    private final String oauthToken;
    private final ObjectMapper mapper;
    private final Logger logger;
    private boolean closed = false;
    private static final Map<String, Class<? extends SearchResultEntity>> searchResultTypes = searchResultTypes();
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
       result.put("email_target",EmailTarget.class);
       result.put("basecamp_target", BasecampTarget.class);
       result.put("campfire_target", CampfireTarget.class);
       result.put("pivotal_target", PivotalTarget.class);
       result.put("twitter_target", TwitterTarget.class);

       // TODO: Implement other Target types
       //result.put("clickatell_target", ClickatellTarget.class);
       //result.put("flowdock_target", FlowdockTarget.class);
       //result.put("get_satisfaction_target", GetSatisfactionTarget.class);
       //result.put("yammer_target", YammerTarget.class);

       return Collections.unmodifiableMap(result);
    }

    private Zendesk(AsyncHttpClient client, String url, String username, String password) {
        this.logger = LoggerFactory.getLogger(Zendesk.class);
        this.closeClient = client == null;
        this.oauthToken = null;
        this.client = client == null ? new DefaultAsyncHttpClient() : client;
        this.url = url.endsWith("/") ? url + "api/v2" : url + "/api/v2";
        if (username != null) {
            this.realm = new Realm.Builder(username, password)
                    .setScheme(Realm.AuthScheme.BASIC)
                    .setUsePreemptiveAuth(true)
                    .build();
        } else {
            if (password != null) {
                throw new IllegalStateException("Cannot specify token or password without specifying username");
            }
            this.realm = null;
        }
        this.mapper = createMapper();
    }


    private Zendesk(AsyncHttpClient client, String url, String oauthToken) {
        this.logger = LoggerFactory.getLogger(Zendesk.class);
        this.closeClient = client == null;
        this.realm = null;
        this.client = client == null ? new DefaultAsyncHttpClient() : client;
        this.url = url.endsWith("/") ? url + "api/v2" : url + "/api/v2";
        if (oauthToken != null) {
            this.oauthToken = oauthToken;
        } else {
            throw new IllegalStateException("Cannot specify token or password without specifying username");
        }

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

    public <T> JobStatus<T> getJobStatus(JobStatus<T> status) {
        return complete(getJobStatusAsync(status));
    }

    public <T> ListenableFuture<JobStatus<T>> getJobStatusAsync(JobStatus<T> status) {
        return submit(req("GET", tmpl("/job_statuses/{id}.json").set("id", status.getId())), handleJobStatus(status.getResultsClass()));
    }

    public List<JobStatus<HashMap<String, Object>>> getJobStatuses(List<JobStatus> statuses) {
        return complete(getJobStatusesAsync(statuses));
    }

    public ListenableFuture<List<JobStatus<HashMap<String, Object>>>> getJobStatusesAsync(List<JobStatus> statuses) {
        List<String> ids = new ArrayList<>(statuses.size());
        for (JobStatus status : statuses) {
            ids.add(status.getId());
        }
        Class<JobStatus<HashMap<String, Object>>> clazz = (Class<JobStatus<HashMap<String, Object>>>)(Object)JobStatus.class;
        return submit(req("GET", tmpl("/job_statuses/show_many.json{?ids}").set("ids", ids)), handleList(clazz, "job_statuses"));
    }

    public List<Brand> getBrands(){
        return complete(submit(req("GET", cnst("/brands.json")), handleList(Brand.class,
                "brands")));
    }

    public TicketForm getTicketForm(long id) {
        return complete(submit(req("GET", tmpl("/ticket_forms/{id}.json").set("id", id)), handle(TicketForm.class,
                "ticket_form")));
    }

    public List<TicketForm> getTicketForms() {
        return complete(submit(req("GET", cnst("/ticket_forms.json")), handleList(TicketForm.class,
                "ticket_forms")));
    }

    public TicketForm createTicketForm(TicketForm ticketForm) {
        return complete(submit(req("POST", cnst("/ticket_forms.json"), JSON, json(
                Collections.singletonMap("ticket_form", ticketForm))), handle(TicketForm.class, "ticket_form")));
    }

    public Ticket importTicket(TicketImport ticketImport) {
        return complete(submit(req("POST", cnst("/imports/tickets.json"),
                JSON, json(Collections.singletonMap("ticket", ticketImport))),
                handle(Ticket.class, "ticket")));
    }

    public JobStatus<Ticket> importTickets(TicketImport... ticketImports) {
        return importTickets(Arrays.asList(ticketImports));
    }

    public JobStatus<Ticket> importTickets(List<TicketImport> ticketImports) {
        return complete(importTicketsAsync(ticketImports));
    }

    public ListenableFuture<JobStatus<Ticket>> importTicketsAsync(List<TicketImport> ticketImports) {
        return submit(req("POST", cnst("/imports/tickets/create_many.json"), JSON, json(
                Collections.singletonMap("tickets", ticketImports))), handleJobStatus(Ticket.class));
    }

    public Ticket getTicket(long id) {
        return complete(submit(req("GET", tmpl("/tickets/{id}.json").set("id", id)), handle(Ticket.class,
                "ticket")));
    }

    public List<Ticket> getTicketIncidents(long id) {
        return complete(submit(req("GET", tmpl("/tickets/{id}/incidents.json").set("id", id)),
                handleList(Ticket.class, "tickets")));
    }

    public List<User> getTicketCollaborators(long id) {
        return complete(submit(req("GET", tmpl("/tickets/{id}/collaborators.json").set("id", id)),
                handleList(User.class, "users")));
    }

    public JobStatus permanentlyDeleteTicket(Ticket ticket) {
        checkHasId(ticket);
        return permanentlyDeleteTicket(ticket.getId());
    }

    public void deleteTicket(Ticket ticket) {
        checkHasId(ticket);
        deleteTicket(ticket.getId());
    }

    public void deleteTicket(long id) {
        complete(submit(req("DELETE", tmpl("/tickets/{id}.json").set("id", id)), handleStatus()));
    }

    public JobStatus permanentlyDeleteTicket(long id) {
        deleteTicket(id);
        return complete(submit(
                req("DELETE", tmpl("/deleted_tickets/{id}.json").set("id", id)),
                handleJobStatus(JobStatus.class))
        );
    }

    public ListenableFuture<JobStatus<Ticket>> queueCreateTicketAsync(Ticket ticket) {
        return submit(req("POST", cnst("/tickets.json?async=true"),
                JSON, json(Collections.singletonMap("ticket", ticket))),
                handleJobStatus(Ticket.class));
    }

    public ListenableFuture<Ticket> createTicketAsync(Ticket ticket) {
        return submit(req("POST", cnst("/tickets.json"),
                JSON, json(Collections.singletonMap("ticket", ticket))),
                handle(Ticket.class, "ticket"));
    }

    public Ticket createTicket(Ticket ticket) {
        return complete(createTicketAsync(ticket));
    }

    public JobStatus<Ticket> createTickets(Ticket... tickets) {
        return createTickets(Arrays.asList(tickets));
    }

    public JobStatus<Ticket> createTickets(List<Ticket> tickets) {
        return complete(createTicketsAsync(tickets));
    }

    public ListenableFuture<JobStatus<Ticket>> createTicketsAsync(List<Ticket> tickets) {
        return submit(req("POST", cnst("/tickets/create_many.json"), JSON, json(
                Collections.singletonMap("tickets", tickets))), handleJobStatus(Ticket.class));
    }

    public Ticket updateTicket(Ticket ticket) {
        checkHasId(ticket);
        return complete(submit(req("PUT", tmpl("/tickets/{id}.json").set("id", ticket.getId()),
                        JSON, json(Collections.singletonMap("ticket", ticket))),
                handle(Ticket.class, "ticket")));
    }

    public ListenableFuture<JobStatus<Ticket>> updateTicketsAsync(List<Ticket> tickets) {
        return submit(req("PUT", cnst("/tickets/update_many.json"), JSON, json(
                Collections.singletonMap("tickets", tickets))), handleJobStatus(Ticket.class));
    }

    public void markTicketAsSpam(Ticket ticket) {
        checkHasId(ticket);
        markTicketAsSpam(ticket.getId());
    }

    public void markTicketAsSpam(long id) {
        complete(submit(req("PUT", tmpl("/tickets/{id}/mark_as_spam.json").set("id", id)), handleStatus()));
    }

    public void deleteTickets(long id, long... ids) {
        complete(submit(req("DELETE", tmpl("/tickets/destroy_many.json{?ids}").set("ids", idArray(id, ids))),
                handleStatus()));
    }

    public JobStatus permanentlyDeleteTickets(long id, long... ids) {
        deleteTickets(id, ids);

        return complete(
                submit(
                        req("DELETE", tmpl("/deleted_tickets/destroy_many.json{?ids}").set("ids", idArray(id, ids))),
                        handleJobStatus(JobStatus.class))
        );
    }

    public Iterable<Ticket> getTickets() {
        return new PagedIterable<>(cnst("/tickets.json"), handleList(Ticket.class, "tickets"));
    }

    /**
     * @deprecated This API is no longer available from the vendor. Use the {@link #getTicketsFromSearch(String)} method instead
     * @param ticketStatus
     * @return
     */
    @Deprecated
    public Iterable<Ticket> getTicketsByStatus(Status... ticketStatus) {
        return new PagedIterable<>(tmpl("/tickets.json{?status}").set("status", statusArray(ticketStatus)),
                handleList(Ticket.class, "tickets"));
    }

    public Iterable<Ticket> getTicketsByExternalId(String externalId, boolean includeArchived) {
        Iterable<Ticket> results =
                new PagedIterable<>(tmpl("/tickets.json{?external_id}").set("external_id", externalId),
                        handleList(Ticket.class, "tickets"));

        if (!includeArchived || results.iterator().hasNext()) {
            return results;
        }
        return new PagedIterable<>(
                tmpl("/search.json{?query}{&type}").set("query", "external_id:" + externalId).set("type", "ticket"),
                handleList(Ticket.class, "results"));
    }

    public Iterable<Ticket> getTicketsByExternalId(String externalId) {
        return getTicketsByExternalId(externalId, false);
    }

    public Iterable<Ticket> getTicketsFromSearch(String searchTerm) {
        return new PagedIterable<>(tmpl("/search.json{?query}").set("query", searchTerm + "+type:ticket"),
                handleList(Ticket.class, "results"));
    }

    public Iterable<Article> getArticleFromSearch(String searchTerm) {
        return new PagedIterable<>(tmpl("/help_center/articles/search.json{?query}").set("query", searchTerm),
                handleList(Article.class, "results"));
    }

    public Iterable<Article> getArticleFromSearch(String searchTerm, Long sectionId) {
        return new PagedIterable<>(tmpl("/help_center/articles/search.json{?section,query}")
                .set("query", searchTerm).set("section", sectionId), handleList(Article.class, "results"));
    }

    public List<ArticleAttachments> getAttachmentsFromArticle(Long articleID) {
        return complete(submit(req("GET", tmpl("/help_center/articles/{id}/attachments.json").set("id", articleID)),
                handleArticleAttachmentsList("article_attachments")));
    }

    public List<Ticket> getTickets(long id, long... ids) {
        return complete(submit(req("GET", tmpl("/tickets/show_many.json{?ids}").set("ids", idArray(id, ids))),
                handleList(Ticket.class, "tickets")));
    }

    public Iterable<Ticket> getRecentTickets() {
        return new PagedIterable<>(cnst("/tickets/recent.json"), handleList(Ticket.class, "tickets"));
    }

    public Iterable<Ticket> getTicketsIncrementally(Date startTime) {
        return new PagedIterable<>(
                tmpl("/incremental/tickets.json{?start_time}").set("start_time", msToSeconds(startTime.getTime())),
                handleIncrementalList(Ticket.class, "tickets"));
    }

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

    public Iterable<Ticket> getUserRequestedTickets(long userId) {
        return new PagedIterable<>(tmpl("/users/{userId}/tickets/requested.json").set("userId", userId),
                handleList(Ticket.class, "tickets"));
    }

    public Iterable<ComplianceDeletionStatus> getComplianceDeletionStatuses(long userId) {
        return new PagedIterable<>(tmpl("/users/{userId}/compliance_deletion_statuses.json").set("userId", userId),
                handleList(ComplianceDeletionStatus.class, "compliance_deletion_statuses"));
    }

    public Iterable<Ticket> getUserCCDTickets(long userId) {
        return new PagedIterable<>(tmpl("/users/{userId}/tickets/ccd.json").set("userId", userId),
                handleList(Ticket.class, "tickets"));
    }

    public UserRelatedInfo getUserRelatedInfo(long userId) {
        return complete(submit(req("GET", tmpl("/users/{userId}/related.json").set("userId", userId)),
                handle(UserRelatedInfo.class, "user_related")));
    }

    public Iterable<Metric> getTicketMetrics() {
        return new PagedIterable<>(cnst("/ticket_metrics.json"), handleList(Metric.class, "ticket_metrics"));
    }

    public Metric getTicketMetricByTicket(long id) {
        return complete(submit(req("GET", tmpl("/tickets/{ticketId}/metrics.json").set("ticketId", id)), handle(Metric.class, "ticket_metric")));
    }

    public Metric getTicketMetric(long id) {
        return complete(submit(req("GET", tmpl("/ticket_metrics/{ticketMetricId}.json").set("ticketMetricId", id)), handle(Metric.class, "ticket_metric")));
    }

    public Iterable<Audit> getTicketAudits(Ticket ticket) {
        checkHasId(ticket);
        return getTicketAudits(ticket.getId());
    }

    public Iterable<Audit> getTicketAudits(Long id) {
        return new PagedIterable<>(tmpl("/tickets/{ticketId}/audits.json").set("ticketId", id),
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
        return complete(submit(req("GET",
                        tmpl("/tickets/{ticketId}/audits/{auditId}.json").set("ticketId", ticketId)
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
        complete(submit(req("PUT", tmpl("/tickets/{ticketId}/audits/{auditId}/trust.json").set("ticketId", ticketId)
                .set("auditId", auditId)), handleStatus()));
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
        complete(submit(req("PUT",
                tmpl("/tickets/{ticketId}/audits/{auditId}/make_private.json").set("ticketId", ticketId)
                        .set("auditId", auditId)), handleStatus()));
    }

    public List<Field> getTicketFields() {
        return complete(submit(req("GET", cnst("/ticket_fields.json")), handleList(Field.class, "ticket_fields")));
    }

    public Field getTicketField(long id) {
        return complete(submit(req("GET", tmpl("/ticket_fields/{id}.json").set("id", id)), handle(Field.class,
                "ticket_field")));
    }

    public Field createTicketField(Field field) {
        return complete(submit(req("POST", cnst("/ticket_fields.json"), JSON, json(
                Collections.singletonMap("ticket_field", field))), handle(Field.class, "ticket_field")));
    }

    public Field updateTicketField(Field field) {
        checkHasId(field);
        return complete(submit(req("PUT", tmpl("/ticket_fields/{id}.json").set("id", field.getId()), JSON,
                json(Collections.singletonMap("ticket_field", field))), handle(Field.class, "ticket_field")));
    }

    public void deleteTicketField(Field field) {
        checkHasId(field);
        deleteTicket(field.getId());
    }

    public void deleteTicketField(long id) {
        complete(submit(req("DELETE", tmpl("/ticket_fields/{id}.json").set("id", id)), handleStatus()));
    }

    public Iterable<SuspendedTicket> getSuspendedTickets() {
        return new PagedIterable<>(cnst("/suspended_tickets.json"),
                handleList(SuspendedTicket.class, "suspended_tickets"));
    }

    public void deleteSuspendedTicket(SuspendedTicket ticket) {
        checkHasId(ticket);
        deleteSuspendedTicket(ticket.getId());
    }

    public void deleteSuspendedTicket(long id) {
        complete(submit(req("DELETE", tmpl("/suspended_tickets/{id}.json").set("id", id)), handleStatus()));
    }

    public Attachment.Upload createUpload(String fileName, byte[] content) {
        return createUpload(null, fileName, "application/binary", content);
    }

    public Attachment.Upload createUpload(String fileName, String contentType, byte[] content) {
        return createUpload(null, fileName, contentType, content);
    }

    public Attachment.Upload createUpload(String token, String fileName, String contentType, byte[] content) {
        TemplateUri uri = tmpl("/uploads.json{?filename,token}").set("filename", fileName);
        if (token != null) {
            uri.set("token", token);
        }
        return complete(
                submit(req("POST", uri, contentType,
                        content), handle(Attachment.Upload.class, "upload")));
    }

  public void associateAttachmentsToArticle(String idArticle, List<Attachment> attachments) {
        TemplateUri uri = tmpl("/help_center/articles/{article_id}/bulk_attachments.json").set("article_id", idArticle);
        List<Long> attachmentsIds = new ArrayList<>();
        for(Attachment item : attachments){
            attachmentsIds.add(item.getId());
        }
        complete(submit(req("POST", uri, JSON, json(Collections.singletonMap("attachment_ids", attachmentsIds))), handleStatus()));
  }

    /**
     * Create upload article with inline false
     */
  public ArticleAttachments createUploadArticle(long articleId, File file) throws IOException {
        return createUploadArticle(articleId, file, false);
  }

  public ArticleAttachments createUploadArticle(long articleId, File file, boolean inline) throws IOException {
        BoundRequestBuilder builder = client.preparePost(tmpl("/help_center/articles/{id}/attachments.json").set("id", articleId).toString());
        if (realm != null) {
            builder.setRealm(realm);
        } else {
            builder.addHeader("Authorization", "Bearer " + oauthToken);
        }
        builder.setHeader("Content-Type", "multipart/form-data");

        if (inline)
            builder.addBodyPart(new StringPart("inline", "true"));

      builder.addBodyPart(
            new FilePart("file", file, "application/octet-stream", Charset.forName("UTF-8"), file.getName()));
        final Request req = builder.build();
        return complete(submit(req, handle(ArticleAttachments.class, "article_attachment")));
    }

    public void deleteUpload(Attachment.Upload upload) {
        checkHasToken(upload);
        deleteUpload(upload.getToken());
    }

    public void deleteUpload(String token) {
        complete(submit(req("DELETE", tmpl("/uploads/{token}.json").set("token", token)), handleStatus()));
    }

    public Attachment getAttachment(Attachment attachment) {
        checkHasId(attachment);
        return getAttachment(attachment.getId());
    }

    public Attachment getAttachment(long id) {
        return complete(submit(req("GET", tmpl("/attachments/{id}.json").set("id", id)), handle(Attachment.class,
                "attachment")));
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
       return complete(submit(req("GET", tmpl("/targets/{id}.json").set("id", id)), handle(Target.class, "target")));
    }

    public Target createTarget(Target target) {
        return complete(submit(req("POST", cnst("/targets.json"), JSON, json(Collections.singletonMap("target", target))),
              handle(Target.class, "target")));
    }

    public void deleteTarget(long targetId) {
       complete(submit(req("DELETE", tmpl("/targets/{id}.json").set("id", targetId)), handleStatus()));
    }

    public Iterable<Trigger> getTriggers() {
        return new PagedIterable<>(cnst("/triggers.json"), handleList(Trigger.class, "triggers"));
    }

    public Trigger getTrigger(long id) {
       return complete(submit(req("GET", tmpl("/triggers/{id}.json").set("id", id)), handle(Trigger.class, "trigger")));
    }

    public Trigger createTrigger(Trigger trigger) {
        return complete(submit(req("POST", cnst("/triggers.json"), JSON, json(Collections.singletonMap("trigger", trigger))),
              handle(Trigger.class, "trigger")));
    }

    public Trigger updateTrigger(Long triggerId, Trigger trigger) {
      return complete(submit(req("PUT", tmpl("/triggers/{id}.json").set("id", triggerId), JSON, json(Collections.singletonMap("trigger", trigger))),
            handle(Trigger.class, "trigger")));
  }

    public void deleteTrigger(long triggerId) {
       complete(submit(req("DELETE", tmpl("/triggers/{id}.json").set("id", triggerId)), handleStatus()));
    }


  // Automations
  public Iterable<Automation> getAutomations() {
    return new PagedIterable<>(cnst("/automations.json"),
            handleList(Automation.class, "automations"));
  }

  public Automation getAutomation(long id) {
    return complete(submit(req("GET", tmpl("/automations/{id}.json").set("id", id)),
        handle(Automation.class, "automation")));
  }

  public Automation createAutomation(Automation automation) {
    return complete(submit(
        req("POST", cnst("/automations.json"), JSON,
            json(Collections.singletonMap("automation", automation))),
        handle(Automation.class, "automation")));
  }

  public Automation updateAutomation(Long automationId, Automation automation) {
    return complete(submit(
        req("PUT", tmpl("/automations/{id}.json").set("id", automationId), JSON,
            json(Collections.singletonMap("automation", automation))),
        handle(Automation.class, "automation")));
  }

  public void deleteAutomation(long automationId) {
    complete(submit(req("DELETE", tmpl("/automations/{id}.json").set("id", automationId)),
        handleStatus()));
  }


    public Iterable<TwitterMonitor> getTwitterMonitors() {
        return new PagedIterable<>(cnst("/channels/twitter/monitored_twitter_handles.json"),
                handleList(TwitterMonitor.class, "monitored_twitter_handles"));
    }


    public Iterable<User> getUsers() {
        return new PagedIterable<>(cnst("/users.json"), handleList(User.class, "users"));
    }

    public Iterable<User> getUsersByRole(String role, String... roles) {
        // Going to have to build this URI manually, because the RFC6570 template spec doesn't support
        // variables like ?role[]=...role[]=..., which is what Zendesk requires.
        // See https://developer.zendesk.com/rest_api/docs/core/users#filters
        final StringBuilder uriBuilder = new StringBuilder("/users.json");
        if (roles.length == 0) {
            uriBuilder.append("?role=").append(encodeUrl(role));
        } else {
            uriBuilder.append("?role[]=").append(encodeUrl(role));
        }
        for (final String curRole : roles) {
            uriBuilder.append("&role[]=").append(encodeUrl(curRole));
        }
        return new PagedIterable<>(cnst(uriBuilder.toString()), handleList(User.class, "users"));
    }

    public Iterable<User> getUsersIncrementally(Date startTime) {
        return new PagedIterable<>(
                tmpl("/incremental/users.json{?start_time}").set("start_time", msToSeconds(startTime.getTime())),
                handleIncrementalList(User.class, "users"));
    }

    public Iterable<User> getGroupUsers(long id) {
        return new PagedIterable<>(tmpl("/groups/{id}/users.json").set("id", id), handleList(User.class, "users"));
    }

    public Iterable<User> getOrganizationUsers(long id) {
        return new PagedIterable<>(tmpl("/organizations/{id}/users.json").set("id", id),
                handleList(User.class, "users"));
    }

    public User getUser(long id) {
        return complete(submit(req("GET", tmpl("/users/{id}.json").set("id", id)), handle(User.class, "user")));
    }

    public User getAuthenticatedUser() {
        return complete(submit(req("GET", cnst("/users/me.json")), handle(User.class, "user")));
    }

    public Iterable<UserField> getUserFields() {
        return complete(submit(req("GET", cnst("/user_fields.json")),
                handleList(UserField.class, "user_fields")));
    }

    public User createUser(User user) {
        return complete(submit(req("POST", cnst("/users.json"), JSON, json(
                Collections.singletonMap("user", user))), handle(User.class, "user")));
    }

    public JobStatus<User> createUsers(User... users) {
        return createUsers(Arrays.asList(users));
    }

    public JobStatus<User> createUsers(List<User> users) {
        return complete(createUsersAsync(users));
    }

    public User createOrUpdateUser(User user) {
        return complete(submit(req("POST", cnst("/users/create_or_update.json"), JSON, json(
                Collections.singletonMap("user", user))), handle(User.class, "user")));
    }

    public ListenableFuture<JobStatus<User>> createUsersAsync(List<User> users) {
        return submit(req("POST", cnst("/users/create_many.json"), JSON, json(
                Collections.singletonMap("users", users))), handleJobStatus(User.class));
    }

    public User updateUser(User user) {
        checkHasId(user);
        return complete(submit(req("PUT", tmpl("/users/{id}.json").set("id", user.getId()), JSON, json(
                Collections.singletonMap("user", user))), handle(User.class, "user")));
    }

    public void deleteUser(User user) {
        checkHasId(user);
        deleteUser(user.getId());
    }

    public void deleteUser(long id) {
        complete(submit(req("DELETE", tmpl("/users/{id}.json").set("id", id)), handleStatus()));
    }

    public User permanentlyDeleteUser(User user) {
        checkHasId(user);
        return permanentlyDeleteUser(user.getId());
    }

    public User permanentlyDeleteUser(long id) {
        deleteUser(id);
        return complete(submit(req("DELETE", tmpl("/deleted_users/{id}.json").set("id", id)), handle(User.class)));
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
        return new PagedIterable<>(tmpl("/users/search.json{?query}").set("query", email),
                handleList(User.class, "users"));
    }

    public Iterable<User> lookupUserByExternalId(String externalId) {
        return new PagedIterable<>(tmpl("/users/search.json{?external_id}").set("external_id", externalId),
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
        complete(submit(req("POST", tmpl("/users/{id}/password.json").set("id", id), JSON,
                json(Collections.singletonMap("password", password))), handleStatus()));
    }

    public void changeUserPassword(User user, String oldPassword, String newPassword) {
        checkHasId(user);
        Map<String, String> req = new HashMap<>();
        req.put("previous_password", oldPassword);
        req.put("password", newPassword);
        complete(submit(req("PUT", tmpl("/users/{id}/password.json").set("id", user.getId()), JSON,
                json(req)), handleStatus()));
    }

    public List<Identity> getUserIdentities(User user) {
        checkHasId(user);
        return getUserIdentities(user.getId());
    }

    public List<Identity> getUserIdentities(long userId) {
        return complete(submit(req("GET", tmpl("/users/{id}/identities.json").set("id", userId)),
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
        return complete(submit(req("GET", tmpl("/users/{userId}/identities/{identityId}.json").set("userId", userId)
                .set("identityId", identityId)), handle(
                Identity.class, "identity")));
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
        return complete(submit(req("PUT",
                        tmpl("/users/{userId}/identities/{identityId}/make_primary.json").set("userId", userId)
                                .set("identityId", identityId), JSON, null),
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
        return complete(submit(req("PUT", tmpl("/users/{userId}/identities/{identityId}/verify.json")
                .set("userId", userId)
                .set("identityId", identityId), JSON, null), handle(Identity.class, "identity")));
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
        return complete(submit(req("PUT", tmpl("/users/{userId}/identities/{identityId}/request_verification.json")
                .set("userId", userId)
                .set("identityId", identityId), JSON, null), handle(Identity.class, "identity")));
    }

    public Identity updateUserIdentity(long userId, Identity identity) {
        checkHasId(identity);
        return complete(submit(req("PUT", tmpl("/users/{userId}/identities/{identityId}.json")
                .set("userId", userId)
                .set("identityId", identity.getId()), JSON, null), handle(Identity.class, "identity")));
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
        complete(submit(req("DELETE", tmpl("/users/{userId}/identities/{identityId}.json")
                        .set("userId", userId)
                        .set("identityId", identityId)
        ), handleStatus()));
    }

    public Identity createUserIdentity(long userId, Identity identity) {
        return complete(submit(req("POST", tmpl("/users/{userId}/identities.json").set("userId", userId), JSON,
                json(Collections.singletonMap("identity", identity))), handle(Identity.class, "identity")));
    }

    public Identity createUserIdentity(User user, Identity identity) {
        return complete(submit(req("POST", tmpl("/users/{userId}/identities.json").set("userId", user.getId()), JSON,
                json(Collections.singletonMap("identity", identity))), handle(Identity.class, "identity")));
    }

    public Iterable<AgentRole> getCustomAgentRoles() {
        return new PagedIterable<>(cnst("/custom_roles.json"),
                handleList(AgentRole.class, "custom_roles"));
    }

    public Iterable<org.zendesk.client.v2.model.Request> getRequests() {
        return new PagedIterable<>(cnst("/requests.json"),
                handleList(org.zendesk.client.v2.model.Request.class, "requests"));
    }

    public Iterable<org.zendesk.client.v2.model.Request> getOpenRequests() {
        return new PagedIterable<>(cnst("/requests/open.json"),
                handleList(org.zendesk.client.v2.model.Request.class, "requests"));
    }

    public Iterable<org.zendesk.client.v2.model.Request> getSolvedRequests() {
        return new PagedIterable<>(cnst("/requests/solved.json"),
                handleList(org.zendesk.client.v2.model.Request.class, "requests"));
    }

    public Iterable<org.zendesk.client.v2.model.Request> getCCRequests() {
        return new PagedIterable<>(cnst("/requests/ccd.json"),
                handleList(org.zendesk.client.v2.model.Request.class, "requests"));
    }

    public Iterable<org.zendesk.client.v2.model.Request> getUserRequests(User user) {
        checkHasId(user);
        return getUserRequests(user.getId());
    }

    public Iterable<org.zendesk.client.v2.model.Request> getUserRequests(long id) {
        return new PagedIterable<>(tmpl("/users/{id}/requests.json").set("id", id),
                handleList(org.zendesk.client.v2.model.Request.class, "requests"));
    }

    public org.zendesk.client.v2.model.Request getRequest(long id) {
        return complete(submit(req("GET", tmpl("/requests/{id}.json").set("id", id)),
                handle(org.zendesk.client.v2.model.Request.class, "request")));
    }

    public org.zendesk.client.v2.model.Request createRequest(org.zendesk.client.v2.model.Request request) {
        return complete(submit(req("POST", cnst("/requests.json"),
                        JSON, json(Collections.singletonMap("request", request))),
                handle(org.zendesk.client.v2.model.Request.class, "request")));
    }

    public org.zendesk.client.v2.model.Request updateRequest(org.zendesk.client.v2.model.Request request) {
        checkHasId(request);
        return complete(submit(req("PUT", tmpl("/requests/{id}.json").set("id", request.getId()),
                        JSON, json(Collections.singletonMap("request", request))),
                handle(org.zendesk.client.v2.model.Request.class, "request")));
    }

    public Iterable<Comment> getRequestComments(org.zendesk.client.v2.model.Request request) {
        checkHasId(request);
        return getRequestComments(request.getId());
    }

    public Iterable<Comment> getRequestComments(long id) {
        return new PagedIterable<>(tmpl("/requests/{id}/comments.json").set("id", id),
                handleList(Comment.class, "comments"));
    }

    public Iterable<Comment> getTicketComments(long id) {
        return new PagedIterable<>(tmpl("/tickets/{id}/comments.json").set("id", id),
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
        return complete(submit(req("GET", tmpl("/requests/{requestId}/comments/{commentId}.json")
                        .set("requestId", requestId)
                        .set("commentId", commentId)),
                handle(Comment.class, "comment")));
    }

    public Ticket createComment(long ticketId, Comment comment) {
        Ticket ticket = new Ticket();
        ticket.setComment(comment);
        return complete(submit(req("PUT", tmpl("/tickets/{id}.json").set("id", ticketId), JSON,
              json(Collections.singletonMap("ticket", ticket))),
              handle(Ticket.class, "ticket")));
    }

    public Ticket createTicketFromTweet(long tweetId, long monitorId) {
       Map<String,Object> map = new HashMap<>();
       map.put("twitter_status_message_id", tweetId);
       map.put("monitored_twitter_handle_id", monitorId);

       return complete(submit(req("POST", cnst("/channels/twitter/tickets.json"), JSON,
             json(Collections.singletonMap("ticket", map))),
             handle(Ticket.class, "ticket")));
    }

    public Iterable<Organization> getOrganizations() {
        return new PagedIterable<>(cnst("/organizations.json"),
                handleList(Organization.class, "organizations"));
    }

    public Iterable<Organization> getOrganizationsIncrementally(Date startTime) {
        return new PagedIterable<>(
                tmpl("/incremental/organizations.json{?start_time}")
                        .set("start_time", msToSeconds(startTime.getTime())),
                handleIncrementalList(Organization.class, "organizations"));
    }

    public Iterable<OrganizationField> getOrganizationFields() {
        //The organization_fields api doesn't seem to support paging
        return complete(submit(req("GET", cnst("/organization_fields.json")),
                handleList(OrganizationField.class, "organization_fields")));
    }

    public Iterable<Organization> getAutoCompleteOrganizations(String name) {
        if (name == null || name.length() < 2) {
            throw new IllegalArgumentException("Name must be at least 2 characters long");
        }
        return new PagedIterable<>(tmpl("/organizations/autocomplete.json{?name}").set("name", name),
                handleList(Organization.class, "organizations"));
    }

    // TODO getOrganizationRelatedInformation

    public Organization getOrganization(long id) {
        return complete(submit(req("GET", tmpl("/organizations/{id}.json").set("id", id)),
                handle(Organization.class, "organization")));
    }

    public Organization createOrganization(Organization organization) {
        return complete(submit(req("POST", cnst("/organizations.json"), JSON, json(
                Collections.singletonMap("organization", organization))), handle(Organization.class, "organization")));
    }

    public JobStatus<Organization> createOrganizations(Organization... organizations) {
        return createOrganizations(Arrays.asList(organizations));
    }

    public JobStatus createOrganizations(List<Organization> organizations) {
        return complete(createOrganizationsAsync(organizations));
    }

    public ListenableFuture<JobStatus<Organization>> createOrganizationsAsync(List<Organization> organizations) {
        return submit(req("POST", cnst("/organizations/create_many.json"), JSON, json(
                Collections.singletonMap("organizations", organizations))), handleJobStatus(Organization.class));
    }

    public Organization updateOrganization(Organization organization) {
        checkHasId(organization);
        return complete(submit(req("PUT", tmpl("/organizations/{id}.json").set("id", organization.getId()), JSON, json(
                Collections.singletonMap("organization", organization))), handle(Organization.class, "organization")));
    }

    public void deleteOrganization(Organization organization) {
        checkHasId(organization);
        deleteOrganization(organization.getId());
    }

    public void deleteOrganization(long id) {
        complete(submit(req("DELETE", tmpl("/organizations/{id}.json").set("id", id)), handleStatus()));
    }

    public Iterable<Organization> lookupOrganizationsByExternalId(String externalId) {
        if (externalId == null || externalId.length() < 2) {
            throw new IllegalArgumentException("Name must be at least 2 characters long");
        }
        return new PagedIterable<>(
                tmpl("/organizations/search.json{?external_id}").set("external_id", externalId),
                handleList(Organization.class, "organizations"));
    }

    public Iterable<OrganizationMembership> getOrganizationMemberships() {
        return new PagedIterable<>(cnst("/organization_memberships.json"),
                handleList(OrganizationMembership.class, "organization_memberships"));
    }

    public Iterable<OrganizationMembership> getOrganizationMembershipsForOrg(long organization_id) {
            return new PagedIterable<>(tmpl("/organizations/{organization_id}/organization_memberships.json")
                    .set("organization_id", organization_id),
                    handleList(OrganizationMembership.class, "organization_memberships"));
    }

    public Iterable<OrganizationMembership> getOrganizationMembershipsForUser(long user_id) {
            return new PagedIterable<>(tmpl("/users/{user_id}/organization_memberships.json").set("user_id", user_id),
                    handleList(OrganizationMembership.class, "organization_memberships"));
    }

    public OrganizationMembership getOrganizationMembershipForUser(long user_id, long id) {
        return complete(submit(req("GET",
                tmpl("/users/{user_id}/organization_memberships/{id}.json").set("user_id", user_id).set("id", id)),
                handle(OrganizationMembership.class, "organization_membership")));
    }

    public OrganizationMembership getOrganizationMembership(long id) {
        return complete(submit(req("GET",
                tmpl("/organization_memberships/{id}.json").set("id", id)),
                handle(OrganizationMembership.class, "organization_membership")));
    }

    public OrganizationMembership createOrganizationMembership(OrganizationMembership organizationMembership) {
        return complete(submit(req("POST",
                cnst("/organization_memberships.json"), JSON, json(
                        Collections.singletonMap("organization_membership",
                                organizationMembership))), handle(OrganizationMembership.class, "organization_membership")));
    }

    public void deleteOrganizationMembership(long id) {
        complete(submit(req("DELETE", tmpl("/organization_memberships/{id}.json").set("id", id)), handleStatus()));
    }

    public Iterable<Group> getGroups() {
        return new PagedIterable<>(cnst("/groups.json"),
                handleList(Group.class, "groups"));
    }

    public Iterable<Group> getAssignableGroups() {
        return new PagedIterable<>(cnst("/groups/assignable.json"),
                handleList(Group.class, "groups"));
    }

    public Group getGroup(long id) {
        return complete(submit(req("GET", tmpl("/groups/{id}.json").set("id", id)),
                handle(Group.class, "group")));
    }

    public Group createGroup(Group group) {
        return complete(submit(req("POST", cnst("/groups.json"), JSON, json(
                Collections.singletonMap("group", group))), handle(Group.class, "group")));
    }

    /**
     * This API will be removed in a future release.  The API endpoint does not exist.
     * Instead, the {@link #createGroup(Group) createGroup} method should be called for each Group
     *
     * @see <a href="https://github.com/cloudbees/zendesk-java-client/issues/111">Zendesk Java Client Issue #111</a>
     */
    @Deprecated
    public List<Group> createGroups(Group... groups) {
        return createGroups(Arrays.asList(groups));
    }

    /**
     * This API will be removed in a future release.  The API endpoint does not exist.
     * Instead, the {@link #createGroup(Group) createGroup} method should be called for each Group
     *
     * @see <a href="https://github.com/cloudbees/zendesk-java-client/issues/111">Zendesk Java Client Issue #111</a>
     */
    @Deprecated
    public List<Group> createGroups(List<Group> groups) {
        throw new ZendeskException("API Endpoint for createGroups does not exist.");
    }

    public Group updateGroup(Group group) {
        checkHasId(group);
        return complete(submit(req("PUT", tmpl("/groups/{id}.json").set("id", group.getId()), JSON, json(
                Collections.singletonMap("group", group))), handle(Group.class, "group")));
    }

    public void deleteGroup(Group group) {
        checkHasId(group);
        deleteGroup(group.getId());
    }

    public void deleteGroup(long id) {
        complete(submit(req("DELETE", tmpl("/groups/{id}.json").set("id", id)), handleStatus()));
    }

    public Iterable<Macro> getMacros(){
        return new PagedIterable<>(cnst("/macros.json"),
                handleList(Macro.class, "macros"));
    }

    public Macro getMacro(long macroId){

      return complete(submit(req("GET", tmpl("/macros/{id}.json").set("id", macroId)), handle(Macro.class, "macro")));
  }

  public Macro createMacro(Macro macro) {
    return complete(submit(
        req("POST", cnst("/macros.json"), JSON, json(Collections.singletonMap("macro", macro))),
        handle(Macro.class, "macro")));
  }

  public Macro updateMacro(Long macroId, Macro macro) {
    return complete(submit(req("PUT", tmpl("/macros/{id}.json").set("id", macroId), JSON,
        json(Collections.singletonMap("macro", macro))), handle(Macro.class, "macro")));
  }


    public Ticket macrosShowChangesToTicket(long macroId) {
        return complete(submit(req("GET", tmpl("/macros/{id}/apply.json").set("id", macroId)),
                handle(TicketResult.class, "result"))).getTicket();
    }

    public Ticket macrosShowTicketAfterChanges(long ticketId, long macroId) {
        return complete(submit(req("GET", tmpl("/tickets/{ticket_id}/macros/{id}/apply.json")
                        .set("ticket_id", ticketId)
                        .set("id", macroId)),
                handle(TicketResult.class, "result"))).getTicket();
    }

    public List<String> addTagToTicket(long id, String... tags) {
        return complete(submit(
                req("PUT", tmpl("/tickets/{id}/tags.json").set("id", id), JSON,
                        json(Collections.singletonMap("tags", tags))),
                handle(List.class, "tags")));
    }

    public List<String> addTagToTopics(long id, String... tags) {
        return complete(submit(
                req("PUT", tmpl("/topics/{id}/tags.json").set("id", id), JSON,
                        json(Collections.singletonMap("tags", tags))),
                handle(List.class, "tags")));
    }

    public List<String> addTagToOrganisations(long id, String... tags) {
        return complete(submit(
                req("PUT", tmpl("/organizations/{id}/tags.json").set("id", id),
                        JSON, json(Collections.singletonMap("tags", tags))),
                handle(List.class, "tags")));
    }

    public List<String> setTagOnTicket(long id, String... tags) {
        return complete(submit(
                req("POST", tmpl("/tickets/{id}/tags.json").set("id", id),
                        JSON, json(Collections.singletonMap("tags", tags))),
                handle(List.class, "tags")));
    }

    public List<String> setTagOnTopics(long id, String... tags) {
        return complete(submit(
                req("POST", tmpl("/topics/{id}/tags.json").set("id", id), JSON,
                        json(Collections.singletonMap("tags", tags))),
                handle(List.class, "tags")));
    }

    public List<String> setTagOnOrganisations(long id, String... tags) {
        return complete(submit(
                req("POST",
                        tmpl("/organizations/{id}/tags.json").set("id", id),
                        JSON, json(Collections.singletonMap("tags", tags))),
                handle(List.class, "tags")));
    }

    public List<String> removeTagFromTicket(long id, String... tags) {
        return complete(submit(
                req("DELETE", tmpl("/tickets/{id}/tags.json").set("id", id),
                        JSON, json(Collections.singletonMap("tags", tags))),
                handle(List.class, "tags")));
    }

    public List<String> removeTagFromTopics(long id, String... tags) {
        return complete(submit(
                req("DELETE", tmpl("/topics/{id}/tags.json").set("id", id),
                        JSON, json(Collections.singletonMap("tags", tags))),
                handle(List.class, "tags")));
    }

    public List<String> removeTagFromOrganisations(long id, String... tags) {
        return complete(submit(
                req("DELETE",
                        tmpl("/organizations/{id}/tags.json").set("id", id),
                        JSON, json(Collections.singletonMap("tags", tags))),
                handle(List.class, "tags")));
    }

    public Map getIncrementalTicketsResult(long unixEpochTime) {
        return complete(submit(
                req("GET",
                        tmpl("/exports/tickets.json?start_time={time}").set(
                                "time", unixEpochTime)), handle(Map.class)));
    }

    public Iterable<GroupMembership> getGroupMemberships() {
        return new PagedIterable<>(cnst("/group_memberships.json"),
                handleList(GroupMembership.class, "group_memberships"));
    }

    public List<GroupMembership> getGroupMembershipByUser(long user_id) {
        return complete(submit(req("GET", tmpl("/users/{user_id}/group_memberships.json").set("user_id", user_id)),
                handleList(GroupMembership.class, "group_memberships")));
    }

    public List<GroupMembership> getGroupMemberships(long group_id) {
        return complete(submit(req("GET", tmpl("/groups/{group_id}/memberships.json").set("group_id", group_id)),
                handleList(GroupMembership.class, "group_memberships")));
    }

    public Iterable<GroupMembership> getAssignableGroupMemberships() {
        return new PagedIterable<>(cnst("/group_memberships/assignable.json"),
                handleList(GroupMembership.class, "group_memberships"));
    }

    public List<GroupMembership> getAssignableGroupMemberships(long group_id) {
        return complete(submit(req("GET",
                        tmpl("/groups/{group_id}/memberships/assignable.json").set("group_id", group_id)),
                handleList(GroupMembership.class, "group_memberships")));
    }

    public GroupMembership getGroupMembership(long id) {
        return complete(submit(req("GET", tmpl("/group_memberships/{id}.json").set("id", id)),
                handle(GroupMembership.class, "group_membership")));
    }

    public GroupMembership getGroupMembership(long user_id, long group_membership_id) {
        return complete(submit(req("GET", tmpl("/users/{uid}/group_memberships/{gmid}.json").set("uid", user_id)
                        .set("gmid", group_membership_id)),
                handle(GroupMembership.class, "group_membership")));
    }

    public GroupMembership createGroupMembership(GroupMembership groupMembership) {
        return complete(submit(req("POST", cnst("/group_memberships.json"), JSON, json(
                        Collections.singletonMap("group_membership", groupMembership))),
                handle(GroupMembership.class, "group_membership")));
    }

    public GroupMembership createGroupMembership(long user_id, GroupMembership groupMembership) {
        return complete(submit(req("POST", tmpl("/users/{id}/group_memberships.json").set("id", user_id), JSON,
                        json(Collections.singletonMap("group_membership", groupMembership))),
                handle(GroupMembership.class, "group_membership")));
    }

    public void deleteGroupMembership(GroupMembership groupMembership) {
        checkHasId(groupMembership);
        deleteGroupMembership(groupMembership.getId());
    }

    public void deleteGroupMembership(long id) {
        complete(submit(req("DELETE", tmpl("/group_memberships/{id}.json").set("id", id)), handleStatus()));
    }

    public void deleteGroupMembership(long user_id, GroupMembership groupMembership) {
        checkHasId(groupMembership);
        deleteGroupMembership(user_id, groupMembership.getId());
    }

    public void deleteGroupMembership(long user_id, long group_membership_id) {
        complete(submit(req("DELETE", tmpl("/users/{uid}/group_memberships/{gmid}.json").set("uid", user_id)
                .set("gmid", group_membership_id)), handleStatus()));
    }

    public List<GroupMembership> setGroupMembershipAsDefault(long user_id, GroupMembership groupMembership) {
        checkHasId(groupMembership);
        return complete(submit(req("PUT", tmpl("/users/{uid}/group_memberships/{gmid}/make_default.json")
                        .set("uid", user_id).set("gmid", groupMembership.getId()), JSON, json(
                        Collections.singletonMap("group_memberships", groupMembership))),
                handleList(GroupMembership.class, "results")));
    }

    public Iterable<Forum> getForums() {
        return new PagedIterable<>(cnst("/forums.json"), handleList(Forum.class, "forums"));
    }

    public List<Forum> getForums(long category_id) {
        return complete(submit(req("GET", tmpl("/categories/{id}/forums.json").set("id", category_id)),
                handleList(Forum.class, "forums")));
    }

    public Forum getForum(long id) {
        return complete(submit(req("GET", tmpl("/forums/{id}.json").set("id", id)),
                handle(Forum.class, "forum")));
    }

    public Forum createForum(Forum forum) {
        return complete(submit(req("POST", cnst("/forums.json"), JSON, json(
                Collections.singletonMap("forum", forum))), handle(Forum.class, "forum")));
    }

    public Forum updateForum(Forum forum) {
        checkHasId(forum);
        return complete(submit(req("PUT", tmpl("/forums/{id}.json").set("id", forum.getId()), JSON, json(
                Collections.singletonMap("forum", forum))), handle(Forum.class, "forum")));
    }

    public void deleteForum(Forum forum) {
        checkHasId(forum);
        complete(submit(req("DELETE", tmpl("/forums/{id}.json").set("id", forum.getId())), handleStatus()));
    }

    public Iterable<Topic> getTopics() {
        return new PagedIterable<>(cnst("/topics.json"), handleList(Topic.class, "topics"));
    }

    public List<Topic> getTopics(long forum_id) {
        return complete(submit(req("GET", tmpl("/forums/{id}/topics.json").set("id", forum_id)),
                handleList(Topic.class, "topics")));
    }

    public List<Topic> getTopicsByUser(long user_id) {
        return complete(submit(req("GET", tmpl("/users/{id}/topics.json").set("id", user_id)),
                handleList(Topic.class, "topics")));
    }

    public Topic getTopic(long id) {
        return complete(submit(req("GET", tmpl("/topics/{id}.json").set("id", id)),
                handle(Topic.class, "topic")));
    }

    public Topic createTopic(Topic topic) {
        checkHasId(topic);
        return complete(submit(req("POST", cnst("/topics.json"), JSON, json(
                Collections.singletonMap("topic", topic))), handle(Topic.class, "topic")));
    }

    public Topic importTopic(Topic topic) {
        checkHasId(topic);
        return complete(submit(req("POST", cnst("/import/topics.json"), JSON, json(
                Collections.singletonMap("topic", topic))), handle(Topic.class, "topic")));
    }

    public List<Topic> getTopics(long id, long... ids) {
        return complete(submit(req("POST", tmpl("/topics/show_many.json{?ids}").set("ids", idArray(id, ids))),
                handleList(Topic.class, "topics")));
    }

    public Topic updateTopic(Topic topic) {
        checkHasId(topic);
        return complete(submit(req("PUT", tmpl("/topics/{id}.json").set("id", topic.getId()), JSON, json(
                Collections.singletonMap("topic", topic))), handle(Topic.class, "topic")));
    }

    public void deleteTopic(Topic topic) {
        checkHasId(topic);
        complete(submit(req("DELETE", tmpl("/topics/{id}.json").set("id", topic.getId())), handleStatus()));
    }

    // https://support.zendesk.com/hc/communities/public/posts/203464106-Managing-Organization-Memberships-via-the-Zendesk-API
    public List<OrganizationMembership> getOrganizationMembershipByUser(long user_id) {
	return complete(submit(req("GET", tmpl("/users/{user_id}/organization_memberships.json").set("user_id", user_id)),
		handleList(OrganizationMembership.class, "organization_memberships")));
    }

    public OrganizationMembership getGroupOrganization(long user_id, long organization_membership_id) {
	return complete(submit(req("GET", tmpl("/users/{uid}/organization_memberships/{oid}.json").set("uid", user_id)
			.set("oid", organization_membership_id)),
		handle(OrganizationMembership.class, "organization_membership")));
    }

    public OrganizationMembership createOrganizationMembership(long user_id, OrganizationMembership organizationMembership) {
	return complete(submit(req("POST", tmpl("/users/{id}/organization_memberships.json").set("id", user_id), JSON,
			json(Collections.singletonMap("organization_membership", organizationMembership))),
		handle(OrganizationMembership.class, "organization_membership")));
    }

    public void deleteOrganizationMembership(long user_id, OrganizationMembership organizationMembership) {
	checkHasId(organizationMembership);
	deleteOrganizationMembership(user_id, organizationMembership.getId());
    }

    public void deleteOrganizationMembership(long user_id, long organization_membership_id) {
	complete(submit(req("DELETE", tmpl("/users/{uid}/organization_memberships/{oid}.json").set("uid", user_id)
		.set("oid", organization_membership_id)), handleStatus()));
    }

    public List<OrganizationMembership> setOrganizationMembershipAsDefault(long user_id, OrganizationMembership organizationMembership) {
	checkHasId(organizationMembership);
	return complete(submit(req("PUT", tmpl("/users/{uid}/organization_memberships/{omid}/make_default.json")
			.set("uid", user_id).set("omid", organizationMembership.getId()), JSON, json(
			Collections.singletonMap("organization_memberships", organizationMembership))),
		handleList(OrganizationMembership.class, "results")));
    }
    //-- END BETA

    public Iterable<SearchResultEntity> getSearchResults(String query) {
        return new PagedIterable<>(tmpl("/search.json{?query}").set("query", query),
                handleSearchList("results"));
    }

    public <T extends SearchResultEntity> Iterable<T> getSearchResults(Class<T> type, String query) {
        return getSearchResults(type, query, null);
    }

    public <T extends SearchResultEntity> Iterable<T> getSearchResults(Class<T> type, String query, String params) {
        String typeName = null;
        for (Map.Entry<String, Class<? extends SearchResultEntity>> entry : searchResultTypes.entrySet()) {
            if (type.equals(entry.getValue())) {
                typeName = entry.getKey();
                break;
            }
        }
        if (typeName == null) {
            return Collections.emptyList();
        }
        return new PagedIterable<>(tmpl("/search.json{?query,params}")
                .set("query", query + "+type:" + typeName)
                .set("params", params),
                handleList(type, "results"));
    }

    public void notifyApp(String json) {
       complete(submit(req("POST", cnst("/apps/notify.json"), JSON, json.getBytes()), handleStatus()));
    }

    public void updateInstallation(int id, String json) {
       complete(submit(req("PUT", tmpl("/apps/installations/{id}.json").set("id", id), JSON, json.getBytes()), handleStatus()));
    }

    public Iterable<SatisfactionRating> getSatisfactionRatings() {
        return new PagedIterable<>(cnst("/satisfaction_ratings.json"),
                handleList(SatisfactionRating.class, "satisfaction_ratings"));
    }

    public SatisfactionRating getSatisfactionRating(long id) {
        return complete(submit(req("GET", tmpl("/satisfaction_ratings/{id}.json").set("id", id)),
                handle(SatisfactionRating.class, "satisfaction_rating")));
    }

    public SatisfactionRating createSatisfactionRating(long ticketId, SatisfactionRating satisfactionRating) {
        return complete(submit(req("POST", tmpl("/tickets/{ticketId}/satisfaction_rating.json")
                        .set("ticketId", ticketId), JSON,
                json(Collections.singletonMap("satisfaction_rating", satisfactionRating))),
                handle(SatisfactionRating.class, "satisfaction_rating")));
    }

    public SatisfactionRating createSatisfactionRating(Ticket ticket, SatisfactionRating satisfactionRating) {
        return createSatisfactionRating(ticket.getId(), satisfactionRating);
    }

    // TODO search with sort order
    // TODO search with query building API

    //////////////////////////////////////////////////////////////////////
    // Action methods for Help Center
    //////////////////////////////////////////////////////////////////////

    /**
     * Get all articles from help center.
     *
     * @return List of Articles.
     */
    public Iterable<Article> getArticles() {
        return new PagedIterable<>(cnst("/help_center/articles.json"),
                handleList(Article.class, "articles"));
    }

    public Iterable<Article> getArticles(Category category) {
        checkHasId(category);
        return new PagedIterable<>(
                tmpl("/help_center/categories/{id}/articles.json").set("id", category.getId()),
                handleList(Article.class, "articles"));
    }

    public Iterable<Article> getArticlesIncrementally(Date startTime) {
      return new PagedIterable<>(
              tmpl("/help_center/incremental/articles.json{?start_time}")
                      .set("start_time", msToSeconds(startTime.getTime())),
              handleIncrementalList(Article.class, "articles"));
    }

    public List<Article> getArticlesFromPage(int page) {
        return complete(submit(req("GET", tmpl("/help_center/articles.json?page={page}").set("page", page)),
                handleList(Article.class, "articles")));
    }

    public Article getArticle(long id) {
        return complete(submit(req("GET", tmpl("/help_center/articles/{id}.json").set("id", id)),
                handle(Article.class, "article")));
    }

    public Iterable<Translation> getArticleTranslations(Long articleId) {
        return new PagedIterable<>(
                tmpl("/help_center/articles/{articleId}/translations.json").set("articleId", articleId),
                handleList(Translation.class, "translations"));
    }
    public Article createArticle(Article article) {
        checkHasSectionId(article);
        return complete(submit(req("POST", tmpl("/help_center/sections/{id}/articles.json").set("id", article.getSectionId()),
                JSON, json(Collections.singletonMap("article", article))), handle(Article.class, "article")));
    }

    public Article updateArticle(Article article) {
        checkHasId(article);
        return complete(submit(req("PUT", tmpl("/help_center/articles/{id}.json").set("id", article.getId()),
                JSON, json(Collections.singletonMap("article", article))), handle(Article.class, "article")));
    }

    public Translation createArticleTranslation(Long articleId, Translation translation) {
        checkHasArticleId(articleId);
        return complete(submit(req("POST", tmpl("/help_center/articles/{id}/translations.json").set("id", articleId),
                JSON, json(Collections.singletonMap("translation", translation))), handle(Translation.class, "translation")));
    }

    public Translation updateArticleTranslation(Long articleId, String locale, Translation translation) {
        checkHasId(translation);
        return complete(submit(req("PUT", tmpl("/help_center/articles/{id}/translations/{locale}.json").set("id", articleId).set("locale",locale),
                JSON, json(Collections.singletonMap("translation", translation))), handle(Translation.class, "translation")));
    }

    public void deleteArticle(Article article) {
        checkHasId(article);
        complete(submit(req("DELETE", tmpl("/help_center/articles/{id}.json").set("id", article.getId())),
                handleStatus()));
    }

    /**
     * Delete attachment from article.
     * @param attachment
     */
    public void deleteArticleAttachment(ArticleAttachments attachment) {
        checkHasId(attachment);
        deleteArticleAttachment(attachment.getId());
    }

    /**
     * Delete attachment from article.
     * @param id attachment identifier.
     */
    public void deleteArticleAttachment(long id) {
        complete(submit(req("DELETE", tmpl("/help_center/articles/attachments/{id}.json").set("id", id)), handleStatus()));
    }

    public Iterable<Category> getCategories() {
        return new PagedIterable<>(cnst("/help_center/categories.json"),
                handleList(Category.class, "categories"));
    }

    public Category getCategory(long id) {
        return complete(submit(req("GET", tmpl("/help_center/categories/{id}.json").set("id", id)),
                handle(Category.class, "category")));
    }

    public Iterable<Translation> getCategoryTranslations(Long categoryId) {
        return new PagedIterable<>(
                tmpl("/help_center/categories/{categoryId}/translations.json").set("categoryId", categoryId),
                handleList(Translation.class, "translations"));
    }
    public Category createCategory(Category category) {
        return complete(submit(req("POST", cnst("/help_center/categories.json"),
                JSON, json(Collections.singletonMap("category", category))), handle(Category.class, "category")));
    }

    public Category updateCategory(Category category) {
        checkHasId(category);
        return complete(submit(req("PUT", tmpl("/help_center/categories/{id}.json").set("id", category.getId()),
                JSON, json(Collections.singletonMap("category", category))), handle(Category.class, "category")));
    }

    public Translation createCategoryTranslation(Long categoryId, Translation translation) {
        checkHasCategoryId(categoryId);
        return complete(submit(req("POST", tmpl("/help_center/categories/{id}/translation.json").set("id", categoryId),
                JSON, json(Collections.singletonMap("translation", translation))), handle(Translation.class, "translation")));
    }

    public Translation updateCategoryTranslation(Long categoryId, String locale, Translation translation) {
        checkHasId(translation);
        return complete(submit(req("PUT", tmpl("/help_center/categories/{id}/translations/{locale}.json").set("id", categoryId).set("locale",locale),
                JSON, json(Collections.singletonMap("translation", translation))), handle(Translation.class, "translation")));
    }

    public void deleteCategory(Category category) {
        checkHasId(category);
        complete(submit(req("DELETE", tmpl("/help_center/categories/{id}.json").set("id", category.getId())),
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
        return complete(submit(req("GET", tmpl("/help_center/sections/{id}.json").set("id", id)),
                handle(Section.class, "section")));
    }

    public Iterable<Translation> getSectionTranslations(Long sectionId) {
        return new PagedIterable<>(
                tmpl("/help_center/sections/{sectionId}/translations.json").set("sectionId", sectionId),
                handleList(Translation.class, "translations"));
    }
    public Section createSection(Section section) {
        checkHasCategoryId(section);
        return complete(submit(req("POST", tmpl("/help_center/categories/{id}/sections.json").set("id", section.getCategoryId()),
                JSON, json(Collections.singletonMap("section", section))), handle(Section.class, "section")));
    }

    public Section updateSection(Section section) {
        checkHasId(section);
        return complete(submit(req("PUT", tmpl("/help_center/sections/{id}.json").set("id", section.getId()),
                JSON, json(Collections.singletonMap("section", section))), handle(Section.class, "section")));
    }

    public Translation createSectionTranslation(Long sectionId, Translation translation) {
        checkHasSectionId(sectionId);
        return complete(submit(req("POST", tmpl("/help_center/sections/{id}/translation.json").set("id", sectionId),
                JSON, json(Collections.singletonMap("translation", translation))), handle(Translation.class, "translation")));
    }

    public Translation updateSectionTranslation(Long sectionId, String locale, Translation translation) {
        checkHasId(translation);
        return complete(submit(req("PUT", tmpl("/help_center/sections/{id}/translations/{locale}.json").set("id", sectionId).set("locale",locale),
                JSON, json(Collections.singletonMap("translation", translation))), handle(Translation.class, "translation")));
    }

    public void deleteSection(Section section) {
        checkHasId(section);
        complete(submit(req("DELETE", tmpl("/help_center/sections/{id}.json").set("id", section.getId())),
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
                tmpl("/help_center{/locale}/articles/{articleId}/subscriptions.json").set("locale", locale)
                        .set("articleId", articleId),
                handleList(Subscription.class, "subscriptions"));
    }

    public Iterable<Subscription> getSectionSubscriptions(Long sectionId) {
        return getSectionSubscriptions(sectionId, null);
    }

    public Iterable<Subscription> getSectionSubscriptions(Long sectionId, String locale) {
        return new PagedIterable<>(
                tmpl("/help_center{/locale}/sections/{sectionId}/subscriptions.json").set("locale", locale)
                        .set("sectionId", sectionId),
                handleList(Subscription.class, "subscriptions"));
    }

    /**
     * Get a list of the current business hours schedules
     * @return A List of Schedules
     */
    public Iterable<Schedule> getSchedules() {
        return complete(submit(req("GET", cnst("/business_hours/schedules.json")),
            handleList(Schedule.class, "schedules")));
    }

    public Schedule getSchedule(Schedule schedule) {
        checkHasId(schedule);
        return getSchedule(schedule.getId());
    }

    public Schedule getSchedule(Long scheduleId) {
        return complete(submit(req("GET", tmpl("/business_hours/schedules/{id}.json").set("id", scheduleId)),
            handle(Schedule.class, "schedule")));
    }

    public Iterable<Holiday> getHolidaysForSchedule(Schedule schedule) {
        checkHasId(schedule);
        return getHolidaysForSchedule(schedule.getId());
    }

    public Iterable<Holiday> getHolidaysForSchedule(Long scheduleId) {
        return complete(submit(req("GET",
            tmpl("/business_hours/schedules/{id}/holidays.json").set("id", scheduleId)),
            handleList(Holiday.class, "holidays")));
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

    private <T> ListenableFuture<T> submit(Request request, ZendeskAsyncCompletionHandler<T> handler) {
        if (logger.isDebugEnabled()) {
            if (request.getStringData() != null) {
                logger.debug("Request {} {}\n{}", request.getMethod(), request.getUrl(), request.getStringData());
            } else if (request.getByteData() != null) {
                logger.debug("Request {} {} {} {} bytes", request.getMethod(), request.getUrl(),
                        request.getHeaders().get("Content-type"), request.getByteData().length);
            } else {
                logger.debug("Request {} {}", request.getMethod(), request.getUrl());
            }
        }
        return client.executeRequest(request, handler);
    }

    private static abstract class ZendeskAsyncCompletionHandler<T> extends AsyncCompletionHandler<T> {
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

    private static final Pattern RESTRICTED_PATTERN = Pattern.compile("%2B", Pattern.LITERAL);

    private Request req(String method, String url) {
        RequestBuilder builder = new RequestBuilder(method);
        if (realm != null) {
            builder.setRealm(realm);
        } else {
            builder.addHeader("Authorization", "Bearer " + oauthToken);
        }
        builder.setUrl(RESTRICTED_PATTERN.matcher(url).replaceAll("+")); // replace out %2B with + due to API restriction
        return builder.build();
    }

    private Request req(String method, Uri template, String contentType, byte[] body) {
        RequestBuilder builder = new RequestBuilder(method);
        if (realm != null) {
            builder.setRealm(realm);
        } else {
            builder.addHeader("Authorization", "Bearer " + oauthToken);
        }
        builder.setUrl(RESTRICTED_PATTERN.matcher(template.toString()).replaceAll("+")); //replace out %2B with + due to API restriction
        builder.addHeader("Content-type", contentType);
        builder.setBody(body);
        return builder.build();
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
                    return (T) mapper.reader(clazz).readValue(response.getResponseBodyAsStream());
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
                    return mapper.convertValue(mapper.readTree(response.getResponseBodyAsStream()).get(name), type);
                }
                return mapper.convertValue(mapper.readTree(response.getResponseBodyAsStream()).get(name), clazz);
            } else if (isRateLimitResponse(response)) {
                throw new ZendeskResponseRateLimitException(response);
            }
            if (response.getStatusCode() == 404) {
                return null;
            }
            throw new ZendeskResponseException(response);
        }
    }

    protected <T> ZendeskAsyncCompletionHandler<T> handle(final Class<T> clazz, final String name, final Class... typeParams) {
        return new BasicAsyncCompletionHandler<>(clazz, name, typeParams);
    }

    protected <T> ZendeskAsyncCompletionHandler<JobStatus<T>> handleJobStatus(final Class<T> resultClass) {
        return new BasicAsyncCompletionHandler<JobStatus<T>>(JobStatus.class, "job_status", resultClass) {
            @Override
            public JobStatus<T> onCompleted(Response response) throws Exception {
                JobStatus<T> result = super.onCompleted(response);
                result.setResultsClass(resultClass);
                return result;
            }
        };
    }

    private static final String NEXT_PAGE = "next_page";
    private static final String END_TIME = "end_time";
    private static final String COUNT = "count";
    private static final int INCREMENTAL_EXPORT_MAX_COUNT_BY_REQUEST = 1000;

    private abstract class PagedAsyncCompletionHandler<T> extends ZendeskAsyncCompletionHandler<T> {
        private String nextPage;

        public void setPagedProperties(JsonNode responseNode, Class<?> clazz) {
            JsonNode node = responseNode.get(NEXT_PAGE);
            if (node == null) {
                this.nextPage = null;
                if (logger.isDebugEnabled()) {
                    logger.debug(NEXT_PAGE + " property not found, pagination not supported" +
                        (clazz != null ? " for " + clazz.getName() : ""));
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

    protected <T> PagedAsyncCompletionHandler<List<T>> handleList(final Class<T> clazz, final String name) {
        return new PagedAsyncListCompletionHandler<>(clazz, name);
    }

    private static final long FIVE_MINUTES = TimeUnit.MINUTES.toMillis(5);

    protected <T> PagedAsyncCompletionHandler<List<T>> handleIncrementalList(final Class<T> clazz, final String name) {
        return new PagedAsyncListCompletionHandler<T>(clazz, name) {
            @Override
            public void setPagedProperties(JsonNode responseNode, Class<?> clazz) {
                JsonNode node = responseNode.get(NEXT_PAGE);
                if (node == null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(NEXT_PAGE + " property not found, pagination not supported" +
                            (clazz != null ? " for " + clazz.getName() : ""));
                    }
                    setNextPage(null);
                    return;
                }
                JsonNode endTimeNode = responseNode.get(END_TIME);
                if (endTimeNode == null || endTimeNode.asLong() == 0) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(END_TIME + " property not found, incremental export pagination not supported" +
                            (clazz != null ? " for " + clazz.getName() : ""));
                    }
                    setNextPage(null);
                    return;
                }
                /*
                  A request after five minutes ago will result in a 422 responds from Zendesk.
                  Therefore, we stop pagination.
                 */
                if (TimeUnit.SECONDS.toMillis(endTimeNode.asLong()) > System.currentTimeMillis() - FIVE_MINUTES) {
                    setNextPage(null);
                } else {
                    // Taking into account documentation found at https://developer.zendesk.com/rest_api/docs/core/incremental_export#polling-strategy
                    JsonNode countNode = responseNode.get(COUNT);
                    if (countNode == null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug(COUNT + " property not found, incremental export pagination not supported" +
                                (clazz != null ? " for " + clazz.getName() : ""));
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

    protected PagedAsyncCompletionHandler<List<SearchResultEntity>> handleSearchList(final String name) {
        return new PagedAsyncCompletionHandler<List<SearchResultEntity>>() {
            @Override
            public List<SearchResultEntity> onCompleted(Response response) throws Exception {
                logResponse(response);
                if (isStatus2xx(response)) {
                    JsonNode responseNode = mapper.readTree(response.getResponseBodyAsStream()).get(name);
                    setPagedProperties(responseNode, null);
                    List<SearchResultEntity> values = new ArrayList<>();
                    for (JsonNode node : responseNode) {
                        Class<? extends SearchResultEntity> clazz = searchResultTypes.get(node.get("result_type").asText());
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


    protected PagedAsyncCompletionHandler<List<ArticleAttachments>> handleArticleAttachmentsList(final String name) {
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

    private TemplateUri tmpl(String template) {
        return new TemplateUri(url + template);
    }

    private Uri cnst(String template) {
        return new FixedUri(url + template);
    }

    private void logResponse(Response response) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Response HTTP/{} {}\n{}", response.getStatusCode(), response.getStatusText(),
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
                    throw new ZendeskResponseRateLimitException((ZendeskResponseRateLimitException) e.getCause());
                }
                if (e.getCause() instanceof ZendeskResponseException) {
                    throw new ZendeskResponseException((ZendeskResponseException)e.getCause());
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

    private static List<String> statusArray(Status... statuses) {
        List<String> result = new ArrayList<>(statuses.length);
        for (Status s : statuses) {
            result.add(s.toString());
        }
        return result;
    }

    public static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new ISO8601DateFormat());
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
        private AsyncHttpClient client = null;
        private final String url;
        private String username = null;
        private String password = null;
        private String token = null;
        private String oauthToken = null;

        public Builder(String url) {
            this.url = url;
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

        public Zendesk build() {
            if (token != null) {
                return new Zendesk(client, url, username + "/token", token);
            } else if (oauthToken != null) {
                return new Zendesk(client, url, oauthToken);
            }
            return new Zendesk(client, url, username, password);
        }
    }
}
