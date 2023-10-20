package medipin.data.mappers;

import medipin.models.UserTopic;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserTopicMapper implements RowMapper<UserTopic> {
    @Override
    public UserTopic mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserTopic userTopic = new UserTopic();
        userTopic.setUserId(rs.getInt("user_id"));
        userTopic.setTopicId(rs.getInt("topic_id"));
        return userTopic;
    }
}
