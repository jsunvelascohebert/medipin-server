package medipin.data;

import medipin.models.TopicArticle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TopicArticleJdbcTemplateRepositoryTest {

    @Autowired
    TopicArticleJdbcTemplateRepository repository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() { knownGoodState.set(); }

    @Test
    void shouldGetAllByExistingTopic() {
        List<TopicArticle> topicArticles = repository.getByTopicId(1);
        assertNotNull(topicArticles);
        assertTrue(topicArticles.size() > 0);
    }

    @Test
    void shouldNotGetByMissingTopic() {
        List<TopicArticle> topicArticles = repository.getByTopicId(100);
        assertEquals(topicArticles.size(), 0);
    }

    @Test
    void shouldGetEmptyFromExistingTopicWithNoArticles() {
        List<TopicArticle> topicArticles = repository.getByTopicId(4);
        assertEquals(topicArticles.size(), 0);
    }

    @Test
    void shouldAddValidTopicArticle() {
        TopicArticle topicArticle = new TopicArticle(3, 30591);
        assertTrue(repository.add(topicArticle));
    }

    @Test
    void shouldDeleteExistingTopicArticle() {
        assertTrue(repository.deleteByKey(3, 30591));
    }

    @Test
    void shouldNotDeleteMissingTopicArticle() {
        assertFalse(repository.deleteByKey(1, 1));
    }
}