package medipin.domain;

import medipin.data.UserTopicRepository;
import medipin.models.User;
import medipin.models.UserTopic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserTopicServiceTest {

    @Autowired
    UserTopicService service;

    @MockBean
    UserTopicRepository repository;

    @Test
    void shouldFindByUserId() {
        List<UserTopic> userTopics = List.of(
          new UserTopic(1, 1),
          new UserTopic(1, 2),
          new UserTopic(1, 3)
        );
        when(repository.getByUserId(1)).thenReturn(userTopics);
        Result<List<UserTopic>> result = service.getByUserId(1);
        assertTrue(result.isSuccess());
        assertEquals(userTopics, result.getPayload());
    }

    @Test
    void shouldSendMessageWhenNoUserTopicsFound() {
        when(repository.getByUserId(1)).thenReturn(null);
        Result<List<UserTopic>> result = service.getByUserId(1);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("No topics found for user id 1"));

        when(repository.getByUserId(2)).thenReturn(new ArrayList<>());
        result = service.getByUserId(2);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("No topics found for user id 2"));
    }

    @Test
    void shouldValidateCorrectly() {
        Result<UserTopic> result = service.add(null);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.INVALID);
        assertTrue(result.getMessages().contains("UserTopic cannot be null"));

        UserTopic userTopic = new UserTopic(0, 0);
        result = service.add(userTopic);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.INVALID);
        assertTrue(result.getMessages().contains("User id in UserTopic must " +
                "be greater than 0"));
        assertTrue(result.getMessages().contains("Topic id in UserTopic must " +
                "be greater than 0"));
    }

    @Test
    void shouldAddValidUserTopic() {
        UserTopic userTopic = new UserTopic(1, 1);
        when(repository.add(userTopic)).thenReturn(true);
        Result<UserTopic> result = service.add(userTopic);
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotAddInvalidUserTopic() {
        UserTopic userTopic = new UserTopic(100, 100);
        when(repository.add(userTopic)).thenReturn(false);
        Result<UserTopic> result = service.add(userTopic);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("Could not add UserTopic, " +
                "user id or topic id not found"));
    }

    @Test
    void shouldDeleteByExistingKey() {
        when(repository.deleteByKey(1, 1)).thenReturn(true);
        Result<UserTopic> result = service.deleteByKey(1, 1);
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotDeleteByMissingKey() {
        when(repository.deleteByKey(2, 2)).thenReturn(false);
        Result<UserTopic> result = service.deleteByKey(2, 2);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("Could not delete UserTopic " +
                "by key with user id 2 and topic id 2"));
    }

}