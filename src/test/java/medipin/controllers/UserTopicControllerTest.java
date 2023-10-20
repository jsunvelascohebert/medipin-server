package medipin.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import medipin.data.UserTopicRepository;
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
class UserTopicControllerTest {

    @MockBean
    UserTopicRepository repository;

    @Autowired
    MockMvc mvc;

    /* ***** ***** getByUserId tests ***** ***** */

    @Test
    void getAllByIdShouldReturn200() throws Exception {
        List<UserTopic> userTopics = List.of(
            new UserTopic(1, 1),
            new UserTopic(1, 2)
        );

        when(repository.getByUserId(1)).thenReturn(userTopics);
        ObjectMapper jsonMapper = new ObjectMapper();

        String json = jsonMapper.writeValueAsString(userTopics);

        mvc.perform(get("/api/user/topic/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(json));
    }

    @Test
    void getAllByIdShouldReturn404() throws Exception {
        when(repository.getByUserId(1)).thenReturn(null);
        mvc.perform(get("/api/user/topic/1"))
            .andExpect(status().isNotFound());

        when(repository.getByUserId(1)).thenReturn(new ArrayList<>());
        mvc.perform(get("/api/user/topic/1"))
            .andExpect(status().isNotFound());
    }

    /* ***** ***** validation and add tests ***** ***** */

    @Test
    void validationShouldReturn400() throws Exception {
        ObjectMapper jsonMapper = new ObjectMapper();

        var request = post("/api/user/topic")
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request).andExpect(status().isBadRequest());

        UserTopic userTopic = new UserTopic(0, 0);
        String json = jsonMapper.writeValueAsString(userTopic);

        request = post("/api/user/topic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    void successfulAddShouldReturn201() throws Exception {
        UserTopic userTopic = new UserTopic(1, 1);

        when(repository.add(userTopic)).thenReturn(true);
        ObjectMapper jsonMapper = new ObjectMapper();

        String json = jsonMapper.writeValueAsString(userTopic);

        var request = post("/api/user/topic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isCreated());
    }

    @Test
    void invalidAddShouldReturn404() throws Exception {
        UserTopic userTopic = new UserTopic(1, 1);
        ObjectMapper jsonMapper = new ObjectMapper();

        String json = jsonMapper.writeValueAsString(userTopic);

        var request = post("/api/user/topic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isNotFound());
    }

    /* ***** ***** deleteByKey tests ***** ***** */

    @Test
    void successfulDeleteShouldReturn204() throws Exception {
        when(repository.deleteByKey(1, 1)).thenReturn(true);
        var request = delete("/api/user/topic/1/1");
        mvc.perform(request).andExpect(status().isNoContent());
    }

    @Test
    void failedDeleteShouldReturn404() throws Exception {
        when(repository.deleteByKey(1, 1)).thenReturn(false);
        var request = delete("/api/user/topic/1/1");
        mvc.perform(request).andExpect(status().isNotFound());
    }
}