package medipin.domain;

import medipin.data.NoteRepository;
import medipin.models.Note;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository repository;

    public NoteService(NoteRepository repository) {
        this.repository = repository;
    }

    public Result<List<Note>> getAll() {
        Result<List<Note>> result = new Result<>();
        List<Note> notes = repository.getAll();

        if (notes == null || notes.isEmpty()) {
            result.addMessage("No notes found", ResultType.NOT_FOUND);
        } else {
            result.setPayload(notes);
        }
        return result;
    }

    public Result<Note> getById(int noteId) {
        Result<Note> result = new Result<>();
        Note note = repository.getById(noteId);

        if (note == null) {
            String msg = String.format("Could not find note with id %s", noteId);
            result.addMessage(msg, ResultType.NOT_FOUND);
        } else {
            result.setPayload(note);
        }
        return result;
    }

    public Result<Note> add(Note note) {
        Result<Note> result = validate(note);
        if (!result.isSuccess()) {
            return result;
        }

        if (note.getNoteId() != 0) {
            result.addMessage("Cannot add note without id of 0", ResultType.INVALID);
            return result;
        }

        note = repository.add(note);
        result.setPayload(note);
        return result;
    }

    public Result<Note> update(Note note) {
        Result<Note> result = validate(note);
        if (!result.isSuccess()) {
            return result;
        }

        if (note.getNoteId() <= 0) {
            result.addMessage("Note id must be set for updating", ResultType.INVALID);
            return result;
        }

        if (!repository.update(note)) {
            String msg = String.format("Could not update missing note with " +
                            "id %s", note.getNoteId());
            result.addMessage(msg, ResultType.NOT_FOUND);
        }

        return result;
    }

    public Result<Note> deleteById(int noteId) {

        boolean isInUserTopicArticleNote =
                repository.isAttachedToUserTopicArticleNote(noteId);

        Result<Note> result = new Result<>();
        if (isInUserTopicArticleNote) {
            String msg = String.format("Cannot delete Note with id %s " +
                    "attached to a UserTopicArticleNote", noteId);
            result.addMessage(msg, ResultType.INVALID);
            return result;
        }

        boolean response = repository.deleteByID(noteId);
        if (!response) {
            String msg = String.format("Note id %s not found, cannot delete", noteId);
            result.addMessage(msg, ResultType.NOT_FOUND);
        }
        return result;
    }

    /* ***** ***** validation ***** ***** */

    private Result<Note> validate(Note note) {
        Result<Note> result = new Result<>();

        if (note == null) {
            result.addMessage("Note cannot be null", ResultType.INVALID);
            return result;
        }

        if (note.getText() == null || note.getText().isBlank()) {
            result.addMessage("Note text cannot be blank",
                    ResultType.INVALID);
        }

        if (note.getDatetimeMade() == null) {
            result.addMessage("Datetime made of note cannot be null",
                    ResultType.INVALID);
        }

        return result;
    }
}
