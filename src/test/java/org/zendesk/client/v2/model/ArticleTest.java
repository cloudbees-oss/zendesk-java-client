package org.zendesk.client.v2.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.zendesk.client.v2.model.hc.Article;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ArticleTest {

    private Article parseJson(byte[] json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, Article.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Test
    public void testParseArticle() {
        String json = "{" +
                "\"id\":918273645013," +
                "\"url\":\"https://example.zendesk.com/api/v2/help_center/en-us/articles/918273645013-Welcome-to-your-Help-Center-.json\"," +
                "\"html_url\":\"https://example.zendesk.com/hc/en-us/articles/918273645013-Welcome-to-your-Help-Center-\"," +
                "\"author_id\":2314596780," +
                "\"comments_disabled\":false," +
                "\"draft\":false," +
                "\"promoted\":false," +
                "\"position\":0," +
                "\"vote_sum\":0," +
                "\"vote_count\":0," +
                "\"section_id\":123456789," +
                "\"created_at\":\"2019-06-10T12:39:23Z\"," +
                "\"updated_at\":\"2019-06-10T12:39:23Z\"," +
                "\"title\":\"Welcome to your Help Center!\"," +
                "\"source_locale\":\"en-us\"," +
                "\"locale\":\"en-us\"," +
                "\"outdated\":false," +
                "\"outdated_locales\":[]," +
                "\"edited_at\":\"2019-06-10T12:39:23Z\"," +
                "\"user_segment_id\":null," +
                "\"permission_group_id\":2739912," +
                "\"label_names\":[]," +
                "\"body\":\"This is a test\"}";
        Article article = parseJson(json.getBytes());
        assertNotNull(article);
        assertEquals(Article.class, article.getClass());
        assertEquals((Long) 918273645013L, article.getId());
        assertEquals("https://example.zendesk.com/api/v2/help_center/en-us/articles/918273645013-Welcome-to-your-Help-Center-.json", article.getUrl());
        assertEquals("https://example.zendesk.com/hc/en-us/articles/918273645013-Welcome-to-your-Help-Center-", article.getHtmlUrl());
        assertEquals((Long) 2314596780L, article.getAuthorId());
        assertEquals(false, article.getCommentsDisabled());
        assertEquals(false, article.getDraft());
        assertEquals(false, article.getPromoted());
        assertEquals((Long) 0L, article.getPosition());
        assertEquals((Long) 0L, article.getVoteSum());
        assertEquals((Long) 0L, article.getVoteCount());
        assertEquals(new Date(1560170363000L), article.getCreatedAt());
        assertEquals(new Date(1560170363000L), article.getUpdatedAt());
        assertEquals("Welcome to your Help Center!", article.getTitle());
        assertEquals("en-us", article.getSourceLocale());
        assertEquals("en-us", article.getLocale());
        assertEquals(false, article.getOutdated());
        assertEquals(new ArrayList<>(), article.getOutdatedLocales());
        assertEquals(new Date(1560170363000L), article.getEditedAt());
        assertNull(article.getUserSegmentId());
        assertEquals((Long) 2739912L, article.getPermissionGroupId());
        assertEquals(new ArrayList<>(), article.getLabelNames());
        assertEquals("This is a test", article.getBody());
    }
}
