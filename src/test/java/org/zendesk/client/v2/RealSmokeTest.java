package org.zendesk.client.v2;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.zendesk.client.v2.model.Audit;
import org.zendesk.client.v2.model.events.Event;
import org.zendesk.client.v2.model.Ticket;

import java.util.Collections;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeThat;

/**
 * @author stephenc
 * @since 04/04/2013 13:57
 */
public class RealSmokeTest {

    private static Properties config;

    private ZenDesk instance;

    @BeforeClass
    public static void loadConfig() {
        config = ZenDeskConfig.load();
        assumeThat("We have a configuration", config, notNullValue());
        assertThat("Configuration has an url", config.getProperty("url"), notNullValue());
    }

    public void assumeHaveToken() {
        assumeThat("We have a username", config.getProperty("username"), notNullValue());
        assumeThat("We have a token", config.getProperty("token"), notNullValue());
    }

    public void assumeHavePassword() {
        assumeThat("We have a username", config.getProperty("username"), notNullValue());
        assumeThat("We have a password", config.getProperty("password"), notNullValue());
    }

    @After
    public void closeClient() {
        if (instance != null) {
            instance.close();
        }
        instance = null;
    }

    @Test
    public void createClientWithToken() throws Exception {
        assumeHaveToken();
        instance = new ZenDesk.Builder(config.getProperty("url"))
                .setUsername(config.getProperty("username"))
                .setToken(config.getProperty("token"))
                .build();
    }

    @Test
    public void getTicket() throws Exception {
        createClientWithToken();
        Ticket ticket = instance.getTicket(1);
        assertThat(ticket, notNullValue());
    }

    @Test
    public void getTicketsPagesRequests() throws Exception {
        createClientWithToken();
        int count = 0;
        for (Ticket t : instance.getTickets()) {
            assertThat(t.getSubject(), notNullValue());
            if (++count > 150) {
                break;
            }
        }
        assertThat(count, is(151));
    }

    @Test
    public void getRecentTickets() throws Exception {
        createClientWithToken();
        int count = 0;
        for (Ticket t : instance.getRecentTickets()) {
            assertThat(t.getSubject(), notNullValue());
            if (++count > 150) {
                break;
            }
        }
        assertThat(count, is(151));
    }

    @Test
    public void getTicketsById() throws Exception {
        createClientWithToken();
        int count = 1;
        for (Ticket t : instance.getTickets(1, 3, 5)) {
            assertThat(t.getSubject(), notNullValue());
            assertThat(t.getId(), is(count));
            count+= 2;
        }
        assertThat(count, is(7));
    }

    @Test
    public void getTicketAudits() throws Exception {
        createClientWithToken();
        for (Audit a : instance.getTicketAudits(1)) {
            assertThat(a, notNullValue());
            assertThat(a.getEvents(), not(Collections.<Event>emptyList()));
        }
    }

    @Test
    public void createClientWithPassword() throws Exception {
        assumeHavePassword();
        instance = new ZenDesk.Builder(config.getProperty("url"))
                .setUsername(config.getProperty("username"))
                .setPassword(config.getProperty("password"))
                .build();
        Ticket t = instance.getTicket(1);
        assertThat(t, notNullValue());
        System.out.println(t);
    }

    @Test
    public void createAnonymousClient() {
        instance = new ZenDesk.Builder(config.getProperty("url"))
                .build();
    }

    @Test
    @Ignore("Don't spam the production zendesk")
    public void createDeleteTicket() throws Exception {
        createClientWithToken();
        assumeThat("Must have a requester email", config.getProperty("requester.email"), notNullValue());
        Ticket t = new Ticket(new Ticket.Requester(config.getProperty("requester.name"), config.getProperty("requester.email")), "This is a test", new Ticket.Comment("Please ignore this ticket"));
        Ticket ticket = instance.createTicket(t);
        System.out.println(ticket.getId() + " -> " + ticket.getUrl());
        assertThat(ticket.getId(), notNullValue());
        try {
            Ticket t2 = instance.getTicket(ticket.getId());
            assertThat(t2, notNullValue());
            assertThat(t2.getId(), is(ticket.getId()));
        } finally {
            instance.deleteTicket(ticket.getId());
        }
        assertThat(ticket.getSubject(), is(t.getSubject()));
        assertThat(ticket.getRequester(), nullValue());
        assertThat(ticket.getRequesterId(), notNullValue());
        assertThat(ticket.getDescription(), is(t.getComment().getBody()));
        assertThat(instance.getTicket(ticket.getId()), nullValue());
    }
}
