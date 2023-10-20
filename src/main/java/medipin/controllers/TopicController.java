package medipin.controllers;

import medipin.domain.Result;
import medipin.domain.TopicService;
import medipin.models.Topic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/topic")
public class TopicController {

    private final TopicService service;

    public TopicController(TopicService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        Result<List<Topic>> result = service.getAll();
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.OK);
        }
        return ErrorResponse.build(result);
    }

    @GetMapping("/{topicId}")
    public ResponseEntity<Object> getById(@PathVariable int topicId) {
        Result<Topic> result = service.getById(topicId);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.OK);
        }
        return ErrorResponse.build(result);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody Topic topic) {
        Result<Topic> result = service.add(topic);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(),
                    HttpStatus.CREATED);
        }
        return ErrorResponse.build(result);
    }

    @PutMapping("/{topicId}")
    public ResponseEntity<Object> update(@PathVariable int topicId,
                                         @RequestBody Topic topic) {
        if (topicId != topic.getTopicId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<Topic> result = service.update(topic);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ErrorResponse.build(result);
    }

    @DeleteMapping("/{topicId}")
    public ResponseEntity<Object> deleteById(@PathVariable int topicId) {
        Result<Topic> result = service.deleteById(topicId);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ErrorResponse.build(result);
    }

    @DeleteMapping("/hard/{topicId}") // not tested
    public ResponseEntity<Object> hardDeleteById(@PathVariable int topicId) {
        Result<Topic> result = service.hardDeleteById(topicId);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ErrorResponse.build(result);
    }
}
