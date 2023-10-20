package medipin.controllers;

import medipin.domain.Result;
import medipin.domain.UserTopicService;
import medipin.models.UserTopic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/user/topic")
public class UserTopicController {

    private final UserTopicService service;

    public UserTopicController(UserTopicService service) {
        this.service = service;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getByUserId(@PathVariable int userId) {
        Result<List<UserTopic>> result = service.getByUserId(userId);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.OK);
        }
        return ErrorResponse.build(result);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody UserTopic userTopic) {
        Result<UserTopic> result = service.add(userTopic);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(),
                    HttpStatus.CREATED);
        }
        return ErrorResponse.build(result);
    }

    @DeleteMapping("/{userId}/{topicId}")
    public ResponseEntity<Object> deleteByKey(@PathVariable int userId,
                                              @PathVariable int topicId) {
        Result<UserTopic> result = service.deleteByKey(userId, topicId);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ErrorResponse.build(result);
    }
}
