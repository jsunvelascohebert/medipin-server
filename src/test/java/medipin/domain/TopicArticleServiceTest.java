package medipin.domain;

import medipin.data.TopicArticleRepository;
import medipin.models.TopicArticle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TopicArticleServiceTest {

    @Autowired
    TopicArticleService service;

    @MockBean
    TopicArticleRepository repository;

    @Test
    void shouldFindByTopicId() {
        List<TopicArticle> topicArticles = List.of(
            new TopicArticle(1, 1),
            new TopicArticle(2, 2)
        );
        when(repository.getByTopicId(1)).thenReturn(topicArticles);
        Result<List<TopicArticle>> result = service.getByTopicId(1);
        assertTrue(result.isSuccess());
        assertEquals(result.getPayload(), topicArticles);
    }

    @Test
    void shouldSendMessageWhenNoArticlesFound() {
        when(repository.getByTopicId(1)).thenReturn(null);
        Result<List<TopicArticle>> result = service.getByTopicId(1);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("No articles found for " +
                "topic id 1"));

        when(repository.getByTopicId(2)).thenReturn(new ArrayList<>());
        result = service.getByTopicId(2);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("No articles found for " +
                "topic id 2"));
    }

    @Test
    void shouldValidateCorrectly() {
        Result<TopicArticle> result = service.add(null);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.INVALID);
        assertTrue(result.getMessages().contains("TopicArticle cannot be " +
                "null"));

        TopicArticle topicArticle = new TopicArticle(0, 0);
        result = service.add(topicArticle);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.INVALID);
        assertTrue(result.getMessages().contains("Topic id in TopicArticle " +
                "must be greater than 0"));
        assertTrue(result.getMessages().contains("Article id in TopicArticle " +
                "must be greater than 0"));
    }

    @Test
    void shouldAddValidTopicArticle() {
        TopicArticle topicArticle = new TopicArticle(1, 1);
        when(repository.add(topicArticle)).thenReturn(true);
        Result<TopicArticle> result = service.add(topicArticle);
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotAddInvalidTopicArticle() {
        TopicArticle topicArticle = new TopicArticle(100, 100);
        when(repository.add(topicArticle)).thenReturn(false);
        Result<TopicArticle> result = service.add(topicArticle);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("Could not add TopicArticle," +
                " topic id or article id not found"));
    }

    @Test
    void shouldDeleteByExistingKey() {
        when(repository.deleteByKey(1, 1)).thenReturn(true);
        Result<TopicArticle> result = service.deleteByKey(1, 1);
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotDeleteByMissingKey() {
        when(repository.deleteByKey(100, 100)).thenReturn(false);
        Result<TopicArticle> result = service.deleteByKey(100, 100);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("Could not delete " +
                "TopicArticle by key with topic id 100 and article id 100"));
    }
}