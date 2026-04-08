Zendesk Java Client
===================

This is a [Zendesk][zd] client implementation written in Java using AsyncHttpClient and Jackson.

[![Java CI with Maven](https://github.com/cloudbees-oss/zendesk-java-client/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/cloudbees-oss/zendesk-java-client/actions?query=workflow%3A%22Java+CI+with+Maven%22) 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.cloudbees.thirdparty/zendesk-java-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.cloudbees.thirdparty/zendesk-java-client/) 
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.cloudbees.thirdparty%3Azendesk-java-client&metric=coverage)](https://sonarcloud.io/dashboard?id=com.cloudbees.thirdparty%3Azendesk-java-client) 
[![Lines](https://sonarcloud.io/api/project_badges/measure?project=com.cloudbees.thirdparty%3Azendesk-java-client&metric=ncloc)](https://sonarcloud.io/dashboard?id=com.cloudbees.thirdparty%3Azendesk-java-client) 
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.cloudbees.thirdparty%3Azendesk-java-client&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.cloudbees.thirdparty%3Azendesk-java-client)

Using the API
-------------

Start by creating a `Zendesk` instance

    Zendesk zd = new Zendesk.Builder("https://{{your domain}}.zendesk.com")
            .setUsername("...")
            .setToken("...") // or .setPassword("...")
            .build();

If you are behind a proxy, or want to otherwise control the lifecycle of the `AsyncHttpClient` instance
you should pass that through to the builder too. If you don't pass an `AsyncHttpClient` instance to the builder
it will create its own which will be closed by the `Zendesk.close()` method.

Where methods return paged data sets, an `Iterable` is returned that will lazy-fetch one page at a time until
all records have been fetched, so e.g.

    for (Ticket ticket: zd.getTickets()) {
        ...
    }

will iterate through *all* tickets. Most likely you will want to implement your own cut-off process to stop iterating
when you have got enough data.

Idempotency
-----------

The Zendesk API supports [idempotency keys](https://developer.zendesk.com/api-reference/ticketing/introduction/#idempotency)
to safely retry operations without creating duplicate resources. This client supports idempotent
ticket creation via `createTicketIdempotent` and `createTicketIdempotentAsync`.
Either method may throw a `ZendeskResponseIdempotencyConflictException` if the same idempotency key
is used in two requests with non-identical payloads.

### Usage Example

The following example illustrates a usage pattern for publishing updates to a Zendesk ticket
that tracks some application specific issue. It ensures that only one ticket is created per
issue, even if multiple updates are published concurrently for the same issue, or if the update is
retried due to a transient failure after the ticket has already been created.

Note that it's intentionally slightly more complicated than it would probably be in real life
so that we can demonstrate non-trivial handling of a `ZendeskResponseIdempotencyConflictException`.

```java
class FooIssueService {
    private final Zendesk zendesk;
    private final IssueRepository issueRepository;
    private final Logger logger = LoggerFactory.getLogger(FooIssueService.class);

    // ...

    public void postIssueUpdate(FooIssue issue, String update) {     
        // Fast path pre-check, would be unsafe without idempotency b/c TOCTOU.
        Optional<Long> optTicketId = issueRepository.getTicketId(issue.getId());
        if (optTicketId.isPresent()) {
            postIssueComment(optTicketId.get(), update);
            return;
        }
        
        // Must map the issue 1-to-1, so that retries for the same issue use the same key.
        String idempotencyKey = String.format("foo-issue-%s", issue.getId());
        
        try {
            issueRepository.reserveTicketId(issue.getId());
            IdempotentResult<Ticket> result = zendesk.createTicketIdempotent(
                    new Ticket(issue.getRequesterId(), issue.getTitle(), new Comment(update)),
                    idempotencyKey);
            
            if (!result.isDuplicateRequest()) {
                Ticket ticket = result.get();
                issueRepository.saveTicketId(issue.getId(), ticket.getId());
                logger.info("Created new ticket (id = {})", ticket.getId());
            }
        } catch (ZendeskResponseIdempotencyConflictException e) {
            // We assume that `getTicketId` will retry internally if the reservation is still
            // fresh and the ticket id has not yet been saved in order to limit potential
            // race conditions.
            long existingTicketId = issueRepository.getTicketId(issue.getId()).orElseThrow(
                () -> new IllegalStateException(
                    String.format("Existing ticket not found for issue %s", issue.getId()), e));
            postIssueComment(existingTicketId, update);
        }
    }
    
    private void postIssueComment(long ticketId, String update) {
        Comment comment = zendesk.createComment(ticketId, new Comment(update));
        logger.info("Added comment (id = {}) to ticket (id = {})", comment.getId(), ticketId);
    }
}
```

Community
-------------

* [Users list](https://groups.google.com/forum/#!forum/zendesk-java-client-users)
* [GitHub discussions](https://github.com/cloudbees-oss/zendesk-java-client/discussions)

Status
------

Here is the status of the various API components:

* [Tickets](https://developer.zendesk.com/api-reference/ticketing/tickets/tickets/) ✓
* [Ticket Audits](https://developer.zendesk.com/api-reference/ticketing/tickets/ticket_audits/) ✓
* [Incremental Export](https://developer.zendesk.com/api-reference/ticketing/ticket-management/incremental_exports/) - Partial (tickets, users, organizations only) 
* [Ticket Fields](https://developer.zendesk.com/api-reference/ticketing/tickets/ticket_fields/) ✓
* [Ticket Import](https://developer.zendesk.com/api-reference/ticketing/tickets/ticket_import/) ✓
* [Ticket Metrics](https://developer.zendesk.com/api-reference/ticketing/tickets/ticket_metrics/) ✓
* [Ticket Forms](https://developer.zendesk.com/api-reference/ticketing/tickets/ticket_forms/) *getTicketForm() and getTicketForms()*
* [Views](https://developer.zendesk.com/api-reference/ticketing/business-rules/views/)
* [Users](https://developer.zendesk.com/api-reference/ticketing/users/users/) ✓
    * [User Related Information](https://developer.zendesk.com/api-reference/ticketing/users/users/#show-user-related-information) ✓
* [User Fields](https://developer.zendesk.com/api-reference/ticketing/users/user_fields/) - Partial - List User Fields (`getUserField()`)
* [Requests](https://developer.zendesk.com/api-reference/ticketing/tickets/ticket-requests/) ✓
* [User Identities](https://developer.zendesk.com/api-reference/ticketing/users/user_identities/) ✓
* [Groups](https://developer.zendesk.com/api-reference/ticketing/groups/groups/) ✓
* [Group Membership](https://developer.zendesk.com/api-reference/ticketing/groups/group_memberships/) ✓
* [Custom Agent Roles](https://developer.zendesk.com/api-reference/ticketing/account-configuration/custom_roles/) ✓
* [Organizations](https://developer.zendesk.com/api-reference/ticketing/organizations/organizations/) ✓ *except for related info*
* [Search](https://developer.zendesk.com/api-reference/ticketing/ticket-management/search/) ✓ *except for topics and sort ordering*
* [Tags](https://developer.zendesk.com/api-reference/ticketing/ticket-management/tags/)
* [Forums](http://developer.zendesk.com/documentation/rest_api/forums.html) ✓
* [Forum Subscriptions](http://developer.zendesk.com/documentation/rest_api/forum_subscriptions.html)
* [Categories](http://developer.zendesk.com/documentation/rest_api/categories.html)
* [Topics](https://developer.zendesk.com/api-reference/help_center/help-center-api/topics/) ✓
* [Post Comments](https://developer.zendesk.com/api-reference/help_center/help-center-api/post_comments/)
* [Content Subscriptions](https://developer.zendesk.com/api-reference/help_center/help-center-api/content_subscriptions/)
* [Help Center Categories](https://developer.zendesk.com/rest_api/docs/help_center/categories) ✓
* [Help Center Sections](https://developer.zendesk.com/rest_api/docs/help_center/sections) ✓
* [Help Center Articles](https://developer.zendesk.com/api-reference/help_center/help-center-api/articles/) ✓
* [Help Center Translations](https://developer.zendesk.com/api-reference/help_center/help-center-api/translations/) - Partial (List Translations, Update Translation, Delete Translation)
* [Help Center Subscriptions](https://developer.zendesk.com/rest_api/docs/help_center/subscriptions)
* [Help Center Management Permission Groups](https://developer.zendesk.com/rest_api/docs/help_center/permission_groups)
* [Help Center User Segments](https://developer.zendesk.com/rest_api/docs/help_center/user_segments)
* [Topic Votes](https://developer.zendesk.com/api-reference/help_center/help-center-api/votes/)
* [Account Settings](https://developer.zendesk.com/api-reference/ticketing/account-configuration/account_settings/)
* [Activity Stream](https://developer.zendesk.com/api-reference/ticketing/tickets/activity_stream/)
* [Attachments](https://developer.zendesk.com/api-reference/ticketing/tickets/ticket-attachments/) ✓
* [Automations](https://developer.zendesk.com/api-reference/ticketing/business-rules/automations/) ✓
* [Job Statuses](https://developer.zendesk.com/api-reference/ticketing/ticket-management/job_statuses/) ✓
* [Locales](https://developer.zendesk.com/api-reference/ticketing/account-configuration/locales/) - Partial (List Locales)
* [Macros](https://developer.zendesk.com/api-reference/ticketing/business-rules/macros/) ✓ *except for restrictions*
* [Satisfaction Ratings](https://developer.zendesk.com/api-reference/ticketing/ticket-management/satisfaction_ratings/) ✓
* [Sharing Agreements](https://developer.zendesk.com/api-reference/ticketing/account-configuration/sharing_agreements/)
* [Suspended Tickets](https://developer.zendesk.com/api-reference/ticketing/tickets/suspended_tickets/)
* [Triggers](https://developer.zendesk.com/api-reference/ticketing/business-rules/triggers/) ✓

JDK Support
------

The current version of this project supports Java 11 and above.
It is built on Java 11 and Java 17.
The release is built using Java 11.

Latest version supporting Java 8: 0.24.3 (https://github.com/cloudbees-oss/zendesk-java-client/releases/tag/zendesk-java-client-0.24.3).

History
-------

* See [releases](https://github.com/cloudbees/zendesk-java-client/releases)

  [zd]: https://zendesk.com
