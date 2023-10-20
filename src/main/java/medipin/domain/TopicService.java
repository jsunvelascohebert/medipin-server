package medipin.domain;

import medipin.data.TopicRepository;
import medipin.models.Topic;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService {

    private final TopicRepository repository;

    public TopicService(TopicRepository repository) {
        this.repository = repository;
    }

    public Result<List<Topic>> getAll() {
        Result<List<Topic>> result = new Result<>();
        List<Topic> topics = repository.getAll();

        if (topics == null || topics.isEmpty()) {
            result.addMessage("No topics found", ResultType.NOT_FOUND);
        } else {
            result.setPayload(topics);
        }
        return result;
    }

    public Result<Topic> getById(int topicId) {
        Result<Topic> result = new Result<>();
        Topic topic = repository.getById(topicId);

        if (topic == null) {
            String msg = String.format("Could not find topic with id %s", topicId);
            result.addMessage(msg, ResultType.NOT_FOUND);
        } else {
            result.setPayload(topic);
        }
        return result;
    }

    public Result<Topic> add(Topic topic) {
        Result<Topic> result = validate(topic);

        if (!result.isSuccess()) {
            return result;
        }

        if (topic.getTopicId() != 0) {
            result.addMessage("Cannot add topic without id of 0", ResultType.INVALID);
            return result;
        }

        topic = repository.add(topic);
        result.setPayload(topic);
        return result;
    }

    public Result<Topic> update(Topic topic) {
        Result<Topic> result = validate(topic);

        if (!result.isSuccess()) {
            return result;
        }

        if (topic.getTopicId() <= 0) {
            result.addMessage("Topic id must be set for updating",
                    ResultType.INVALID);
            return result;
        }

        if (!repository.update(topic)) {
            result.addMessage(String.format("Could not update missing topic " +
                            "with id %s", topic.getTopicId()),
                    ResultType.NOT_FOUND);
        }

        return result;
    }

    public Result<Topic> deleteById(int topicId) {

        boolean inUserTopic = repository.isAttachedToUserTopic(topicId);
        boolean inTopicArticle = repository.isAttachedToTopicArticle(topicId);
        boolean inUTAN = repository.isAttachedToUTAN(topicId);

        Result<Topic> result = new Result<>();
        if (inUserTopic || inTopicArticle || inUTAN) {
            String msg = String.format("Cannot delete Topic with id %s " +
                    "attached to a UserTopic, TopicArticle, or " +
                    "UserTopicArticleNote", topicId);
            result.addMessage(msg, ResultType.INVALID);
            return result;
        }

        boolean response = repository.deleteById(topicId);
        if (!response) {
            String msg = String.format("Topic id %s not found, cannot delete", topicId);
            result.addMessage(msg, ResultType.NOT_FOUND);
        }
        return result;
    }

    public Result<Topic> hardDeleteById(int topicId) { // untested
        Result<Topic> result = new Result<>();
        boolean response = repository.hardDeleteById(topicId);
        if (!response) {
            String msg = "Failed to hard delete topic";
            result.addMessage(msg, ResultType.NOT_FOUND);
        }
        return result;
    }

    /* ***** ***** validations ***** ***** */

    private Result<Topic> validate(Topic topic) {
        Result<Topic> result = new Result<>();

        if (topic == null) {
            result.addMessage("Topic cannot be null", ResultType.INVALID);
            return result;
        }

        if (topic.getName() == null || topic.getName().isBlank()) {
            result.addMessage("Topic name cannot be blank", ResultType.INVALID);
        }

        return result;
    }
}
