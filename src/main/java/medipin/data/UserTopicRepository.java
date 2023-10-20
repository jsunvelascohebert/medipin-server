package medipin.data;

import medipin.models.UserTopic;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserTopicRepository {

    @Transactional
    List<UserTopic> getByUserId(int userId);

    boolean add(UserTopic userTopic);

    @Transactional
    boolean deleteByKey(int userId, int topicId);

}
