package medipin.data;

import medipin.data.mappers.TopicArticleMapper;
import medipin.models.TopicArticle;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class TopicArticleJdbcTemplateRepository implements TopicArticleRepository {

    private final JdbcTemplate jdbcTemplate;

    public TopicArticleJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public List<TopicArticle> getByTopicId(int topicId) {
        final String sql = """
            select topic_id, article_id
            from topic_article
            where topic_id = ?;""";

        return jdbcTemplate.query(sql, new TopicArticleMapper(), topicId);
    }

    @Override
    public boolean add(TopicArticle topicArticle) {
        final String sql = """
            insert into topic_article (topic_id, article_id) values (?, ?);""";

        return jdbcTemplate.update(sql, topicArticle.getTopicId(),
                topicArticle.getArticleId()) > 0;
    }

    @Override
    @Transactional
    public boolean deleteByTopicId(int topicId) { // untested
        return jdbcTemplate.update(
                "delete from topic_article where topic_id = ?;", topicId) > 0;
    }

    @Override
    @Transactional
    public boolean deleteByKey(int topicId, int articleId) {
        return jdbcTemplate.update("delete from topic_article where " +
                "topic_id = ? and article_id = ?;", topicId, articleId) > 0;
    }


}
