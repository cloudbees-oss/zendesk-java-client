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
import org.zendesk.client.v2.model.Field;
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
    private static final String JSON = "application/json";
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
        createMapper();
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
        Map<String,String> req = new HashMap<String,String>();
        req.put("previous_password", oldPassword);
        req.put("password", newPassword);
        complete(submit(req("PUT", tmpl("/users/{id}/password.json").set("id", user.getId()), JSON,
                json(req)), handleStatus()));
    }

    //////////////////////////////////////////////////////////////////////
    // Helper methods
    //////////////////////////////////////////////////////////////////////

    private String json(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ZenDeskException(e.getMessage(), e);
        }
    }

    private <T> ListenableFuture<T> submit(Request request, AsyncCompletionHandler<T> handler) {
        try {
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

    private Request req(String method, Uri template, String contentType, String body) {
        RequestBuilder builder = new RequestBuilder(method);
        if (realm != null) {
            builder.setRealm(realm);
        }
        builder.setUrl(template.toString());
        builder.addHeader("Content-type", contentType);
        builder.setBody(body);
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
                logger.debug("Response HTTP/{} {}\n{}", response.getStatusCode(), response.getStatusText(),
                        response.getResponseBody());
                if (response.getStatusCode() / 100 == 2) {
                    return null;
                }
                throw new ZenDeskException(response.getStatusText());
            }
        };
    }

    protected <T> AsyncCompletionHandler<T> handle(final Class<T> clazz) {
        return new AsyncCompletionHandler<T>() {
            @Override
            public T onCompleted(Response response) throws Exception {
                logger.debug("Response HTTP/{} {}\n{}", response.getStatusCode(), response.getStatusText(),
                        response.getResponseBody());
                if (response.getStatusCode() / 100 == 2) {
                    return (T) mapper.reader(clazz).readValue(response.getResponseBodyAsBytes());
                }
                if (response.getStatusCode() == 404) {
                    return null;
                }
                throw new ZenDeskException(response.getStatusText());
            }
        };
    }

    protected <T> AsyncCompletionHandler<T> handle(final Class<T> clazz, final String name) {
        return new AsyncCompletionHandler<T>() {
            @Override
            public T onCompleted(Response response) throws Exception {
                logger.debug("Response HTTP/{} {}\n{}", response.getStatusCode(), response.getStatusText(),
                        response.getResponseBody());
                if (response.getStatusCode() / 100 == 2) {
                    return mapper.convertValue(mapper.readTree(response.getResponseBodyAsBytes()).get(name), clazz);
                }
                if (response.getStatusCode() == 404) {
                    return null;
                }
                throw new ZenDeskException(response.getStatusText());
            }
        };
    }

    protected <T> AsyncCompletionHandler<List<T>> handleList(final Class<T> clazz) {
        return new AsyncCompletionHandler<List<T>>() {
            @Override
            public List<T> onCompleted(Response response) throws Exception {
                logger.info("Response HTTP/{} {}\n{}", response.getStatusCode(), response.getStatusText(),
                        response.getResponseBody());
                if (response.getStatusCode() / 100 == 2) {
                    List<T> values = new ArrayList<T>();
                    for (JsonNode node : mapper.readTree(response.getResponseBodyAsBytes())) {
                        values.add(mapper.convertValue(node, clazz));
                    }
                    return values;
                }
                throw new ZenDeskException(response.getStatusText());
            }
        };
    }

    protected <T> AsyncCompletionHandler<List<T>> handleList(final Class<T> clazz, final String name) {
        return new AsyncCompletionHandler<List<T>>() {
            @Override
            public List<T> onCompleted(Response response) throws Exception {
                logger.debug("Response HTTP/{} {}\n{}", response.getStatusCode(), response.getStatusText(),
                        response.getResponseBody());
                if (response.getStatusCode() / 100 == 2) {
                    List<T> values = new ArrayList<T>();
                    for (JsonNode node : mapper.readTree(response.getResponseBodyAsBytes()).get(name)) {
                        values.add(mapper.convertValue(node, clazz));
                    }
                    return values;
                }
                throw new ZenDeskException(response.getStatusText());
            }
        };
    }

    private TemplateUri tmpl(String template) {
        return new TemplateUri(url + template);
    }

    private Uri cnst(String template) {
        return new FixedUri(url + template);
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
            throw new ZenDeskException(e.getMessage(), e);
        }
    }

    private static void checkHasId(Ticket ticket) {
        if (ticket.getId() == null) {
            throw new IllegalArgumentException("Ticket requires id");
        }
    }

    private static void checkHasId(Audit audit) {
        if (audit.getId() == null) {
            throw new IllegalArgumentException("Audit requires id");
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
