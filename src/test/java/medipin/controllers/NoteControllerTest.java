package medipin.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import medipin.data.NoteRepository;
import medipin.models.Note;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig
@AutoConfigureMockMvc
@SpringBootTest
@Import(TestConfig.class)
class NoteControllerTest {

    @MockBean
    NoteRepository repository;

    @Autowired
    MockMvc mvc;

    List<Note> notes = List.of(
            new Note(1, "note 1", LocalDateTime.parse("2023-07-23T12:34:56")),
            new Note(2, "note 2", LocalDateTime.parse("2023-07-23T12:34:56")),
            new Note(3, "note 3", LocalDateTime.parse("2023-07-23T12:34:56"))
    );

    /* ***** ***** getAll and getById tests ***** ***** */

    @Test
    void getAllShouldReturn200() throws Exception {
        when(repository.getAll()).thenReturn(notes);
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());

        String expected = jsonMapper.writeValueAsString(notes);

        mvc.perform(get("/api/note"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expected, true));
    }

    @Test
    void getAllShouldReturn404() throws Exception {
        when(repository.getAll()).thenReturn(null);
        mvc.perform(get("/api/note"))
                .andExpect(status().isNotFound());

        when(repository.getAll()).thenReturn(new ArrayList<>());
        mvc.perform(get("/api/note"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByIdShouldReturn200() throws Exception {
        Note expectedNote = new Note(1, "note 1", LocalDateTime.parse("2023" +
                "-07-23T12:34:56"));

        when(repository.getById(1)).thenReturn(expectedNote);
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());

        String expected = jsonMapper.writeValueAsString(expectedNote);

        mvc.perform(get("/api/note/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expected));
    }

    @Test
    void getByIdShouldReturn404() throws Exception {
        when(repository.getById(1)).thenReturn(null);
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());

        mvc.perform(get("/api/note/1"))
                .andExpect(status().isNotFound());
    }

    /* ***** ***** validation and add tests ***** ***** */

    @Test
    void validationCatchesShouldReturn400() throws Exception {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());

        var request = post("/api/note")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request).andExpect(status().isBadRequest());

        Note note = new Note(0, null, null);
        String jsonIn = jsonMapper.writeValueAsString(note);

        request = post("/api/note")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn);

        mvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    void successfulAddShouldReturn201() throws Exception {
        Note noteIn = new Note(0, "test", LocalDateTime.parse("2023" +
                "-07-23T12:34:56"));
        Note noteOut = new Note(1, "test", LocalDateTime.parse("2023" +
                "-07-23T12:34:56"));

        when(repository.add(noteIn)).thenReturn(noteOut);

        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());

        String jsonIn = jsonMapper.writeValueAsString(noteIn);
        String jsonOut = jsonMapper.writeValueAsString(noteOut);

        var request = post("/api/note")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonOut));
    }

    @Test
    void invalidAddShouldReturn400() throws Exception {
        Note noteIn = new Note(1, "test", LocalDateTime.parse("2023" +
                "-07-23T12:34:56"));

        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());

        String json = jsonMapper.writeValueAsString(noteIn);

        var request = post("/api/note")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isBadRequest());
    }

    /* ***** ***** update tests ***** ***** */

    @Test
    void successfulUpdateShouldReturn204() throws Exception {
        Note noteIn = new Note(1, "test", LocalDateTime.parse("2023" +
                "-07-23T12:34:56"));
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());

        String json = jsonMapper.writeValueAsString(noteIn);
        when(repository.update(noteIn)).thenReturn(true);

        var request = put("/api/note/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isNoContent());
    }

    @Test
    void conflictedUpdateShouldReturn409() throws Exception {
        Note noteIn = new Note(1, "test", LocalDateTime.parse("2023" +
                "-07-23T12:34:56"));
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());

        String json = jsonMapper.writeValueAsString(noteIn);

        var request = put("/api/note/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isConflict());
    }

    @Test
    void invalidUpdateShouldReturn401() throws Exception {
        Note noteIn = new Note(0, "test", LocalDateTime.parse("2023" +
                "-07-23T12:34:56"));
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());

        String json = jsonMapper.writeValueAsString(noteIn);

        var request = put("/api/note/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    void invalidUpdateShouldReturn404() throws Exception {
        Note noteIn = new Note(1, "test", LocalDateTime.parse("2023" +
                "-07-23T12:34:56"));
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());

        String json = jsonMapper.writeValueAsString(noteIn);

        when(repository.update(noteIn)).thenReturn(false);
        var request = put("/api/note/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isNotFound());
    }

    /* ***** ***** deleteById tests ***** ***** */

    @Test
    void successfulDeleteShouldReturn204() throws Exception {
        when(repository.deleteByID(1)).thenReturn(true);
        var request = delete("/api/note/1");
        mvc.perform(request).andExpect(status().isNoContent());
    }

    @Test
    void failedDeleteAttachedToUTANShouldReturn400() throws Exception {
        when(repository.isAttachedToUserTopicArticleNote(1)).thenReturn(true);
        when(repository.deleteByID(1)).thenReturn(false);
        var request = delete("/api/note/1");
        mvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    void failedDeleteShouldReturn404() throws Exception {
        when(repository.deleteByID(100)).thenReturn(false);
        var request = delete("/api/note/100");
        mvc.perform(request).andExpect(status().isNotFound());
    }
}