package medipin.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import medipin.data.UserTopicArticleNoteRepository;
import medipin.models.UserTopicArticleNote;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;

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
class UserTopicArticleNoteControllerTest {

    @MockBean
    UserTopicArticleNoteRepository repository;

    @Autowired
    MockMvc mvc;

    /* ***** ***** getByUserTopicArticleId tests ***** ***** */

    @Test
    void getAllByIdShouldReturn200() throws Exception {
        List<UserTopicArticleNote> utans = List.of(
            new UserTopicArticleNote(1, 1, 1, 1),
            new UserTopicArticleNote(1, 1, 1, 2)
        );

        when(repository.getByUserTopicArticle(1, 1, 1)).thenReturn(utans);
        ObjectMapper jsonMapper = new ObjectMapper();

        String json = jsonMapper.writeValueAsString(utans);

        mvc.perform(get("/api/user/topic/article/note/1/1/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(json));
    }

    @Test
    void getAllByIdShouldReturn404() throws Exception {
        when(repository.getByUserTopicArticle(1, 1, 1)).thenReturn(null);
        mvc.perform(get("/api/user/topic/article/note/1/1/1"))
            .andExpect(status().isNotFound());

        when(repository.getByUserTopicArticle(1, 1, 1)).thenReturn(new ArrayList<>());
        mvc.perform(get("/api/user/topic/article/note/1/1/1"))
            .andExpect(status().isNotFound());
    }

    /* ***** **** validation and add tests ***** ***** */

    @Test
    void validationShouldReturn400() throws Exception {
        ObjectMapper jsonMapper = new ObjectMapper();

        var request = post("/api/user/topic/article/note")
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest());

        UserTopicArticleNote utan = new UserTopicArticleNote(0,0,0,0);
        String json = jsonMapper.writeValueAsString(utan);

        request = post("/api/user/topic/article/note")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    void successfulAddShouldReturn201() throws Exception {
        UserTopicArticleNote utan = new UserTopicArticleNote(1, 1, 1, 1);

        when(repository.add(utan)).thenReturn(true);
        ObjectMapper jsonMapper = new ObjectMapper();

        String json = jsonMapper.writeValueAsString(utan);

        var request = post("/api/user/topic/article/note")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isCreated());
    }

    @Test
    void failedAddShouldReturn404() throws Exception {
        UserTopicArticleNote utan = new UserTopicArticleNote(1,1,1,1);
        ObjectMapper jsonMapper = new ObjectMapper();

        String json = jsonMapper.writeValueAsString(utan);

        var request = post("/api/user/topic/article/note")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isNotFound());
    }

    /* ***** ***** deleteByKey tests ***** ***** */

    @Test
    void successfulDeleteShouldReturn204() throws Exception {
        when(repository.deleteByKey(1, 1, 1, 1)).thenReturn(true);
        var request = delete("/api/user/topic/article/note/1/1/1/1");
        mvc.perform(request).andExpect(status().isNoContent());
    }

    @Test
    void failedDeleteShouldReturn404() throws Exception {
        when(repository.deleteByKey(1,1,1,1)).thenReturn(false);
        var request = delete("/api/user/topic/article/note/1/1/1/1");
        mvc.perform(request).andExpect(status().isNotFound());
    }
}