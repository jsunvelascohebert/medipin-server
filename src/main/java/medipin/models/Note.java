package medipin.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

public class Note {
    private int noteId;
    private String text;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime datetimeMade;

    public Note() {};

    public Note(int noteId, String text, LocalDateTime datetimeMade) {
        this.noteId = noteId;
        this.text = text;
        this.datetimeMade = datetimeMade;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDatetimeMade() {
        return datetimeMade;
    }

    public void setDatetimeMade(LocalDateTime datetimeMade) {
        this.datetimeMade = datetimeMade;
    }

    @Override
    public String toString() {
        return "Note{" +
                "noteId=" + noteId +
                ", text='" + text + '\'' +
                ", datetimeMade=" + datetimeMade +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return noteId == note.noteId && Objects.equals(text, note.text) && Objects.equals(datetimeMade, note.datetimeMade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(noteId, text, datetimeMade);
    }
}
