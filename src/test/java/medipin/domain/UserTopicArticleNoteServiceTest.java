package medipin.domain;

import medipin.data.UserTopicArticleNoteRepository;
import medipin.models.UserTopicArticleNote;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserTopicArticleNoteServiceTest {

    @Autowired
    UserTopicArticleNoteService service;

    @MockBean
    UserTopicArticleNoteRepository repository;

    @Test
    void shouldFindByUserTopicArticleId() {
        List<UserTopicArticleNote> utans = List.of(
                new UserTopicArticleNote(1, 1, 1, 1),
                new UserTopicArticleNote(1, 2, 2, 2)
        );
        when(repository.getByUserTopicArticle(1, 1, 1)).thenReturn(utans);
        Result<List<UserTopicArticleNote>> result =
                service.getByUserTopicArticleId(1, 1, 1);
        assertTrue(result.isSuccess());
        assertEquals(result.getPayload(), utans);
    }

    @Test
    void shouldSendMessageWhenNotFound() {
        when(repository.getByUserTopicArticle(1, 1, 1)).thenReturn(null);
        Result<List<UserTopicArticleNote>> result =
                service.getByUserTopicArticleId(1, 1, 1);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("No notes found for user id " +
                "1, topic id 1, and article id 1"));

        when(repository.getByUserTopicArticle(2, 2, 2)).thenReturn(new ArrayList<>());
        result = service.getByUserTopicArticleId(2, 2, 2);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("No notes found for user id " +
                "2, topic id 2, and article id 2"));
    }

    @Test
    void shouldValidateCorrectly() {
        Result<UserTopicArticleNote> result = service.add(null);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.INVALID);
        assertTrue(result.getMessages().contains("UserTopicArticleNote cannot" +
                " be null"));

        UserTopicArticleNote utan = new UserTopicArticleNote(0, 0, 0, 0);
        result = service.add(utan);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.INVALID);
        assertTrue(result.getMessages().contains("User id in " +
                "UserTopicArticleNote must be greater than 0"));
        assertTrue(result.getMessages().contains("Topic id in " +
                "UserTopicArticleNote must be greater than 0"));
        assertTrue(result.getMessages().contains("Article id in " +
                "UserTopicArticleNote must be greater than 0"));
        assertTrue(result.getMessages().contains("Note id in " +
                "UserTopicArticleNote must be greater than 0"));
    }

    @Test
    void shouldAddValidUserTopicArticleNote() {
        UserTopicArticleNote utan = new UserTopicArticleNote(1, 1, 1, 1);
        when(repository.add(utan)).thenReturn(true);
        Result<UserTopicArticleNote> result = service.add(utan);
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotAddInvalidUserTopicArticleNote() {
        UserTopicArticleNote utan = new UserTopicArticleNote(100, 100, 100,
                100);
        when (repository.add(utan)).thenReturn(false);
        Result<UserTopicArticleNote> result = service.add(utan);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("Could not add " +
                "UserTopicArticleNote; user id, topic id, article id, or note" +
                " id not found"));
    }

    @Test
    void shouldDeleteByExistingKey() {
        when(repository.deleteByKey(1, 1, 1, 1)).thenReturn(true);
        Result<UserTopicArticleNote> result = service.deleteByKey(1, 1, 1, 1);
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotDeleteByMissingKey() {
        when(repository.deleteByKey(100, 100, 100, 100)).thenReturn(false);
        Result<UserTopicArticleNote> result = service.deleteByKey(100, 100,
                100, 100);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("Could not delete " +
                "UserTopicArticleNote by key with user id 100, topic id 100, " +
                "article id 100, and note id 100"));
    }

}