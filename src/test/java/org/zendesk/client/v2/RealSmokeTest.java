package org.zendesk.client.v2;

import org.apache.commons.lang3.time.DateUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zendesk.client.v2.model.AgentRole;
import org.zendesk.client.v2.model.Audit;
import org.zendesk.client.v2.model.Brand;
import org.zendesk.client.v2.model.Collaborator;
import org.zendesk.client.v2.model.Comment;
import org.zendesk.client.v2.model.ComplianceDeletionStatus;
import org.zendesk.client.v2.model.DeletedTicket;
import org.zendesk.client.v2.model.Field;
import org.zendesk.client.v2.model.Group;
import org.zendesk.client.v2.model.Identity;
import org.zendesk.client.v2.model.JobResult;
import org.zendesk.client.v2.model.JobStatus;
import org.zendesk.client.v2.model.Organization;
import org.zendesk.client.v2.model.OrganizationMembership;
import org.zendesk.client.v2.model.Priority;
import org.zendesk.client.v2.model.Request;
import org.zendesk.client.v2.model.SortOrder;
import org.zendesk.client.v2.model.Status;
import org.zendesk.client.v2.model.SuspendedTicket;
import org.zendesk.client.v2.model.Ticket;
import org.zendesk.client.v2.model.TicketForm;
import org.zendesk.client.v2.model.TicketImport;
import org.zendesk.client.v2.model.Type;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;

/**
 * @author stephenc
 * @since 04/04/2013 13:57
 */
public class RealSmokeTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealSmokeTest.class);

    // TODO: Find a better way to manage our test environment (this is the PUBLIC_FORM_ID of the cloudbees org)
    private static final long CLOUDBEES_ORGANIZATION_ID = 360507899132L;
    private static final long PUBLIC_FORM_ID = 360000434032L;
    private static final Random RANDOM = new Random();
    private static final String TICKET_COMMENT1 = "Please ignore this ticket";
    private static final String TICKET_COMMENT2 = "Yes ignore this ticket";

    private static Properties config;

    private Zendesk instance;

    /**
     * Global timeout applied on each test to avoid to wait forever if something goes wrong with the remote server
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);

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
    }

    @Test
    public void createAnonymousClient() {
        instance = new Zendesk.Builder(config.getProperty("url"))
                .build();
        assertThat("An instance is created", instance, Matchers.notNullValue());
    }

    @Test
    public void createDeleteTicket() throws Exception {
        createClientWithTokenOrPassword();

        Ticket t = newTestTicket();
        Ticket ticket = instance.createTicket(t);
        assertThat(ticket.getId(), notNullValue());

        try {
            Ticket t2 = instance.getTicket(ticket.getId());
            assertThat(t2, notNullValue());
            assertThat(t2.getId(), is(ticket.getId()));

            List<User> ticketCollaborators = instance.getTicketCollaborators(ticket.getId());
            assertThat("Collaborators", ticketCollaborators.size(), is(2));
            assertThat("First Collaborator", ticketCollaborators.get(0).getEmail(),
                    anyOf(is("alice@example.org"), is("bob@example.org")));
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

    // https://github.com/cloudbees/zendesk-java-client/issues/94
    @Test
    public void createTaskTicketWithDueDate() throws Exception {
        // given
        createClientWithTokenOrPassword();

        Date dueDate = Calendar.getInstance().getTime();
        Ticket t = newTestTicket();
        t.setType(Type.TASK);
        t.setDueAt(dueDate);

        // when
        Ticket ticket = instance.createTicket(t);

        try {
            // then
            assertThat("The ticket now has an ID", ticket.getId(), notNullValue());
            assertThat("The Due Date must be the same (rounded at the second)",
                    DateUtils.truncate(ticket.getDueAt(),Calendar.SECOND) ,
                    is(DateUtils.truncate(dueDate,Calendar.SECOND)));
        } finally {
            instance.deleteTicket(ticket.getId());
        }
    }

    @Test
    public void createPermanentlyDeleteTicket() throws Exception {
        createClientWithTokenOrPassword();
        Ticket t = newTestTicket();
        Ticket ticket = instance.createTicket(t);
        assertThat(ticket.getId(), notNullValue());

        try {
            Ticket t2 = instance.getTicket(ticket.getId());
            assertThat(t2, notNullValue());
            assertThat(t2.getId(), is(ticket.getId()));
        } finally {
            instance.deleteTicket(ticket.getId());
            waitTicketDeleted(ticket.getId());
            waitJobCompletion(instance.permanentlyDeleteTicket(ticket.getId()));
        }
        assertThat(instance.getTicket(ticket.getId()), nullValue());
    }

    @Test
    public void createPermanentlyDeleteTickets() throws Exception {
        createClientWithTokenOrPassword();
        // given
        // We create some tickets
        final List<Ticket> tickets = createTestTicketsInZendesk();
        final Long[] ticketsIds = tickets.stream().map(Ticket::getId).toArray(Long[]::new);
        // when
        // We soft delete them
        instance.deleteTickets(firstElement(ticketsIds), otherElements(ticketsIds));
        waitTicketsDeleted(ticketsIds);
        // We permanently delete them
        JobStatus jobStatus =
                waitJobCompletion(
                        instance.permanentlyDeleteTickets(firstElement(ticketsIds), otherElements(ticketsIds)));
        // then
        assertThat("Job is completed", jobStatus.getStatus(), is(JobStatus.JobStatusEnum.completed));
        jobStatus.getResults().forEach(jobResult -> {
            assertThat("The job result has no account_id entry", jobResult.getAccountId(), nullValue());
            assertThat("The job result has no action entry", jobResult.getAction(), nullValue());
            assertThat("The job result has no details entry", jobResult.getDetails(), nullValue());
            assertThat("The job result has no email entry", jobResult.getEmail(), nullValue());
            assertThat("The job result has no error entry", jobResult.getError(), nullValue());
            assertThat("The job result has no external_id entry", jobResult.getExternalId(), nullValue());
            assertThat("The job result has no id entry", jobResult.getId(), nullValue());
            assertThat("The job result has no index entry", jobResult.getIndex(), nullValue());
            assertThat("The job result has no status entry", jobResult.getStatus(), nullValue());
            assertThat("The job result has a success entry", jobResult.getSuccess(), is(TRUE));
        });
        assumeThat("We cannot find them anymore",
                instance.getTickets(firstElement(ticketsIds), otherElements(ticketsIds)), empty());
    }

    @Test
    public void createSolveTickets() throws Exception {
        createClientWithTokenOrPassword();
        Ticket ticket;
        long firstId = Long.MAX_VALUE;
        do {
            Ticket t = newTestTicket();
            ticket = instance.createTicket(t);
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
    public void createTickets() throws Exception {
        // given
        createClientWithTokenOrPassword();
        final Ticket[] ticketsToCreate = newTestTickets();

        // when

        final JobStatus status = waitJobCompletion(instance.createTickets(ticketsToCreate));

        // then
        final Long[] createdTicketsIds =
                status.getResults().stream().map(JobResult::getId).toArray(Long[]::new);
        try {
            final List<Ticket> createdTickets =
                    instance.getTickets(firstElement(createdTicketsIds), otherElements(createdTicketsIds));

            assertThat("We have the same number of tickets", status.getResults(), hasSize(ticketsToCreate.length));

            status.getResults().forEach(jobResult -> {
                assertThat("The job result has an account_id entry", jobResult.getAccountId(), notNullValue());
                assertThat("The job result has no action entry", jobResult.getAction(), nullValue());
                assertThat("The job result has no details entry", jobResult.getDetails(), nullValue());
                assertThat("The job result has no email entry", jobResult.getEmail(), nullValue());
                assertThat("The job result has no error entry", jobResult.getError(), nullValue());
                assertThat("The job result has no external_id entry", jobResult.getExternalId(), nullValue());
                assertThat("The job result has an id entry", jobResult.getId(), notNullValue());
                assertThat("The job result has an index entry", jobResult.getIndex(), notNullValue());
                assertThat("The job result has no status entry", jobResult.getStatus(), nullValue());
                assertThat("The job result has no success entry", jobResult.getSuccess(), nullValue());
            });

            assertThat("All tickets are created (we verify that all titles are present)",
                    createdTickets
                            .stream()
                            .map(Ticket::getSubject)
                            .collect(Collectors.toList()),
                    containsInAnyOrder(
                            Arrays.stream(ticketsToCreate)
                                    .map(Ticket::getSubject)
                                    .toArray()));
            createdTickets.stream().map(Ticket::getId).forEach(id ->
                    assertThat("A unique ID must be set", id, notNullValue()));
        } finally {
            // cleanup
            instance.deleteTickets(firstElement(createdTicketsIds), otherElements(createdTicketsIds));
        }
    }

    @Test
    public void updateTickets() throws Exception {
        createClientWithTokenOrPassword();

        // given
        // We create some test tickets
        final List<Ticket> tickets = createTestTicketsInZendesk();
        final Long[] ticketsIds = tickets.stream().map(Ticket::getId).toArray(Long[]::new);

        try {
            // when
            // We update them
            tickets.forEach(ticket -> {
                ticket.setPriority(Priority.HIGH);
                ticket.setStatus(Status.OPEN);
            });
            final JobStatus status = waitJobCompletion(instance.updateTickets(tickets));

            // then
            assertThat("Job is completed", status.getStatus(), is(JobStatus.JobStatusEnum.completed));
            assertThat("The good number of tickets were processed", status.getTotal(), is(ticketsIds.length));
            assertThat("We have a result for each ticket", status.getResults(), hasSize(ticketsIds.length));
            assertThat("Each ticket has a result",
                    status.getResults().stream().map(JobResult::getId).collect(Collectors.toList()),
                    containsInAnyOrder(ticketsIds));
            status.getResults().forEach(jobResult -> {
                assertThat("The job result has no account_id entry", jobResult.getAccountId(), nullValue());
                assertThat("The job result has an action entry", jobResult.getAction(), is("update"));
                assertThat("The job result has no details entry", jobResult.getDetails(), nullValue());
                assertThat("The job result has no email entry", jobResult.getEmail(), nullValue());
                assertThat("The job result has no error entry", jobResult.getError(), nullValue());
                assertThat("The job result has no external_id entry", jobResult.getExternalId(), nullValue());
                assertThat("The job result has an id entry", jobResult.getId(), notNullValue());
                assertThat("The job result has no index entry", jobResult.getIndex(), nullValue());
                assertThat("The job result has a status entry", jobResult.getStatus(), is("Updated"));
                assertThat("The job result has a success entry", jobResult.getSuccess(), is(TRUE));
            });
        } finally {
            instance.deleteTickets(firstElement(ticketsIds), otherElements(ticketsIds));
        }
    }

    @Test
    public void importTicket() throws Exception {

        createClientWithTokenOrPassword();

        // given
        final TicketImport ticketImport = newTestTicketImport();

        // when
        Ticket importedTicket = instance.importTicket(ticketImport);

        try {
            // then
            assertThat("The imported ticket has an ID", importedTicket.getId(), notNullValue());
            assertThat("The imported ticket has a subject", importedTicket.getSubject(),
                    CoreMatchers.containsString("[zendesk-java-client] This is a test"));
            assertThat("The imported ticket is closed", importedTicket.getStatus(), is(Status.CLOSED));
            assertThat("The imported ticket has a createdAt value", importedTicket.getCreatedAt(), notNullValue());
            assertThat("The imported ticket has an updatedAt value", importedTicket.getUpdatedAt(), notNullValue());
            assertThat("The imported ticket has tags", importedTicket.getTags(),
                    containsInAnyOrder("zendesk-java-client", "smoke-test"));
        } finally {
            // cleanup
            instance.deleteTicket(importedTicket);
        }
    }

    @Test
    public void importTickets() throws Exception {

        createClientWithTokenOrPassword();

        // given
        final TicketImport[] ticketsToImport = newTestTicketImports();

        // when
        JobStatus status = waitJobCompletion(instance.importTickets(ticketsToImport));
        final Long[] createdTicketsIds =
                status.getResults().stream().map(JobResult::getId).toArray(Long[]::new);

        try {
            final List<Ticket> createdTickets =
                    instance.getTickets(firstElement(createdTicketsIds), otherElements(createdTicketsIds));

            assertThat("We have the same number of tickets", status.getResults(), hasSize(ticketsToImport.length));

            status.getResults().forEach(jobResult -> {
                assertThat("The job result has an account_id entry", jobResult.getAccountId(), notNullValue());
                assertThat("The job result has no action entry", jobResult.getAction(), nullValue());
                assertThat("The job result has no details entry", jobResult.getDetails(), nullValue());
                assertThat("The job result has no email entry", jobResult.getEmail(), nullValue());
                assertThat("The job result has no error entry", jobResult.getError(), nullValue());
                assertThat("The job result has no external_id entry", jobResult.getExternalId(), nullValue());
                assertThat("The job result has an id entry", jobResult.getId(), notNullValue());
                assertThat("The job result has an index entry", jobResult.getIndex(), notNullValue());
                assertThat("The job result has no status entry", jobResult.getStatus(), nullValue());
                assertThat("The job result has no success entry", jobResult.getSuccess(), nullValue());
            });

            assertThat("All tickets are created (we verify that all titles are present)",
                    createdTickets
                            .stream()
                            .map(Ticket::getSubject)
                            .collect(Collectors.toList()),
                    containsInAnyOrder(
                            Arrays.stream(ticketsToImport)
                                    .map(Ticket::getSubject)
                                    .toArray()));
            createdTickets.forEach(importedTicket -> {
                assertThat("The imported ticket has an ID", importedTicket.getId(), notNullValue());
                assertThat("The imported ticket has a subject", importedTicket.getSubject(),
                        CoreMatchers.containsString("[zendesk-java-client] This is a test"));
                assertThat("The imported ticket is closed", importedTicket.getStatus(), is(Status.CLOSED));
                assertThat("The imported ticket has a createdAt value", importedTicket.getCreatedAt(), notNullValue());
                assertThat("The imported ticket has an updatedAt value", importedTicket.getUpdatedAt(), notNullValue());
                assertThat("The imported ticket has tags", importedTicket.getTags(),
                        containsInAnyOrder("zendesk-java-client", "smoke-test"));

            });

            // then
        } finally {
            // cleanup
            instance.deleteTickets(firstElement(createdTicketsIds), otherElements(createdTicketsIds));
        }
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
    public void createUsers() throws Exception {
        // given
        createClientWithTokenOrPassword();
        final User[] usersToCreate = newTestUsers();

        // when
        final JobStatus status = waitJobCompletion(instance.createUsers(usersToCreate));

        // then
        final Long[] createdUsersIds =
                status.getResults().stream().map(JobResult::getId).toArray(Long[]::new);
        try {
            final List<User> createdUsers =
                    Arrays.stream(createdUsersIds).map(instance::getUser).collect(Collectors.toList());

            assertThat("We have the same number of users", status.getResults(), hasSize(usersToCreate.length));

            status.getResults().forEach(jobResult -> {
                assertThat("The job result has no account_id entry", jobResult.getAccountId(), nullValue());
                assertThat("The job result has no action entry", jobResult.getAction(), nullValue());
                assertThat("The job result has no details entry", jobResult.getDetails(), nullValue());
                assertThat("The job result has an email entry", jobResult.getEmail(), notNullValue());
                assertThat("The job result has no error entry", jobResult.getError(), nullValue());
                assertThat("The job result has an external_id entry", jobResult.getExternalId(), notNullValue());
                assertThat("The job result has an id entry", jobResult.getId(), notNullValue());
                assertThat("The job result has no index entry", jobResult.getIndex(), nullValue());
                assertThat("The job result has a status entry", jobResult.getStatus(), is("Created"));
                assertThat("The job result has no success entry", jobResult.getSuccess(), nullValue());
            });

            assertThat("All users are created (we verify that all names are present)",
                    createdUsers
                            .stream()
                            .map(User::getName)
                            .collect(Collectors.toList()),
                    containsInAnyOrder(
                            Arrays.stream(usersToCreate)
                                    .map(User::getName)
                                    .toArray()));
            createdUsers.stream().map(User::getId).forEach(id ->
                    assertThat("A unique ID must be set", id, notNullValue()));
        } finally {
            // cleanup
            Arrays.stream(createdUsersIds).forEach(instance::deleteUser);
        }
    }

    @Test
    public void updateUsers() throws Exception {
        createClientWithTokenOrPassword();

        // given
        // We create some test users
        final List<User> users = createTestUsersInZendesk();
        final Long[] usersIds = users.stream().map(User::getId).toArray(Long[]::new);

        try {
            // when
            // We update them
            users.forEach(user -> user.setNotes("This user was updated"));
            final JobStatus status = waitJobCompletion(instance.updateUsers(users));

            // then
            assertThat("Job is completed", status.getStatus(), is(JobStatus.JobStatusEnum.completed));
            assertThat("The good number of users were processed", status.getTotal(), is(usersIds.length));
            assertThat("We have a result for each user", status.getResults(), hasSize(usersIds.length));
            assertThat("Each user has a result",
                    status.getResults().stream().map(JobResult::getId).collect(Collectors.toList()),
                    containsInAnyOrder(usersIds));
            status.getResults().forEach(jobResult -> {
                assertThat("The job result has no account_id entry", jobResult.getAccountId(), nullValue());
                assertThat("The job result has an action entry", jobResult.getAction(), is("update"));
                assertThat("The job result has no details entry", jobResult.getDetails(), nullValue());
                assertThat("The job result has no email entry", jobResult.getEmail(), nullValue());
                assertThat("The job result has no error entry", jobResult.getError(), nullValue());
                assertThat("The job result has no external_id entry", jobResult.getExternalId(), nullValue());
                assertThat("The job result has an id entry", jobResult.getId(), notNullValue());
                assertThat("The job result has no index entry", jobResult.getIndex(), nullValue());
                assertThat("The job result has a status entry", jobResult.getStatus(), is("Updated"));
                assertThat("The job result has a success entry", jobResult.getSuccess(), is(TRUE));
            });
        } finally {
            // cleanup
            Arrays.stream(usersIds).forEach(instance::deleteUser);
        }
    }

    @Test
    public void createOrUpdateUsers() throws Exception {
        createClientWithTokenOrPassword();

        // given
        // We create some test users
        final List<User> existingUsers = createTestUsersInZendesk();
        final Long[] existingUsersIds = existingUsers.stream().map(User::getId).toArray(Long[]::new);
        // And we add new users
        final List<User> newUsers = Arrays.asList(newTestUsers());
        final List<User> allUsers = new ArrayList<>(existingUsers);
        allUsers.addAll(newUsers);
        Long[] newUsersIds = null;
        try {
            // when
            // We update them
            allUsers.forEach(user -> user.setNotes("This user was updated"));
            final JobStatus status =
                    waitJobCompletion(instance.createOrUpdateUsers(allUsers));

            // then
            assertThat("Job is completed", status.getStatus(), is(JobStatus.JobStatusEnum.completed));
            assertThat("The good number of users were processed", status.getTotal(), is(allUsers.size()));
            assertThat("We have a result for each user", status.getResults(), hasSize(allUsers.size()));
            assertThat("Each existing user has a result",
                    status.getResults()
                            .stream()
                            .map(JobResult::getId)
                            .collect(Collectors.toList()),
                    hasItems(existingUsersIds));
            status.getResults().forEach(jobResult -> {
                assertThat("The job result has no account_id entry", jobResult.getAccountId(), nullValue());
                assertThat("The job result has an action entry", jobResult.getAction(), nullValue());
                assertThat("The job result has no details entry", jobResult.getDetails(), nullValue());
                assertThat("The job result has an email entry", jobResult.getEmail(), notNullValue());
                assertThat("The job result has no error entry", jobResult.getError(), nullValue());
                assertThat("The job result has an external_id entry", jobResult.getExternalId(), notNullValue());
                assertThat("The job result has an id entry", jobResult.getId(), notNullValue());
                assertThat("The job result has no index entry", jobResult.getIndex(), nullValue());
                assertThat("The job result has a status entry", jobResult.getStatus(), notNullValue());
                assertThat("The job result has a success entry", jobResult.getSuccess(), nullValue());
            });
            assertThat("Existing users are updated",
                    status.getResults()
                            .stream()
                            .filter(jobResult -> Objects.equals(jobResult.getStatus(), "Updated"))
                            .map(JobResult::getId)
                            .collect(Collectors.toList()),
                    containsInAnyOrder(existingUsersIds)
            );
            assertThat("New users are created",
                    status.getResults()
                            .stream()
                            .filter(jobResult -> Objects.equals(jobResult.getStatus(), "Created"))
                            .map(JobResult::getExternalId)
                            .collect(Collectors.toList()),
                    containsInAnyOrder(newUsers.stream().map(User::getExternalId).toArray())
            );
            newUsersIds = status.getResults()
                    .stream()
                    .filter(jobResult -> Objects.equals(jobResult.getStatus(), "Created"))
                    .map(JobResult::getId)
                    .toArray(Long[]::new);
        } finally {
            // cleanup
            Arrays.stream(existingUsersIds).forEach(instance::deleteUser);
            if (newUsersIds != null) {
                Arrays.stream(newUsersIds).forEach(instance::deleteUser);
            }
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

    @Test
    public void createOrganizations() throws Exception {
        // given
        createClientWithTokenOrPassword();
        final Organization[] orgsToCreate = newTestOrganizations();

        // when
        final JobStatus status = waitJobCompletion(instance.createOrganizations(orgsToCreate));

        // then
        final Long[] createdOrgsIds =
                status.getResults().stream().map(JobResult::getId).toArray(Long[]::new);
        try {
            final List<Organization> createdOrgs =
                    Arrays.stream(createdOrgsIds).map(instance::getOrganization).collect(Collectors.toList());

            assertThat("We have the same number of organizations", status.getResults(), hasSize(orgsToCreate.length));

            status.getResults().forEach(jobResult -> {
                assertThat("The job result has no account_id entry", jobResult.getAccountId(), nullValue());
                assertThat("The job result has no action entry", jobResult.getAction(), nullValue());
                assertThat("The job result has no details entry", jobResult.getDetails(), nullValue());
                assertThat("The job result has no email entry", jobResult.getEmail(), nullValue());
                assertThat("The job result has no error entry", jobResult.getError(), nullValue());
                assertThat("The job result has no external_id entry", jobResult.getExternalId(), nullValue());
                assertThat("The job result has an id entry", jobResult.getId(), notNullValue());
                assertThat("The job result has no index entry", jobResult.getIndex(), nullValue());
                assertThat("The job result has a status entry", jobResult.getStatus(), is("Created"));
                assertThat("The job result has no success entry", jobResult.getSuccess(), nullValue());
            });

            assertThat("All organizations are created (we verify that all names are present)",
                    createdOrgs
                            .stream()
                            .map(Organization::getName)
                            .collect(Collectors.toList()),
                    containsInAnyOrder(
                            Arrays.stream(orgsToCreate)
                                    .map(Organization::getName)
                                    .toArray()));
            createdOrgs.stream().map(Organization::getId).forEach(id ->
                    assertThat("A unique ID must be set", id, notNullValue()));
        } finally {
            // cleanup
            Arrays.stream(createdOrgsIds).forEach(instance::deleteOrganization);
        }
    }

    @Test
    public void updateOrganizations() throws Exception {
        createClientWithTokenOrPassword();

        // given
        // We create some test organizations
        final List<Organization> organizations = createTestOrganizationsInZendesk();
        final Long[] orgsIds = organizations.stream().map(Organization::getId).toArray(Long[]::new);

        try {
            // when
            // We update them
            organizations.forEach(organization -> organization.setNotes("This organization was updated"));
            final JobStatus status =
                    waitJobCompletion(instance.updateOrganizations(organizations));

            // then
            assertThat("Job is completed", status.getStatus(), is(JobStatus.JobStatusEnum.completed));
            assertThat("The good number of organizations were processed", status.getTotal(), is(orgsIds.length));
            assertThat("We have a result for each organization", status.getResults(), hasSize(orgsIds.length));
            assertThat("Each organization has a result",
                    status.getResults().stream().map(JobResult::getId).collect(Collectors.toList()),
                    containsInAnyOrder(orgsIds));
            status.getResults().forEach(jobResult -> {
                assertThat("The job result has no account_id entry", jobResult.getAccountId(), nullValue());
                assertThat("The job result has an action entry", jobResult.getAction(), is("update"));
                assertThat("The job result has no details entry", jobResult.getDetails(), nullValue());
                assertThat("The job result has no email entry", jobResult.getEmail(), nullValue());
                assertThat("The job result has no error entry", jobResult.getError(), nullValue());
                assertThat("The job result has no external_id entry", jobResult.getExternalId(), nullValue());
                assertThat("The job result has an id entry", jobResult.getId(), notNullValue());
                assertThat("The job result has no index entry", jobResult.getIndex(), nullValue());
                assertThat("The job result has a status entry", jobResult.getStatus(), is("Updated"));
                assertThat("The job result has a success entry", jobResult.getSuccess(), is(TRUE));
            });
        } finally {
            // cleanup
            Arrays.stream(orgsIds).forEach(instance::deleteOrganization);
        }
    }

    @Test
    public void createOrganizationMemberships() throws Exception {
        createClientWithTokenOrPassword();

        // given
        // We create some test organizations
        final List<Organization> organizations = createTestOrganizationsInZendesk();
        final Long[] orgsIds = organizations.stream().map(Organization::getId).toArray(Long[]::new);
        // We create some test users
        final List<User> users = createTestUsersInZendesk();
        final Long[] usersIds = users.stream().map(User::getId).toArray(Long[]::new);

        final List<OrganizationMembership> organizationMemberships = new ArrayList<>();
        // We add all users by default in the first org
        users.forEach(user -> {
            OrganizationMembership defaultOrganizationMembership = new OrganizationMembership();
            defaultOrganizationMembership.setOrganizationId(firstElement(orgsIds));
            defaultOrganizationMembership.setUserId(user.getId());
            defaultOrganizationMembership.setDefault(TRUE);
            organizationMemberships.add(defaultOrganizationMembership);
        });
        // We add them in others orgs too
        Arrays.stream(otherElements(orgsIds)).forEach(orgId -> {
                    users.forEach(user -> {
                        OrganizationMembership organizationMembership = new OrganizationMembership();
                        organizationMembership.setOrganizationId(orgId);
                        organizationMembership.setUserId(user.getId());
                        organizationMembership.setDefault(FALSE);
                        organizationMemberships.add(organizationMembership);
                    });
                }
        );

        // when
        // We create them
        final JobStatus status =
                waitJobCompletion(instance.createOrganizationMemberships(organizationMemberships));

        // then
        final Long[] orgMembershipsIds =
                status.getResults().stream().map(JobResult::getId).toArray(Long[]::new);

        try {

            assertThat("We have the same number of memberships", status.getResults(),
                    hasSize(organizationMemberships.size()));

            status.getResults().forEach(jobResult -> {
                assertThat("The job result has no account_id entry", jobResult.getAccountId(), nullValue());
                assertThat("The job result has no action entry", jobResult.getAction(), nullValue());
                assertThat("The job result has no details entry", jobResult.getDetails(), nullValue());
                assertThat("The job result has no email entry", jobResult.getEmail(), nullValue());
                assertThat("The job result has no error entry", jobResult.getError(), nullValue());
                assertThat("The job result has no external_id entry", jobResult.getExternalId(), nullValue());
                assertThat("The job result has an id entry", jobResult.getId(), notNullValue());
                assertThat("The job result has no index entry", jobResult.getIndex(), notNullValue());
                assertThat("The job result has a status entry", jobResult.getStatus(), nullValue());
                assertThat("The job result has no success entry", jobResult.getSuccess(), nullValue());
            });

        } finally {
            // cleanup
            Arrays.stream(orgsIds).forEach(instance::deleteOrganization);
            Arrays.stream(usersIds).forEach(instance::deleteUser);
            instance.deleteOrganizationMemberships(firstElement(orgMembershipsIds), otherElements(orgMembershipsIds));
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

        Ticket t = newTestTicket();
        Ticket ticket = null;
        try {
            ticket = instance.createTicket(t);
            instance.createComment(ticket.getId(), new Comment(TICKET_COMMENT2));
            Iterable<Comment> ticketCommentsIt = instance.getTicketComments(ticket.getId());
            List<Comment> comments = new ArrayList<>();
            ticketCommentsIt.forEach(comments::add);

            assertThat(comments.size(), is(2));
            assertThat(comments.get(0).getBody(), containsString(TICKET_COMMENT1));
            assertThat(comments.get(1).getBody(), containsString(TICKET_COMMENT2));
        } finally {
            if (ticket != null) {
                instance.deleteTicket(ticket.getId());
            }
        }
    }

    @Test
    public void getTicketCommentsDescending() throws Exception {
        createClientWithTokenOrPassword();

        Ticket t = newTestTicket();
        Ticket ticket = null;
        try {
            ticket = instance.createTicket(t);
            instance.createComment(ticket.getId(), new Comment(TICKET_COMMENT2));
            Iterable<Comment> ticketCommentsIt = instance.getTicketComments(ticket.getId(), SortOrder.DESCENDING);
            List<Comment> comments = new ArrayList<>();
            ticketCommentsIt.forEach(comments::add);

            assertThat(comments.size(), is(2));
            assertThat(comments.get(0).getBody(), containsString(TICKET_COMMENT2));
            assertThat(comments.get(1).getBody(), containsString(TICKET_COMMENT1));
        } finally {
            if (ticket != null) {
                instance.deleteTicket(ticket.getId());
            }
        }
    }

    // UTILITIES

    /**
     * Creates in zendesk few organizations (2 entries min, 5 entries max) and verify their existence.
     *
     * @return The new organizations
     */
    private List<Organization> createTestOrganizationsInZendesk() {
        final Organization[] orgsToCreate = newTestOrganizations();
        final Long[] createdOrgsIds = waitJobCompletion(instance.createOrganizations(orgsToCreate))
                .getResults()
                .stream()
                .map(JobResult::getId)
                .toArray(Long[]::new);
        assumeThat("All created organizations should have an ID", createdOrgsIds.length, is(orgsToCreate.length));
        final List<Organization> createdOrganizations = Arrays.stream(createdOrgsIds)
                .map(instance::getOrganization)
                .collect(Collectors.toList());
        assumeThat("All created organizations are found in zendesk",
                createdOrganizations.stream().map(Organization::getId).collect(Collectors.toList()),
                containsInAnyOrder(createdOrgsIds));
        LOGGER.info("Test organizations: {}", Arrays.toString(createdOrgsIds));
        return createdOrganizations;
    }

    /**
     * Creates several new organizations (2 min, 5 max)
     */
    private Organization[] newTestOrganizations() {
        final ArrayList<Organization> organizations = new ArrayList<>();
        for (int i = 0; i < 2 + RANDOM.nextInt(3); i++) {
            organizations.add(newTestOrganization());
        }
        return organizations.toArray(new Organization[0]);
    }

    /**
     * Creates a new organization
     */
    private Organization newTestOrganization() {
        final Organization organization = new Organization();
        final String id = UUID.randomUUID().toString();
        organization.setExternalId("org-" + id);
        organization.setName("[zendesk-java-client] Organization " + id);
        organization.setDetails("This organization is created by zendesk-java-client Integration Tests");
        organization.setTags(Arrays.asList("zendesk-java-client", "smoke-test"));
        return organization;
    }

    /**
     * Creates in zendesk few users (2 entries min, 5 entries max) and verify their existence.
     *
     * @return The new users
     */
    private List<User> createTestUsersInZendesk() {
        final User[] usersToCreate = newTestUsers();
        final Long[] createdUsersIds = waitJobCompletion(instance.createUsers(usersToCreate))
                .getResults()
                .stream()
                .map(JobResult::getId)
                .toArray(Long[]::new);
        assumeThat("All created users should have an ID", createdUsersIds.length, is(usersToCreate.length));
        final List<User> createdUsers = Arrays.stream(createdUsersIds)
                .map(instance::getUser)
                .collect(Collectors.toList());
        assumeThat("All created users are found in zendesk",
                createdUsers.stream().map(User::getId).collect(Collectors.toList()),
                containsInAnyOrder(createdUsersIds));
        LOGGER.info("Test users: {}", Arrays.toString(createdUsersIds));
        return createdUsers;
    }

    /**
     * Creates several new users (2 min, 5 max)
     */
    private User[] newTestUsers() {
        final ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < 2 + RANDOM.nextInt(3); i++) {
            users.add(newTestUser());
        }
        return users.toArray(new User[0]);
    }

    /**
     * Creates a new user
     */
    private User newTestUser() {
        final User user = new User();
        final String id = UUID.randomUUID().toString();
        user.setExternalId("user-" + id);
        user.setName("[zendesk-java-client] User " + id);
        user.setDetails("This user is created by zendesk-java-client Integration Tests");
        user.setTags(Arrays.asList("zendesk-java-client", "smoke-test"));
        user.setEmail(id + "@test.com");
        return user;
    }

    /**
     * Creates in zendesk few tickets (2 entries min, 5 entries max) and verify their existence.
     *
     * @return The new tickets
     */
    private List<Ticket> createTestTicketsInZendesk() {
        final Ticket[] ticketsToCreate = newTestTickets();
        final Long[] createdTicketsIds = waitJobCompletion(instance.createTickets(ticketsToCreate))
                .getResults()
                .stream()
                .map(JobResult::getId)
                .toArray(Long[]::new);
        assumeThat("All created tickets should have an ID", createdTicketsIds.length, is(ticketsToCreate.length));
        final List<Ticket> createdTickets =
                instance.getTickets(firstElement(createdTicketsIds), otherElements(createdTicketsIds));
        assumeThat("All created tickets are found in zendesk",
                createdTickets.stream().map(Ticket::getId).collect(Collectors.toList()),
                containsInAnyOrder(createdTicketsIds));
        LOGGER.info("Test tickets: {}", Arrays.toString(createdTicketsIds));
        return createdTickets;
    }

    /**
     * Creates several new tickets (2 min, 5 max)
     */
    private Ticket[] newTestTickets() {
        final ArrayList<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < 2 + RANDOM.nextInt(3); i++) {
            tickets.add(newTestTicket());
        }
        return tickets.toArray(new Ticket[0]);
    }

    /**
     * Creates a new ticket
     */
    private Ticket newTestTicket() {
        assumeThat("Must have a requester email", config.getProperty("requester.email"), notNullValue());
        assumeThat("Must have a requester name", config.getProperty("requester.name"), notNullValue());
        final Ticket ticket = new Ticket(
                new Ticket.Requester(config.getProperty("requester.name"), config.getProperty("requester.email")),
                "[zendesk-java-client] This is a test " + UUID.randomUUID().toString(),
                new Comment(TICKET_COMMENT1));
        ticket.setCollaborators(Arrays.asList(new Collaborator("Bob Example", "bob@example.org"),
                new Collaborator("Alice Example", "alice@example.org")));
        ticket.setTags(Arrays.asList("zendesk-java-client", "smoke-test"));
        return ticket;
    }

    /**
     * Creates several new ticketImport (2 min, 5 max)
     */
    private TicketImport[] newTestTicketImports() {
        final ArrayList<TicketImport> ticketImports = new ArrayList<>();
        for (int i = 0; i < 2 + RANDOM.nextInt(3); i++) {
            ticketImports.add(newTestTicketImport());
        }
        return ticketImports.toArray(new TicketImport[0]);
    }

    /**
     * Creates a new ticketImport
     */
    private TicketImport newTestTicketImport() {
        assumeThat("Must have a requester email", config.getProperty("requester.email"), notNullValue());
        assumeThat("Must have a requester name", config.getProperty("requester.name"), notNullValue());
        Date now = Calendar.getInstance().getTime();
        final TicketImport ticketImport = new TicketImport(
                new Ticket.Requester(config.getProperty("requester.name"), config.getProperty("requester.email")),
                "[zendesk-java-client] This is a test " + UUID.randomUUID().toString(),
                Collections.singletonList(new Comment(TICKET_COMMENT1)));
        ticketImport.setCollaborators(Arrays.asList(new Collaborator("Bob Example", "bob@example.org"),
                new Collaborator("Alice Example", "alice@example.org")));
        ticketImport.setTags(Arrays.asList("zendesk-java-client", "smoke-test"));
        ticketImport.setStatus(Status.CLOSED);
        ticketImport.setCreatedAt(now);
        ticketImport.setUpdatedAt(now);
        ticketImport.setSolvedAt(now);
        return ticketImport;
    }

    /**
     * Wait until a given JobStatus is marked as completed
     *
     * @param result The Job result to verify
     * @return The completed job result
     */
    private JobStatus waitJobCompletion(final JobStatus result) {
        // Let's validate the first result
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getStatus());

        // Let's wait for its completion (5 seconds max)
        await().atMost(10, TimeUnit.SECONDS).until(() ->
                instance.getJobStatus(result).getStatus() == JobStatus.JobStatusEnum.completed);

        // Let's validate and return the completed result
        final JobStatus completedResult = instance.getJobStatus(result);
        assertNotNull(completedResult);
        assertNotNull(completedResult.getId());
        assertNotNull(completedResult.getStatus());
        LOGGER.info("Completed Job Result: {}", completedResult);
        return completedResult;
    }

    /**
     * Wait to have a ticket listed in the deleted tickets end-point
     *
     * @param ticketId The identifier of the ticket to delete
     */
    private void waitTicketDeleted(long ticketId) {
        // Wait for the confirmation
        await().atMost(10, TimeUnit.SECONDS).until(() -> StreamSupport
                .stream(instance.getDeletedTickets("id", SortOrder.DESCENDING).spliterator(), false)
                .map(DeletedTicket::getId)
                .collect(Collectors.toList())
                .contains(ticketId));
    }

    /**
     * Wait to have the tickets listed in the deleted tickets end-point
     *
     * @param ticketsIds The identifier of tickets to delete
     */
    private void waitTicketsDeleted(Long[] ticketsIds) {
        // Wait for the confirmation
        await().atMost(10, TimeUnit.SECONDS).until(() -> StreamSupport
                .stream(instance.getDeletedTickets("id", SortOrder.DESCENDING).spliterator(), false)
                .map(DeletedTicket::getId)
                .collect(Collectors.toList())
                .containsAll(Arrays.asList(ticketsIds)));
    }

    private long firstElement(Long[] array) {
        return array[0];
    }

    private long[] otherElements(Long[] array) {
        return Arrays.stream(Arrays.copyOfRange(array, 1, array.length))
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .toArray();
    }

}
