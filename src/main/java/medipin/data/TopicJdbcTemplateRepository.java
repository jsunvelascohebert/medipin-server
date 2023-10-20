package medipin.data;

import medipin.data.mappers.ArticleMapper;
import medipin.data.mappers.TopicMapper;
import medipin.models.Topic;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class TopicJdbcTemplateRepository implements TopicRepository {
    private final JdbcTemplate jdbcTemplate;

    public TopicJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Topic> getAll() {
        final String sql = "select topic_id, `name` from topic;";
        return jdbcTemplate.query(sql, new TopicMapper());
    }

    @Override
    @Transactional
    public Topic getById(int topicId) {
        final String sql =
                "select topic_id, `name` " +
                "from topic " +
                "where topic_id = ?;";
        return jdbcTemplate.query(sql, new TopicMapper(), topicId)
                .stream().findAny().orElse(null);
    }

    @Override
    public Topic add(Topic topic) {
        final String sql =
                "insert into topic " +
                "(`name`) " +
                "value (?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, topic.getName());
            return ps;
        }, keyHolder);
        if (rowsAffected <= 0) {
            return null;
        }
        topic.setTopicId(keyHolder.getKey().intValue());
        return topic;
    }

    @Override
    public boolean update(Topic topic) {
        final String sql =
                "update topic set " +
                "`name` = ? " +
                "where topic_id = ?;";

        return jdbcTemplate.update(sql,
                topic.getName(),
                topic.getTopicId()) > 0;
    }

    @Override
    @Transactional
    public boolean deleteById(int topicId) {
        return jdbcTemplate.update("delete from topic where topic_id = ?;",
                topicId) > 0;
    }

    @Override
    public boolean hardDeleteById(int topicId) { // untested
        jdbcTemplate.update("delete from user_topic where topic_id = ?;",
                topicId);
        jdbcTemplate.update("delete from topic_article where topic_id = ?;",
                topicId);
        jdbcTemplate.update("delete from user_topic_article_note where " +
                "topic_id = ?;", topicId);
        return deleteById(topicId);
    }

    @Override
    @Transactional
    public boolean isAttachedToUserTopic(int topicId) {
        final String sql = """
                select count(*)
                from user_topic
                where topic_id = ?;""";

        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, topicId) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean isAttachedToTopicArticle(int topicId) {
        final String sql = """
                select count(*)
                from topic_article
                where topic_id = ?;""";

        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, topicId) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean isAttachedToUTAN(int topicId) {
        final String sql = """
                select count(*)
                from user_topic_article_note
                where topic_id = ?;""";

        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, topicId) > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
