package org.zendesk.client.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
import org.zendesk.client.v2.model.Comment;
import org.zendesk.client.v2.model.Field;
import org.zendesk.client.v2.model.Group;
import org.zendesk.client.v2.model.Identity;
import org.zendesk.client.v2.model.Organization;
import org.zendesk.client.v2.model.Ticket;
import org.zendesk.client.v2.model.User;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

/**
 * @author stephenc
 * @since 04/04/2013 13:08
 */
public class ZenDesk implements Closeable {
    private static final String JSON = "application/json; charset=UTF-8";
    private final boolean closeClient;
    private final AsyncHttpClient client;
    private final Realm realm;
    private final String url;
    private final ObjectMapper mapper;
    private final Logger logger;
    private boolean closed = false;

    private ZenDesk(AsyncHttpClient client, String url, String username, String password) {
        this.logger = LoggerFactory.getLogger(ZenDesk.class);
        this.closeClient = client == null;
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

    public Ticket getTicket(int id) {
        return complete(submit(req("GET", tmpl("/tickets/{id}.json").set("id", id)), handle(Ticket.class,
                "ticket")));
    }

    public void deleteTicket(Ticket ticket) {
        checkHasId(ticket);
        deleteTicket(ticket.getId());
    }

    public void deleteTicket(int id) {
        complete(submit(req("DELETE", tmpl("/tickets/{id}.json").set("id", id)), handleStatus()));
    }

    public Ticket createTicket(Ticket ticket) {
        return complete(submit(req("POST", cnst("/tickets.json"),
                JSON, json(Collections.singletonMap("ticket", ticket))),
                handle(Ticket.class, "ticket")));
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

    public void markTicketAsSpam(int id) {
        complete(submit(req("PUT", tmpl("/tickets/{id}/mark_as_spam.json").set("id", id)), handleStatus()));
    }

    public void deleteTickets(int id, int... ids) {
        complete(submit(req("DELETE", tmpl("/tickets/destroy_many.json{?ids}").set("ids", idArray(id, ids))),
                handleStatus()));
    }

    public Iterable<Ticket> getTickets() {
        return new PagedIterable<Ticket>(cnst("/tickets.json"), handleList(Ticket.class, "tickets"));
    }

    public List<Ticket> getTickets(int id, int... ids) {
        return complete(submit(req("GET", tmpl("/tickets/show_many.json{?ids}").set("ids", idArray(id, ids))),
                handleList(Ticket.class, "tickets")));
    }

    public Iterable<Ticket> getRecentTickets() {
        return new PagedIterable<Ticket>(cnst("/tickets/recent.json"), handleList(Ticket.class, "tickets"));
    }

    public Iterable<Ticket> getOrganizationTickets(int organizationId) {
        return new PagedIterable<Ticket>(
                tmpl("/organizations/{organizationId}/tickets.json").set("organizationId", organizationId),
                handleList(Ticket.class, "tickets"));
    }

    public Iterable<Ticket> getUserRequestedTickets(int userId) {
        return new PagedIterable<Ticket>(tmpl("/users/{userId}/tickets/requested.json").set("userId", userId),
                handleList(Ticket.class, "tickets"));
    }

    public Iterable<Ticket> getUserCCDTickets(int userId) {
        return new PagedIterable<Ticket>(tmpl("/users/{userId}/tickets/ccd.json").set("userId", userId),
                handleList(Ticket.class, "tickets"));
    }

    public Iterable<Audit> getTicketAudits(Ticket ticket) {
        checkHasId(ticket);
        return getTicketAudits(ticket.getId());
    }

    public Iterable<Audit> getTicketAudits(Integer id) {
        return new PagedIterable<Audit>(tmpl("/tickets/{ticketId}/audits.json").set("ticketId", id),
                handleList(Audit.class, "audits"));
    }

    public Audit getTicketAudit(Ticket ticket, Audit audit) {
        checkHasId(audit);
        return getTicketAudit(ticket, audit.getId());
    }

    public Audit getTicketAudit(Ticket ticket, int id) {
        checkHasId(ticket);
        return getTicketAudit(ticket.getId(), id);
    }

    public Audit getTicketAudit(int ticketId, int auditId) {
        return complete(submit(req("GET",
                tmpl("/tickets/{ticketId}/audits/{auditId}.json").set("ticketId", ticketId).set("auditId", auditId)),
                handle(Audit.class, "audit")));
    }

    public void trustTicketAudit(Ticket ticket, Audit audit) {
        checkHasId(audit);
        trustTicketAudit(ticket, audit.getId());
    }

    public void trustTicketAudit(Ticket ticket, int id) {
        checkHasId(ticket);
        trustTicketAudit(ticket.getId(), id);
    }

    public void trustTicketAudit(int ticketId, int auditId) {
        complete(submit(req("PUT", tmpl("/tickets/{ticketId}/audits/{auditId}/trust.json").set("ticketId", ticketId)
                .set("auditId", auditId)), handleStatus()));
    }

    public void makePrivateTicketAudit(Ticket ticket, Audit audit) {
        checkHasId(audit);
        makePrivateTicketAudit(ticket, audit.getId());
    }

    public void makePrivateTicketAudit(Ticket ticket, int id) {
        checkHasId(ticket);
        makePrivateTicketAudit(ticket.getId(), id);
    }

    public void makePrivateTicketAudit(int ticketId, int auditId) {
        complete(submit(req("PUT",
                tmpl("/tickets/{ticketId}/audits/{auditId}/make_private.json").set("ticketId", ticketId)
                        .set("auditId", auditId)), handleStatus()));
    }

    public List<Field> getTicketFields() {
        return complete(submit(req("GET", cnst("/ticket_fields.json")), handleList(Field.class, "ticket_fields")));
    }

    public Field getTicketField(int id) {
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

    public void deleteTicketField(int id) {
        complete(submit(req("DELETE", tmpl("/ticket_fields/{id}.json").set("id", id)), handleStatus()));
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

    public Attachment getAttachment(int id) {
        return complete(submit(req("GET", tmpl("/attachments/{id}.json").set("id", id)), handle(Attachment.class,
                "attachment")));
    }

    public void deleteAttachment(Attachment attachment) {
        checkHasId(attachment);
        deleteAttachment(attachment.getId());
    }

    public void deleteAttachment(int id) {
        complete(submit(req("DELETE", tmpl("/attachments/{id}.json").set("id", id)), handleStatus()));
    }

    public Iterable<User> getUsers() {
        return new PagedIterable<User>(cnst("/users.json"), handleList(User.class, "users"));
    }

    public Iterable<User> getGroupUsers(int id) {
        return new PagedIterable<User>(tmpl("/groups/{id}/users.json").set("id", id), handleList(User.class, "users"));
    }

    public Iterable<User> getOrganizationUsers(int id) {
        return new PagedIterable<User>(tmpl("/organization/{id}/users.json").set("id", id),
                handleList(User.class, "users"));
    }

    public User getUser(int id) {
        return complete(submit(req("GET", tmpl("/users/{id}.json").set("id", id)), handle(User.class, "user")));
    }

    public User createUser(User user) {
        return complete(submit(req("POST", cnst("/users.json"), JSON, json(
                Collections.singletonMap("user", user))), handle(User.class, "user")));
    }

    public List<User> createUsers(User... users) {
        return createUsers(Arrays.asList(users));
    }

    public List<User> createUsers(List<User> users) {
        return complete(submit(req("POST", cnst("/users/create_many.json"), JSON, json(
                Collections.singletonMap("users", users))), handleList(User.class, "results")));
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

    public void deleteUser(int id) {
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

    public void resetUserPassword(int id, String password) {
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

    public List<Identity> getUserIdentities(int userId) {
        return complete(submit(req("GET", tmpl("/users/{id}/identities.json").set("id", userId)),
                handleList(Identity.class, "identities")));
    }

    public Identity getUserIdentity(User user, Identity identity) {
        checkHasId(identity);
        return getUserIdentity(user, identity.getId());
    }

    public Identity getUserIdentity(User user, int identityId) {
        checkHasId(user);
        return getUserIdentity(user.getId(), identityId);
    }

    public Identity getUserIdentity(int userId, int identityId) {
        return complete(submit(req("GET", tmpl("/users/{userId}/identities/{identityId}.json").set("userId", userId)
                .set("identityId", identityId)), handle(
                Identity.class, "identity")));
    }

    public List<Identity> setUserPrimaryIdentity(User user, Identity identity) {
        checkHasId(identity);
        return setUserPrimaryIdentity(user, identity.getId());
    }

    public List<Identity> setUserPrimaryIdentity(User user, int identityId) {
        checkHasId(user);
        return setUserPrimaryIdentity(user.getId(), identityId);
    }

    public List<Identity> setUserPrimaryIdentity(int userId, int identityId) {
        return complete(submit(req("PUT",
                tmpl("/users/{userId}/identities/{identityId}/make_primary.json").set("userId", userId)
                        .set("identityId", identityId), JSON, null),
                handleList(Identity.class, "identities")));
    }

    public Identity verifyUserIdentity(User user, Identity identity) {
        checkHasId(identity);
        return verifyUserIdentity(user, identity.getId());
    }

    public Identity verifyUserIdentity(User user, int identityId) {
        checkHasId(user);
        return verifyUserIdentity(user.getId(), identityId);
    }

    public Identity verifyUserIdentity(int userId, int identityId) {
        return complete(submit(req("PUT", tmpl("/users/{userId}/identities/{identityId}/verify.json")
                .set("userId", userId)
                .set("identityId", identityId), JSON, null), handle(Identity.class, "identity")));
    }

    public Identity requestVerifyUserIdentity(User user, Identity identity) {
        checkHasId(identity);
        return requestVerifyUserIdentity(user, identity.getId());
    }

    public Identity requestVerifyUserIdentity(User user, int identityId) {
        checkHasId(user);
        return requestVerifyUserIdentity(user.getId(), identityId);
    }

    public Identity requestVerifyUserIdentity(int userId, int identityId) {
        return complete(submit(req("PUT", tmpl("/users/{userId}/identities/{identityId}/request_verification.json")
                .set("userId", userId)
                .set("identityId", identityId), JSON, null), handle(Identity.class, "identity")));
    }

    public void deleteUserIdentity(User user, Identity identity) {
        checkHasId(identity);
        deleteUserIdentity(user, identity.getId());
    }

    public void deleteUserIdentity(User user, int identityId) {
        checkHasId(user);
        deleteUserIdentity(user.getId(), identityId);
    }

    public void deleteUserIdentity(int userId, int identityId) {
        complete(submit(req("DELETE", tmpl("/users/{userId}/identities/{identityId}.json")
                .set("userId", userId)
                .set("identityId", identityId)
        ), handleStatus()));
    }

    public void createUserIdentity(int userId, Identity identity) {
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

    public Iterable<org.zendesk.client.v2.model.Request> getUserRequests(int id) {
        return new PagedIterable<org.zendesk.client.v2.model.Request>(tmpl("/users/{id}/requests.json").set("id", id),
                handleList(org.zendesk.client.v2.model.Request.class, "requests"));
    }

    public org.zendesk.client.v2.model.Request getRequest(int id) {
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

    public Iterable<Comment> getRequestComments(int id) {
        return new PagedIterable<Comment>(tmpl("/requests/{id}/comments.json").set("id", id),
                handleList(Comment.class, "comments"));
    }

    public Comment getRequestComment(org.zendesk.client.v2.model.Request request, Comment comment) {
        checkHasId(comment);
        return getRequestComment(request, comment.getId());
    }

    public Comment getRequestComment(org.zendesk.client.v2.model.Request request, int commentId) {
        checkHasId(request);
        return getRequestComment(request.getId(), commentId);
    }

    public Comment getRequestComment(int requestId, int commentId) {
        return complete(submit(req("GET", tmpl("/requests/{requestId}/comments/{commentId}.json")
                .set("requestId", requestId)
                .set("commentId", commentId)),
                handle(Comment.class, "comment")));
    }

    public Iterable<Organization> getOrganizations() {
        return new PagedIterable<Organization>(cnst("/organizations.json"),
                handleList(Organization.class, "organizations"));
    }

    public Iterable<Organization> getAutoCompleteOrganizations(String name) {
        if (name == null || name.length() < 2) {
            throw new IllegalArgumentException("Name must be at least 2 characters long");
        }
        return new PagedIterable<Organization>(tmpl("/organizations/autocomplete.json{?name}").set("name", name),
                handleList(Organization.class, "organizations"));
    }

    // TODO getOrganizationRelatedInformation

    public Organization getOrganization(int id) {
        return complete(submit(req("GET", tmpl("/organizations/{id}.json").set("id", id)),
                handle(Organization.class, "organization")));
    }

    public Organization createOrganization(Organization organization) {
        return complete(submit(req("POST", cnst("/organizations.json"), JSON, json(
                Collections.singletonMap("organization", organization))), handle(Organization.class, "organization")));
    }

    public List<Organization> createOrganizations(Organization... organizations) {
        return createOrganizations(Arrays.asList(organizations));
    }

    public List<Organization> createOrganizations(List<Organization> organizations) {
        return complete(submit(req("POST", cnst("/organizations/create_many.json"), JSON, json(
                Collections.singletonMap("organizations", organizations))), handleList(Organization.class, "results")));
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

    public void deleteOrganization(int id) {
        complete(submit(req("DELETE", tmpl("/organizations/{id}.json").set("id", id)), handleStatus()));
    }

    public Iterable<Organization> lookupOrganizationsByExternalId(String externalId) {
        if (externalId == null || externalId.length() < 2) {
            throw new IllegalArgumentException("Name must be at least 2 characters long");
        }
        return new PagedIterable<Organization>(tmpl("/organizations/search.json{?external_id}").set("external_id", externalId),
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

    public Group getGroup(int id) {
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

    public void deleteGroup(int id) {
        complete(submit(req("DELETE", tmpl("/groups/{id}.json").set("id", id)), handleStatus()));
    }

    //////////////////////////////////////////////////////////////////////
    // Helper methods
    //////////////////////////////////////////////////////////////////////

    private byte[] json(Object object) {
        try {
            return mapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new ZenDeskException(e.getMessage(), e);
        }
    }

    private <T> ListenableFuture<T> submit(Request request, AsyncCompletionHandler<T> handler) {
        try {
            if (request.getStringData() != null) {
                logger.debug("Request {} {}\n{}", request.getMethod(), request.getUrl(), request.getStringData());
            } else if (request.getByteData() != null) {
                logger.debug("Request {} {} {} {} bytes", request.getMethod(), request.getUrl(), //
                    request.getHeaders().getFirstValue("Content-type"), request.getByteData().length);
            } else {
                logger.debug("Request {} {}", request.getMethod(), request.getUrl());
            }
            return client.executeRequest(request, handler);
        } catch (IOException e) {
            throw new ZenDeskException(e.getMessage(), e);
        }
    }

    private Request req(String method, Uri template) {
        RequestBuilder builder = new RequestBuilder(method);
        if (realm != null) {
            builder.setRealm(realm);
        }
        builder.setUrl(template.toString());
        return builder.build();
    }

    private Request req(String method, Uri template, String contentType, byte[] body) {
        RequestBuilder builder = new RequestBuilder(method);
        if (realm != null) {
            builder.setRealm(realm);
        }
        builder.setUrl(template.toString());
        builder.addHeader("Content-type", contentType);
        builder.setBody(body);
        return builder.build();
    }

    private Request req(String method, Uri template, int page) {
        RequestBuilder builder = new RequestBuilder(method);
        if (realm != null) {
            builder.setRealm(realm);
        }
        builder.addQueryParameter("page", Integer.toString(page));
        builder.setUrl(template.toString());
        return builder.build();
    }

    protected AsyncCompletionHandler<Void> handleStatus() {
        return new AsyncCompletionHandler<Void>() {
            @Override
            public Void onCompleted(Response response) throws Exception {
                logResponse(response);
                if (isStatus2xx(response)) {
                    return null;
                }
                throw new ZenDeskResponseException(response);
            }
        };
    }

    @SuppressWarnings("unchecked")
    protected <T> AsyncCompletionHandler<T> handle(final Class<T> clazz) {
        return new AsyncCompletionHandler<T>() {
            @Override
            public T onCompleted(Response response) throws Exception {
                logResponse(response);
                if (isStatus2xx(response)) {
                    return (T) mapper.reader(clazz).readValue(response.getResponseBodyAsBytes());
                }
                if (response.getStatusCode() == 404) {
                    return null;
                }
                throw new ZenDeskResponseException(response);
            }
        };
    }

    protected <T> AsyncCompletionHandler<T> handle(final Class<T> clazz, final String name) {
        return new AsyncCompletionHandler<T>() {
            @Override
            public T onCompleted(Response response) throws Exception {
                logResponse(response);
                if (isStatus2xx(response)) {
                    return mapper.convertValue(mapper.readTree(response.getResponseBodyAsBytes()).get(name), clazz);
                }
                if (response.getStatusCode() == 404) {
                    return null;
                }
                throw new ZenDeskResponseException(response);
            }
        };
    }

    protected <T> AsyncCompletionHandler<List<T>> handleList(final Class<T> clazz) {
        return new AsyncCompletionHandler<List<T>>() {
            @Override
            public List<T> onCompleted(Response response) throws Exception {
                logResponse(response);
                if (isStatus2xx(response)) {
                    List<T> values = new ArrayList<T>();
                    for (JsonNode node : mapper.readTree(response.getResponseBodyAsBytes())) {
                        values.add(mapper.convertValue(node, clazz));
                    }
                    return values;
                }
                throw new ZenDeskResponseException(response);
            }
        };
    }

    protected <T> AsyncCompletionHandler<List<T>> handleList(final Class<T> clazz, final String name) {
        return new AsyncCompletionHandler<List<T>>() {
            @Override
            public List<T> onCompleted(Response response) throws Exception {
                logResponse(response);
                if (isStatus2xx(response)) {
                    List<T> values = new ArrayList<T>();
                    for (JsonNode node : mapper.readTree(response.getResponseBodyAsBytes()).get(name)) {
                        values.add(mapper.convertValue(node, clazz));
                    }
                    return values;
                }
                throw new ZenDeskResponseException(response);
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
        logger.debug("Response HTTP/{} {}\n{}", response.getStatusCode(), response.getStatusText(),
            response.getResponseBody());
        if (logger.isTraceEnabled()) {
            logger.trace("Response headers {}", response.getHeaders());
        }
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
            throw new ZenDeskException(e.getMessage(), e);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ZenDeskException) {
                throw (ZenDeskException) e.getCause();
            }
            throw new ZenDeskException(e.getMessage(), e);
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

    private static void checkHasToken(Attachment.Upload upload) {
        if (upload.getToken() == null) {
            throw new IllegalArgumentException("Upload requires token");
        }
    }

    private static List<Integer> idArray(int id, int... ids) {
        List<Integer> result = new ArrayList<Integer>(ids.length + 1);
        result.add(id);
        for (int i : ids) {
            result.add(i);
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
        private final AsyncCompletionHandler<List<T>> handler;
        private final int initialPage;

        private PagedIterable(Uri url, AsyncCompletionHandler<List<T>> handler) {
            this(url, handler, 1);
        }

        private PagedIterable(Uri url, AsyncCompletionHandler<List<T>> handler, int initialPage) {
            this.handler = handler;
            this.url = url;
            this.initialPage = initialPage;
        }

        public Iterator<T> iterator() {
            return new PagedIterator(initialPage);
        }

        private class PagedIterator implements Iterator<T> {

            private Iterator<T> current;
            private int page;

            private PagedIterator(int page) {
                this.page = page;
            }

            public boolean hasNext() {
                if (current == null || !current.hasNext()) {
                    if (page > 0) {
                        List<T> values = complete(submit(req("GET", url, page++), handler));
                        if (values.isEmpty()) {
                            page = -1;
                        }
                        current = values.iterator();
                    } else {
                        return false;
                    }
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
            }
            return this;
        }

        public Builder setToken(String token) {
            this.token = token;
            if (token != null) {
                this.password = null;
            }
            return this;
        }

        public Builder setRetry(boolean retry) {
            return this;
        }

        public ZenDesk build() {
            if (token == null) {
                return new ZenDesk(client, url, username, password);
            }
            return new ZenDesk(client, url, username + "/token", token);
        }
    }
}
