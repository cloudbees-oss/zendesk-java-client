ZenDesk Java Client
===================

This is a ZenDesk client implementation written in Java using AsyncHttpClient and Jackson.

Using the API
-------------

Start by creating a `ZenDesk` instance

    ZenDesk zd = new ZenDesk.Builder("https://{{your domain}}.zendesk.com")
            .setUsername("...")
            .setToken("...") // or .setPassword("...")
            .build();

If you are behind a proxy, or want to otherwise control the lifecycle of the `AsyncHttpClient` instance
you should pass that through to the builder too. If you don't pass an `AsyncHttpClient` instance to the builder
it will create its own which will be closed by the `ZenDesk.close()` method.

Where methods return paged data sets, an `Iterable` is returned that will lazy-fetch one page at a time until
all records have been fetched, so e.g.

    for (Ticket ticket: zd.getTickets()) {
        ...
    }

will iterate through *all* tickets. Most likely you will want to implement your own cut-off process to stop iterating
when you have got enough data.