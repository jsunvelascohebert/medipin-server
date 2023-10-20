package medipin.domain;

import medipin.data.UserTopicRepository;
import medipin.models.UserTopic;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserTopicService {

    private final UserTopicRepository repository;

    public UserTopicService(UserTopicRepository repository) {
        this.repository = repository;
    }

    public Result<List<UserTopic>> getByUserId(int userId) {
        Result<List<UserTopic>> result = new Result<>();
        List<UserTopic> userTopics = repository.getByUserId(userId);

        if (userTopics == null || userTopics.isEmpty()) {
            String msg = String.format("No topics found for user id %s", userId);
            result.addMessage(msg, ResultType.NOT_FOUND);
        } else {
            result.setPayload(userTopics);
        }
        return result;
    }

    public Result<UserTopic> add(UserTopic userTopic) {
        Result<UserTopic> result = validate(userTopic);
        if (!result.isSuccess()) {
            return result;
        }

        boolean response = repository.add(userTopic);
        if (!response) {
            result.addMessage("Could not add UserTopic, user id or topic id " +
                    "not found", ResultType.NOT_FOUND);
        }

        return result;
    }

    public Result<UserTopic> deleteByKey(int userId, int topicId) {
        UserTopic userTopic = new UserTopic(userId, topicId);
        Result<UserTopic> result = validate(userTopic);

        if (!result.isSuccess()) {
            return result;
        }

        boolean response = repository.deleteByKey(userId, topicId);
        if (!response) {
            String msg = String.format("Could not delete UserTopic by key " +
                    "with user id %s and topic id %s", userId, topicId);
            result.addMessage(msg, ResultType.NOT_FOUND);
        }

        return result;
    }

    /* ***** ***** validation ***** ***** */

    private Result<UserTopic> validate(UserTopic userTopic) {
        Result<UserTopic> result = new Result<>();

        if (userTopic == null) {
            result.addMessage("UserTopic cannot be null", ResultType.INVALID);
            return result;
        }

        if (userTopic.getUserId() <= 0) {
            result.addMessage("User id in UserTopic must be greater than 0",
             ResultType.INVALID);
        }

        if (userTopic.getTopicId() <= 0) {
            result.addMessage("Topic id in UserTopic must be greater than 0",
                    ResultType.INVALID);
        }

        return result;
    }
}
