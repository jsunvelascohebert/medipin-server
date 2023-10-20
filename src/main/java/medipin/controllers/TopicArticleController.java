package medipin.controllers;

import medipin.domain.Result;
import medipin.domain.TopicArticleService;
import medipin.models.Article;
import medipin.models.TopicArticle;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/topic/article")
public class TopicArticleController {

    private final TopicArticleService service;

    public TopicArticleController(TopicArticleService service) {
        this.service = service;
    }

    @GetMapping("/{topicId}")
    public ResponseEntity<Object> getByTopicId(@PathVariable int topicId) {
        Result<List<TopicArticle>> result = service.getByTopicId(topicId);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.OK);
        }
        return ErrorResponse.build(result);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody TopicArticle topicArticle) {
        Result<TopicArticle> result = service.add(topicArticle);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(),
                    HttpStatus.CREATED);
        }
        return ErrorResponse.build(result);
    }

    @DeleteMapping("/{topicId}") // untested
    public ResponseEntity<Object> deleteByTopicId(@PathVariable int topicId) {
        Result<TopicArticle> result = service.deleteByTopicId(topicId);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ErrorResponse.build(result);
    }

    @DeleteMapping("/{topicId}/{articleId}")
    public ResponseEntity<Object> deleteByKey(@PathVariable int topicId,
                                              @PathVariable int articleId) {
        Result<TopicArticle> result = service.deleteByKey(topicId, articleId);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ErrorResponse.build(result);
    }


}
