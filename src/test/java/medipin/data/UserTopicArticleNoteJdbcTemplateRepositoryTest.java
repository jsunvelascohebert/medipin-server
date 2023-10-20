package medipin.data;

import medipin.models.UserTopicArticleNote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserTopicArticleNoteJdbcTemplateRepositoryTest {

    @Autowired
    UserTopicArticleNoteJdbcTemplateRepository repository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() { knownGoodState.set(); }

    @Test
    void shouldGetByValidUserTopicArticle() {
        List<UserTopicArticleNote> utans =
                repository.getByUserTopicArticle(1, 1, 30574);
        assertEquals(utans.size(), 1);
    }

    @Test
    void shouldNotGetByInvalidUserTopicArticle() {
        List<UserTopicArticleNote> utans =
                repository.getByUserTopicArticle(100, 100, 100);
        assertEquals(utans.size(), 0);
    }

    @Test
    void shouldAddValidUtan() {
        UserTopicArticleNote utan = new UserTopicArticleNote(1, 1, 30574, 3);
        assertTrue(repository.add(utan));
    }

    @Test
    void shouldDeleteExistingUTAN() {
        assertTrue(repository.deleteByKey(1, 2, 30574, 2));
    }

    @Test
    void shouldNotDeleteMissingUTAN() {
        assertFalse(repository.deleteByKey(1, 2, 3, 4));
    }

}