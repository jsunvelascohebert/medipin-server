package medipin.domain;

import medipin.models.Topic;
import medipin.data.TopicRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TopicServiceTest {

    @Autowired
    TopicService service;

    @MockBean
    TopicRepository repository;

    /* ***** ***** getAll and getId tests ***** ***** */

    @Test
    void shouldGetAllTopics() {
        List<Topic> topics = List.of(
            new Topic(1, "Personal"),
            new Topic(2, "Partner"),
            new Topic(3, "Friend")
        );
        when(repository.getAll()).thenReturn(topics);

        Result<List<Topic>> result = service.getAll();
        assertTrue(result.isSuccess());
        assertEquals(result.getPayload(), topics);
    }

    @Test
    void shouldReturnMessageWhenNoTopicsFound() {
        when(repository.getAll()).thenReturn(null);
        Result<List<Topic>> topics = service.getAll();
        assertFalse(topics.isSuccess());
        assertEquals(topics.getType(), ResultType.NOT_FOUND);
        assertTrue(topics.getMessages().contains("No topics found"));

        when(repository.getAll()).thenReturn(new ArrayList<>());
        topics = service.getAll();
        assertFalse(topics.isSuccess());
        assertEquals(topics.getType(), ResultType.NOT_FOUND);
        assertTrue(topics.getMessages().contains("No topics found"));
    }

    @Test
    void shouldGetByValidTopicId() {
        Topic topic = new Topic(1, "Personal");
        when(repository.getById(1)).thenReturn(topic);

        Result<Topic> result = service.getById(1);
        assertTrue(result.isSuccess());
        assertEquals(result.getPayload(), topic);
    }

    @Test
    void shouldNotGetByInvalidTopicId() {
        when(repository.getById(100)).thenReturn(null);

        Result<Topic> result = service.getById(100);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("Could not find topic with " +
                "id 100"));
    }

    /* ***** ***** validate and add tests ***** ***** */

    @Test
    void shouldAddValidTopic() {
        Topic topic = new Topic(0, "testing add");
        Topic expected = new Topic(1, "testing add");
        when(repository.add(topic)).thenReturn(expected);

        Result<Topic> result = service.add(topic);
        assertTrue(result.isSuccess());
        assertEquals(result.getPayload(), expected);
    }

    @Test // also tests for validations
    void shouldNotAddInvalidTopic() {
        // null case
        when(repository.add(null)).thenReturn(null);
        Result<Topic> result = service.add(null);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.INVALID);
        assertTrue(result.getMessages().contains("Topic cannot be null"));

        // not zero case
        Topic topic = new Topic(1, "testing invalid add");
        result = service.add(topic);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.INVALID);
        assertTrue(result.getMessages().contains("Cannot add topic without " +
                "id of 0"));

        // empty name case
        topic = new Topic(0, "");
        result = service.add(topic);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.INVALID);
        assertTrue(result.getMessages().contains("Topic name cannot be blank"));
    }

    /* ***** ***** update tests ***** ***** */

    @Test
    void shouldUpdateValidTopic() {
        Topic updated = new Topic(1, "valid update");
        when(repository.update(updated)).thenReturn(true);

        Result<Topic> result = service.update(updated);
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotUpdateInvalidTopic() {
        // invalid id
        Topic updated = new Topic(-1, "invalid id");
        Result<Topic> result = service.update(updated);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.INVALID);
        assertTrue(result.getMessages().contains("Topic id must be set for " +
                "updating"));

        // missing topic
        updated = new Topic(1, "missing id");
        when(repository.update(updated)).thenReturn(false);
        result = service.update(updated);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("Could not update missing " +
                "topic with id 1"));
    }

    /* ***** ***** delete tests ***** ***** */

    @Test
    void shouldNotDeleteByExistingAttachedId() {
        when(repository.isAttachedToTopicArticle(1)).thenReturn(true);
        when(repository.isAttachedToUserTopic(1)).thenReturn(true);
        when(repository.isAttachedToUTAN(1)).thenReturn(true);

        Result<Topic> result = service.deleteById(1);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.INVALID);
    }

    @Test
    void shouldDeleteExistingUnattachedTopic() {
        when(repository.isAttachedToUTAN(1)).thenReturn(false);
        when(repository.isAttachedToUserTopic(1)).thenReturn(false);
        when(repository.isAttachedToTopicArticle(1)).thenReturn(false);
        when(repository.deleteById(1)).thenReturn(true);

        Result<Topic> result = service.deleteById(1);
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotDeleteByMissingId() {
        when(repository.deleteById(100)).thenReturn(false);
        Result<Topic> result = service.deleteById(100);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("Topic id 100 not found, " +
                "cannot delete"));
    }
}