package medipin.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import medipin.data.TopicArticleRepository;
import medipin.models.TopicArticle;
import medipin.models.UserTopic;
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
class TopicArticleControllerTest {

    @MockBean
    TopicArticleRepository repository;

    @Autowired
    MockMvc mvc;

    /* ***** ***** getByTopicId test ***** ***** */

    @Test
    void getAllByTopicIdShouldReturn200() throws Exception {
        List<TopicArticle> topicArticles = List.of(
            new TopicArticle(1, 1),
            new TopicArticle(1, 2)
        );

        when(repository.getByTopicId(1)).thenReturn(topicArticles);
        ObjectMapper jsonMapper = new ObjectMapper();

        String json = jsonMapper.writeValueAsString(topicArticles);

        mvc.perform(get("/api/topic/article/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(json));
    }

    @Test
    void getAllByTopicIdShouldReturn404() throws Exception {
        when(repository.getByTopicId(1)).thenReturn(null);
        mvc.perform(get("/api/topic/article/1"))
                .andExpect(status().isNotFound());

        when(repository.getByTopicId(1)).thenReturn(new ArrayList<>());
        mvc.perform(get("/api/topic/article/1"))
                .andExpect(status().isNotFound());
    }

    /* ***** ***** validation and add test ***** ***** */

    @Test
    void validationShouldReturn400() throws Exception {
        ObjectMapper jsonMapper = new ObjectMapper();

        var request = post("/api/topic/article")
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest());

        TopicArticle topicArticle = new TopicArticle(0, 0);
        String json = jsonMapper.writeValueAsString(topicArticle);

        request = post("/api/topic/article")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    void successfulAddShouldReturn201() throws Exception {
        TopicArticle topicArticle = new TopicArticle(1, 1);

        when(repository.add(topicArticle)).thenReturn(true);
        ObjectMapper jsonMapper = new ObjectMapper();

        String json = jsonMapper.writeValueAsString(topicArticle);

        var request = post("/api/topic/article")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isCreated());
    }

    @Test
    void invalidAddShouldReturn404() throws Exception {
        TopicArticle topicArticle = new TopicArticle(1, 1);
        ObjectMapper jsonMapper = new ObjectMapper();

        String json = jsonMapper.writeValueAsString(topicArticle);

        var request = post("/api/topic/article")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isNotFound());
    }

    /* ***** ***** deleteByKey test ***** ***** */

    @Test
    void successfulDeleteShouldReturn204() throws Exception {
        when(repository.deleteByKey(1, 1)).thenReturn(true);
        var request = delete("/api/topic/article/1/1");
        mvc.perform(request).andExpect(status().isNoContent());
    }

    @Test
    void failedDeleteShouldReturn404() throws Exception {
        when(repository.deleteByKey(1, 1)).thenReturn(false);
        var request = delete("/api/topic/article/1/1");
        mvc.perform(request).andExpect(status().isNotFound());
    }
}