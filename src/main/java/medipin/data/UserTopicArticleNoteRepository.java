package medipin.data;

import medipin.models.UserTopicArticleNote;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserTopicArticleNoteRepository {

    @Transactional
    List<UserTopicArticleNote> getByUserTopicArticle(int userId, int topicId,
                                                     int articleId);
    boolean add(UserTopicArticleNote utan);

    @Transactional
    boolean deleteByKey(int userId, int topicId, int articleId, int noteId);
}
