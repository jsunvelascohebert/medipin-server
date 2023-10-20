package medipin.domain;

import medipin.data.UserTopicArticleNoteRepository;
import medipin.models.UserTopicArticleNote;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserTopicArticleNoteService {

    private final UserTopicArticleNoteRepository repository;

    public UserTopicArticleNoteService(UserTopicArticleNoteRepository repository) {
        this.repository = repository;
    }

    public Result<List<UserTopicArticleNote>> getByUserTopicArticleId(int userId, int topicId, int articleId) {
        Result<List<UserTopicArticleNote>> result = new Result<>();
        List<UserTopicArticleNote> utans =
                repository.getByUserTopicArticle(userId, topicId, articleId);

        if (utans == null || utans.isEmpty()) {
            String msg = String.format("No notes found for user id %s, topic " +
                    "id %s, and article id %s", userId, topicId, articleId);
            result.addMessage(msg, ResultType.NOT_FOUND);
        } else {
            result.setPayload(utans);
        }
        return result;
    }

    public Result<UserTopicArticleNote> add(UserTopicArticleNote utan) {
        Result<UserTopicArticleNote> result = validate(utan);
        if (!result.isSuccess()) {
            return result;
        }

        boolean response = repository.add(utan);
        if (!response) {
            String msg = "Could not add UserTopicArticleNote; user id, topic " +
                    "id, article id, or note id not found";
            result.addMessage(msg, ResultType.NOT_FOUND);
        }

        return result;
    }

    public Result<UserTopicArticleNote> deleteByKey(int userId, int topicId,
                                                    int articleId, int noteId) {
        UserTopicArticleNote utan = new UserTopicArticleNote(userId, topicId,
                articleId, noteId);
        Result<UserTopicArticleNote> result = validate(utan);

        if (!result.isSuccess()) {
            return result;
        }

        boolean response = repository.deleteByKey(userId, topicId, articleId,
                noteId);
        if (!response) {
            String msg = String.format("Could not delete UserTopicArticleNote" +
                    " by key with user id %s, topic id %s, article id %s, and" +
                    " note id %s", userId, topicId, articleId, noteId);
            result.addMessage(msg, ResultType.NOT_FOUND);
        }

        return result;
    }

    /* ***** ***** validation ***** ***** */

    private Result<UserTopicArticleNote> validate(UserTopicArticleNote utan) {
        Result<UserTopicArticleNote> result = new Result<>();

        if (utan == null) {
            String msg = "UserTopicArticleNote cannot be null";
            result.addMessage(msg, ResultType.INVALID);
            return result;
        }

        if (utan.getUserId() <= 0) {
            result.addMessage("User id in UserTopicArticleNote must be " +
                    "greater than 0", ResultType.INVALID);
        }

        if (utan.getTopicId() <= 0) {
            result.addMessage("Topic id in UserTopicArticleNote must be " +
                    "greater than 0", ResultType.INVALID);
        }

        if (utan.getArticleId() <= 0) {
            result.addMessage("Article id in UserTopicArticleNote must be " +
                    "greater than 0", ResultType.INVALID);
        }

        if (utan.getNoteId() <= 0) {
            result.addMessage("Note id in UserTopicArticleNote must be " +
                    "greater than 0", ResultType.INVALID);
        }

        return result;
    }
}
