package org.zendesk.client.v2;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.zendesk.client.v2.model.Audit;
import org.zendesk.client.v2.model.Comment;
import org.zendesk.client.v2.model.Field;
import org.zendesk.client.v2.model.Group;
import org.zendesk.client.v2.model.Identity;
import org.zendesk.client.v2.model.Organization;
import org.zendesk.client.v2.model.Request;
import org.zendesk.client.v2.model.Status;
import org.zendesk.client.v2.model.Ticket;
import org.zendesk.client.v2.model.TicketForm;
import org.zendesk.client.v2.model.User;
import org.zendesk.client.v2.model.events.Event;
import org.zendesk.client.v2.model.targets.Target;

import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;

/**
 * @author stephenc
 * @since 04/04/2013 13:57
 */
public class RealSmokeTest {

    private static Properties config;

    private Zendesk instance;

    @BeforeClass
    public static void loadConfig() {
        config = ZendeskConfig.load();
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

    public void assumeHaveTokenOrPassword() {
        assumeThat("We have a username", config.getProperty("username"), notNullValue());
        assumeThat("We have a token or password", config.getProperty("token") != null || config.getProperty("password") != null, is(
                true));
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
        instance = new Zendesk.Builder(config.getProperty("url"))
                .setUsername(config.getProperty("username"))
                .setToken(config.getProperty("token"))
                .build();
    }

    @Test
    public void createClientWithTokenOrPassword() throws Exception {
        assumeHaveTokenOrPassword();
        final Zendesk.Builder builder = new Zendesk.Builder(config.getProperty("url"))
                .setUsername(config.getProperty("username"));
        if (config.getProperty("token") != null) {
            builder.setToken(config.getProperty("token"));
        } else if (config.getProperty("password") != null) {
            builder.setPassword(config.getProperty("password"));
        }
        instance = builder.build();
    }

    @Test
    public void getTicket() throws Exception {
        createClientWithTokenOrPassword();
        Ticket ticket = instance.getTicket(1);
        assertThat(ticket, notNullValue());
    }

    @Test
    public void getTicketForm() throws Exception {
        createClientWithTokenOrPassword();
        TicketForm ticketForm = instance.getTicketForm(27562);
        assertThat(ticketForm, notNullValue());
        assertTrue(ticketForm.isEndUserVisible());
    }
 
    @Test
    public void getTicketForms() throws Exception {
        createClientWithTokenOrPassword();
        Iterable<TicketForm> ticketForms = instance.getTicketForms();
        assertTrue(ticketForms.iterator().hasNext());
        for(TicketForm ticketForm : ticketForms){
        	assertThat(ticketForm, notNullValue());
        }
    }
    
    @Test
    public void getTicketFieldsOnForm() throws Exception {
        createClientWithTokenOrPassword();
        TicketForm ticketForm = instance.getTicketForm(27562);
        for(Integer id :ticketForm.getTicketFieldIds()){
        	Field f = instance.getTicketField(id);
        	assertNotNull(f);
        }
        assertThat(ticketForm, notNullValue());
        assertTrue(ticketForm.isEndUserVisible());
    }

    @Test
    public void getTargets() throws Exception {
        createClientWithTokenOrPassword();
        Long firstTargetId = null;
        for (Target target : instance.getTargets()) {
            assertNotNull(target);
            if (firstTargetId != null) {
                assertNotEquals(firstTargetId, target.getId()); // check for infinite loop
            } else {
                firstTargetId = target.getId();
            }
        }
    }
    
    @Test
    public void getTicketsPagesRequests() throws Exception {
        createClientWithTokenOrPassword();
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
        createClientWithTokenOrPassword();
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
        createClientWithTokenOrPassword();
        long count = 1;
        for (Ticket t : instance.getTickets(1, 6, 11)) {
            assertThat(t.getSubject(), notNullValue());
            assertThat(t.getId(), is(count));
            count += 5;
        }
        assertThat(count, is(16L));
    }

    @Test
    public void getTicketAudits() throws Exception {
        createClientWithTokenOrPassword();
        for (Audit a : instance.getTicketAudits(1L)) {
            assertThat(a, notNullValue());
            assertThat(a.getEvents(), not(Collections.<Event>emptyList()));
        }
    }

    @Test
    public void getTicketFields() throws Exception {
        createClientWithTokenOrPassword();
        int count = 0;
        for (Field f : instance.getTicketFields()) {
            assertThat(f, notNullValue());
            assertThat(f.getId(), notNullValue());
            assertThat(f.getType(), notNullValue());
            if (++count > 10) {
                break;
            }
        }
    }

    @Test
    public void createClientWithPassword() throws Exception {
        assumeHavePassword();
        instance = new Zendesk.Builder(config.getProperty("url"))
                .setUsername(config.getProperty("username"))
                .setPassword(config.getProperty("password"))
                .build();
        Ticket t = instance.getTicket(1);
        assertThat(t, notNullValue());
        System.out.println(t);
    }

    @Test
    public void createAnonymousClient() {
        instance = new Zendesk.Builder(config.getProperty("url"))
                .build();
    }

    @Test
    @Ignore("Don't spam zendesk")
    public void createDeleteTicket() throws Exception {
        createClientWithTokenOrPassword();
        assumeThat("Must have a requester email", config.getProperty("requester.email"), notNullValue());
        Ticket t = new Ticket(
                new Ticket.Requester(config.getProperty("requester.name"), config.getProperty("requester.email")),
                "This is a test", new Comment("Please ignore this ticket"));
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

    @Test
    @Ignore("Don't spam zendesk")
    public void createSolveTickets() throws Exception {
        createClientWithTokenOrPassword();
        assumeThat("Must have a requester email", config.getProperty("requester.email"), notNullValue());
        Ticket ticket;
        do {
            Ticket t = new Ticket(
                    new Ticket.Requester(config.getProperty("requester.name"), config.getProperty("requester.email")),
                    "This is a test " + UUID.randomUUID().toString(), new Comment("Please ignore this ticket"));
            ticket = instance.createTicket(t);
            System.out.println(ticket.getId() + " -> " + ticket.getUrl());
            assertThat(ticket.getId(), notNullValue());
                Ticket t2 = instance.getTicket(ticket.getId());
                assertThat(t2, notNullValue());
                assertThat(t2.getId(), is(ticket.getId()));
                t2.setAssigneeId(instance.getCurrentUser().getId());
                t2.setStatus(Status.CLOSED);
                instance.updateTicket(t2);
            assertThat(ticket.getSubject(), is(t.getSubject()));
            assertThat(ticket.getRequester(), nullValue());
            assertThat(ticket.getRequesterId(), notNullValue());
            assertThat(ticket.getDescription(), is(t.getComment().getBody()));
            assertThat(instance.getTicket(ticket.getId()), notNullValue());
        } while (ticket.getId() < 200L); // seed enough data for the paging tests
    }

    @Test
    public void lookupUserByEmail() throws Exception {
        createClientWithTokenOrPassword();
        String requesterEmail = config.getProperty("requester.email");
        assumeThat("Must have a requester email", requesterEmail, notNullValue());
        for (User user : instance.lookupUserByEmail(requesterEmail)) {
            assertThat(user.getEmail(), is(requesterEmail));
        }
    }

    @Test
    public void searchUserByEmail() throws Exception {
        createClientWithTokenOrPassword();
        String requesterEmail = config.getProperty("requester.email");
        assumeThat("Must have a requester email", requesterEmail, notNullValue());
        for (User user : instance.getSearchResults(User.class, "requester:"+requesterEmail)) {
            assertThat(user.getEmail(), is(requesterEmail));
        }
    }

    @Test
    public void lookupUserIdentities() throws Exception {
        createClientWithTokenOrPassword();
        User user = instance.getCurrentUser();
        for (Identity i : instance.getUserIdentities(user)) {
            assertThat(i.getId(), notNullValue());
            Identity j = instance.getUserIdentity(user, i);
            assertThat(j.getId(), is(i.getId()));
            assertThat(j.getType(), is(i.getType()));
            assertThat(j.getValue(), is(i.getValue()));
        }
    }

    @Test
    public void getUserRequests() throws Exception {
        createClientWithTokenOrPassword();
        User user = instance.getCurrentUser();
        int count = 5;
        for (Request r : instance.getUserRequests(user)) {
            assertThat(r.getId(), notNullValue());
            System.out.println(r.getSubject());
            for (Comment c : instance.getRequestComments(r)) {
                assertThat(c.getId(), notNullValue());
                System.out.println("  " + c.getBody());
            }
            if (--count < 0) {
                break;
            }
        }
    }

    @Test
    public void getOrganizations() throws Exception {
        createClientWithTokenOrPassword();
        int count = 0;
        for (Organization t : instance.getOrganizations()) {
            assertThat(t.getName(), notNullValue());
            if (++count > 10) {
                break;
            }
        }
    }

    @Test
    public void getGroups() throws Exception {
        createClientWithTokenOrPassword();
        int count = 0;
        for (Group t : instance.getGroups()) {
            assertThat(t.getName(), notNullValue());
            if (++count > 10) {
                break;
            }
        }
    }

}
