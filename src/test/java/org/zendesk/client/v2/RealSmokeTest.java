package org.zendesk.client.v2;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.stream.StreamSupport;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zendesk.client.v2.model.AgentRole;
import org.zendesk.client.v2.model.Audit;
import org.zendesk.client.v2.model.Brand;
import org.zendesk.client.v2.model.Collaborator;
import org.zendesk.client.v2.model.Comment;
import org.zendesk.client.v2.model.ComplianceDeletionStatus;
import org.zendesk.client.v2.model.Field;
import org.zendesk.client.v2.model.Group;
import org.zendesk.client.v2.model.Identity;
import org.zendesk.client.v2.model.JobStatus;
import org.zendesk.client.v2.model.JobStatus.JobStatusEnum;
import org.zendesk.client.v2.model.Organization;
import org.zendesk.client.v2.model.Priority;
import org.zendesk.client.v2.model.Request;
import org.zendesk.client.v2.model.SortOrder;
import org.zendesk.client.v2.model.Status;
import org.zendesk.client.v2.model.SuspendedTicket;
import org.zendesk.client.v2.model.Ticket;
import org.zendesk.client.v2.model.TicketForm;
import org.zendesk.client.v2.model.User;
import org.zendesk.client.v2.model.dynamic.DynamicContentItem;
import org.zendesk.client.v2.model.dynamic.DynamicContentItemVariant;
import org.zendesk.client.v2.model.events.Event;
import org.zendesk.client.v2.model.hc.Article;
import org.zendesk.client.v2.model.hc.Category;
import org.zendesk.client.v2.model.hc.Section;
import org.zendesk.client.v2.model.hc.Subscription;
import org.zendesk.client.v2.model.hc.Translation;
import org.zendesk.client.v2.model.schedules.Holiday;
import org.zendesk.client.v2.model.schedules.Interval;
import org.zendesk.client.v2.model.schedules.Schedule;
import org.zendesk.client.v2.model.targets.Target;

/**
 * @author stephenc
 * @since 04/04/2013 13:57
 */
public class RealSmokeTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealSmokeTest.class);

    // TODO: Find a better way to manage our test environment (this is the PUBLIC_FORM_ID of the cloudbees org)
    private static final long CLOUDBEES_ORGANIZATION_ID = 360507899132L;
    private static final long PUBLIC_FORM_ID = 360000434032L;

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
    public void getBrands() throws Exception {
        createClientWithTokenOrPassword();
        List<Brand> brands = instance.getBrands();
        assertTrue(brands.iterator().hasNext());
        for(Brand brand : brands){
            assertThat(brand, notNullValue());
        }
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
        TicketForm ticketForm = instance.getTicketForm(PUBLIC_FORM_ID);
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
        TicketForm ticketForm = instance.getTicketForm(PUBLIC_FORM_ID);
        for(Long id :ticketForm.getTicketFieldIds()){
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
            if (++count > 10) {
                break;
            }
        }
        // Recent tickets are always < 5
        assertThat(count, lessThanOrEqualTo(5));
    }

    @Test
    public void getTicketsById() throws Exception {
        createClientWithTokenOrPassword();
        long count = 24;
        final List<Long> ticketIds = Collections.unmodifiableList(Arrays.asList(22L, 24L, 26L));

        for (Ticket t : instance.getTickets(22, 24, 26)) {
            assertThat(t.getSubject(), notNullValue());
            assertThat(ticketIds.contains(t.getId()), is(true));
            count += 2;
        }
        assertThat(count, is(30L));
    }

    @Test
    public void getTicketsIncrementally() throws Exception {
        createClientWithTokenOrPassword();
        int count = 0;
        for (Ticket t : instance.getTicketsIncrementally(new Date(0L))) {
            assertThat(t.getId(), notNullValue());
            if (++count > 10) {
                break;
            }
        }
    }

    @Test
    public void getOrganizationTickets() throws Exception {
        createClientWithTokenOrPassword();
        int count = 0;
        for (Ticket t : instance.getOrganizationTickets(CLOUDBEES_ORGANIZATION_ID)) {
            assertThat(t.getId(), notNullValue());
            if (++count > 10) {
                break;
            }
        }
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
    public void createDeleteTicket() throws Exception {
        createClientWithTokenOrPassword();
        assumeThat("Must have a requester email", config.getProperty("requester.email"), notNullValue());
        Ticket t = new Ticket(
                new Ticket.Requester(config.getProperty("requester.name"), config.getProperty("requester.email")),
                "This is a test", new Comment("Please ignore this ticket"));
        t.setCollaborators(Arrays.asList(new Collaborator("Bob Example", "bob@example.org"), new Collaborator("Alice Example", "alice@example.org")));
        Ticket ticket = instance.createTicket(t);
        System.out.println(ticket.getId() + " -> " + ticket.getUrl());
        assertThat(ticket.getId(), notNullValue());
        try {
            Ticket t2 = instance.getTicket(ticket.getId());
            assertThat(t2, notNullValue());
            assertThat(t2.getId(), is(ticket.getId()));

            List<User> ticketCollaborators = instance.getTicketCollaborators(ticket.getId());
            assertThat("Collaborators", ticketCollaborators.size(), is(2));
            assertThat("First Collaborator", ticketCollaborators.get(0).getEmail(), anyOf(is("alice@example.org"), is("bob@example.org")));
        } finally {
            instance.deleteTicket(ticket.getId());
        }
        assertThat(ticket.getSubject(), is(t.getSubject()));
        assertThat(ticket.getRequester(), nullValue());
        assertThat(ticket.getRequesterId(), notNullValue());
        assertThat(ticket.getDescription(), is(t.getComment().getBody()));
        assertThat("Collaborators", ticket.getCollaboratorIds().size(), is(2));
        assertThat(instance.getTicket(ticket.getId()), nullValue());
    }

    @Test
    public void createPermanentlyDeleteTicket() throws Exception {
        createClientWithTokenOrPassword();
        assumeThat("Must have a requester email", config.getProperty("requester.email"), notNullValue());
        Ticket t = new Ticket(
                new Ticket.Requester(config.getProperty("requester.name"), config.getProperty("requester.email")),
                "This is a test", new Comment("Please ignore this ticket"));
        t.setCollaborators(Arrays.asList(new Collaborator("Bob Example", "bob@example.org"), new Collaborator("Alice Example", "alice@example.org")));
        Ticket ticket = instance.createTicket(t);
        System.out.println(ticket.getId() + " -> " + ticket.getUrl());
        assertThat(ticket.getId(), notNullValue());

        try {
            Ticket t2 = instance.getTicket(ticket.getId());
            assertThat(t2, notNullValue());
            assertThat(t2.getId(), is(ticket.getId()));
        } finally {
            instance.permanentlyDeleteTicket(ticket.getId());
        }
        assertThat(instance.getTicket(ticket.getId()), nullValue());
    }

    @Test
    @Ignore("This test isn't stable in the CI env. Not sure why, it's working locally.")
    // TODO: Fix this test
    public void createPermanentlyDeleteTickets() throws Exception {
        createClientWithTokenOrPassword();
        assumeThat("Must have a requester email", config.getProperty("requester.email"), notNullValue());
        Ticket t = new Ticket(
                new Ticket.Requester(config.getProperty("requester.name"), config.getProperty("requester.email")),
                "This is a test", new Comment("Please ignore this ticket"));
        t.setCollaborators(Arrays.asList(new Collaborator("Bob Example", "bob@example.org"), new Collaborator("Alice Example", "alice@example.org")));
        Ticket t2 = new Ticket(
                new Ticket.Requester(config.getProperty("requester.name"), config.getProperty("requester.email")),
                "This is a test_2", new Comment("Please ignore this ticket_2"));
        t2.setCollaborators(Arrays.asList(new Collaborator("Bob Example_2", "bob@example.org"), new Collaborator("Alice Example_2", "alice@example.org")));
        Ticket ticket = instance.createTicket(t);
        Ticket ticket2 = instance.createTicket(t2);
        System.out.println(ticket.getId() + " -> " + ticket.getUrl());
        System.out.println(ticket2.getId() + " -> " + ticket2.getUrl());
        assertThat(ticket.getId(), notNullValue());
        assertThat(ticket2.getId(), notNullValue());

        try {
            Ticket t3 = instance.getTicket(ticket.getId());
            assertThat(t3, notNullValue());
            assertThat(t3.getId(), is(ticket.getId()));

            Ticket t4 = instance.getTicket(ticket2.getId());
            assertThat(t4, notNullValue());
            assertThat(t4.getId(), is(ticket2.getId()));
        } finally {
            instance.permanentlyDeleteTickets(ticket.getId(), ticket2.getId());
        }
        assertThat(instance.getTicket(ticket.getId()), nullValue());
        assertThat(instance.getTicket(ticket2.getId()), nullValue());
    }

    @Test
    public void createSolveTickets() throws Exception {
        createClientWithTokenOrPassword();
        assumeThat("Must have a requester email", config.getProperty("requester.email"), notNullValue());
        Ticket ticket;
        long firstId = Long.MAX_VALUE;
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
            firstId = Math.min(ticket.getId(), firstId);
        } while (ticket.getId() < firstId + 5L); // seed enough data for the paging tests
    }

    @Test
    public void testUpdateTickets() throws Exception {
        createClientWithTokenOrPassword();
        Ticket t = new Ticket(
                new Ticket.Requester(config.getProperty("requester.name"), config.getProperty("requester.email")),
                "This is a test " + UUID.randomUUID().toString(), new Comment("Please ignore this ticket"));
        Ticket ticket1 = instance.createTicket(t);
        Ticket t2 = new Ticket(
                new Ticket.Requester(config.getProperty("requester.name"), config.getProperty("requester.email")),
                "This is a test " + UUID.randomUUID().toString(), new Comment("Please ignore this ticket"));
        Ticket ticket2 = instance.createTicket(t2);
        ticket1.setPriority(Priority.HIGH);
        ticket2.setPriority(Priority.LOW);
        ticket1.setStatus(Status.SOLVED);
        ticket2.setStatus(Status.SOLVED);

        JobStatus<Ticket> jobstatus = instance.updateTicketsAsync(Arrays.asList(ticket1, ticket2)).toCompletableFuture().join();
        assertThat(jobstatus.getStatus(), is(JobStatus.JobStatusEnum.queued));
        //TODO: uncomment the rest of this test once issue #98 is resolved: https://github.com/cloudbees/zendesk-java-client/issues/98
//        Instant startUpdateAt = Instant.now();
//        while (instance.getJobStatus(jobstatus).getStatus() != JobStatus.JobStatusEnum.completed
//                && startUpdateAt.plusSeconds(10).isAfter(Instant.now())) {
//            Thread.sleep(100);
//        }
//        JobStatus<Ticket> completedJobStatus = instance.getJobStatus(jobstatus);
//        assertThat(completedJobStatus.getStatus(), is(JobStatus.JobStatusEnum.completed));
//        assertNotNull(jobstatus.getResults());
//        assertThat(jobstatus.getResults().size(), is(2));
//        jobstatus.getResults().forEach(ticket -> {
//            if (ticket.getId().equals(ticket1.getId())) {
//                assertThat(ticket.getPriority(), is(Priority.HIGH));
//                assertThat(ticket.getStatus(), is(Status.SOLVED));
//            } else if (ticket.getId().equals(ticket2.getId())) {
//                assertThat(ticket.getPriority(), is(Priority.LOW));
//                assertThat(ticket.getStatus(), is(Status.SOLVED));
//            } else {
//                fail("Received a different ticket back in response: " + ticket.getId());
//            }
//        });
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
    public void updateUserIdentity() throws Exception {
        createClientWithTokenOrPassword();
        User user = instance.getCurrentUser();

        Identity identity = new Identity();
        identity.setUserId(user.getId());
        identity.setType("email");
        identity.setValue("first@test.com");

        Identity createdIdentity = instance.createUserIdentity(user, identity);
        try {
            assertThat(createdIdentity.getValue(), is("first@test.com"));

            createdIdentity.setValue("second@test.com");
            Identity updatedIdentity = instance.updateUserIdentity(user, createdIdentity);

            assertThat(updatedIdentity.getValue(), is("second@test.com"));
        } finally {
            if (createdIdentity != null) {
                instance.deleteUserIdentity(user, createdIdentity.getId());
            }
        }
    }

    @Test
    public void suspendUser() throws Exception {
        createClientWithTokenOrPassword();

        String name = "testSuspendUser";
        String externalId = "testSuspendUser";

        // Clean up to avoid conflicts
        for (User u: instance.lookupUserByExternalId(externalId)){
            instance.deleteUser(u.getId());
        }

        // Create user
        User newUser = new User(true, name);
        newUser.setExternalId(externalId);
        User user = instance.createOrUpdateUser(newUser);
        assertNotNull(user);
        assertNotNull(user.getId());
        assertThat(user.getSuspended(), is(false));

        User suspendResult = instance.suspendUser(user.getId());
        assertNotNull(suspendResult);
        assertNotNull(suspendResult.getId());
        assertThat(suspendResult.getId(), is(user.getId()));
        assertThat(suspendResult.getSuspended(), is(true));

        User unsuspendResult = instance.unsuspendUser(user.getId());
        assertNotNull(unsuspendResult);
        assertNotNull(unsuspendResult.getId());
        assertThat(unsuspendResult.getId(), is(user.getId()));
        assertThat(unsuspendResult.getSuspended(), is(false));

        // Cleanup
        instance.deleteUser(user);
    }

    @Test
    public void showUserComplianceDeletionStatusExpectException() throws Exception {
        createClientWithTokenOrPassword();

        final String name = "testSuspendUser";
        final String externalId = "testSuspendUser";

        User newUser = new User(true, name);
        newUser.setExternalId(externalId);
        User user = instance.createOrUpdateUser(newUser);
        assertNotNull(user);
        assertNotNull(user.getId());

        try {
            instance.getComplianceDeletionStatuses(user.getId());
        } catch (Exception e) {
            assertThat(e.getMessage(), is("HTTP/404: Not Found - {\"error\":\"RecordNotFound\",\"description\":\"Not found\"}"));
        }

        instance.permanentlyDeleteUser(user);
    }

    @Test
    public void showUserComplianceDeletionStatusExpectValidCompletionStatus() throws Exception {
        createClientWithTokenOrPassword();

        final String name = "testSuspendUser";
        final String externalId = "testSuspendUser";

        User newUser = new User(true, name);
        newUser.setExternalId(externalId);
        User user = instance.createOrUpdateUser(newUser);
        assertNotNull(user);
        assertNotNull(user.getId());

        instance.permanentlyDeleteUser(user);

        // https://developer.zendesk.com/rest_api/docs/support/users#show-compliance-deletion-statuses
        // The deletion is going through different states ( request_deletion -> started -> complete )
        // for different applications and they are described in compliance_deletion_statuses

        final Iterable<ComplianceDeletionStatus> complianceDeletionStatuses =
                instance.getComplianceDeletionStatuses(user.getId());

        // Let's validate

        assertThat("There is at least one entry",
                StreamSupport.stream(complianceDeletionStatuses.spliterator(), false).count(), greaterThan(0L));

        assertTrue("There is at least an entry for the application \"all\"",
                StreamSupport.stream(complianceDeletionStatuses.spliterator(), false)
                        .anyMatch(complianceDeletionStatus -> "all".equals(complianceDeletionStatus.getApplication())));

        complianceDeletionStatuses.forEach(status -> {
            LOGGER.info("Compliance Deletion Status : {}", status);
            // All entries are about this user
            assertThat(status.getUserId(), is(user.getId()));
        });
    }

    @Test
    public void permanentlyDeleteUser() throws Exception {
        createClientWithTokenOrPassword();

        String name = "testSuspendUser";
        String externalId = "testSuspendUser";

        // Clean up to avoid conflicts
        for (User u: instance.lookupUserByExternalId(externalId)){
            instance.deleteUser(u.getId());
        }

        // Create user
        User newUser = new User(true, name);
        newUser.setExternalId(externalId);
        User user = instance.createOrUpdateUser(newUser);
        assertNotNull(user);
        assertNotNull(user.getId());

        instance.permanentlyDeleteUser(user);

        assertThat(instance.getUser(user.getId()).getActive(), is(false));
        // Cleanup
        instance.deleteUser(user);
    }

    @Test
    public void getUserRequests() throws Exception {
        createClientWithTokenOrPassword();
        User user = instance.getCurrentUser();
        int count = 5;
        for (Request r : instance.getUserRequests(user)) {
            assertThat(r.getId(), notNullValue());
            for (Comment c : instance.getRequestComments(r)) {
                assertThat(c.getId(), notNullValue());
            }
            if (--count < 0) {
                break;
            }
        }
    }

    @Test
    public void getUsers() throws Exception {
        createClientWithTokenOrPassword();
        int count = 0;
        for (User u : instance.getUsers()) {
            assertThat(u.getName(), notNullValue());
            if (++count > 10) {
                break;
            }
        }
    }

    @Test
    public void getUsersIncrementally() throws Exception {
        createClientWithTokenOrPassword();
        int count = 0;
        for (User u : instance.getUsersIncrementally(new Date(0L))) {
            assertThat(u.getName(), notNullValue());
            if (++count > 10) {
                break;
            }
        }
    }

    @Test
    public void getSuspendedTickets() throws Exception {
        createClientWithTokenOrPassword();
        int count = 0;
        for (SuspendedTicket ticket : instance.getSuspendedTickets()) {
            assertThat(ticket.getId(), notNullValue());
            if (++count > 10) {
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
    public void getOrganizationsIncrementally() throws Exception {
        createClientWithTokenOrPassword();
        int count = 0;
        for (Organization t : instance.getOrganizationsIncrementally(new Date(0L))) {
            assertThat(t.getName(), notNullValue());
            if (++count > 10) {
                break;
            }
        }
    }

    @Test
    public void createOrganization() throws Exception {
        createClientWithTokenOrPassword();

        // Clean up to avoid conflicts
        for (Organization t : instance.getOrganizations()) {
            if ("testorg".equals(t.getExternalId())) {
                instance.deleteOrganization(t);
            }
        }

        Organization org = new Organization();
        org.setExternalId("testorg");
        org.setName("Test Organization");
        Organization result = instance.createOrganization(org);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Test Organization", result.getName());
        assertEquals("testorg", result.getExternalId());
        instance.deleteOrganization(result);
    }

    @Test(timeout = 10000)
    public void createOrganizations() throws Exception {
        createClientWithTokenOrPassword();

        // Clean up to avoid conflicts
        for (Organization t : instance.getOrganizations()) {
            if ("testorg1".equals(t.getExternalId()) || "testorg2".equals(t.getExternalId())) {
                instance.deleteOrganization(t);
            }
        }

        Organization org1 = new Organization();
        org1.setExternalId("testorg1");
        org1.setName("Test Organization 1");

        Organization org2 = new Organization();
        org2.setExternalId("testorg2");
        org2.setName("Test Organization 2");

        JobStatus<Organization> result = instance.createOrganizations(org1, org2);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getStatus());

        while (result.getStatus() != JobStatus.JobStatusEnum.completed) {
            result = instance.getJobStatus(result);
            assertNotNull(result);
            assertNotNull(result.getId());
            assertNotNull(result.getStatus());
        }

        List<Organization> resultOrgs = result.getResults();

        assertEquals(2, resultOrgs.size());
        for (Organization org : resultOrgs) {
            assertNotNull(org.getId());
            instance.deleteOrganization(org);
        }
    }

    @Test(timeout = 10000)
    public void createOrUpdateUsers() throws Exception {
        createClientWithTokenOrPassword();

        User user1 = new User();
        user1.setEmail("example@example.com");
        user1.setName("Chuck Norris");

        User user2 = new User();
        user2.setEmail("example+user2$example.com");
        user2.setName("Norris Chuck");

        JobStatus<User> result = instance.createUsers(user1, user2);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getStatus());

        while (result.getStatus() != JobStatusEnum.completed) {
            result = instance.getJobStatus(result);
            assertNotNull(result);
            assertNotNull(result.getId());
            assertNotNull(result.getStatus());
        }
        List<User> resultUsers = result.getResults();

        assertEquals(2, resultUsers.size());
        for (User user : resultUsers){
            assertNotNull(user.getId());
            instance.deleteUser(user);
        };


    }

    @Test(timeout = 10000)
    public void bulkCreateMultipleJobs() throws Exception {
        createClientWithTokenOrPassword();

        List<Organization> orgs = new ArrayList<>(4);
        for (int i = 1; i <= 5; i++) {
            Organization org = new Organization();
            org.setExternalId("testorg" + i);
            org.setName("Test Organization " + i);
            orgs.add(org);
        }

        // Clean up to avoid conflicts
        for (Organization t : instance.getOrganizations()) {
            for (Organization org : orgs) {
                if (org.getExternalId().equals(t.getExternalId())) {
                    instance.deleteOrganization(t);
                }
            }
        }


        JobStatus result1 = instance.createOrganizations(orgs.subList(0, 2));
        JobStatus result2 = instance.createOrganizations(orgs.subList(2, 5));

        while (result1.getStatus() != JobStatus.JobStatusEnum.completed || result2.getStatus() != JobStatus.JobStatusEnum.completed) {
            List<JobStatus<HashMap<String, Object>>> results = instance.getJobStatuses(Arrays.asList(result1, result2));
            result1 = results.get(0);
            result2 = results.get(1);
            assertNotNull(result1);
            assertNotNull(result1.getId());
            assertNotNull(result2);
            assertNotNull(result2.getId());
        }

        List<HashMap> resultOrgs1 = result1.getResults();
        assertEquals(2, resultOrgs1.size());
        List<HashMap> resultOrgs2 = result2.getResults();
        assertEquals(3, resultOrgs2.size());

        for (HashMap org : resultOrgs1) {
            assertNotNull(org.get("id"));
            instance.deleteOrganization(((Number) org.get("id")).longValue());
        }

        for (HashMap org : resultOrgs2) {
            assertNotNull(org.get("id"));
            instance.deleteOrganization(((Number) org.get("id")).longValue());
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

    @Test
    public void getArticles() throws Exception {
        createClientWithTokenOrPassword();
        int count = 0;
        for (Article t : instance.getArticles()) {
            assertThat(t.getTitle(), notNullValue());
            if (++count > 40) {  // Check enough to pull 2 result pages
                break;
            }
        }
    }

    @Test
    public void getArticlesFromAnyLabels() throws Exception {
        createClientWithTokenOrPassword();
        /*
         Given 3 articles
            Article 1 with title "SomeLabelOne" and label "SomeLabelA"
            Article 2 with title "SomeLabelTwo" and labels "SomeLabelB" and "SomeLabelC"
            Article 3 with title "SomeLabelThree" and label "SomeLabelD"
         When a search by labels "SomeLabelA", "SomeLabelB"
         Then we get Article 1 and Article 2 but not Article 3
            because Article 1 and 2 have at least one of the labels, Article 3 has none
         */
        Iterable<Article> result = instance.getArticlesFromAnyLabels(Arrays.asList("SomeLabelA", "SomeLabelB"));
        Set<String> actualTitles = extractTitles(result);
        assertThat(actualTitles.size(), is(2));
        assertThat(actualTitles, IsCollectionContaining.hasItems("SomeLabelOne", "SomeLabelTwo"));
    }

    @Test
    public void getArticlesFromAllLabels() throws Exception {
        createClientWithTokenOrPassword();
        /*
         Given 2 articles
            Article 1 with title "AllLabelOne" and label "AllLabelA"
            Article 2 with title "AllLabelTwo" and labels "AllLabelA" and "AllLabelB"
         When a search by labels "AllLabelA", "AllLabelB"
         Then we get Article 2 but not Article 1
            because Article 2 has both labels and Article 1 has only one
         */
        Iterable<Article> result = instance.getArticlesFromAllLabels(Arrays.asList("AllLabelA", "AllLabelB"));
        Set<String> actualTitles = extractTitles(result);
        assertThat(actualTitles.size(), is(1));
        assertThat(actualTitles, IsCollectionContaining.hasItems("AllLabelTwo"));
    }

    private Set<String> extractTitles(Iterable<Article> iter) {
        Set<String> result = new HashSet<>();
        iter.forEach(article -> result.add(article.getTitle()));
        return result;
    }

    @Test
    public void getArticleSubscriptions() throws Exception {
        createClientWithTokenOrPassword();
        int articleCount = 0;
        int subCount = 0;
        for (Article t : instance.getArticles()) {
            if (++articleCount > 50) {
                break; // Stop if we're not finding articles with subscriptions
            }
            for (Subscription sub : instance.getArticleSubscriptions(t.getId())) {
                assertThat(sub.getId(), notNullValue());
                assertThat(sub.getUserId(), notNullValue());
                assertThat(sub.getContentId(), notNullValue());
                assertThat(sub.getCreatedAt(), notNullValue());
                assertThat(sub.getUpdatedAt(), notNullValue());
                if (++subCount > 10) {
                    break;
                }
            }
        }
    }

    @Test
    public void getArticleTranslations() throws Exception {
        createClientWithTokenOrPassword();
        int articleCount = 0;
        int translationCount = 0;  // Count total translations checked, not per-article
        for (Article art : instance.getArticles()) {
            assertNotNull(art.getId());
            if (++articleCount > 10) {
                break; // Do not overwhelm the getArticles API
            }
            for (Translation t : instance.getArticleTranslations(art.getId())) {
                assertNotNull(t.getId());
                assertNotNull(t.getTitle());
                // body is not mandatory <https://developer.zendesk.com/rest_api/docs/help_center/translations.html>
                //assertNotNull(t.getBody());
                if (++translationCount > 3) {
                    return;
                }
            }
        }
    }

    @Test
    public void getSectionTranslations() throws Exception {
        createClientWithTokenOrPassword();
        int sectionCount = 0;
        int translationCount = 0;
        for (Section sect : instance.getSections()) {
            assertNotNull(sect.getId());
            if (++sectionCount > 10) {
                break;
            }
            for (Translation t : instance.getSectionTranslations(sect.getId())) {
                assertNotNull(t.getId());
                assertNotNull(t.getTitle());
                // body is not mandatory <https://developer.zendesk.com/rest_api/docs/help_center/translations.html>
                //assertNotNull(t.getBody());
                if (++translationCount > 3) {
                    return;
                }
            }
        }
    }

    @Test
    public void getCategoryTranslations() throws Exception {
        createClientWithTokenOrPassword();
        int categoryCount = 0;
        int translationCount = 0;
        for (Category cat : instance.getCategories()) {
            assertNotNull(cat.getId());
            if (++categoryCount > 10) {
                break;
            }
            for (Translation t: instance.getCategoryTranslations(cat.getId())) {
                assertNotNull(t.getId());
                assertNotNull(t.getTitle());
                // body is not mandatory <https://developer.zendesk.com/rest_api/docs/help_center/translations.html>
                //assertNotNull(t.getBody());
                if (++translationCount > 3) {
                    return;
                }
            }
        }
    }

    @Test
    public void getArticlesIncrementally() throws Exception {
        createClientWithTokenOrPassword();
        final long ONE_WEEK = 7*24*60*60*1000;
        int count = 0;
        try {
            for (Article t : instance.getArticlesIncrementally(new Date(new Date().getTime() - ONE_WEEK))) {
                assertThat(t.getTitle(), notNullValue());
                if (++count > 10) {
                    break;
                }
            }
        } catch (ZendeskResponseException zre) {
            if (zre.getStatusCode() == 502) {
                // Ignore, this is an API limitation
                // A "Bad Gateway" response is returned if HelpCenter was not active at the given time
            } else {
                throw zre;
            }
        }
    }

    @Test
    public void getCategories() throws Exception {
        createClientWithTokenOrPassword();
        int count = 0;
        for (Category cat : instance.getCategories()) {
            assertThat(cat.getName(), notNullValue());
            if (++count > 10) {
                break;
            }
        }
    }

    @Test
    public void getSections() throws Exception {
        createClientWithTokenOrPassword();
        int count = 0;
        for (Section s : instance.getSections()) {
            assertThat(s.getName(), notNullValue());
            assertThat(s.getCategoryId(), notNullValue());
            if (++count > 10) {
                break;
            }
        }
    }

    @Test
    public void getSectionSubscriptions() throws Exception {
        createClientWithTokenOrPassword();
        int sectionCount = 0;
        int count = 0;
        for (Section s : instance.getSections()) {
            if (++sectionCount > 50) {
                break; // Stop if we're not finding sections with subscriptions
            }
            for (Subscription sub : instance.getSectionSubscriptions(s.getId())) {
                assertThat(sub.getId(), notNullValue());
                assertThat(sub.getUserId(), notNullValue());
                assertThat(sub.getContentId(), notNullValue());
                assertThat(sub.getCreatedAt(), notNullValue());
                assertThat(sub.getUpdatedAt(), notNullValue());
                if (++count > 10) {
                    break;
                }
            }
        }
    }

    @Test
    public void getSchedules() throws Exception {
        createClientWithTokenOrPassword();
        int count = 0;
        for (Schedule t : instance.getSchedules()) {
            assertThat(t.getId(), notNullValue());
            assertThat(t.getName(), notNullValue());
            assertThat(t.getCreatedAt(), notNullValue());
            assertThat(t.getUpdatedAt(), notNullValue());
            assertThat(t.getTimeZone(), notNullValue());
            for (Interval i : t.getIntervals()) {
                assertThat(i.getStartTime(), notNullValue());
                assertThat(i.getEndTime(), notNullValue());
            }
            for (Holiday h : instance.getHolidaysForSchedule(t)) {
                assertThat(h.getId(), notNullValue());
                assertThat(h.getName(), notNullValue());
                assertThat(h.getStartDate(), notNullValue());
                assertThat(h.getEndDate(), notNullValue());
            }
            if (++count > 10) {
                break;
            }
        }
    }

    @Test
    public void getCustomAgentRoles() throws Exception {
        createClientWithTokenOrPassword();
        int count = 0;
        for (AgentRole role : instance.getCustomAgentRoles()) {
            assertThat(role.getId(), notNullValue());
            assertThat(role.getName(), notNullValue());
            assertThat(role.getCreatedAt(), notNullValue());
            assertThat(role.getUpdatedAt(), notNullValue());
            assertThat(role.getConfiguration(), notNullValue());
            assertTrue(role.getConfiguration().containsKey("ticket_access"));
            if (++count > 10) {
                break;
            }
        }
    }

    @Test
    @Ignore("Failing and I don't know why - caching issue ?")
    // TODO: Fix this test
    public void createOrUpdateUser() throws Exception {
        createClientWithTokenOrPassword();

        String name = "testCreateOrUpdateUser";
        String externalId = "testCreateOrUpdateUser";

        // Clean up to avoid conflicts
        for (User u: instance.lookupUserByExternalId(externalId)){
            instance.deleteUser(u.getId());
        }

        String phoneAtCreation = "5555551234";
        User user = new User(true, name);
        user.setExternalId(externalId);
        user.setPhone(phoneAtCreation);

        User createResult = instance.createOrUpdateUser(user);
        assertNotNull(createResult);
        assertNotNull(createResult.getId());
        assertEquals(name, createResult.getName());
        assertEquals(externalId, createResult.getExternalId());
        assertEquals(phoneAtCreation, createResult.getPhone());

        String phoneAtUpdate = "5555551235";
        User updateUser = new User(true, name);
        updateUser.setId(createResult.getId());
        updateUser.setExternalId(externalId);
        updateUser.setPhone(phoneAtUpdate);

        User updateResult = instance.createOrUpdateUser(updateUser);
        assertNotNull(updateResult);
        assertEquals(createResult.getId(), updateResult.getId());
        assertEquals(name, updateResult.getName());
        assertEquals(externalId, updateResult.getExternalId());
        assertEquals(phoneAtUpdate, updateResult.getPhone());

        instance.deleteUser(updateResult);
    }

    @Test
    public void createTicketForm() throws Exception {
        createClientWithTokenOrPassword();
        TicketForm form = new TicketForm();
        form.setActive(true);
        final String givenName = "Test ticket form";
        form.setName(givenName);
        form.setDisplayName(givenName);
        form.setRawName(givenName);
        form.setRawDisplayName(givenName);

        final TicketForm createdForm = instance.createTicketForm(form);
        assertNotNull(createdForm);
        assertNotNull(createdForm.getId());
        assertEquals(givenName, createdForm.getName());
        assertEquals(givenName, createdForm.getDisplayName());
        assertEquals(givenName, createdForm.getRawName());
        assertEquals(givenName, createdForm.getRawDisplayName());
    }

    @Test
    public void getDynamicContentItems() throws Exception {
        createClientWithTokenOrPassword();
        int count = 0;
        for (DynamicContentItem i : instance.getDynamicContentItems()) {
            assertThat(i.getName(), notNullValue());
            assertThat(i.getId(), notNullValue());
            if (++count > 10) {
                break;
            }

            DynamicContentItem item = instance.getDynamicContentItem(i.getId());
            assertThat(item, notNullValue());
            assertEquals(i.getId(), item.getId());

            Iterable<DynamicContentItemVariant> variants = instance.getDynamicContentItemVariants(item);
            assertThat(variants, notNullValue());

            int secondaryCount = 0;
            for (DynamicContentItemVariant v : variants) {
                assertThat(v.getId(), notNullValue());
                assertThat(v.getContent(), notNullValue());

                DynamicContentItemVariant fetch = instance.getDynamicContentItemVariant(i.getId(), v.getId());
                assertEquals(v.getId(), fetch.getId());

                if (++secondaryCount > 10) {
                  break;
                }
            }
        }
    }

    @Test
    public void getTicketCommentsShouldBeAscending() throws Exception {
        createClientWithTokenOrPassword();

        Ticket t = new Ticket(
              new Ticket.Requester(config.getProperty("requester.name"), config.getProperty("requester.email")),
              "This is an automated test ticket", new Comment("1"));
        Ticket ticket = null;
        try {
            ticket = instance.createTicket(t);
            instance.createComment(ticket.getId(), new Comment("2"));
            Iterable<Comment> ticketCommentsIt = instance.getTicketComments(ticket.getId());
            List<Comment> comments = new ArrayList<>();
            ticketCommentsIt.forEach(comments::add);

            assertThat(comments.size(), is(2));
            assertThat(comments.get(0).getBody(), containsString("1"));
            assertThat(comments.get(1).getBody(), containsString("2"));
        } finally {
            if (ticket != null) {
                instance.deleteTicket(ticket.getId());
            }
        }
    }

    @Test
    public void getTicketCommentsDescending() throws Exception {
        createClientWithTokenOrPassword();

        Ticket t = new Ticket(
              new Ticket.Requester(config.getProperty("requester.name"), config.getProperty("requester.email")),
              "This is an automated test ticket", new Comment("1"));
        Ticket ticket = null;
        try {
            ticket = instance.createTicket(t);
            instance.createComment(ticket.getId(), new Comment("2"));
            Iterable<Comment> ticketCommentsIt = instance.getTicketComments(ticket.getId(), SortOrder.DESCENDING);
            List<Comment> comments = new ArrayList<>();
            ticketCommentsIt.forEach(comments::add);

            assertThat(comments.size(), is(2));
            assertThat(comments.get(0).getBody(), containsString("2"));
            assertThat(comments.get(1).getBody(), containsString("1"));
        } finally {
            if (ticket != null) {
                instance.deleteTicket(ticket.getId());
            }
        }
    }
}
