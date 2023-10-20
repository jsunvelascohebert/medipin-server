package medipin.data;

import medipin.models.UserTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserTopicJdbcTemplateRepositoryTest {

    @Autowired
    UserTopicJdbcTemplateRepository repository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() { knownGoodState.set(); }

    @Test
    void shouldGetByValidUserId() {
        List<UserTopic> userTopics = repository.getByUserId(1);
        assertNotNull(userTopics);
        assertEquals(userTopics.size(), 2);
    }

    @Test
    void shouldNotGetByMissingUserId() {
        List<UserTopic> userTopics = repository.getByUserId(100);
        assertEquals(userTopics.size(), 0);
    }

    @Test
    void shouldFindInByExistingUser() {
        List<UserTopic> userTopics = repository.getByUserId(4);
        assertEquals(userTopics.size(), 0);
    }

    @Test
    void shouldAddValidUserTopic() {
        UserTopic userTopic = new UserTopic(2, 2);
        assertTrue(repository.add(userTopic));
    }

    @Test
    void shouldDeleteExistingUserTopic() {
        assertTrue(repository.deleteByKey(2, 1));
    }

    @Test
    void shouldNotDeleteMissingUserTopic() {
        assertFalse(repository.deleteByKey(100, 100));
    }

}