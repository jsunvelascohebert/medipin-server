package medipin.controllers;

import medipin.domain.Result;
import medipin.domain.UserTopicArticleNoteService;
import medipin.models.UserTopicArticleNote;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/user/topic/article/note")
public class UserTopicArticleNoteController {

    private final UserTopicArticleNoteService service;

    public UserTopicArticleNoteController(UserTopicArticleNoteService service) {
        this.service = service;
    }

    @GetMapping("/{userId}/{topicId}/{articleId}")
    public ResponseEntity<Object> getByUserTopicArticleId(@PathVariable int userId,
                                                          @PathVariable int topicId,
                                                          @PathVariable int articleId) {
        Result<List<UserTopicArticleNote>> result =
                service.getByUserTopicArticleId(userId, topicId, articleId);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.OK);
        }
        return ErrorResponse.build(result);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody UserTopicArticleNote utan) {
        Result<UserTopicArticleNote> result = service.add(utan);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
        }
        return ErrorResponse.build(result);
    }

    @DeleteMapping("/{userId}/{topicId}/{articleId}/{noteId}")
    public ResponseEntity<Object> deleteByKey(@PathVariable int userId,
                                              @PathVariable int topicId,
                                              @PathVariable int articleId,
                                              @PathVariable int noteId) {
        Result<UserTopicArticleNote> result = service.deleteByKey(userId,
                topicId, articleId, noteId);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ErrorResponse.build(result);
    }



}
