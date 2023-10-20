package medipin.data;

import medipin.models.TopicArticle;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TopicArticleRepository {

    @Transactional
    List<TopicArticle> getByTopicId(int topicId);

    boolean add(TopicArticle topicArticle);

    @Transactional
    boolean deleteByTopicId(int topicId);

    @Transactional
    boolean deleteByKey(int topicId, int articleId);


}
