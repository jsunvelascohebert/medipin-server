package medipin.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import medipin.data.TopicRepository;
import medipin.models.Topic;
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
class TopicControllerTest {

    @MockBean
    TopicRepository repository;

    @Autowired
    MockMvc mvc;

    /* ***** ***** getAll and getById tests ***** ***** */

    @Test
    void getAllShouldReturn200() throws Exception {
        List<Topic> topics = List.of(
            new Topic(1, "topic 1"),
            new Topic(2, "topic 2")
        );

        when(repository.getAll()).thenReturn(topics);
        ObjectMapper jsonMapper = new ObjectMapper();

        String json = jsonMapper.writeValueAsString(topics);

        mvc.perform(get("/api/topic"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(json));
    }

    @Test
    void getAllShouldReturn404() throws Exception {
        when(repository.getAll()).thenReturn(null);
        mvc.perform(get("/api/topic"))
                .andExpect(status().isNotFound());

        when(repository.getAll()).thenReturn(new ArrayList<>());
        mvc.perform(get("/api/topic"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByIdShouldReturn200() throws Exception {
        Topic topic = new Topic(1, "test");
        when(repository.getById(1)).thenReturn(topic);

        ObjectMapper jsonMapper = new ObjectMapper();
        String json = jsonMapper.writeValueAsString(topic);

        mvc.perform(get("/api/topic/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(json));
    }

    @Test
    void getByIdShouldReturn404() throws Exception {
        when(repository.getById(1)).thenReturn(null);
        mvc.perform(get("/api/topic/1"))
                .andExpect(status().isNotFound());
    }

    /* ***** ***** validation and add tests ***** ***** */

    @Test
    void validationShouldReturn400() throws Exception {
        ObjectMapper jsonMapper = new ObjectMapper();

        var request = post("/api/topic")
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest());

        Topic topic = new Topic(0, "");
        String jsonIn = jsonMapper.writeValueAsString(topic);

        request = post("/api/topic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn);

        mvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    void successfulAddShouldReturn201() throws Exception {
        Topic topicIn = new Topic(0, "test");
        Topic topicOut = new Topic(1, "test");

        when(repository.add(topicIn)).thenReturn(topicOut);
        ObjectMapper jsonMapper = new ObjectMapper();

        String jsonIn = jsonMapper.writeValueAsString(topicIn);
        String jsonOut = jsonMapper.writeValueAsString(topicOut);

        var request = post("/api/topic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonOut));
    }

    @Test
    void invalidAddShouldReturn404() throws Exception {
        Topic topic = new Topic(1, "test");
        ObjectMapper jsonMapper = new ObjectMapper();

        String json = jsonMapper.writeValueAsString(topic);

        var request = post("/api/topic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isBadRequest());
    }

    /* ***** ***** update tests ***** ***** */

    @Test
    void successfulUpdateShouldReturn204() throws Exception {
        Topic topic = new Topic(1, "test");
        ObjectMapper jsonMapper = new ObjectMapper();
        String json = jsonMapper.writeValueAsString(topic);
        when(repository.update(topic)).thenReturn(true);

        var request = put("/api/topic/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isNoContent());
    }

    @Test
    void conflictedUpdateShouldReturn409() throws Exception {
        Topic topic = new Topic(1, "test");
        ObjectMapper jsonMapper = new ObjectMapper();
        String json = jsonMapper.writeValueAsString(topic);

        var request = put("/api/topic/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isConflict());
    }

    @Test
    void invalidUpdateShouldReturn401() throws Exception {
        Topic topic = new Topic(0, "test");
        ObjectMapper jsonMapper = new ObjectMapper();
        String json = jsonMapper.writeValueAsString(topic);

        var request = put("/api/topic/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    void invalidUpdateShouldReturn404() throws Exception {
        Topic topic = new Topic(1, "test");
        ObjectMapper jsonMapper = new ObjectMapper();
        String json = jsonMapper.writeValueAsString(topic);
        when(repository.update(topic)).thenReturn(false);

        var request = put("/api/topic/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isNotFound());
    }

    /* ***** ***** deleteById tests ***** ***** */

    @Test
    void successfulDeleteShouldReturn204() throws Exception {
        when(repository.deleteById(1)).thenReturn(true);
        var request = delete("/api/topic/1");
        mvc.perform(request).andExpect(status().isNoContent());
    }

    @Test
    void failedDeleteAttachedToBridgesShouldReturn400() throws Exception {
        when(repository.isAttachedToTopicArticle(1)).thenReturn(true);
        when(repository.isAttachedToUserTopic(1)).thenReturn(true);
        when(repository.isAttachedToUTAN(1)).thenReturn(true);
        var request = delete("/api/topic/1");
        mvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    void failedDeleteShouldReturn404() throws Exception {
        when(repository.deleteById(100)).thenReturn(false);
        var request = delete("/api/topic/100");
        mvc.perform(request).andExpect(status().isNotFound());
    }
}