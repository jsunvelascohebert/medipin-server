package medipin.data.mappers;

import medipin.models.TopicArticle;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TopicArticleMapper implements RowMapper<TopicArticle> {

    @Override
    public TopicArticle mapRow(ResultSet rs, int rowNum) throws SQLException {
        TopicArticle topicArticle = new TopicArticle();
        topicArticle.setTopicId(rs.getInt("topic_id"));
        topicArticle.setArticleId(rs.getInt("article_id"));
        return topicArticle;
    }
}
