package medipin.domain;

import medipin.data.NoteRepository;
import medipin.models.Note;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class NoteServiceTest {

    @Autowired
    NoteService service;

    @MockBean
    NoteRepository repository;

    /* ***** ***** getAll and getId tests ***** ***** */

    @Test
    void shouldGetAllNotes() {
        List<Note> notes = List.of(
            new Note(1, "note 1", LocalDateTime.parse("2023-07-23T12:34:56")),
            new Note(2, "note 2", LocalDateTime.parse("2023-07-23T12:34:56"))
        );

        when(repository.getAll()).thenReturn(notes);
        Result<List<Note>> result = service.getAll();
        assertTrue(result.isSuccess());
        assertEquals(result.getPayload(), notes);
    }

    @Test
    void shouldReturnMessageWhenNoNotesFound() {
        when(repository.getAll()).thenReturn(null);
        Result<List<Note>> result = service.getAll();
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("No notes found"));

        when(repository.getAll()).thenReturn(new ArrayList<>());
        result = service.getAll();
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("No notes found"));
    }

    @Test
    void shouldGetByValidId() {
        Note note = new Note(1, "note 1",
                LocalDateTime.parse("2023-07-23T12:34:56"));
        when(repository.getById(1)).thenReturn(note);

        Result<Note> result = service.getById(1);
        assertTrue(result.isSuccess());
        assertEquals(result.getPayload(), note);
    }

    @Test
    void shouldNotGetByInvalidId() {
        when(repository.getById(100)).thenReturn(null);
        Result<Note> result = service.getById(100);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("Could not find note with " +
                "id 100"));
    }

    /* ***** ***** validate and add tests ***** ***** */

    @Test
    void shouldValidateCorrectly() {
        Result<Note> result = service.add(null);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.INVALID);
        assertTrue(result.getMessages().contains("Note cannot be null"));

        Note invalid = new Note(0, null, null);
        result = service.add(invalid);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.INVALID);
        assertTrue(result.getMessages().contains("Note text cannot be blank"));
        assertTrue(result.getMessages().contains("Datetime made of note " +
                "cannot be null"));

        invalid = new Note(0, "", null);
        result = service.add(invalid);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.INVALID);
        assertTrue(result.getMessages().contains("Note text cannot be blank"));
        assertTrue(result.getMessages().contains("Datetime made of note " +
                "cannot be null"));
    }

    @Test
    void shouldAddValidNote() {
        Note note = new Note(0, "note 1",
                LocalDateTime.parse("2023-07-23T12:34:56"));
        Note expected = new Note(1, "note 1",
                LocalDateTime.parse("2023-07-23T12:34:56"));
        when(repository.add(note)).thenReturn(expected);

        Result<Note> result = service.add(note);
        assertTrue(result.isSuccess());
        assertEquals(result.getPayload(), expected);
    }

    @Test
    void shouldNotAddInvalidNote() {
        Note note = new Note(1, "note 1",
                LocalDateTime.parse("2023-07-23T12:34:56"));
        Result<Note> result = service.add(note);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.INVALID);
        assertTrue(result.getMessages().contains("Cannot add note without id " +
                "of 0"));
    }

    /* ***** ***** update tests ***** ***** */

    @Test
    void shouldUpdateValidNote() {
        Note note = new Note(1, "note 1",
                LocalDateTime.parse("2023-07-23T12:34:56"));
        when(repository.update(note)).thenReturn(true);
        Result<Note> result = service.update(note);
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotUpdateInvalidNote() {
        // invalid id
        Note note = new Note(-1, "note 1",
                LocalDateTime.parse("2023-07-23T12:34:56"));
        Result<Note> result = service.update(note);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.INVALID);
        assertTrue(result.getMessages().contains("Note id must be set for " +
                "updating"));

        // missing note
        note = new Note(100, "note 1",
                LocalDateTime.parse("2023-07-23T12:34:56"));
        when(repository.update(note)).thenReturn(false);
        result = service.update(note);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("Could not update missing " +
                "note with id 100"));
    }

    /* ***** ***** delete tests ***** ***** */

    @Test
    void shouldDeleteValidId() {
        when(repository.deleteByID(1)).thenReturn(true);
        Result<Note> result = service.deleteById(1);
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotDeleteExistingAttachedToUTAN() {
        when(repository.isAttachedToUserTopicArticleNote(1)).thenReturn(true);
        when(repository.deleteByID(1)).thenReturn(false);
        Result<Note> result = service.deleteById(1);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.INVALID);
        assertTrue(result.getMessages().contains("Cannot delete Note with id " +
                "1 attached to a UserTopicArticleNote"));
    }

    @Test
    void shouldNotDeleteMissingId() {
        when(repository.deleteByID(100)).thenReturn(false);
        Result<Note> result = service.deleteById(100);
        assertFalse(result.isSuccess());
        assertEquals(result.getType(), ResultType.NOT_FOUND);
        assertTrue(result.getMessages().contains("Note id 100 not found, " +
                "cannot delete"));
    }
}