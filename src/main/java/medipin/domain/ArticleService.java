package medipin.domain;

import medipin.data.ArticleRepository;
import medipin.models.Article;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepository repository;

    public ArticleService(ArticleRepository repository) {
        this.repository = repository;
    }

    public Result<List<Article>> getAll() {
        Result<List<Article>> result = new Result<>();
        List<Article> articles = repository.getAll();

        if (articles == null || articles.isEmpty()) {
            result.addMessage("No articles found", ResultType.NOT_FOUND);
        } else {
            result.setPayload(articles);
        }
        return result;
    }

    public Result<Article> getById(int articleId) {
        Result<Article> result = new Result<>();
        Article article = repository.getById(articleId);

        if (article == null) {
            String msg = String.format("Could not find article with id %s", articleId);
            result.addMessage(msg, ResultType.NOT_FOUND);
        } else {
            result.setPayload(article);
        }
        return result;
    }

    public Result<Article> add(Article article) {
        Result<Article> result = validate(article);
        if (!result.isSuccess()) {
            return result;
        }

        article = repository.add(article);
        result.setPayload(article);
        return result;
    }

    public Result<Article> update(Article article) {
        Result<Article> result = validate(article);
        if (!result.isSuccess()) {
            return result;
        }

        if (article.getArticleId() <= 0) {
            result.addMessage("Article id must be set for updating", ResultType.INVALID);
            return result;
        }

        if (!repository.update(article)) {
            String msg = String.format("Could not update missing article " +
                    "with id %s", article.getArticleId());
            result.addMessage(msg, ResultType.NOT_FOUND);
        }

        return result;
    }

    public Result<Article> deleteById(int articleId) {

        boolean isInTopicArticle =
                repository.isAttachedToTopicArticle(articleId);

        boolean isInUserTopicArticleNote =
                repository.isAttachedToUTAN(articleId);

        Result<Article> result = new Result<>();
        if (isInTopicArticle || isInUserTopicArticleNote) {
            String msg = String.format("Cannot delete Article with id %s " +
                    "attached to either TopicArticle or UserTopicArticleNote", articleId);
            result.addMessage(msg, ResultType.INVALID);
            return result;
        }

        boolean response = repository.deleteById(articleId);
        if (!response) {
            String msg = String.format("Article id %s not found, cannot " +
                    "delete", articleId);
            result.addMessage(msg, ResultType.NOT_FOUND);
        }
        return result;
    }

    /* ***** ***** validations ***** ***** */

    private Result<Article> validate(Article article) {
        Result<Article> result = new Result<>();

        if (article == null) {
            result.addMessage("Article cannot be null", ResultType.INVALID);
            return result;
        }

        if (article.getTitle() == null || article.getTitle().isBlank()) {
            result.addMessage("Article title cannot be blank",
                    ResultType.INVALID);
        }

        return result;
    }
}
