package medipin.controllers;

import medipin.domain.ArticleService;
import medipin.domain.Result;
import medipin.models.Article;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/article")
public class ArticleController {

    private final ArticleService service;

    public ArticleController(ArticleService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        Result<List<Article>> result = service.getAll();
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.OK);
        }
        return ErrorResponse.build(result);
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<Object> getById(@PathVariable int articleId) {
        Result<Article> result = service.getById(articleId);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.OK);
        }
        return ErrorResponse.build(result);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody Article article) {
        Result<Article> result = service.add(article);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(),
                    HttpStatus.CREATED);
        }
        return ErrorResponse.build(result);
    }

    @PutMapping("/{articleId}")
    public ResponseEntity<Object> update(@PathVariable int articleId,
                                         @RequestBody Article article) {
        if (articleId != article.getArticleId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<Article> result = service.update(article);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ErrorResponse.build(result);
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Object> deleteById(@PathVariable int articleId) {
        Result<Article> result = service.deleteById(articleId);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ErrorResponse.build(result);
    }
}
