package org.zendesk.client.v2;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.zendesk.client.v2.model.SearchResultPage;
import org.zendesk.client.v2.model.Ticket;
import org.zendesk.client.v2.model.request.page.SearchPageableRequest;
import org.zendesk.client.v2.model.request.page.TicketPageableRequest;
import org.zendesk.client.v2.model.sort.SortOrder;
import org.zendesk.client.v2.model.sort.search.SearchSort;
import org.zendesk.client.v2.model.sort.search.SearchSortableField;
import org.zendesk.client.v2.model.sort.ticket.TicketSort;
import org.zendesk.client.v2.model.sort.ticket.TicketSortableField;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class ZendeskTest {
	private final static String API_TOKEN = "token";
	private final static String USERNAME = "user@company.org";

	@Rule
	public WireMockRule wireMockRule = new WireMockRule();

	private Zendesk client;

	@Before
	public void before() {
		client = new Zendesk.Builder("http://localhost:8080").setUsername(USERNAME).setToken(API_TOKEN).build();
	}

	@After
	public void after() {
		if (client != null) {
			client.close();
		}
		client = null;
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetTicketsTicketSort_whenSortIsNull_thenIllegalArgumentException() {
		client.getTickets(null);
	}

	@Test
	public void testGetTicketsTicketSort_whenSortPresent_thenSortQueryParamsPresent() {
		String responseJson = "{\"tickets\":[{\"id\":0,\"subject\":\"ticket0\"},{\"id\":1,\"subject\":\"ticket1\"}],\"next_page\":\"http://localhost:8080/api/v2/tickets.json?page=2&sort_by=id&sort_order=asc\",\"previous_page\":null,\"count\":79686}";
		wireMockRule.stubFor(get(urlPathEqualTo("/api/v2/tickets.json")).withQueryParam("sort_by", equalTo("id"))
				.withQueryParam("sort_order", equalTo("asc")).willReturn(ok().withBody(responseJson)));
		TicketSort sort = new TicketSort(TicketSortableField.ID, SortOrder.ASC);
		Iterable<Ticket> iterable = client.getTickets(sort);
		Iterator<Ticket> iterator = iterable.iterator();
		assertTrue(iterator.hasNext());
		assertEquals("ticket0", iterator.next().getSubject());
		assertEquals("ticket1", iterator.next().getSubject());

		responseJson = "{\"tickets\":[{\"id\":1,\"subject\":\"ticket1\"},{\"id\":2,\"subject\":\"ticket2\"}],\"next_page\":\"http://localhost:8080/api/v2/tickets.json?page=2&sort_by=requester&sort_order=desc\",\"previous_page\":null,\"count\":79686}";
		wireMockRule.stubFor(get(urlPathEqualTo("/api/v2/tickets.json")).withQueryParam("sort_by", equalTo("requester"))
				.withQueryParam("sort_order", equalTo("desc")).willReturn(ok().withBody(responseJson)));
		sort = new TicketSort(TicketSortableField.REQUESTER, SortOrder.DESC);
		iterable = client.getTickets(sort);
		iterator = iterable.iterator();
		assertTrue(iterator.hasNext());
		assertEquals("ticket1", iterator.next().getSubject());
		assertEquals("ticket2", iterator.next().getSubject());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetTicketsPage_whenPageableRequestNull_thenIllegalArgumentException() {
		client.getTicketsPage(null);
	}

	@Test
	public void testGetTicketsPage_whenPageableRequestPresent_thenOk() {
		String responseJson = "{\"tickets\":[{\"id\":0,\"subject\":\"ticket0\"},{\"id\":1,\"subject\":\"ticket1\"}],\"next_page\":\"http://localhost:8080/api/v2/tickets.json?page=3&per_page=5&sort_by=id&sort_order=asc\",\"previous_page\":\"http://localhost:8080/api/v2/tickets.json?page=1&per_page=5&sort_by=id&sort_order=asc\",\"count\":79686}";
		wireMockRule.stubFor(get(urlPathEqualTo("/api/v2/tickets.json")).withQueryParam("page", equalTo("2"))
				.withQueryParam("per_page", equalTo("5")).withQueryParam("sort_by", equalTo("id"))
				.withQueryParam("sort_order", equalTo("asc")).willReturn(ok().withBody(responseJson)));
		TicketSort sort = new TicketSort(TicketSortableField.ID, SortOrder.ASC);
		TicketPageableRequest request = new TicketPageableRequest(2, 5, sort);
		SearchResultPage<Ticket> resultPage = client.getTicketsPage(request);
		assertNotNull(resultPage);
		assertEquals(79686, resultPage.getCount());
		assertEquals("http://localhost:8080/api/v2/tickets.json?page=1&per_page=5&sort_by=id&sort_order=asc",
				resultPage.getPrevPage());
		assertEquals("http://localhost:8080/api/v2/tickets.json?page=3&per_page=5&sort_by=id&sort_order=asc",
				resultPage.getNextPage());
		assertEquals(2, resultPage.getResults().size());
		assertEquals("ticket0", resultPage.getResults().get(0).getSubject());
		assertEquals("ticket1", resultPage.getResults().get(1).getSubject());
	}


	@Test(expected=IllegalArgumentException.class)
	public void testGetSortedSearchResults_whenSortNull_thenIllegalARgumentException() {
		client.getSortedSearchResults(Ticket.class, "via:api", null);
	}
	
	@Test
	public void testGetSortedSearchResults_whenSortNotNull_thenOk() {
		String responseJson = "{\"results\":[{\"id\":0,\"subject\":\"ticket0\"},{\"id\":1,\"subject\":\"ticket1\"}],\"next_page\":\"http://localhost:8080/api/v2/tickets.json?query=via:api+type:ticket&page=2&sort_by=status&sort_order=asc\",\"previous_page\":null,\"count\":79686}";
		wireMockRule.stubFor(get(urlPathEqualTo("/api/v2/search.json")).withQueryParam("sort_by", equalTo("status"))
				.withQueryParam("sort_order", equalTo("asc"))
				.withQueryParam("query", equalTo("via:api type:ticket")).willReturn(ok().withBody(responseJson)));
		
		SearchSort sort = new SearchSort(SearchSortableField.STATUS, SortOrder.ASC);
		Iterable<Ticket> iterable = client.getSortedSearchResults(Ticket.class, "via:api", sort);
		Iterator<Ticket> iterator = iterable.iterator();
		assertTrue(iterator.hasNext());
		assertEquals("ticket0", iterator.next().getSubject());
		assertEquals("ticket1", iterator.next().getSubject());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetSearchResultsPage_whenRequestNull_thenIllegalArgumentException() {
		client.getSearchResultsPage(Ticket.class, "via:api", null);
	}
	@Test
	public void testGetSearchResultsPage_whenRequestWithSortOnly_thenOk() {
		String responseJson = "{\"results\":[{\"id\":0,\"subject\":\"ticket0\"},{\"id\":1,\"subject\":\"ticket1\"}],\"next_page\":\"http://localhost:8080/api/v2/tickets.json?query=via:api+type:ticket&page=2&sort_by=status&sort_order=asc\",\"previous_page\":null,\"count\":111}";
		wireMockRule.stubFor(get(urlPathEqualTo("/api/v2/search.json")).withQueryParam("sort_by", equalTo("status"))
				.withQueryParam("sort_order", equalTo("asc"))
				.withQueryParam("query", equalTo("via:api type:ticket")).willReturn(ok().withBody(responseJson)));
		
		SearchSort sort = new SearchSort(SearchSortableField.STATUS, SortOrder.ASC);
		SearchPageableRequest request = new SearchPageableRequest(sort);
		SearchResultPage<Ticket> searchResultPage = client.getSearchResultsPage(Ticket.class, "via:api", request);
		assertNotNull(searchResultPage);
		assertEquals(111, searchResultPage.getCount());
		assertEquals(2, searchResultPage.getResults().size());
		assertEquals("http://localhost:8080/api/v2/tickets.json?query=via:api+type:ticket&page=2&sort_by=status&sort_order=asc", searchResultPage.getNextPage());
	}
	
	@Test
	public void testGetSearchResultsPage_whenRequestWithSortAndPage_thenOk() {
		String responseJson = "{\"results\":[{\"id\":0,\"subject\":\"ticket0\"},{\"id\":1,\"subject\":\"ticket1\"}],\"next_page\":\"http://localhost:8080/api/v2/tickets.json?query=via:api+type:ticket&page=3&per_page=5&sort_by=status&sort_order=asc\",\"previous_page\":\"http://localhost:8080/api/v2/tickets.json?query=via:api+type:ticket&page=1&per_page=5&sort_by=status&sort_order=asc\",\"count\":222}";
		wireMockRule.stubFor(get(urlPathEqualTo("/api/v2/search.json")).withQueryParam("sort_by", equalTo("status"))
				.withQueryParam("sort_order", equalTo("asc"))
				.withQueryParam("page", equalTo("2")).withQueryParam("per_page", equalTo("5"))
				.withQueryParam("query", equalTo("via:api type:ticket")).willReturn(ok().withBody(responseJson)));
		
		SearchSort sort = new SearchSort(SearchSortableField.STATUS, SortOrder.ASC);
		SearchPageableRequest request = new SearchPageableRequest(2, 5, sort);
		SearchResultPage<Ticket> searchResultPage = client.getSearchResultsPage(Ticket.class, "via:api", request);
		assertNotNull(searchResultPage);
		assertEquals(222, searchResultPage.getCount());
		assertEquals(2, searchResultPage.getResults().size());
		assertEquals("http://localhost:8080/api/v2/tickets.json?query=via:api+type:ticket&page=3&per_page=5&sort_by=status&sort_order=asc", searchResultPage.getNextPage());
		assertEquals("http://localhost:8080/api/v2/tickets.json?query=via:api+type:ticket&page=1&per_page=5&sort_by=status&sort_order=asc", searchResultPage.getPrevPage());
	
	}
	

}
