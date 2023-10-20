package medipin.data.mappers;

import medipin.models.UserTopicArticleNote;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserTopicArticleNoteMapper implements RowMapper<UserTopicArticleNote> {
    @Override
    public UserTopicArticleNote mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserTopicArticleNote utan = new UserTopicArticleNote();
        utan.setUserId(rs.getInt("user_id"));
        utan.setTopicId(rs.getInt("topic_id"));
        utan.setArticleId(rs.getInt("article_id"));
        utan.setNoteId(rs.getInt("note_id"));
        return utan;
    }
}
