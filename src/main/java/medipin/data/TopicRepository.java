package medipin.data;

import medipin.models.Topic;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TopicRepository {

    List<Topic> getAll();

    @Transactional
    Topic getById(int topicId);

    Topic add(Topic topic);

    boolean update(Topic topic);

    @Transactional
    boolean deleteById(int topicId);

    @Transactional
    boolean hardDeleteById(int topicId);

    @Transactional
    boolean isAttachedToUserTopic(int topicId);

    @Transactional
    boolean isAttachedToTopicArticle(int topicId);

    @Transactional
    boolean isAttachedToUTAN(int topicId);

}
