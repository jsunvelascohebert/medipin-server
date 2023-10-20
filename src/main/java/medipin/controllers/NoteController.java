package medipin.controllers;

import medipin.domain.NoteService;
import medipin.domain.Result;
import medipin.models.Note;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/note")
public class NoteController {

    private final NoteService service;

    public NoteController(NoteService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        Result<List<Note>> result = service.getAll();
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.OK);
        }
        return ErrorResponse.build(result);
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<Object> getById(@PathVariable int noteId) {
        Result<Note> result = service.getById(noteId);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.OK);
        }
        return ErrorResponse.build(result);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody Note note) {
        Result<Note> result = service.add(note);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(),
                    HttpStatus.CREATED);
        }
        return ErrorResponse.build(result);
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<Object> update(@PathVariable int noteId,
                                         @RequestBody Note note) {
        if (noteId != note.getNoteId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<Note> result = service.update(note);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ErrorResponse.build(result);
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Object> deleteById(@PathVariable int noteId) {
        Result<Note> result = service.deleteById(noteId);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ErrorResponse.build(result);
    }
}
