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

Community
-------------

* [Users list](https://groups.google.com/forum/#!forum/zendesk-java-client-users)
* [GitHub discussions](https://github.com/cloudbees-oss/zendesk-java-client/discussions)

Status
------

Here is the status of the various API components:

* [Tickets](http://developer.zendesk.com/documentation/rest_api/tickets.html) ✓
* [Ticket Audits](http://developer.zendesk.com/documentation/rest_api/ticket_audits.html) ✓
* [Incremental Export](https://developer.zendesk.com/rest_api/docs/core/incremental_export) - Partial (tickets, users, organizations only) 
* [Ticket Fields](http://developer.zendesk.com/documentation/rest_api/ticket_fields.html) ✓
* [Ticket Import](http://developer.zendesk.com/documentation/rest_api/ticket_import.html) ✓
* [Ticket Metrics](http://developer.zendesk.com/documentation/rest_api/ticket_metrics.html) ✓
* [Ticket Forms](http://developer.zendesk.com/documentation/rest_api/ticket_forms.html) *getTicketForm() and getTicketForms()*
* [Views](http://developer.zendesk.com/documentation/rest_api/views.html)
* [Users](http://developer.zendesk.com/documentation/rest_api/users.html) ✓
    * [User Related Information](https://developer.zendesk.com/rest_api/docs/core/users#user-related-information) ✓
* [User Fields](https://developer.zendesk.com/rest_api/docs/core/user_fields.html) - Partial - List User Fields (`getUserField()`)
* [Requests](http://developer.zendesk.com/documentation/rest_api/requests.html) ✓
* [User Identities](http://developer.zendesk.com/documentation/rest_api/user_identities.html) ✓
* [Groups](http://developer.zendesk.com/documentation/rest_api/groups.html) ✓
* [Group Membership](http://developer.zendesk.com/documentation/rest_api/group_memberships.html) ✓
* [Custom Agent Roles](http://developer.zendesk.com/documentation/rest_api/custom_roles.html) ✓
* [Organizations](http://developer.zendesk.com/documentation/rest_api/organizations.html) ✓ *except for related info*
* [Search](http://developer.zendesk.com/documentation/rest_api/search.html) ✓ *except for topics and sort ordering*
* [Tags](http://developer.zendesk.com/documentation/rest_api/tags.html)
* [Forums](http://developer.zendesk.com/documentation/rest_api/forums.html) ✓
* [Forum Subscriptions](http://developer.zendesk.com/documentation/rest_api/forum_subscriptions.html)
* [Categories](http://developer.zendesk.com/documentation/rest_api/categories.html)
* [Topics](http://developer.zendesk.com/documentation/rest_api/topics.html) ✓
* [Topic Comments](http://developer.zendesk.com/documentation/rest_api/topic_comments.html)
* [Topic Subscriptions](http://developer.zendesk.com/documentation/rest_api/topic_subscriptions.html)
* [Help Center Categories](https://developer.zendesk.com/rest_api/docs/help_center/categories) ✓
* [Help Center Sections](https://developer.zendesk.com/rest_api/docs/help_center/sections) ✓
* [Help Center Articles](https://developer.zendesk.com/rest_api/docs/help_center/articles) ✓
* [Help Center Translations](https://developer.zendesk.com/rest_api/docs/help_center/translations) - Partial (List Translations, Update Translation, Delete Translation)
* [Help Center Subscriptions](https://developer.zendesk.com/rest_api/docs/help_center/subscriptions)
* [Help Center Management Permission Groups](https://developer.zendesk.com/rest_api/docs/help_center/permission_groups)
* [Help Center User Segments](https://developer.zendesk.com/rest_api/docs/help_center/user_segments)
* [Topic Votes](http://developer.zendesk.com/documentation/rest_api/topic_votes.html)
* [Account Settings](http://developer.zendesk.com/documentation/rest_api/account_settings.html)
* [Activity Stream](http://developer.zendesk.com/documentation/rest_api/activity_stream.html)
* [Attachments](http://developer.zendesk.com/documentation/rest_api/attachments.html) ✓
* [Autocompletion](http://developer.zendesk.com/documentation/rest_api/autocomplete.html)
* [Automations](http://developer.zendesk.com/documentation/rest_api/automations.html) ✓
* [Job Statuses](http://developer.zendesk.com/documentation/rest_api/job_statuses.html)
* [Locales](http://developer.zendesk.com/documentation/rest_api/locales.html)
* [Macros](http://developer.zendesk.com/documentation/rest_api/macros.html) ✓ *except for restrictions*
* [Restrictions and Responsibilities](http://developer.zendesk.com/documentation/rest_api/restrictions.html)
* [Satisfaction Ratings](http://developer.zendesk.com/documentation/rest_api/satisfaction_ratings.html) ✓
* [Sharing Agreements](http://developer.zendesk.com/documentation/rest_api/sharing_agreements.html)
* [Suspended Tickets](http://developer.zendesk.com/documentation/rest_api/suspended_tickets.html)
* [Triggers](http://developer.zendesk.com/documentation/rest_api/triggers.html) ✓

History
-------

* See [releases](https://github.com/cloudbees/zendesk-java-client/releases)

  [zd]: http://zendesk.com
