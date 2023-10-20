package medipin.domain;

import medipin.data.TopicArticleRepository;
import medipin.models.TopicArticle;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicArticleService {

    private final TopicArticleRepository repository;

    public TopicArticleService(TopicArticleRepository repository) {
        this.repository = repository;
    }

    public Result<List<TopicArticle>> getByTopicId(int topicId) {
        Result<List<TopicArticle>> result = new Result<>();
        List<TopicArticle> topicArticles = repository.getByTopicId(topicId);

        if (topicArticles == null || topicArticles.isEmpty()) {
            String msg = String.format("No articles found for topic id %s",
                    topicId);
            result.addMessage(msg, ResultType.NOT_FOUND);
        } else {
            result.setPayload(topicArticles);
        }
        return result;
    }

    public Result<TopicArticle> add(TopicArticle topicArticle) {
        Result<TopicArticle> result = validate(topicArticle);
        if (!result.isSuccess()) {
            return result;
        }

        boolean response = repository.add(topicArticle);
        if (!response) {
            String msg = "Could not add TopicArticle, topic id or article id " +
                    "not found";
            result.addMessage(msg, ResultType.NOT_FOUND);
        }

        return result;
    }

    public Result<TopicArticle> deleteByTopicId(int topicId) { // untested
        Result<TopicArticle> result = new Result<>();
        boolean response = repository.deleteByTopicId(topicId);
        if (!response) {
            String msg = String.format("Could not delete all articles " +
                    "associated with topic id %s", topicId);
            result.addMessage(msg, ResultType.NOT_FOUND);
        }
        return result;
    }

    public Result<TopicArticle> deleteByKey(int topicId, int articleId) {
        TopicArticle topicArticle = new TopicArticle(topicId, articleId);
        Result<TopicArticle> result = validate(topicArticle);

        if (!result.isSuccess()) {
            return result;
        }

        boolean response = repository.deleteByKey(topicId, articleId);
        if (!response) {
            String msg = String.format("Could not delete TopicArticle by key " +
                    "with topic id %s and article id %s", topicId,
                    articleId);
            result.addMessage(msg, ResultType.NOT_FOUND);
        }

        return result;
    }


    /* ***** ***** validation ***** ***** */

    private Result<TopicArticle> validate(TopicArticle topicArticle) {
        Result<TopicArticle> result = new Result<>();

        if (topicArticle == null) {
            result.addMessage("TopicArticle cannot be null", ResultType.INVALID);
            return result;
        }

        if (topicArticle.getTopicId() <= 0) {
            result.addMessage("Topic id in TopicArticle must be greater than 0",
                    ResultType.INVALID);
        }

        if (topicArticle.getArticleId() <= 0) {
            result.addMessage("Article id in TopicArticle must be greater " +
                    "than 0", ResultType.INVALID);
        }

        return result;
    }
}
