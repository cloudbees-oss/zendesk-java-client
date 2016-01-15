package org.zendesk.client.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Realm;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zendesk.client.v2.model.Attachment;
import org.zendesk.client.v2.model.Audit;
import org.zendesk.client.v2.model.Automation;
import org.zendesk.client.v2.model.Comment;
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
import org.zendesk.client.v2.model.SearchResultEntity;
import org.zendesk.client.v2.model.Status;
import org.zendesk.client.v2.model.SuspendedTicket;
import org.zendesk.client.v2.model.Ticket;
import org.zendesk.client.v2.model.TicketResult;
import org.zendesk.client.v2.model.TicketForm;
import org.zendesk.client.v2.model.Topic;
import org.zendesk.client.v2.model.Trigger;
import org.zendesk.client.v2.model.TwitterMonitor;
import org.zendesk.client.v2.model.User;
import org.zendesk.client.v2.model.UserField;
import org.zendesk.client.v2.model.hc.Article;
import org.zendesk.client.v2.model.hc.ArticleAttachments;
import org.zendesk.client.v2.model.hc.Category;
import org.zendesk.client.v2.model.hc.Section;
import org.zendesk.client.v2.model.hc.Translation;
import org.zendesk.client.v2.model.targets.BasecampTarget;
import org.zendesk.client.v2.model.targets.CampfireTarget;
import org.zendesk.client.v2.model.targets.EmailTarget;
import org.zendesk.client.v2.model.targets.PivotalTarget;
import org.zendesk.client.v2.model.targets.Target;
import org.zendesk.client.v2.model.targets.TwitterTarget;
import org.zendesk.client.v2.model.targets.UrlTarget;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
       Map<String, Class<? extends SearchResultEntity>> result = new HashMap<String, Class<? extends
             SearchResultEntity>>();
       result.put("ticket", Ticket.class);
       result.put("user", User.class);
       result.put("group", Group.class);
       result.put("organization", Organization.class);
       result.put("topic", Topic.class);
        result.put("article", Article.class);
       return Collections.unmodifiableMap(result);
    }
    
    private static Map<String, Class<? extends Target>> targetTypes() {
       Map<String, Class<? extends Target>> result = new HashMap<String, Class<? extends Target>>();
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
        this.client = client == null ? new AsyncHttpClient() : client;
        this.url = url.endsWith("/") ? url + "api/v2" : url + "/api/v2";
        if (username != null) {
            this.realm = new Realm.RealmBuilder()
                    .setScheme(Realm.AuthScheme.BASIC)
                    .setPrincipal(username)
                    .setPassword(password)
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
        this.client = client == null ? new AsyncHttpClient() : client;
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
            client.close();
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
        List<String> ids = new ArrayList<String>(statuses.size());
        for (JobStatus status : statuses) {
            ids.add(status.getId());
        }
        Class<JobStatus<HashMap<String, Object>>> clazz = (Class<JobStatus<HashMap<String, Object>>>)(Object)JobStatus.class;
        return submit(req("GET", tmpl("/job_statuses/show_many.json{?ids}").set("ids", ids)), handleList(clazz, "job_statuses"));
    }

    public TicketForm getTicketForm(long id) {
        return complete(submit(req("GET", tmpl("/ticket_forms/{id}.json").set("id", id)), handle(TicketForm.class,
                "ticket_form")));
    }

    public List<TicketForm> getTicketForms() {
        return complete(submit(req("GET", cnst("/ticket_forms.json")), handleList(TicketForm.class,
                "ticket_forms")));
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

    public void deleteTicket(Ticket ticket) {
        checkHasId(ticket);
        deleteTicket(ticket.getId());
    }

    public void deleteTicket(long id) {
        complete(submit(req("DELETE", tmpl("/tickets/{id}.json").set("id", id)), handleStatus()));
    }

    public Ticket createTicket(Ticket ticket) {
        return complete(submit(req("POST", cnst("/tickets.json"),
                        JSON, json(Collections.singletonMap("ticket", ticket))),
                handle(Ticket.class, "ticket")));
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

    public Iterable<Ticket> getTickets() {
        return new PagedIterable<Ticket>(cnst("/tickets.json"), handleList(Ticket.class, "tickets"));
    }

    public Iterable<Ticket> getTicketsByStatus(Status... ticketStatus) {
        return new PagedIterable<Ticket>(tmpl("/tickets.json{?status}").set("status", statusArray(ticketStatus)),
                handleList(Ticket.class, "tickets"));
    }

    public Iterable<Ticket> getTicketsByExternalId(String externalId, boolean includeArchived) {
        Iterable<Ticket> results = new PagedIterable<Ticket>(tmpl("/tickets.json{?external_id}").set("external_id", externalId),
                handleList(Ticket.class, "tickets"));

        if (!includeArchived || results.iterator().hasNext()) {
            return results;
        }
        return new PagedIterable<Ticket>(tmpl("/search.json{?query}{&type}").set("query", "external_id:" + externalId).set("type", "ticket"),
                handleList(Ticket.class, "results"));
    }

    public Iterable<Ticket> getTicketsByExternalId(String externalId) {
        return getTicketsByExternalId(externalId, false);
    }

    public Iterable<Ticket> getTicketsFromSearch(String searchTerm) {
        return new PagedIterable<Ticket>(tmpl("/search.json{?query}").set("query", searchTerm + "+type:ticket"),
                handleList(Ticket.class, "results"));
    }

    public Iterable<Article> getArticleFromSearch(String searchTerm) {
        return new PagedIterable<Article>(tmpl("/help_center/articles/search.json{?query}").set("query", searchTerm),
                handleList(Article.class, "results"));
    }

    public Iterable<Article> getArticleFromSearch(String searchTerm, Long sectionId) {
        return new PagedIterable<Article>(tmpl("/help_center/articles/search.json{?section,query}")
                .set("query", searchTerm).set("section", sectionId), handleList(Article.class, "results"));
    }

    public List<ArticleAttachments> getAttachmentsFromArticle(Long articleID) {
        return complete(submit(req("GET", tmpl("/help_center/articles/{?query}/attachments.json").set("query", articleID)),
                handleList(ArticleAttachments.class, "articles")));
    }

    public List<Ticket> getTickets(long id, long... ids) {
        return complete(submit(req("GET", tmpl("/tickets/show_many.json{?ids}").set("ids", idArray(id, ids))),
                handleList(Ticket.class, "tickets")));
    }

    public Iterable<Ticket> getRecentTickets() {
        return new PagedIterable<Ticket>(cnst("/tickets/recent.json"), handleList(Ticket.class, "tickets"));
    }
    
    public Iterable<Ticket> getTicketsIncrementally(Date startTime) {
        return new PagedIterable<Ticket>(
                tmpl("/incremental/tickets.json{?start_time}").set("start_time", msToSeconds(startTime.getTime())), 
                handleIncrementalList(Ticket.class, "tickets"));                
    }
    
    public Iterable<Ticket> getTicketsIncrementally(Date startTime, Date endTime) {
        return new PagedIterable<Ticket>(
                tmpl("/incremental/tickets.json{?start_time,end_time}")
                    .set("start_time", msToSeconds(startTime.getTime()))
                    .set("end_time", msToSeconds(endTime.getTime())), 
                    handleIncrementalList(Ticket.class, "tickets"));                
    }

    public Iterable<Ticket> getOrganizationTickets(long organizationId) {
        return new PagedIterable<Ticket>(
                tmpl("/organizations/{organizationId}/tickets.json").set("organizationId", organizationId),
                handleList(Ticket.class, "tickets"));
    }

    public Iterable<Ticket> getUserRequestedTickets(long userId) {
        return new PagedIterable<Ticket>(tmpl("/users/{userId}/tickets/requested.json").set("userId", userId),
                handleList(Ticket.class, "tickets"));
    }

    public Iterable<Ticket> getUserCCDTickets(long userId) {
        return new PagedIterable<Ticket>(tmpl("/users/{userId}/tickets/ccd.json").set("userId", userId),
                handleList(Ticket.class, "tickets"));
    }

    public Iterable<Metric> getTicketMetrics() {
        return new PagedIterable<Metric>(cnst("/ticket_metrics.json"), handleList(Metric.class, "ticket_metrics"));
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
        return new PagedIterable<Audit>(tmpl("/tickets/{ticketId}/audits.json").set("ticketId", id),
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
        return new PagedIterable<SuspendedTicket>(cnst("/suspended_tickets.json"),
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
        TemplateUri uri = tmpl("/uploads.json{?filename}{?token}").set("filename", fileName);
        if (token != null) {
            uri.set("token", token);
        }
        return complete(
                submit(req("POST", uri, contentType,
                        content), handle(Attachment.Upload.class, "upload")));
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
        return new PagedIterable<Target>(cnst("/targets.json"), handleTargetList("targets"));
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
        return new PagedIterable<Trigger>(cnst("/triggers.json"), handleList(Trigger.class, "triggers"));
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
    return new PagedIterable<Automation>(cnst("/automations.json"),
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
        return new PagedIterable<TwitterMonitor>(cnst("/channels/twitter/monitored_twitter_handles.json"),  
              handleList(TwitterMonitor.class, "monitored_twitter_handles"));
    }

    
    public Iterable<User> getUsers() {
        return new PagedIterable<User>(cnst("/users.json"), handleList(User.class, "users"));
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
        return new PagedIterable<User>(cnst(uriBuilder.toString()), handleList(User.class, "users"));
    }

    public Iterable<User> getUsersIncrementally(Date startTime) {
        return new PagedIterable<User>(
              tmpl("/incremental/users.json{?start_time}").set("start_time", msToSeconds(startTime.getTime())), 
              handleIncrementalList(User.class, "users"));                
    }

    public Iterable<User> getGroupUsers(long id) {
        return new PagedIterable<User>(tmpl("/groups/{id}/users.json").set("id", id), handleList(User.class, "users"));
    }

    public Iterable<User> getOrganizationUsers(long id) {
        return new PagedIterable<User>(tmpl("/organizations/{id}/users.json").set("id", id),
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

    public Iterable<User> lookupUserByEmail(String email) {
        return new PagedIterable<User>(tmpl("/users/search.json{?query}").set("query", email),
                handleList(User.class, "users"));
    }

    public Iterable<User> lookupUserByExternalId(String externalId) {
        return new PagedIterable<User>(tmpl("/users/search.json{?external_id}").set("external_id", externalId),
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
        Map<String, String> req = new HashMap<String, String>();
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

    public void createUserIdentity(long userId, Identity identity) {
        complete(submit(req("POST", tmpl("/users/{userId}/identities.json").set("userId", userId), JSON, json(
                Collections.singletonMap("identity", identity))), handle(Identity.class, "identity")));
    }

    public void createUserIdentity(User user, Identity identity) {
        complete(submit(req("POST", tmpl("/users/{userId}/identities.json").set("userId", user.getId()), JSON, json(
                Collections.singletonMap("identity", identity))), handle(Identity.class, "identity")));
    }

    public Iterable<org.zendesk.client.v2.model.Request> getRequests() {
        return new PagedIterable<org.zendesk.client.v2.model.Request>(cnst("/requests.json"),
                handleList(org.zendesk.client.v2.model.Request.class, "requests"));
    }

    public Iterable<org.zendesk.client.v2.model.Request> getOpenRequests() {
        return new PagedIterable<org.zendesk.client.v2.model.Request>(cnst("/requests/open.json"),
                handleList(org.zendesk.client.v2.model.Request.class, "requests"));
    }

    public Iterable<org.zendesk.client.v2.model.Request> getSolvedRequests() {
        return new PagedIterable<org.zendesk.client.v2.model.Request>(cnst("/requests/solved.json"),
                handleList(org.zendesk.client.v2.model.Request.class, "requests"));
    }

    public Iterable<org.zendesk.client.v2.model.Request> getCCRequests() {
        return new PagedIterable<org.zendesk.client.v2.model.Request>(cnst("/requests/ccd.json"),
                handleList(org.zendesk.client.v2.model.Request.class, "requests"));
    }

    public Iterable<org.zendesk.client.v2.model.Request> getUserRequests(User user) {
        checkHasId(user);
        return getUserRequests(user.getId());
    }

    public Iterable<org.zendesk.client.v2.model.Request> getUserRequests(long id) {
        return new PagedIterable<org.zendesk.client.v2.model.Request>(tmpl("/users/{id}/requests.json").set("id", id),
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
        return new PagedIterable<Comment>(tmpl("/requests/{id}/comments.json").set("id", id),
                handleList(Comment.class, "comments"));
    }

    public Iterable<Comment> getTicketComments(long id) {
        return new PagedIterable<Comment>(tmpl("/tickets/{id}/comments.json").set("id", id),
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
       Map<String,Object> map = new HashMap<String,Object>();
       map.put("twitter_status_message_id", tweetId);
       map.put("monitored_twitter_handle_id", monitorId);
      
       return complete(submit(req("POST", cnst("/channels/twitter/tickets.json"), JSON,              
             json(Collections.singletonMap("ticket", map))),
             handle(Ticket.class, "ticket")));
    }
    
    public Iterable<Organization> getOrganizations() {
        return new PagedIterable<Organization>(cnst("/organizations.json"),
                handleList(Organization.class, "organizations"));
    }

    public Iterable<Organization> getOrganizationsIncrementally(Date startTime) {
        return new PagedIterable<Organization>(
            tmpl("/incremental/organizations.json{?start_time}").set("start_time", msToSeconds(startTime.getTime())), 
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
        return new PagedIterable<Organization>(tmpl("/organizations/autocomplete.json{?name}").set("name", name),
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
        return new PagedIterable<Organization>(
                tmpl("/organizations/search.json{?external_id}").set("external_id", externalId),
                handleList(Organization.class, "organizations"));
    }

    public Iterable<Group> getGroups() {
        return new PagedIterable<Group>(cnst("/groups.json"),
                handleList(Group.class, "groups"));
    }

    public Iterable<Group> getAssignableGroups() {
        return new PagedIterable<Group>(cnst("/groups/assignable.json"),
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

    public List<Group> createGroups(Group... groups) {
        return createGroups(Arrays.asList(groups));
    }

    public List<Group> createGroups(List<Group> groups) {
        return complete(submit(req("POST", cnst("/groups/create_many.json"), JSON, json(
                Collections.singletonMap("groups", groups))), handleList(Group.class, "results")));
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
        return new PagedIterable<Macro>(cnst("/macros.json"),
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
        return new PagedIterable<GroupMembership>(cnst("/group_memberships.json"),
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
        return new PagedIterable<GroupMembership>(cnst("/group_memberships/assignable.json"),
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
        complete(submit(req("DELETE", tmpl("/groups_memberships/{id}.json").set("id", id)), handleStatus()));
    }

    public void deleteGroupMembership(long user_id, GroupMembership groupMembership) {
        checkHasId(groupMembership);
        deleteGroupMembership(user_id, groupMembership.getId());
    }

    public void deleteGroupMembership(long user_id, long group_membership_id) {
        complete(submit(req("DELETE", tmpl("/users/{uid}/groups_memberships/{gmid}.json").set("uid", user_id)
                .set("gmid", group_membership_id)), handleStatus()));
    }

    public List<GroupMembership> setGroupMembershipAsDefault(long user_id, GroupMembership groupMembership) {
        checkHasId(groupMembership);
        return complete(submit(req("POST", tmpl("/users/{uid}/group_memberships/{gmid}/make_default.json")
                        .set("uid", user_id).set("gmid", groupMembership.getId()), JSON, json(
                        Collections.singletonMap("group_memberships", groupMembership))),
                handleList(GroupMembership.class, "results")));
    }

    public Iterable<Forum> getForums() {
        return new PagedIterable<Forum>(cnst("/forums.json"), handleList(Forum.class, "forums"));
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
        return new PagedIterable<Topic>(cnst("/topics.json"), handleList(Topic.class, "topics"));
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

    public Iterable<SearchResultEntity> getSearchResults(String query) {
        return new PagedIterable<SearchResultEntity>(tmpl("/search.json{?query}").set("query", query),
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
        return new PagedIterable<T>(tmpl("/search.json{?query,params}")
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
        return new PagedIterable<Article>(cnst("/help_center/articles.json"),
                handleList(Article.class, "articles"));
    }

    public List<Article> getArticlesFromPage(int page) {
        return complete(submit(req("GET", tmpl("/help_center/articles.json?page={page}").set("page", page)),
                handleList(Article.class, "articles")));
    }

    public Article getArticle(int id) {
        return complete(submit(req("GET", tmpl("/help_center/articles/{id}.json").set("id", id)),
                handle(Article.class, "article")));
    }

    public Iterable<Translation> getArticleTranslations(Long articleId) {
        return new PagedIterable<Translation>(
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

    public void deleteArticle(Article article) {
        checkHasId(article);
        complete(submit(req("DELETE", tmpl("/help_center/articles/{id}.json").set("id", article.getId())),
                handleStatus()));
    }

    public List<Category> getCategories() {
        return complete(submit(req("GET", cnst("/help_center/categories.json")),
                handleList(Category.class, "categories")));
    }

    public Category getCategory(int id) {
        return complete(submit(req("GET", tmpl("/help_center/categories/{id}.json").set("id", id)),
                handle(Category.class, "category")));
    }

    public Iterable<Translation> getCategoryTranslations(Long categoryId) {
        return new PagedIterable<Translation>(
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

    public void deleteCategory(Category category) {
        checkHasId(category);
        complete(submit(req("DELETE", tmpl("/help_center/categories/{id}.json").set("id", category.getId())),
                handleStatus()));
    }

    public List<Section> getSections() {
        return complete(submit(req("GET", cnst("/help_center/sections.json")), handleList(Section.class, "sections")));
    }

    public List<Section> getSections(Category category) {
        checkHasId(category);
        return complete(submit(req("GET", tmpl("/help_center/categories/{id}/sections.json").set("id", category.getId())),
                handleList(Section.class, "sections")));
    }

    public Section getSection(int id) {
        return complete(submit(req("GET", tmpl("/help_center/sections/{id}.json").set("id", id)),
                handle(Section.class, "section")));
    }

    public Iterable<Translation> getSectionTranslations(Long sectionId) {
        return new PagedIterable<Translation>(
            tmpl("/help_center/sections/{sectionId}/translations.json").set("sectionId", sectionId),
            handleList(Translation.class, "translations"));
    }
    public Section createSection(Section section) {
        return complete(submit(req("POST", cnst("/help_center/sections.json"), JSON,
                json(Collections.singletonMap("section", section))), handle(Section.class, "section")));
    }

    public Section updateSection(Section section) {
        checkHasId(section);
        return complete(submit(req("PUT", tmpl("/help_center/sections/{id}.json").set("id", section.getId()),
                JSON, json(Collections.singletonMap("section", section))), handle(Section.class, "section")));
    }

    public void deleteSection(Section section) {
        checkHasId(section);
        complete(submit(req("DELETE", tmpl("/help_center/sections/{id}.json").set("id", section.getId())),
                handleStatus()));
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
                        request.getHeaders().getFirstValue("Content-type"), request.getByteData().length);
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
            }
            if (response.getStatusCode() == 404) {
                return null;
            }
            throw new ZendeskResponseException(response);
        }
    }

    protected <T> ZendeskAsyncCompletionHandler<T> handle(final Class<T> clazz, final String name, final Class... typeParams) {
        return new BasicAsyncCompletionHandler<T>(clazz, name, typeParams);
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
                throw new NullPointerException(NEXT_PAGE + " property not found, pagination not supported" +
                        (clazz != null ? " for " + clazz.getName() : ""));
            }
            this.nextPage = node.asText();
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
                List<T> values = new ArrayList<T>();
                for (JsonNode node : responseNode.get(name)) {
                    values.add(mapper.convertValue(node, clazz));
                }
                return values;
            }
            throw new ZendeskResponseException(response);
        }
    }

    protected <T> PagedAsyncCompletionHandler<List<T>> handleList(final Class<T> clazz, final String name) {
        return new PagedAsyncListCompletionHandler<T>(clazz, name);
    }

    private static final long FIVE_MINUTES = TimeUnit.MINUTES.toMillis(5);

    protected <T> PagedAsyncCompletionHandler<List<T>> handleIncrementalList(final Class<T> clazz, final String name) {
        return new PagedAsyncListCompletionHandler<T>(clazz, name) {
            @Override
            public void setPagedProperties(JsonNode responseNode, Class<?> clazz) {
                JsonNode node = responseNode.get(NEXT_PAGE);
                if (node == null) {
                    throw new NullPointerException(NEXT_PAGE + " property not found, pagination not supported" +
                            (clazz != null ? " for " + clazz.getName() : ""));
                }
                JsonNode endTimeNode = responseNode.get(END_TIME);
                if (endTimeNode == null || endTimeNode.asLong() == 0) {
                    throw new NullPointerException(END_TIME + " property not found, incremental export pagination not supported" +
                            (clazz != null ? " for " + clazz.getName() : ""));
                }
                /**
                 * A request after five minutes ago will result in a 422 responds from Zendesk.
                 * Therefore, we stop pagination.
                 */
                if (TimeUnit.SECONDS.toMillis(endTimeNode.asLong()) > System.currentTimeMillis() - FIVE_MINUTES) {
                    setNextPage(null);
                } else {
                    // Taking into account documentation found at https://developer.zendesk.com/rest_api/docs/core/incremental_export#polling-strategy
                    JsonNode countNode = responseNode.get(COUNT);
                    if (countNode == null) {
                        throw new NullPointerException(COUNT + " property not found, incremental export pagination not supported" +
                                (clazz != null ? " for " + clazz.getName() : ""));
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
                    List<SearchResultEntity> values = new ArrayList<SearchResultEntity>();
                    for (JsonNode node : responseNode) {
                        Class<? extends SearchResultEntity> clazz = searchResultTypes.get(node.get("result_type"));
                        if (clazz != null) {
                            values.add(mapper.convertValue(node, clazz));
                        }
                    }
                    return values;
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
                    List<Target> values = new ArrayList<Target>();
                    for (JsonNode node : responseNode.get(name)) {
                        Class<? extends Target> clazz = targetTypes.get(node.get("type").asText());
                        if (clazz != null) {
                            values.add(mapper.convertValue(node, clazz));
                        }
                    }
                    return values;
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
                throw (ZendeskException) e.getCause();
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

    private static void checkHasToken(Attachment.Upload upload) {
        if (upload.getToken() == null) {
            throw new IllegalArgumentException("Upload requires token");
        }
    }

    private static List<Long> idArray(long id, long... ids) {
        List<Long> result = new ArrayList<Long>(ids.length + 1);
        result.add(id);
        for (long i : ids) {
            result.add(i);
        }
        return result;
    }

    private static List<String> statusArray(Status... statuses) {
        List<String> result = new ArrayList<String>(statuses.length);
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
