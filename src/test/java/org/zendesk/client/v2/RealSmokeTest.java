package org.zendesk.client.v2;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;
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
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.time.DateUtils;
import org.awaitility.Awaitility;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zendesk.client.v2.model.Action;
import org.zendesk.client.v2.model.Activity;
import org.zendesk.client.v2.model.AgentRole;
import org.zendesk.client.v2.model.Audit;
import org.zendesk.client.v2.model.Automation;
import org.zendesk.client.v2.model.Brand;
import org.zendesk.client.v2.model.Collaborator;
import org.zendesk.client.v2.model.Comment;
import org.zendesk.client.v2.model.CommentType;
import org.zendesk.client.v2.model.ComplianceDeletionStatus;
import org.zendesk.client.v2.model.DeletedTicket;
import org.zendesk.client.v2.model.Field;
import org.zendesk.client.v2.model.Group;
import org.zendesk.client.v2.model.GroupMembership;
import org.zendesk.client.v2.model.Identity;
import org.zendesk.client.v2.model.JobResult;
import org.zendesk.client.v2.model.JobStatus;
import org.zendesk.client.v2.model.Macro;
import org.zendesk.client.v2.model.Metric;
import org.zendesk.client.v2.model.Organization;
import org.zendesk.client.v2.model.OrganizationMembership;
import org.zendesk.client.v2.model.Priority;
import org.zendesk.client.v2.model.RecipientAddress;
import org.zendesk.client.v2.model.Request;
import org.zendesk.client.v2.model.Role;
import org.zendesk.client.v2.model.SatisfactionRating;
import org.zendesk.client.v2.model.Skip;
import org.zendesk.client.v2.model.SortOrder;
import org.zendesk.client.v2.model.Status;
import org.zendesk.client.v2.model.SuspendedTicket;
import org.zendesk.client.v2.model.Tag;
import org.zendesk.client.v2.model.Ticket;
import org.zendesk.client.v2.model.TicketCount;
import org.zendesk.client.v2.model.TicketForm;
import org.zendesk.client.v2.model.TicketImport;
import org.zendesk.client.v2.model.Trigger;
import org.zendesk.client.v2.model.Type;
import org.zendesk.client.v2.model.User;
import org.zendesk.client.v2.model.View;
import org.zendesk.client.v2.model.dynamic.DynamicContentItem;
import org.zendesk.client.v2.model.dynamic.DynamicContentItemVariant;
import org.zendesk.client.v2.model.events.Event;
import org.zendesk.client.v2.model.hc.Article;
import org.zendesk.client.v2.model.hc.Category;
import org.zendesk.client.v2.model.hc.Locales;
import org.zendesk.client.v2.model.hc.PermissionGroup;
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

  // TODO: Find a better way to manage our test environment (this is the PUBLIC_FORM_ID of the
  // cloudbees org)
  private static final long CLOUDBEES_ORGANIZATION_ID = 360507899132L;
  private static final long USER_ID = 381626101132L; // Pierre B
  private static final long PUBLIC_FORM_ID = 360000434032L;
  private static final long UNRESOLVED_TICKETS_VIEW_ID = 360094600471L;
  private static final Random RANDOM = new Random();
  private static final String TICKET_COMMENT1 = "Please ignore this ticket";
  private static final String TICKET_COMMENT2 = "Yes ignore this ticket";

  private static Properties config;

  private Zendesk instance;

  /**
   * Global timeout applied on each test to avoid to wait forever if something goes wrong with the
   * remote server
   */
  @Rule public Timeout globalTimeout = Timeout.seconds(60);

  @BeforeClass
  public static void loadConfig() {
    config = ZendeskConfig.load();
    assumeThat("We have a configuration", config, notNullValue());
    assumeThat("Configuration has an url", config.getProperty("url"), not(isEmptyOrNullString()));
    Awaitility.setDefaultTimeout(2, TimeUnit.MINUTES);
    Awaitility.setDefaultPollDelay(10, TimeUnit.SECONDS);
    Awaitility.setDefaultPollInterval(20, TimeUnit.SECONDS);
  }

  public void assumeHaveToken() {
    assumeThat("We have a username", config.getProperty("username"), not(isEmptyOrNullString()));
    assumeThat("We have a token", config.getProperty("token"), not(isEmptyOrNullString()));
  }

  public void assumeHavePassword() {
    assumeThat("We have a username", config.getProperty("username"), not(isEmptyOrNullString()));
    assumeThat("We have a password", config.getProperty("password"), not(isEmptyOrNullString()));
  }

  public void assumeHaveTokenOrPassword() {
    assumeThat("We have a username", config.getProperty("username"), not(isEmptyOrNullString()));
    assumeThat(
        "We have a token or password",
        config.getProperty("token") != null || config.getProperty("password") != null,
        is(true));
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
    instance =
        new Zendesk.Builder(config.getProperty("url"))
            .setUsername(config.getProperty("username"))
            .setToken(config.getProperty("token"))
            .build();
  }

  @Test
  public void createClientWithTokenOrPassword() throws Exception {
    createClientWithTokenOrPassword(null);
  }

  public void createClientWithTokenOrPassword(Integer cbpPageSize) throws Exception {
    assumeHaveTokenOrPassword();
    final Zendesk.Builder builder =
        new Zendesk.Builder(config.getProperty("url")).setUsername(config.getProperty("username"));
    if (config.getProperty("token") != null) {
      builder.setToken(config.getProperty("token"));
    } else if (config.getProperty("password") != null) {
      builder.setPassword(config.getProperty("password"));
    }
    if (cbpPageSize != null) {
      builder.setCbpPageSize(cbpPageSize);
    }
    instance = builder.build();
  }

  @Test
  public void getBrands() throws Exception {
    createClientWithTokenOrPassword();
    List<Brand> brands = instance.getBrands();
    assertTrue(brands.iterator().hasNext());
    for (Brand brand : brands) {
      assertThat(brand, notNullValue());
    }
  }

  @Test
  public void getBrandsCbp() throws Exception {
    createClientWithTokenOrPassword(1);
    int count = 0;
    for (Brand brand : instance.getBrandsCbp()) {
      assertThat(brand, notNullValue());
      if (count++ > 10) {
        break;
      }
    }
  }

  @Test
  public void getSkips() throws Exception {
    createClientWithTokenOrPassword(2);
    int count = 0;
    Long ticketId = null;
    for (Skip skip : instance.getSkips()) {
      assertThat(skip.getTicket(), notNullValue());
      ticketId = skip.getTicket().getId();
      if (count++ > 10) {
        break;
      }
    }
    count = 0;
    Map<String, List<String>> parameters = new HashMap<>();
    if (ticketId != null) {
      parameters.put("ticket_id", singletonList(ticketId.toString()));
      for (Skip skip : instance.getSkips(parameters)) {
        assertEquals(skip.getTicket().getId(), ticketId);
        if (count++ > 10) {
          break;
        }
      }
    }
  }

  @Test
  public void getTicketsCount() throws Exception {
    createClientWithTokenOrPassword();
    TicketCount ticketCount = instance.getTicketsCount();
    assertThat(ticketCount, notNullValue());
    assertThat(ticketCount.getValue(), greaterThan(0L));
    assertThat(ticketCount.getRefreshedAt(), notNullValue());
  }

  @Test
  public void getTicketsCountForOrganization() throws Exception {
    createClientWithTokenOrPassword();
    TicketCount ticketCount = instance.getTicketsCountForOrganization(CLOUDBEES_ORGANIZATION_ID);
    assertThat(ticketCount, notNullValue());
    assertThat(ticketCount.getValue(), greaterThan(0L));
    assertThat(ticketCount.getRefreshedAt(), notNullValue());
  }

  @Test
  public void getCcdTicketsCountForUser() throws Exception {
    createClientWithTokenOrPassword();
    TicketCount ticketCount = instance.getCcdTicketsCountForUser(USER_ID);
    assertThat(ticketCount, notNullValue());
    assertThat(ticketCount.getValue(), greaterThan(0L));
    assertThat(ticketCount.getRefreshedAt(), notNullValue());
  }

  @Test
  public void getAssignedTicketsCountForUser() throws Exception {
    createClientWithTokenOrPassword();
    TicketCount ticketCount = instance.getAssignedTicketsCountForUser(USER_ID);
    assertThat(ticketCount, notNullValue());
    assertThat(ticketCount.getValue(), greaterThan(0L));
    assertThat(ticketCount.getRefreshedAt(), notNullValue());
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
    for (TicketForm ticketForm : ticketForms) {
      assertThat(ticketForm, notNullValue());
    }
  }

  @Test
  public void getActivities() throws Exception {
    createClientWithTokenOrPassword(2);
    int count = 0;
    for (Activity activity : instance.getActivities()) {
      assertThat(activity.getId(), notNullValue());
      if (count++ > 10) {
        break;
      }
    }
    count = 0;
    Map<String, List<String>> parameters = new HashMap<>();
    parameters.put("since", singletonList("2023-07-17T20:56:06Z"));
    for (Activity activity : instance.getActivities(parameters)) {
      assertThat(activity.getId(), notNullValue());
      if (count++ > 10) {
        break;
      }
    }
  }

  @Test
  public void getRecipientAddresses() throws Exception {
    createClientWithTokenOrPassword(2);
    Iterable<RecipientAddress> addresses = instance.getRecipientAddresses();
    assertTrue(addresses.iterator().hasNext());
    int count = 0;
    for (RecipientAddress address : addresses) {
      assertThat(address, notNullValue());
      assertThat(address.getId(), notNullValue());
      assertThat(address.getName(), notNullValue());
      assertThat(address.getEmail(), notNullValue());
      if (count++ > 10) {
        break;
      }
    }
  }

  @Test
  public void getTicketFieldsOnForm() throws Exception {
    createClientWithTokenOrPassword();
    TicketForm ticketForm = instance.getTicketForm(PUBLIC_FORM_ID);
    for (Long id : ticketForm.getTicketFieldIds()) {
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
  public void getTriggers() throws Exception {
    createClientWithTokenOrPassword(2);
    int count = 0;
    for (Trigger t : instance.getTriggers()) {
      assertThat(t.getTitle(), notNullValue());
      if (++count > 10) {
        break;
      }
    }

    count = 0;
    Map<String, List<String>> parameters = new HashMap<>();
    parameters.put("active", singletonList("true"));
    for (Trigger t : instance.getTriggers(parameters)) {
      assertThat(t.getTitle(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getTriggersWithParameters() throws Exception {
    createClientWithTokenOrPassword(2);
    int count = 0;
    String title = null;
    for (Trigger t : instance.getTriggers(null, true, "title", SortOrder.ASCENDING)) {
      if (title != null) {
        assertTrue(title.compareTo(t.getTitle()) < 0);
      }
      title = t.getTitle();
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getActiveTriggers() throws Exception {
    createClientWithTokenOrPassword(2);
    int count = 0;
    for (Trigger t : instance.getActiveTriggers()) {
      assertThat(t.getTitle(), notNullValue());
      assertTrue(t.isActive());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void createTrigger() throws Exception {
    createClientWithTokenOrPassword();
    Trigger trigger = createTestTrigger();
    try {
      trigger = instance.createTrigger(trigger);
      assertThat(trigger.getId(), notNullValue());
    } finally {
      if (trigger.getId() != null) {
        instance.deleteTrigger(trigger.getId());
      }
    }
  }

  @Test
  public void updateTrigger() throws Exception {
    createClientWithTokenOrPassword();
    Trigger trigger = createTestTrigger();
    try {
      trigger = instance.createTrigger(trigger);
      assertThat(trigger.getId(), notNullValue());
      trigger.setTitle(trigger.getTitle() + " Updated");
      trigger = instance.updateTrigger(trigger.getId(), trigger);
      assertTrue(trigger.getTitle().contains("Updated"));
    } finally {
      if (trigger.getId() != null) {
        instance.deleteTrigger(trigger.getId());
      }
    }
  }

  @Test
  public void searchTrigger() throws Exception {
    createClientWithTokenOrPassword();
    List<Trigger> triggers = new ArrayList<>();
    final String title = "[zendesk-java-client] SearchTriggerTest " + UUID.randomUUID();
    for (int i = 0; i < 3; i++) {
      triggers.add(instance.createTrigger(createTestTrigger(title + " " + i)));
    }
    // It's taking a while before the search returns them
    Awaitility.with()
        .pollDelay(45, SECONDS)
        .and()
        .pollInterval(5, SECONDS)
        .await()
        .timeout(60, SECONDS)
        .until(
            () ->
                StreamSupport.stream(instance.searchTriggers(title).spliterator(), false).count()
                    == 3L);
    try {
      assertThat(
          StreamSupport.stream(instance.searchTriggers(title).spliterator(), false).count(),
          is(3L));
    } finally {
      triggers.stream().map(Trigger::getId).forEach(instance::deleteTrigger);
    }
  }

  @Test
  public void searchTriggerWithParameters() throws Exception {
    createClientWithTokenOrPassword();
    List<Trigger> triggers = new ArrayList<>();
    final String title = "[zendesk-java-client] SearchTriggerTestWithParams " + UUID.randomUUID();
    for (int i = 0; i < 3; i++) {
      triggers.add(instance.createTrigger(createTestTrigger(title + " " + i)));
    }
    // It's taking a while before the search returns them
    Awaitility.with()
        .pollDelay(45, SECONDS)
        .and()
        .pollInterval(5, SECONDS)
        .await()
        .timeout(60, SECONDS)
        .until(
            () ->
                StreamSupport.stream(instance.searchTriggers(title).spliterator(), false).count()
                    == 3L);
    try {
      assertThat(
          StreamSupport.stream(
                  instance.searchTriggers(title, true, "title", SortOrder.ASCENDING).spliterator(),
                  false)
              .count(),
          is(3L));
    } finally {
      triggers.stream().map(Trigger::getId).forEach(instance::deleteTrigger);
    }
  }

  private Trigger createTestTrigger() {
    return createTestTrigger("[zendesk-java-client] Test trigger: " + UUID.randomUUID());
  }

  private Trigger createTestTrigger(String title) {
    Trigger.Condition condition = new Trigger.Condition();
    condition.setField("status");
    condition.setOperator("is");
    condition.setValue("solved");
    Trigger.Conditions conditions = new Trigger.Conditions();
    conditions.setAll(singletonList(condition));
    Trigger trigger = new Trigger();
    trigger.setTitle(title);
    trigger.setActive(true);
    trigger.setConditions(conditions);
    Action action = new Action();
    action.setField("status");
    action.setValue(new String[] {"solved"});
    trigger.setActions(singletonList(action));
    return trigger;
  }

  @Test
  public void getTicketsPagesRequests() throws Exception {
    createClientWithTokenOrPassword(2);
    int count = 0;
    for (Ticket t : instance.getTickets()) {
      assertThat(t.getSubject(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getTicketIncidentsCbp() throws Exception {
    createClientWithTokenOrPassword(2);
    int count = 0;
    for (Ticket t : instance.getTicketIncidentsCbp(8)) {
      assertThat(t.getSubject(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getDeletedTickets() throws Exception {
    createClientWithTokenOrPassword(2);
    int count = 0;
    for (DeletedTicket t : instance.getDeletedTickets()) {
      assertThat(t.getSubject(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getSatisfactionRatings() throws Exception {
    createClientWithTokenOrPassword(2);
    int count = 0;
    for (SatisfactionRating sr : instance.getSatisfactionRatings()) {
      assertThat(sr.getUrl(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getTicketMetrics() throws Exception {
    createClientWithTokenOrPassword(2);
    int count = 0;
    for (Metric m : instance.getTicketMetrics()) {
      assertThat(m.getId(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getMacros() throws Exception {
    createClientWithTokenOrPassword(2);
    int count = 0;
    for (Macro m : instance.getMacros()) {
      assertThat(m.getId(), notNullValue());
      if (++count > 10) {
        break;
      }
    }

    count = 0;
    Map<String, List<String>> parameters = new HashMap<>();
    parameters.put("active", singletonList("true"));
    for (Macro m : instance.getMacros(parameters)) {
      assertTrue(m.getActive());
      if (++count > 10) {
        break;
      }
    }
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
  public void getOrganizationRequests() throws Exception {
    createClientWithTokenOrPassword();
    int count = 0;
    for (Request request : instance.getOrganizationRequests(CLOUDBEES_ORGANIZATION_ID)) {
      assertThat(request.getId(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getTicketAudits() throws Exception {
    createClientWithTokenOrPassword(2);
    Iterator<Ticket> tickets = instance.getTickets().iterator();
    Ticket ticket = null;
    if (tickets.hasNext()) {
      ticket = tickets.next();
    }
    for (Audit a : instance.getTicketAudits(ticket.getId())) {
      assertThat(a, notNullValue());
      assertThat(a.getEvents(), not(Collections.<Event>emptyList()));
    }
    for (Audit a : instance.getTicketAudits()) {
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
  public void getTicketFieldsCbp() throws Exception {
    createClientWithTokenOrPassword(2);
    int count = 0;
    for (Field f : instance.getTicketFieldsCbp()) {
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
    instance =
        new Zendesk.Builder(config.getProperty("url"))
            .setUsername(config.getProperty("username"))
            .setPassword(config.getProperty("password"))
            .build();
    Ticket t = instance.getTicket(1);
    assertThat(t, notNullValue());
  }

  @Test
  public void createAnonymousClient() {
    instance = new Zendesk.Builder(config.getProperty("url")).build();
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
      assertThat(
          "First Collaborator",
          ticketCollaborators.get(0).getEmail(),
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
      assertThat(
          "The Due Date must be the same (rounded at the second)",
          DateUtils.truncate(ticket.getDueAt(), Calendar.SECOND),
          is(DateUtils.truncate(dueDate, Calendar.SECOND)));
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
    JobStatus softDeleteJobStatus =
        waitJobCompletion(
            instance.deleteTickets(firstElement(ticketsIds), otherElements(ticketsIds)));
    assertThat(
        "Soft Delete Job is completed",
        softDeleteJobStatus.getStatus(),
        is(JobStatus.JobStatusEnum.completed));
    // We permanently delete them
    JobStatus permDeleteJobStatus =
        waitJobCompletion(
            instance.permanentlyDeleteTickets(firstElement(ticketsIds), otherElements(ticketsIds)));
    // then
    assertThat(
        "Job is completed", permDeleteJobStatus.getStatus(), is(JobStatus.JobStatusEnum.completed));
    permDeleteJobStatus
        .getResults()
        .forEach(
            jobResult -> {
              assertThat(
                  "The job result has no account_id entry", jobResult.getAccountId(), nullValue());
              assertThat("The job result has no action entry", jobResult.getAction(), nullValue());
              assertThat(
                  "The job result has no details entry", jobResult.getDetails(), nullValue());
              assertThat("The job result has no email entry", jobResult.getEmail(), nullValue());
              assertThat("The job result has no error entry", jobResult.getError(), nullValue());
              assertThat(
                  "The job result has no external_id entry",
                  jobResult.getExternalId(),
                  nullValue());
              assertThat("The job result has no id entry", jobResult.getId(), nullValue());
              assertThat("The job result has no index entry", jobResult.getIndex(), nullValue());
              assertThat("The job result has no status entry", jobResult.getStatus(), nullValue());
              assertThat("The job result has a success entry", jobResult.getSuccess(), is(TRUE));
            });
    assumeThat(
        "We cannot find them anymore",
        instance.getTickets(firstElement(ticketsIds), otherElements(ticketsIds)),
        empty());
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

      assertThat(
          "We have the same number of tickets",
          status.getResults(),
          hasSize(ticketsToCreate.length));

      status
          .getResults()
          .forEach(
              jobResult -> {
                assertThat(
                    "The job result has an account_id entry",
                    jobResult.getAccountId(),
                    notNullValue());
                assertThat(
                    "The job result has no action entry", jobResult.getAction(), nullValue());
                assertThat(
                    "The job result has no details entry", jobResult.getDetails(), nullValue());
                assertThat("The job result has no email entry", jobResult.getEmail(), nullValue());
                assertThat("The job result has no error entry", jobResult.getError(), nullValue());
                assertThat(
                    "The job result has no external_id entry",
                    jobResult.getExternalId(),
                    nullValue());
                assertThat("The job result has an id entry", jobResult.getId(), notNullValue());
                assertThat(
                    "The job result has an index entry", jobResult.getIndex(), notNullValue());
                assertThat(
                    "The job result has no status entry", jobResult.getStatus(), nullValue());
                assertThat(
                    "The job result has no success entry", jobResult.getSuccess(), nullValue());
              });

      assertThat(
          "All tickets are created (we verify that all titles are present)",
          createdTickets.stream().map(Ticket::getSubject).collect(Collectors.toList()),
          containsInAnyOrder(Arrays.stream(ticketsToCreate).map(Ticket::getSubject).toArray()));
      createdTickets.stream()
          .map(Ticket::getId)
          .forEach(id -> assertThat("A unique ID must be set", id, notNullValue()));
    } finally {
      // cleanup
      waitJobCompletion(
          instance.deleteTickets(
              firstElement(createdTicketsIds), otherElements(createdTicketsIds)));
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
      tickets.forEach(
          ticket -> {
            ticket.setPriority(Priority.HIGH);
            ticket.setStatus(Status.OPEN);
          });
      final JobStatus status = waitJobCompletion(instance.updateTickets(tickets));

      // then
      assertThat("Job is completed", status.getStatus(), is(JobStatus.JobStatusEnum.completed));
      assertThat(
          "The good number of tickets were processed", status.getTotal(), is(ticketsIds.length));
      assertThat(
          "We have a result for each ticket", status.getResults(), hasSize(ticketsIds.length));
      assertThat(
          "Each ticket has a result",
          status.getResults().stream().map(JobResult::getId).collect(Collectors.toList()),
          containsInAnyOrder(ticketsIds));
      status
          .getResults()
          .forEach(
              jobResult -> {
                assertThat(
                    "The job result has no account_id entry",
                    jobResult.getAccountId(),
                    nullValue());
                assertThat(
                    "The job result has an action entry", jobResult.getAction(), is("update"));
                assertThat(
                    "The job result has no details entry", jobResult.getDetails(), nullValue());
                assertThat("The job result has no email entry", jobResult.getEmail(), nullValue());
                assertThat("The job result has no error entry", jobResult.getError(), nullValue());
                assertThat(
                    "The job result has no external_id entry",
                    jobResult.getExternalId(),
                    nullValue());
                assertThat("The job result has an id entry", jobResult.getId(), notNullValue());
                assertThat("The job result has no index entry", jobResult.getIndex(), nullValue());
                assertThat(
                    "The job result has a status entry", jobResult.getStatus(), is("Updated"));
                assertThat("The job result has a success entry", jobResult.getSuccess(), is(TRUE));
              });
    } finally {
      waitJobCompletion(
          instance.deleteTickets(firstElement(ticketsIds), otherElements(ticketsIds)));
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
      assertThat(
          "The imported ticket has a subject",
          importedTicket.getSubject(),
          CoreMatchers.containsString("[zendesk-java-client] This is a test"));
      assertThat("The imported ticket is closed", importedTicket.getStatus(), is(Status.CLOSED));
      assertThat(
          "The imported ticket has a createdAt value",
          importedTicket.getCreatedAt(),
          notNullValue());
      assertThat(
          "The imported ticket has an updatedAt value",
          importedTicket.getUpdatedAt(),
          notNullValue());
      assertThat(
          "The imported ticket has tags",
          importedTicket.getTags(),
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

      assertThat(
          "We have the same number of tickets",
          status.getResults(),
          hasSize(ticketsToImport.length));

      status
          .getResults()
          .forEach(
              jobResult -> {
                assertThat(
                    "The job result has an account_id entry",
                    jobResult.getAccountId(),
                    notNullValue());
                assertThat(
                    "The job result has no action entry", jobResult.getAction(), nullValue());
                assertThat(
                    "The job result has no details entry", jobResult.getDetails(), nullValue());
                assertThat("The job result has no email entry", jobResult.getEmail(), nullValue());
                assertThat("The job result has no error entry", jobResult.getError(), nullValue());
                assertThat(
                    "The job result has no external_id entry",
                    jobResult.getExternalId(),
                    nullValue());
                assertThat("The job result has an id entry", jobResult.getId(), notNullValue());
                assertThat(
                    "The job result has an index entry", jobResult.getIndex(), notNullValue());
                assertThat(
                    "The job result has no status entry", jobResult.getStatus(), nullValue());
                assertThat(
                    "The job result has no success entry", jobResult.getSuccess(), nullValue());
              });

      assertThat(
          "All tickets are created (we verify that all titles are present)",
          createdTickets.stream().map(Ticket::getSubject).collect(Collectors.toList()),
          containsInAnyOrder(Arrays.stream(ticketsToImport).map(Ticket::getSubject).toArray()));
      createdTickets.forEach(
          importedTicket -> {
            assertThat("The imported ticket has an ID", importedTicket.getId(), notNullValue());
            assertThat(
                "The imported ticket has a subject",
                importedTicket.getSubject(),
                CoreMatchers.containsString("[zendesk-java-client] This is a test"));
            assertThat(
                "The imported ticket is closed", importedTicket.getStatus(), is(Status.CLOSED));
            assertThat(
                "The imported ticket has a createdAt value",
                importedTicket.getCreatedAt(),
                notNullValue());
            assertThat(
                "The imported ticket has an updatedAt value",
                importedTicket.getUpdatedAt(),
                notNullValue());
            assertThat(
                "The imported ticket has tags",
                importedTicket.getTags(),
                containsInAnyOrder("zendesk-java-client", "smoke-test"));
          });

      // then
    } finally {
      // cleanup
      waitJobCompletion(
          instance.deleteTickets(
              firstElement(createdTicketsIds), otherElements(createdTicketsIds)));
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
    for (User user : instance.getSearchResults(User.class, "requester:" + requesterEmail)) {
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

      assertThat(
          "We have the same number of users", status.getResults(), hasSize(usersToCreate.length));

      status
          .getResults()
          .forEach(
              jobResult -> {
                assertThat(
                    "The job result has no account_id entry",
                    jobResult.getAccountId(),
                    nullValue());
                assertThat(
                    "The job result has no action entry", jobResult.getAction(), nullValue());
                assertThat(
                    "The job result has no details entry", jobResult.getDetails(), nullValue());
                assertThat(
                    "The job result has an email entry", jobResult.getEmail(), notNullValue());
                assertThat("The job result has no error entry", jobResult.getError(), nullValue());
                assertThat(
                    "The job result has an external_id entry",
                    jobResult.getExternalId(),
                    notNullValue());
                assertThat("The job result has an id entry", jobResult.getId(), notNullValue());
                assertThat("The job result has no index entry", jobResult.getIndex(), nullValue());
                assertThat(
                    "The job result has a status entry", jobResult.getStatus(), is("Created"));
                assertThat(
                    "The job result has no success entry", jobResult.getSuccess(), nullValue());
              });

      assertThat(
          "All users are created (we verify that all names are present)",
          createdUsers.stream().map(User::getName).collect(Collectors.toList()),
          containsInAnyOrder(Arrays.stream(usersToCreate).map(User::getName).toArray()));
      createdUsers.stream()
          .map(User::getId)
          .forEach(id -> assertThat("A unique ID must be set", id, notNullValue()));
    } finally {
      // cleanup
      Arrays.stream(createdUsersIds).forEach(instance::deleteUser);
    }
  }

  @Test
  public void createUser() throws Exception {
    // given
    createClientWithTokenOrPassword();
    final User userToCreate = newTestUser();
    userToCreate.setTimeZone("Pacific Time (US & Canada)");

    // when
    User createdUser = instance.createUser(userToCreate);

    // then
    assertThat("A unique ID must be set", createdUser.getId(), notNullValue());
    assertEquals("Time Zone must be set", userToCreate.getTimeZone(), createdUser.getTimeZone());
    assertEquals(
        "Iana Time Zone must be automatically set",
        "America/Los_Angeles",
        createdUser.getIanaTimeZone());
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
      assertThat(
          "Each user has a result",
          status.getResults().stream().map(JobResult::getId).collect(Collectors.toList()),
          containsInAnyOrder(usersIds));
      status
          .getResults()
          .forEach(
              jobResult -> {
                assertThat(
                    "The job result has no account_id entry",
                    jobResult.getAccountId(),
                    nullValue());
                assertThat(
                    "The job result has an action entry", jobResult.getAction(), is("update"));
                assertThat(
                    "The job result has no details entry", jobResult.getDetails(), nullValue());
                assertThat("The job result has no email entry", jobResult.getEmail(), nullValue());
                assertThat("The job result has no error entry", jobResult.getError(), nullValue());
                assertThat(
                    "The job result has no external_id entry",
                    jobResult.getExternalId(),
                    nullValue());
                assertThat("The job result has an id entry", jobResult.getId(), notNullValue());
                assertThat("The job result has no index entry", jobResult.getIndex(), nullValue());
                assertThat(
                    "The job result has a status entry", jobResult.getStatus(), is("Updated"));
                assertThat("The job result has a success entry", jobResult.getSuccess(), is(TRUE));
              });
    } finally {
      // cleanup
      Arrays.stream(usersIds).forEach(instance::deleteUser);
    }
  }

  @Test
  public void deleteUsersById() throws Exception {
    createClientWithTokenOrPassword();

    final List<User> users = createTestUsersInZendesk();
    final long[] ids = users.stream().mapToLong(User::getId).toArray();
    final JobStatus status = waitJobCompletion(instance.deleteUsers(ids));

    assertThat("Job is completed", status.getStatus(), is(JobStatus.JobStatusEnum.completed));
    assertThat("The good number of users were processed", status.getTotal(), is(users.size()));
    assertThat("We have a result for each user", status.getResults(), hasSize(users.size()));
    assertThat(
        "Job reports that the same users requested to be deleted were deleted",
        status.getResults().stream().map(JobResult::getId).collect(Collectors.toList()),
        containsInAnyOrder(Arrays.stream(ids).boxed().toArray()));
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
      final JobStatus status = waitJobCompletion(instance.createOrUpdateUsers(allUsers));

      // then
      assertThat("Job is completed", status.getStatus(), is(JobStatus.JobStatusEnum.completed));
      assertThat("The good number of users were processed", status.getTotal(), is(allUsers.size()));
      assertThat("We have a result for each user", status.getResults(), hasSize(allUsers.size()));
      assertThat(
          "Each existing user has a result",
          status.getResults().stream().map(JobResult::getId).collect(Collectors.toList()),
          hasItems(existingUsersIds));
      status
          .getResults()
          .forEach(
              jobResult -> {
                assertThat(
                    "The job result has no account_id entry",
                    jobResult.getAccountId(),
                    nullValue());
                assertThat(
                    "The job result has an action entry", jobResult.getAction(), nullValue());
                assertThat(
                    "The job result has no details entry", jobResult.getDetails(), nullValue());
                assertThat(
                    "The job result has an email entry", jobResult.getEmail(), notNullValue());
                assertThat("The job result has no error entry", jobResult.getError(), nullValue());
                assertThat(
                    "The job result has an external_id entry",
                    jobResult.getExternalId(),
                    notNullValue());
                assertThat("The job result has an id entry", jobResult.getId(), notNullValue());
                assertThat("The job result has no index entry", jobResult.getIndex(), nullValue());
                assertThat(
                    "The job result has a status entry", jobResult.getStatus(), notNullValue());
                assertThat(
                    "The job result has a success entry", jobResult.getSuccess(), nullValue());
              });
      assertThat(
          "Existing users are updated",
          status.getResults().stream()
              .filter(jobResult -> Objects.equals(jobResult.getStatus(), "Updated"))
              .map(JobResult::getId)
              .collect(Collectors.toList()),
          containsInAnyOrder(existingUsersIds));
      assertThat(
          "New users are created",
          status.getResults().stream()
              .filter(jobResult -> Objects.equals(jobResult.getStatus(), "Created"))
              .map(JobResult::getExternalId)
              .collect(Collectors.toList()),
          containsInAnyOrder(newUsers.stream().map(User::getExternalId).toArray()));
      newUsersIds =
          status.getResults().stream()
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
    for (User u : instance.lookupUserByExternalId(externalId)) {
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
      assertThat(
          e.getMessage(),
          is("HTTP/404: Not Found - {\"error\":\"RecordNotFound\",\"description\":\"Not found\"}"));
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

    assertThat(
        "There is at least one entry",
        StreamSupport.stream(complianceDeletionStatuses.spliterator(), false).count(),
        greaterThan(0L));

    assertTrue(
        "There is at least an entry for the application \"all\"",
        StreamSupport.stream(complianceDeletionStatuses.spliterator(), false)
            .anyMatch(
                complianceDeletionStatus ->
                    "all".equals(complianceDeletionStatus.getApplication())));

    complianceDeletionStatuses.forEach(
        status -> {
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
    for (User u : instance.lookupUserByExternalId(externalId)) {
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
    createClientWithTokenOrPassword(2);
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
    createClientWithTokenOrPassword(2);
    int count = 0;
    for (User u : instance.getUsers()) {
      assertThat(u.getName(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getUsersByRole() throws Exception {
    // Try to fetch a few pages with max 2 results per page to exercise pagination
    final int maxResults = 10;
    createClientWithTokenOrPassword(2);

    StreamSupport.stream(instance.getUsersByRole(Role.ADMIN.toString()).spliterator(), false)
        .limit(maxResults)
        .forEach(
            user ->
                assertEquals(
                    "A request for admins only returns admins", user.getRole(), Role.ADMIN));

    StreamSupport.stream(instance.getUsersByRole(Role.END_USER.toString()).spliterator(), false)
        .limit(maxResults)
        .forEach(
            user ->
                assertEquals(
                    "A request for end-users only returns end-users",
                    user.getRole(),
                    Role.END_USER));

    StreamSupport.stream(
            instance.getUsersByRole(Role.END_USER.toString(), Role.ADMIN.toString()).spliterator(),
            false)
        .limit(maxResults)
        .forEach(
            user ->
                assertThat(
                    "Multiple roles can be requested together",
                    user.getRole(),
                    isOneOf(Role.END_USER, Role.ADMIN)));
  }

  @Test
  public void getUsersById() throws Exception {
    createClientWithTokenOrPassword();

    Long[] usersIds =
        StreamSupport.stream(instance.getUsers().spliterator(), false)
            .limit(5)
            .map(User::getId)
            .toArray(Long[]::new);

    for (User user :
        instance.getUsers(usersIds[0], usersIds[1], usersIds[2], usersIds[3], usersIds[4])) {
      assertThat(user.getId(), notNullValue());
      assertThat(user.getName(), notNullValue());
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
    createClientWithTokenOrPassword(2);
    int count = 0;
    for (SuspendedTicket ticket : instance.getSuspendedTickets()) {
      assertThat(ticket.getId(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
    count = 0;
    Map<String, List<String>> parameters = new HashMap<>();
    parameters.put("order_by", singletonList("subject"));
    for (SuspendedTicket ticket : instance.getSuspendedTickets(parameters)) {
      assertThat(ticket.getId(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getAutomations() throws Exception {
    createClientWithTokenOrPassword(2);
    int count = 0;
    for (Automation automation : instance.getAutomations()) {
      assertThat(automation.getId(), notNullValue());
      if (++count > 10) {
        break;
      }
    }

    count = 0;
    Map<String, List<String>> parameters = new HashMap<>();
    parameters.put("active", singletonList("true"));
    for (Automation automation : instance.getAutomations(parameters)) {
      assertThat(automation.getId(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getOrganizations() throws Exception {
    createClientWithTokenOrPassword(2);
    int count = 0;
    for (Organization t : instance.getOrganizations()) {
      assertThat(t.getName(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getOrganizationsById() throws Exception {
    createClientWithTokenOrPassword();

    Long[] orgIds =
        StreamSupport.stream(instance.getOrganizations().spliterator(), false)
            .limit(5)
            .map(Organization::getId)
            .toArray(Long[]::new);

    for (Organization org :
        instance.getOrganizations(orgIds[0], orgIds[1], orgIds[2], orgIds[3], orgIds[4])) {
      assertThat(org.getId(), notNullValue());
      assertThat(org.getName(), notNullValue());
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
    for (Organization t : instance.lookupOrganizationsByExternalId("testorg")) {
      instance.deleteOrganization(t);
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

      assertThat(
          "We have the same number of organizations",
          status.getResults(),
          hasSize(orgsToCreate.length));

      status
          .getResults()
          .forEach(
              jobResult -> {
                assertThat(
                    "The job result has no account_id entry",
                    jobResult.getAccountId(),
                    nullValue());
                assertThat(
                    "The job result has no action entry", jobResult.getAction(), nullValue());
                assertThat(
                    "The job result has no details entry", jobResult.getDetails(), nullValue());
                assertThat("The job result has no email entry", jobResult.getEmail(), nullValue());
                assertThat("The job result has no error entry", jobResult.getError(), nullValue());
                assertThat(
                    "The job result has no external_id entry",
                    jobResult.getExternalId(),
                    nullValue());
                assertThat("The job result has an id entry", jobResult.getId(), notNullValue());
                assertThat("The job result has no index entry", jobResult.getIndex(), nullValue());
                assertThat(
                    "The job result has a status entry", jobResult.getStatus(), is("Created"));
                assertThat(
                    "The job result has no success entry", jobResult.getSuccess(), nullValue());
              });

      assertThat(
          "All organizations are created (we verify that all names are present)",
          createdOrgs.stream().map(Organization::getName).collect(Collectors.toList()),
          containsInAnyOrder(Arrays.stream(orgsToCreate).map(Organization::getName).toArray()));
      createdOrgs.stream()
          .map(Organization::getId)
          .forEach(id -> assertThat("A unique ID must be set", id, notNullValue()));
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
      final JobStatus status = waitJobCompletion(instance.updateOrganizations(organizations));

      // then
      assertThat("Job is completed", status.getStatus(), is(JobStatus.JobStatusEnum.completed));
      assertThat(
          "The good number of organizations were processed", status.getTotal(), is(orgsIds.length));
      assertThat(
          "We have a result for each organization", status.getResults(), hasSize(orgsIds.length));
      assertThat(
          "Each organization has a result",
          status.getResults().stream().map(JobResult::getId).collect(Collectors.toList()),
          containsInAnyOrder(orgsIds));
      status
          .getResults()
          .forEach(
              jobResult -> {
                assertThat(
                    "The job result has no account_id entry",
                    jobResult.getAccountId(),
                    nullValue());
                assertThat(
                    "The job result has an action entry", jobResult.getAction(), is("update"));
                assertThat(
                    "The job result has no details entry", jobResult.getDetails(), nullValue());
                assertThat("The job result has no email entry", jobResult.getEmail(), nullValue());
                assertThat("The job result has no error entry", jobResult.getError(), nullValue());
                assertThat(
                    "The job result has no external_id entry",
                    jobResult.getExternalId(),
                    nullValue());
                assertThat("The job result has an id entry", jobResult.getId(), notNullValue());
                assertThat("The job result has no index entry", jobResult.getIndex(), nullValue());
                assertThat(
                    "The job result has a status entry", jobResult.getStatus(), is("Updated"));
                assertThat("The job result has a success entry", jobResult.getSuccess(), is(TRUE));
              });
    } finally {
      // cleanup
      Arrays.stream(orgsIds).forEach(instance::deleteOrganization);
    }
  }

  @Test
  public void deleteOrganizationsById() throws Exception {
    createClientWithTokenOrPassword();

    final List<Organization> organizations = createTestOrganizationsInZendesk();
    final long[] ids = organizations.stream().mapToLong(Organization::getId).toArray();
    final JobStatus status = waitJobCompletion(instance.deleteOrganizations(ids));

    assertThat("Job is completed", status.getStatus(), is(JobStatus.JobStatusEnum.completed));
    assertThat(
        "The good number of organizations were processed",
        status.getTotal(),
        is(organizations.size()));
    assertThat(
        "We have a result for each organization",
        status.getResults(),
        hasSize(organizations.size()));
    assertThat(
        "Job reports that the same organization requested to be deleted were deleted",
        status.getResults().stream().map(JobResult::getId).collect(Collectors.toList()),
        containsInAnyOrder(Arrays.stream(ids).boxed().toArray()));
  }

  @Test
  public void createOrUpdateOrganization() throws Exception {
    createClientWithTokenOrPassword();

    String name = "testCreateOrUpdateOrganization";
    String externalId = "testCreateOrUpdateOrganization";

    // Clean up to avoid conflicts
    for (Organization o : instance.lookupOrganizationsByExternalId(externalId)) {
      instance.deleteOrganization(o.getId());
    }

    String noteAtCreation = "This is the initial organization note.";
    Organization org = new Organization();
    org.setExternalId(externalId);
    org.setName(name);
    org.setNotes(noteAtCreation);

    Organization createResult = instance.createOrUpdateOrganization(org);
    assertNotNull(createResult);
    assertNotNull(createResult.getId());
    assertEquals(name, createResult.getName());
    assertEquals(externalId, createResult.getExternalId());
    assertEquals(noteAtCreation, createResult.getNotes());

    String noteAtUpdate = "This is the updated organization note.";
    Organization updateOrg = new Organization();
    updateOrg.setId(createResult.getId());
    updateOrg.setExternalId(externalId);
    updateOrg.setNotes(noteAtUpdate);

    Organization updateResult = instance.createOrUpdateOrganization(updateOrg);
    assertNotNull(updateResult);
    assertEquals(createResult.getId(), updateResult.getId());
    assertEquals(name, updateResult.getName());
    assertEquals(externalId, updateResult.getExternalId());
    assertEquals(noteAtUpdate, updateResult.getNotes());

    instance.deleteOrganization(updateResult);
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
    users.forEach(
        user -> {
          OrganizationMembership defaultOrganizationMembership = new OrganizationMembership();
          defaultOrganizationMembership.setOrganizationId(firstElement(orgsIds));
          defaultOrganizationMembership.setUserId(user.getId());
          defaultOrganizationMembership.setDefault(TRUE);
          organizationMemberships.add(defaultOrganizationMembership);
        });
    // We add them in others orgs too
    Arrays.stream(otherElements(orgsIds))
        .forEach(
            orgId -> {
              users.forEach(
                  user -> {
                    OrganizationMembership organizationMembership = new OrganizationMembership();
                    organizationMembership.setOrganizationId(orgId);
                    organizationMembership.setUserId(user.getId());
                    organizationMembership.setDefault(FALSE);
                    organizationMemberships.add(organizationMembership);
                  });
            });

    // when
    // We create them
    final JobStatus status =
        waitJobCompletion(instance.createOrganizationMemberships(organizationMemberships));

    // then
    final Long[] orgMembershipsIds =
        status.getResults().stream().map(JobResult::getId).toArray(Long[]::new);

    try {

      assertThat(
          "We have the same number of memberships",
          status.getResults(),
          hasSize(organizationMemberships.size()));

      status
          .getResults()
          .forEach(
              jobResult -> {
                assertThat(
                    "The job result has no account_id entry",
                    jobResult.getAccountId(),
                    nullValue());
                assertThat(
                    "The job result has no action entry", jobResult.getAction(), nullValue());
                assertThat(
                    "The job result has no details entry", jobResult.getDetails(), nullValue());
                assertThat("The job result has no email entry", jobResult.getEmail(), nullValue());
                assertThat("The job result has no error entry", jobResult.getError(), nullValue());
                assertThat(
                    "The job result has no external_id entry",
                    jobResult.getExternalId(),
                    nullValue());
                assertThat("The job result has an id entry", jobResult.getId(), notNullValue());
                assertThat(
                    "The job result has no index entry", jobResult.getIndex(), notNullValue());
                assertThat("The job result has a status entry", jobResult.getStatus(), nullValue());
                assertThat(
                    "The job result has no success entry", jobResult.getSuccess(), nullValue());
              });

    } finally {
      // cleanup
      Arrays.stream(orgsIds).forEach(instance::deleteOrganization);
      Arrays.stream(usersIds).forEach(instance::deleteUser);
      instance.deleteOrganizationMemberships(
          firstElement(orgMembershipsIds), otherElements(orgMembershipsIds));
    }
  }

  @Test
  public void lookupOrganizationByExternalId() throws Exception {
    String orgId = "i";
    createClientWithTokenOrPassword();

    Organization newOrganization = newTestOrganization();
    newOrganization.setExternalId(orgId);
    Organization resultOrganization = null;
    try {
      resultOrganization = instance.createOrganization(newOrganization);
      assertNotNull(resultOrganization);

      Iterable<Organization> or = instance.lookupOrganizationsByExternalId(orgId);
      assertEquals(1, StreamSupport.stream(or.spliterator(), false).count());
    } finally {
      if (resultOrganization != null) {
        instance.deleteOrganization(resultOrganization);
      }
    }

    assertThrows(
        IllegalArgumentException.class, () -> instance.lookupOrganizationsByExternalId(""));
  }

  @Test
  public void getGroups() throws Exception {
    createClientWithTokenOrPassword(2);
    int count = 0;
    for (Group t : instance.getGroups()) {
      assertThat(t.getName(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getAssignableGroups() throws Exception {
    createClientWithTokenOrPassword(2);
    int count = 0;
    for (Group g : instance.getAssignableGroups()) {
      assertThat(g.getName(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getGroupUsers() throws Exception {
    createClientWithTokenOrPassword(2);
    Iterator<Group> groups = instance.getGroups().iterator();
    if (!groups.hasNext()) {
      return;
    }
    Group group = groups.next();
    int count = 0;
    for (User u : instance.getGroupUsers(group.getId())) {
      assertThat(u.getName(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getOrganizationUsers() throws Exception {
    createClientWithTokenOrPassword(2);
    Iterator<Organization> organizations = instance.getOrganizations().iterator();
    if (!organizations.hasNext()) {
      return;
    }
    Organization organization = organizations.next();
    int count = 0;
    for (User u : instance.getOrganizationUsers(organization.getId())) {
      assertThat(u.getName(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getRequests() throws Exception {
    createClientWithTokenOrPassword(2);
    int count = 0;
    for (Request r : instance.getRequests()) {
      assertThat(r.getId(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getOpenRequests() throws Exception {
    createClientWithTokenOrPassword(2);
    int count = 0;
    for (Request r : instance.getOpenRequests()) {
      assertThat(r.getId(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getSolvedRequests() throws Exception {
    createClientWithTokenOrPassword(1);
    int count = 0;
    for (Request r : instance.getSolvedRequests()) {
      assertThat(r.getId(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getOrganizationMemberships() throws Exception {
    createClientWithTokenOrPassword(2);
    int count = 0;
    for (OrganizationMembership m : instance.getOrganizationMemberships()) {
      assertThat(m.getId(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getOrganizationMembershipsForOrg() throws Exception {
    createClientWithTokenOrPassword(2);
    Iterator<Organization> organizations = instance.getOrganizations().iterator();
    if (!organizations.hasNext()) {
      return;
    }
    Organization organization = organizations.next();
    int count = 0;
    for (OrganizationMembership m : instance.getOrganizationMembershipsForOrg(organization.getId())) {
      assertThat(m.getId(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getOrganizationMembershipsForUser() throws Exception {
    createClientWithTokenOrPassword(2);
    Iterator<OrganizationMembership> organizationMemberships = instance.getOrganizationMemberships().iterator();
    if (!organizationMemberships.hasNext()) {
      return;
    }
    long userId = organizationMemberships.next().getUserId();
    int count = 0;
    for (OrganizationMembership m : instance.getOrganizationMembershipsForUser(userId)) {
      assertThat(m.getId(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getGroupMembershipsForUser() throws Exception {
    createClientWithTokenOrPassword(2);
    Iterator<GroupMembership> groupMemberships = instance.getGroupMemberships().iterator();
    if (!groupMemberships.hasNext()) {
      return;
    }
    long userId = groupMemberships.next().getUserId();
    int count = 0;
    for (GroupMembership m : instance.getGroupMembershipByUserCbp(userId)) {
      assertThat(m.getId(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getGroupMembershipsCbp() throws Exception {
    createClientWithTokenOrPassword(2);
    Iterator<Group> groups = instance.getGroups().iterator();
    if (!groups.hasNext()) {
      return;
    }
    Group group = groups.next();
    int count = 0;
    for (GroupMembership m : instance.getGroupMembershipsCbp(group.getId())) {
      assertThat(m.getId(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getAssignableGroupMemberships() throws Exception {
    createClientWithTokenOrPassword(2);
    int count = 0;
    for (GroupMembership m : instance.getAssignableGroupMemberships()) {
      assertThat(m.getId(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getAssignableGroupMembershipsCbp() throws Exception {
    createClientWithTokenOrPassword(2);
    Iterator<Group> groups = instance.getGroups().iterator();
    if (!groups.hasNext()) {
      return;
    }
    Group group = groups.next();
    int count = 0;
    for (GroupMembership m : instance.getAssignableGroupMembershipsCbp(group.getId())) {
      assertThat(m.getId(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getGroupMemberships() throws Exception {
    createClientWithTokenOrPassword(2);
    Long groupId = null;
    Long userId = null;
    int maxIterations = 5;
    int count = 0;
    for (GroupMembership gm : instance.getGroupMemberships()) {
      assertThat(gm.getGroupId(), notNullValue());
      assertThat(gm.getUserId(), notNullValue());
      if (++count > maxIterations) {
        groupId = gm.getGroupId();
        userId = gm.getUserId();
        break;
      }
    }

    count = 0;
    for (GroupMembership gm : instance.getGroupMemberships(null, groupId)) {
      assertEquals(groupId, gm.getGroupId());
      assertThat(gm.getUserId(), notNullValue());
      if (++count > maxIterations) {
        break;
      }
    }

    count = 0;
    for (GroupMembership gm : instance.getGroupMemberships(userId, null)) {
      assertEquals(userId, gm.getUserId());
      assertThat(gm.getGroupId(), notNullValue());
      if (++count > maxIterations) {
        break;
      }
    }
  }

  @Test
  public void getPermissionGroups() throws Exception {
    createClientWithTokenOrPassword();
    int count = 0;
    for (PermissionGroup pg : instance.getPermissionGroups()) {
      assertThat(pg.getId(), notNullValue());
      assertThat(pg.getName(), notNullValue());
      assertThat(pg.getBuiltIn(), notNullValue());
      assertThat(pg.getCreatedAt(), notNullValue());
      assertThat(pg.getUpdatedAt(), notNullValue());
      if (++count > 1) {
        break;
      }
    }
  }

  @Test
  public void permissionGroupCRUD() throws Exception {
    createClientWithTokenOrPassword();
    PermissionGroup pg = new PermissionGroup();
    pg.setName("[zendesk-java-client] This is a creation test " + UUID.randomUUID());
    pg = instance.createPermissionGroup(pg);
    Long pgId = pg.getId();
    try {
      assertThat(pg.getId(), notNullValue());
      assertThat(pg.getName(), containsString("[zendesk-java-client] This is a creation test"));
      assertThat(pg.getCreatedAt(), notNullValue());
      assertThat(pg.getUpdatedAt(), notNullValue());
      assertThat(pg.getCreatedAt(), is(pg.getUpdatedAt()));
      pg.setName("[zendesk-java-client] This is an update test" + UUID.randomUUID());
      pg = instance.updatePermissionGroup(pg);
      assertThat(pg.getId(), is(pgId));
      assertThat(pg.getName(), containsString("[zendesk-java-client] This is an update test"));
      assertThat(pg.getCreatedAt(), notNullValue());
      assertThat(pg.getUpdatedAt(), notNullValue());
      assertThat(pg.getCreatedAt(), lessThanOrEqualTo(pg.getUpdatedAt()));
    } finally {
      instance.deletePermissionGroup(pg);
    }
    PermissionGroup ghost = instance.getPermissionGroup(pgId);
    assertThat(ghost, nullValue());
  }

  @Test
  public void getArticles() throws Exception {
    createClientWithTokenOrPassword();
    int count = 0;
    for (Article t : instance.getArticles()) {
      assertThat(t.getTitle(), notNullValue());
      if (++count > 40) { // Check enough to pull 2 result pages
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
    Iterable<Article> result =
        instance.getArticlesFromAnyLabels(Arrays.asList("SomeLabelA", "SomeLabelB"));
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
    Iterable<Article> result =
        instance.getArticlesFromAllLabels(Arrays.asList("AllLabelA", "AllLabelB"));
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
  public void listHelpCenterLocales() throws Exception {
    createClientWithTokenOrPassword();

    Locales locales = instance.listHelpCenterLocales();

    assertNotNull(locales);
    assertNotNull(locales.getDefaultLocale());
    assertNotNull(locales.getLocales());
    assertFalse(locales.getLocales().isEmpty());
    assertTrue(locales.getLocales().contains(locales.getDefaultLocale()));
  }

  @Test
  public void getArticleTranslations() throws Exception {
    createClientWithTokenOrPassword();
    int articleCount = 0;
    int translationCount = 0; // Count total translations checked, not per-article
    for (Article art : instance.getArticles()) {
      assertNotNull(art.getId());
      if (++articleCount > 10) {
        break; // Do not overwhelm the getArticles API
      }
      for (Translation t : instance.getArticleTranslations(art.getId())) {
        assertNotNull(t.getId());
        assertNotNull(t.getTitle());
        // body is not mandatory
        // <https://developer.zendesk.com/rest_api/docs/help_center/translations.html>
        // assertNotNull(t.getBody());
        if (++translationCount > 3) {
          return;
        }
      }
    }
  }

  @Test
  public void showArticleTranslation() throws Exception {
    createClientWithTokenOrPassword();
    List<String> locales = instance.listHelpCenterLocales().getLocales();

    int articleCount = 0;

    for (Article article : instance.getArticles()) {
      assertNotNull(article.getId());

      if (++articleCount > 10) {
        break;
      }

      int translationCount = 0;

      for (String locale : locales) {
        Translation translation = instance.showArticleTranslation(article.getId(), locale);

        // if there is no translation for the given locale the endpoint will return null
        if (translation != null) {
          assertNotNull(translation.getId());
          assertNotNull(translation.getTitle());
        }

        if (++translationCount > 3) {
          break;
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
        // body is not mandatory
        // <https://developer.zendesk.com/rest_api/docs/help_center/translations.html>
        // assertNotNull(t.getBody());
        if (++translationCount > 3) {
          return;
        }
      }
    }
  }

  @Test
  public void showSectionTranslation() throws Exception {
    createClientWithTokenOrPassword();
    List<String> locales = instance.listHelpCenterLocales().getLocales();

    int sectionCount = 0;

    for (Section section : instance.getSections()) {
      assertNotNull(section.getId());

      if (++sectionCount > 10) {
        break;
      }

      int translationCount = 0;

      for (String locale : locales) {
        Translation translation = instance.showSectionTranslation(section.getId(), locale);

        // if there is no translation for the given locale the endpoint will return null
        if (translation != null) {
          assertNotNull(translation.getId());
          assertNotNull(translation.getTitle());
        }

        if (++translationCount > 3) {
          break;
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
      for (Translation t : instance.getCategoryTranslations(cat.getId())) {
        assertNotNull(t.getId());
        assertNotNull(t.getTitle());
        // body is not mandatory
        // <https://developer.zendesk.com/rest_api/docs/help_center/translations.html>
        // assertNotNull(t.getBody());
        if (++translationCount > 3) {
          return;
        }
      }
    }
  }

  @Test
  public void showCategoryTranslation() throws Exception {
    createClientWithTokenOrPassword();
    List<String> locales = instance.listHelpCenterLocales().getLocales();

    int categoryCount = 0;

    for (Category category : instance.getCategories()) {
      assertNotNull(category.getId());

      if (++categoryCount > 10) {
        break;
      }

      int translationCount = 0;

      for (String locale : locales) {
        Translation translation = instance.showCategoryTranslation(category.getId(), locale);

        // if there is no translation for the given locale the endpoint will return null
        if (translation != null) {
          assertNotNull(translation.getId());
          assertNotNull(translation.getTitle());
        }

        if (++translationCount > 3) {
          break;
        }
      }
    }
  }

  @Test
  public void getArticlesIncrementally() throws Exception {
    createClientWithTokenOrPassword();
    final long ONE_WEEK = 7 * 24 * 60 * 60 * 1000;
    int count = 0;
    try {
      for (Article t :
          instance.getArticlesIncrementally(new Date(new Date().getTime() - ONE_WEEK))) {
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
  // Zendesk api behavior note - When update is used to change a phone number, a second phone number
  // is added to the record. An update works as expected for non-identify fields, additional
  // constraints exist for identify field updates
  public void createOrUpdateUser() throws Exception {
    createClientWithTokenOrPassword();

    String name = "testCreateOrUpdateUser";
    String externalId = "testCreateOrUpdateUser";

    // Clean up to avoid conflicts
    for (User u : instance.lookupUserByExternalId(externalId)) {
      instance.deleteUser(u.getId());
    }

    String detailsAtCreation = "details at creation";
    User user = new User(true, name);
    user.setExternalId(externalId);
    user.setDetails(detailsAtCreation);

    User createResult = instance.createOrUpdateUser(user);
    assertNotNull(createResult);
    assertNotNull(createResult.getId());
    assertEquals(name, createResult.getName());
    assertEquals(externalId, createResult.getExternalId());
    assertEquals(detailsAtCreation, createResult.getDetails());

    String detailsAtUpdate = "details at update";
    User updateUser = new User(true, name);
    updateUser.setId(createResult.getId());
    updateUser.setExternalId(externalId);
    updateUser.setDetails(detailsAtUpdate);

    User updateResult = instance.createOrUpdateUser(updateUser);
    assertNotNull(updateResult);
    assertEquals(createResult.getId(), updateResult.getId());
    assertEquals(name, updateResult.getName());
    assertEquals(externalId, updateResult.getExternalId());
    assertEquals(detailsAtUpdate, updateResult.getDetails());

    instance.deleteUser(updateResult);
  }

  @Test
  public void createTicketForm() throws Exception {
    createClientWithTokenOrPassword();
    String givenName = "Test create ticket form";
    TicketForm form = newTicketForm(givenName);
    TicketForm createdForm = null;
    try {
      createdForm = instance.createTicketForm(form);
      checkFields(createdForm, givenName);
    } finally {
      if (createdForm != null) {
        instance.deleteTicketForm(createdForm);
      }
    }
  }

  @Test
  public void updateTicketForm() throws Exception {
    createClientWithTokenOrPassword();

    String name1 = "Test update ticket form 1";
    TicketForm form1 = newTicketForm(name1);

    String name2 = "Test update ticket form 2";
    TicketForm form2 = newTicketForm(name2);

    TicketForm updatedForm = null;
    try {
      updatedForm = instance.createTicketForm(form1);
      checkFields(updatedForm, name1);

      form2.setId(updatedForm.getId());

      updatedForm = instance.updateTicketForm(form2);
      checkFields(updatedForm, name2);
    } finally {
      if (updatedForm != null) {
        instance.deleteTicketForm(updatedForm);
      }
    }
  }

  @Test
  public void getDynamicContentItems() throws Exception {
    createClientWithTokenOrPassword(2);
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

        DynamicContentItemVariant fetch =
            instance.getDynamicContentItemVariant(i.getId(), v.getId());
        assertEquals(v.getId(), fetch.getId());

        if (++secondaryCount > 10) {
          break;
        }
      }
    }
  }

  @Test
  public void getTicketCommentsShouldBeAscending() throws Exception {
    createClientWithTokenOrPassword(1);

    Ticket t = newTestTicket();
    Ticket ticket = null;
    try {
      ticket = instance.createTicket(t);
      final Comment comment = new Comment(TICKET_COMMENT2);
      comment.setType(CommentType.COMMENT);
      instance.createComment(ticket.getId(), comment);
      Iterable<Comment> ticketCommentsIt = instance.getTicketComments(ticket.getId());
      List<Comment> comments = new ArrayList<>();
      ticketCommentsIt.forEach(comments::add);

      assertThat(comments.size(), is(2));
      assertThat(comments.get(0).getBody(), containsString(TICKET_COMMENT1));
      assertThat(comments.get(0).getType(), is(CommentType.COMMENT));
      assertNotNull(comments.get(0).getVia());
      assertThat(comments.get(0).getVia().getChannel(), is("api"));
      assertThat(comments.get(1).getBody(), containsString(TICKET_COMMENT2));
      assertThat(comments.get(1).getType(), is(CommentType.COMMENT));
      assertNotNull(comments.get(1).getVia());
      assertThat(comments.get(1).getVia().getChannel(), is("api"));
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
      final Comment comment = new Comment(TICKET_COMMENT2);
      comment.setType(CommentType.COMMENT);
      instance.createComment(ticket.getId(), comment);
      Iterable<Comment> ticketCommentsIt =
          instance.getTicketComments(ticket.getId(), SortOrder.DESCENDING);
      List<Comment> comments = new ArrayList<>();
      ticketCommentsIt.forEach(comments::add);

      assertThat(comments.size(), is(2));
      assertThat(comments.get(0).getBody(), containsString(TICKET_COMMENT2));
      assertThat(comments.get(0).getType(), is(CommentType.COMMENT));
      assertNotNull(comments.get(0).getVia());
      assertThat(comments.get(0).getVia().getChannel(), is("api"));
      assertThat(comments.get(1).getBody(), containsString(TICKET_COMMENT1));
      assertThat(comments.get(1).getType(), is(CommentType.COMMENT));
      assertNotNull(comments.get(1).getVia());
      assertThat(comments.get(1).getVia().getChannel(), is("api"));
    } finally {
      if (ticket != null) {
        instance.deleteTicket(ticket.getId());
      }
    }
  }

  @Test
  public void getTicketsFromSearch() throws Exception {
    createClientWithTokenOrPassword();

    Ticket t = newTestTicket();
    t.setRequester(new Ticket.Requester("a name", "email+alias@acme.org"));
    Ticket ticket = null;
    try {
      ticket = instance.createTicket(t);
      // according to the doc, it takes about 1 minute for the ticket to be indexed
      // running several time, it seems that the actual value is around 30-40s
      Awaitility.with()
          .pollDelay(20, SECONDS)
          .and()
          .pollInterval(10, SECONDS)
          .await()
          .timeout(90, SECONDS)
          .until(
              () -> {
                Iterable<Ticket> tickets =
                    instance.getTicketsFromSearch("requester:email+alias@acme.org");
                return StreamSupport.stream(tickets.spliterator(), false).findAny().isPresent();
              });
    } finally {
      if (ticket != null) {
        instance.deleteTicket(ticket.getId());
      }
    }
  }

  @Test
  public void getUnresolvedViewReturnsANewlyCreatedTicket() throws Exception {
    createClientWithTokenOrPassword();
    Ticket ticket = instance.createTicket(newTestTicket());
    try {
      assertThat(ticket.getId(), notNullValue());

      Optional<Ticket> maybeTicket =
          StreamSupport.stream(instance.getView(UNRESOLVED_TICKETS_VIEW_ID).spliterator(), false)
              .filter(t -> Objects.equals(t.getId(), ticket.getId()))
              .findFirst();
      assertTrue(maybeTicket.isPresent());
    } finally {
      instance.deleteTicket(ticket.getId());
    }
  }

  @Test
  public void getViewReturnsTheUnresolvedView() throws Exception {
    createClientWithTokenOrPassword();
    Optional<View> maybeView =
        StreamSupport.stream(instance.getViews().spliterator(), false)
            .filter(v -> Objects.equals(v.getId(), UNRESOLVED_TICKETS_VIEW_ID))
            .findFirst();
    assertTrue(maybeView.isPresent());
  }

  @Test
  public void getViews() throws Exception {
    createClientWithTokenOrPassword(2);
    int count = 0;
    for (View v : instance.getViews()) {
      assertThat(v.getId(), notNullValue());
      if (++count > 10) {
        break;
      }
    }

    count = 0;
    Map<String, List<String>> parameters = new HashMap<>();
    parameters.put("active", singletonList("true"));
    for (View v : instance.getViews(parameters)) {
      assertThat(v.getId(), notNullValue());
      if (++count > 10) {
        break;
      }
    }
  }

  @Test
  public void getTags() throws Exception {
    createClientWithTokenOrPassword(2);
    int count = 0;
    for (Tag tag : instance.getTags()) {
      assertThat(tag.getName(), notNullValue());
      if (++count > 10) {
        break;
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
    final Long[] createdOrgsIds =
        waitJobCompletion(instance.createOrganizations(orgsToCreate)).getResults().stream()
            .map(JobResult::getId)
            .toArray(Long[]::new);
    assumeThat(
        "All created organizations should have an ID",
        createdOrgsIds.length,
        is(orgsToCreate.length));
    final List<Organization> createdOrganizations =
        Arrays.stream(createdOrgsIds).map(instance::getOrganization).collect(Collectors.toList());
    assumeThat(
        "All created organizations are found in zendesk",
        createdOrganizations.stream().map(Organization::getId).collect(Collectors.toList()),
        containsInAnyOrder(createdOrgsIds));
    LOGGER.info("Test organizations: {}", Arrays.toString(createdOrgsIds));
    return createdOrganizations;
  }

  /** Creates several new organizations (2 min, 5 max) */
  private Organization[] newTestOrganizations() {
    final ArrayList<Organization> organizations = new ArrayList<>();
    for (int i = 0; i < 2 + RANDOM.nextInt(3); i++) {
      organizations.add(newTestOrganization());
    }
    return organizations.toArray(new Organization[0]);
  }

  /** Creates a new organization */
  private Organization newTestOrganization() {
    final Organization organization = new Organization();
    final String id = UUID.randomUUID().toString();
    organization.setExternalId("org-" + id);
    organization.setName("[zendesk-java-client] Organization " + id);
    organization.setDetails(
        "This organization is created by zendesk-java-client Integration Tests");
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
    final Long[] createdUsersIds =
        waitJobCompletion(instance.createUsers(usersToCreate)).getResults().stream()
            .map(JobResult::getId)
            .toArray(Long[]::new);
    assumeThat(
        "All created users should have an ID", createdUsersIds.length, is(usersToCreate.length));
    final List<User> createdUsers =
        Arrays.stream(createdUsersIds).map(instance::getUser).collect(Collectors.toList());
    assumeThat(
        "All created users are found in zendesk",
        createdUsers.stream().map(User::getId).collect(Collectors.toList()),
        containsInAnyOrder(createdUsersIds));
    LOGGER.info("Test users: {}", Arrays.toString(createdUsersIds));
    return createdUsers;
  }

  /** Creates several new users (2 min, 5 max) */
  private User[] newTestUsers() {
    final ArrayList<User> users = new ArrayList<>();
    for (int i = 0; i < 2 + RANDOM.nextInt(3); i++) {
      users.add(newTestUser());
    }
    return users.toArray(new User[0]);
  }

  /** Creates a new user */
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
    final Long[] createdTicketsIds =
        waitJobCompletion(instance.createTickets(ticketsToCreate)).getResults().stream()
            .map(JobResult::getId)
            .toArray(Long[]::new);
    assumeThat(
        "All created tickets should have an ID",
        createdTicketsIds.length,
        is(ticketsToCreate.length));
    final List<Ticket> createdTickets =
        instance.getTickets(firstElement(createdTicketsIds), otherElements(createdTicketsIds));
    assumeThat(
        "All created tickets are found in zendesk",
        createdTickets.stream().map(Ticket::getId).collect(Collectors.toList()),
        containsInAnyOrder(createdTicketsIds));
    LOGGER.info("Test tickets: {}", Arrays.toString(createdTicketsIds));
    return createdTickets;
  }

  /** Creates several new tickets (2 min, 5 max) */
  private Ticket[] newTestTickets() {
    final ArrayList<Ticket> tickets = new ArrayList<>();
    for (int i = 0; i < 2 + RANDOM.nextInt(3); i++) {
      tickets.add(newTestTicket());
    }
    return tickets.toArray(new Ticket[0]);
  }

  /** Creates a new ticket */
  private Ticket newTestTicket() {
    assumeThat(
        "Must have a requester email", config.getProperty("requester.email"), notNullValue());
    assumeThat("Must have a requester name", config.getProperty("requester.name"), notNullValue());
    final Ticket ticket =
        new Ticket(
            new Ticket.Requester(
                config.getProperty("requester.name"), config.getProperty("requester.email")),
            "[zendesk-java-client] This is a test " + UUID.randomUUID().toString(),
            new Comment(TICKET_COMMENT1));
    ticket.setCollaborators(
        Arrays.asList(
            new Collaborator("Bob Example", "bob@example.org"),
            new Collaborator("Alice Example", "alice@example.org")));
    ticket.setTags(Arrays.asList("zendesk-java-client", "smoke-test"));
    return ticket;
  }

  /** Creates several new ticketImport (2 min, 5 max) */
  private TicketImport[] newTestTicketImports() {
    final ArrayList<TicketImport> ticketImports = new ArrayList<>();
    for (int i = 0; i < 2 + RANDOM.nextInt(3); i++) {
      ticketImports.add(newTestTicketImport());
    }
    return ticketImports.toArray(new TicketImport[0]);
  }

  /** Creates a new ticketImport */
  private TicketImport newTestTicketImport() {
    assumeThat(
        "Must have a requester email", config.getProperty("requester.email"), notNullValue());
    assumeThat("Must have a requester name", config.getProperty("requester.name"), notNullValue());
    Date now = Calendar.getInstance().getTime();
    final TicketImport ticketImport =
        new TicketImport(
            new Ticket.Requester(
                config.getProperty("requester.name"), config.getProperty("requester.email")),
            "[zendesk-java-client] This is a test " + UUID.randomUUID().toString(),
            singletonList(new Comment(TICKET_COMMENT1)));
    ticketImport.setCollaborators(
        Arrays.asList(
            new Collaborator("Bob Example", "bob@example.org"),
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

    // Let's wait for its completion (2 minutes max)
    await()
        .until(
            () -> instance.getJobStatus(result).getStatus() == JobStatus.JobStatusEnum.completed);

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
    await()
        .until(
            () ->
                StreamSupport.stream(
                        instance.getDeletedTickets("id", SortOrder.DESCENDING).spliterator(), false)
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
    await()
        .until(
            () ->
                StreamSupport.stream(
                        instance.getDeletedTickets("id", SortOrder.DESCENDING).spliterator(), false)
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

  /**
   * Creates a new ticket form
   *
   * @param givenName provided name of the form
   * @return created form object
   */
  private TicketForm newTicketForm(String givenName) {
    TicketForm form = new TicketForm();
    form.setActive(true);
    form.setName(givenName);
    form.setDisplayName(givenName);
    form.setRawName(givenName);
    form.setRawDisplayName(givenName);
    return form;
  }

  /**
   * Verifies field on the ticket form
   *
   * @param form the ticket form to verify fields in
   * @param name expected name of the form
   */
  private void checkFields(TicketForm form, String name) {
    assertNotNull(form);
    assertNotNull(form.getId());
    assertEquals(name, form.getName());
    assertEquals(name, form.getDisplayName());
    assertEquals(name, form.getRawName());
    assertEquals(name, form.getRawDisplayName());
  }
}
