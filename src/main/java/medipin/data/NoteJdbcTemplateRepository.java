package medipin.data;

import medipin.data.mappers.NoteMapper;
import medipin.models.Note;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class NoteJdbcTemplateRepository implements NoteRepository {

    private final JdbcTemplate jdbcTemplate;

    public NoteJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Note> getAll() {
        final String sql = "select note_id, `text`, datetime_made from note;";
        return jdbcTemplate.query(sql, new NoteMapper());
    }

    @Override
    @Transactional
    public Note getById(int noteId) {
        final String sql =
                "select note_id, `text`, datetime_made " +
                "from note " +
                "where note_id = ?;";
        return jdbcTemplate.query(sql, new NoteMapper(), noteId)
                .stream().findAny().orElse(null);
    }

    @Override
    public Note add(Note note) {
        final String sql =
                "insert into note " +
                "(`text`, datetime_made) " +
                "value (?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, note.getText());
            ps.setObject(2, note.getDatetimeMade());
            return ps;
        }, keyHolder);
        if (rowsAffected <= 0) {
            return null;
        }
        note.setNoteId(keyHolder.getKey().intValue());
        return note;
    }

    @Override
    public boolean update(Note note) {
        final String sql =
                "update note set " +
                "`text` = ?, " +
                "datetime_made = ? " +
                "where note_id = ?;";

        return jdbcTemplate.update(sql,
                note.getText(),
                note.getDatetimeMade(),
                note.getNoteId()) > 0;
    }

    @Override
    @Transactional
    public boolean deleteByID(int noteId) {
        // delete from note and check size
        return jdbcTemplate.update("delete from note where note_id = ?;",
                noteId) > 0;
    }

    @Override
    @Transactional
    public boolean isAttachedToUserTopicArticleNote(int noteId) {
        final String sql = """
                select count(*)
                from user_topic_article_note
                where note_id = ?;""";

        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, noteId) > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
