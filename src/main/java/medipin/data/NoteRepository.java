package medipin.data;

import medipin.models.Note;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NoteRepository {
    List<Note> getAll();

    @Transactional
    Note getById(int noteId);

    Note add(Note note);

    boolean update(Note note);

    @Transactional
    boolean deleteByID(int noteId);

    @Transactional
    boolean isAttachedToUserTopicArticleNote(int noteId);
}
