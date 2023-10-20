package medipin.data.mappers;

import medipin.models.Topic;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TopicMapper implements RowMapper<Topic> {
    @Override
    public Topic mapRow(ResultSet rs, int rowNum) throws SQLException {
        Topic topic = new Topic();
        topic.setTopicId(rs.getInt("topic_id"));
        topic.setName(rs.getString("name"));
        return topic;
    }
}
