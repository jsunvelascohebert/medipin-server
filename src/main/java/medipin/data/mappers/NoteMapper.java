package medipin.data.mappers;

import medipin.models.Note;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NoteMapper implements RowMapper<Note> {
    @Override
    public Note mapRow(ResultSet rs, int rowNum) throws SQLException {
        Note note = new Note();
        note.setNoteId(rs.getInt("note_id"));
        note.setText(rs.getString("text"));

        String datetimeMadeString = rs.getString("datetime_made");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime datetimeMade = LocalDateTime.parse(datetimeMadeString, formatter);
        note.setDatetimeMade(datetimeMade);
        return note;
    }
}
