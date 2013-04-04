package org.zendesk.client.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Realm;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zendesk.client.v2.model.Ticket;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

/**
 * @author stephenc
 * @since 04/04/2013 13:08
 */
public class ZenDesk implements Closeable {
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
        this.mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public boolean isClosed() {
        return closed || client.isClosed();
    }

    public void close() {
        if (closeClient && !client.isClosed()) {
            client.close();
        }
        closed = true;
    }

    public Ticket getTicket(int id) {
        return complete(submit(req("GET", "/tickets/" + id + ".json"), handle(Ticket.class, "ticket")));
    }

    public void deleteTicket(int id) {
        complete(submit(req("DELETE", "/tickets/" + id + ".json"), handleStatus()));
    }

    public Ticket createTicket(Ticket ticket) {
        try {
            return complete(submit(jsonReq("POST", "/tickets.json",
                    mapper.writeValueAsString(Collections.singletonMap("ticket", ticket))),
                    handle(Ticket.class, "ticket")));
        } catch (JsonProcessingException e) {
            throw new ZenDeskException(e.getMessage(), e);
        }
    }

    public void deleteTickets(int id, int... ids) {
        StringBuilder buf = new StringBuilder("/tickets/destroy_many.json?ids=");
        buf.append(id);
        for (int i : ids) {
            buf.append(',');
            buf.append(i);
        }
        complete(submit(req("DELETE", buf.toString()), handleStatus()));
    }

    public Iterable<Ticket> getTickets() {
        return new PagedIterable<Ticket>("/tickets.json", handleList(Ticket.class, "tickets"));
    }

    public List<Ticket> getTickets(int id, int... ids) {
        StringBuilder buf = new StringBuilder("/tickets/show_many.json?ids=");
        buf.append(id);
        for (int i : ids) {
            buf.append(',');
            buf.append(i);
        }
        return complete(submit(req("GET", buf.toString()), handleList(Ticket.class, "tickets")));
    }

    public Iterable<Ticket> getRecentTickets() {
        return new PagedIterable<Ticket>("/tickets/recent.json", handleList(Ticket.class, "tickets"));
    }

    public Iterable<Ticket> getOrganizationTickets(int organizationId) {
        return new PagedIterable<Ticket>("/organizations/" + organizationId + "/tickets.json",
                handleList(Ticket.class, "tickets"));
    }

    public Iterable<Ticket> getUserRequestedTickets(int userId) {
        return new PagedIterable<Ticket>("/users/" + userId + "/tickets/requested.json",
                handleList(Ticket.class, "tickets"));
    }

    public Iterable<Ticket> getUserCCDTickets(int userId) {
        return new PagedIterable<Ticket>("/users/" + userId + "/tickets/ccd.json", handleList(Ticket.class, "tickets"));
    }

    private <T> ListenableFuture<T> submit(Request request, AsyncCompletionHandler<T> handler) {
        try {
            return client.executeRequest(request, handler);
        } catch (IOException e) {
            throw new ZenDeskException(e.getMessage(), e);
        }
    }

    private Request req(String method, String url) {
        RequestBuilder builder = new RequestBuilder(method);
        if (realm != null) {
            builder.setRealm(realm);
        }
        builder.setUrl(this.url + url);
        return builder.build();
    }

    private Request jsonReq(String method, String url, String body) {
        RequestBuilder builder = new RequestBuilder(method);
        if (realm != null) {
            builder.setRealm(realm);
        }
        builder.setUrl(this.url + url);
        builder.addHeader("Content-type", "application/json");
        builder.setBody(body);
        return builder.build();
    }

    private Request req(String method, String url, int page) {
        RequestBuilder builder = new RequestBuilder(method);
        if (realm != null) {
            builder.setRealm(realm);
        }
        builder.addQueryParameter("page", Integer.toString(page));
        builder.setUrl(this.url + url);
        return builder.build();
    }

    private <T> T complete(ListenableFuture<T> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            throw new ZenDeskException(e.getMessage(), e);
        } catch (ExecutionException e) {
            throw new ZenDeskException(e.getMessage(), e);
        }
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
                logger.debug("Response HTTP/{} {}\n{}", response.getStatusCode(), response.getStatusText(),
                        response.getResponseBody());
                if (response.getStatusCode() / 100 == 2) {
                    List<T> values = new ArrayList<T>();
                    MappingIterator<T> iterator =
                            mapper.reader(clazz).readValues(response.getResponseBody());
                    while (iterator.hasNext()) {
                        values.add(iterator.next());
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

    private class PagedIterable<T> implements Iterable<T> {

        private final String url;
        private final AsyncCompletionHandler<List<T>> handler;
        private final int initialPage;

        private PagedIterable(String url, AsyncCompletionHandler<List<T>> handler) {
            this(url, handler, 1);
        }

        private PagedIterable(String url, AsyncCompletionHandler<List<T>> handler, int initialPage) {
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
