package medipin.data;

import medipin.data.mappers.UserTopicMapper;
import medipin.models.User;
import medipin.models.UserTopic;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class UserTopicJdbcTemplateRepository implements UserTopicRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserTopicJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public List<UserTopic> getByUserId(int userId) {
        final String sql = """
            select user_id, topic_id
            from user_topic
            where user_id = ?;""";

        return jdbcTemplate.query(sql, new UserTopicMapper(), userId);
    }

    @Override
    public boolean add(UserTopic userTopic) {
        final String sql = """
            insert into user_topic (user_id, topic_id)
            values (?, ?);""";

        return jdbcTemplate.update(sql, userTopic.getUserId(),
                userTopic.getTopicId()) > 0;
    }

    @Override
    @Transactional
    public boolean deleteByKey(int userId, int topicId) {
        return jdbcTemplate.update("delete from user_topic where user_id = ? " +
                "and topic_id = ?;", userId, topicId) > 0;
    }
}
