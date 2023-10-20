package medipin.data;

import medipin.data.mappers.UserTopicArticleNoteMapper;
import medipin.models.UserTopic;
import medipin.models.UserTopicArticleNote;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class UserTopicArticleNoteJdbcTemplateRepository implements UserTopicArticleNoteRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserTopicArticleNoteJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public List<UserTopicArticleNote> getByUserTopicArticle(int userId, int topicId, int articleId) {
        final String sql = """
            select user_id, topic_id, article_id, note_id
            from user_topic_article_note
            where user_id = ? and topic_id = ? and article_id = ?;""";

        return jdbcTemplate.query(sql, new UserTopicArticleNoteMapper(),
                userId, topicId, articleId);
    }

    @Override
    public boolean add(UserTopicArticleNote utan) {
        final String sql = """
            insert into user_topic_article_note
                (user_id, topic_id, article_id, note_id)
            values (?, ?, ?, ?);""";

        return jdbcTemplate.update(sql, utan.getUserId(), utan.getTopicId(),
                utan.getArticleId(), utan.getNoteId()) > 0;
    }

    @Override
    @Transactional
    public boolean deleteByKey(int userId, int topicId, int articleId, int noteId) {
        final String sql = """
            delete from user_topic_article_note
            where user_id = ? and topic_id = ? and article_id = ? and note_id = ?;""";

        return jdbcTemplate.update(sql, userId, topicId, articleId, noteId) > 0;
    }
}
