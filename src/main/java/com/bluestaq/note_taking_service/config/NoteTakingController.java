package com.bluestaq.note_taking_service.config;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bluestaq.mapper.NoteMapper;
import com.bluestaq.note_taking_service.dto.NoteDto;
import com.bluestaq.note_taking_service.entity.Note;
import com.bluestaq.note_taking_service.service.NoteTakingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/notes")
public class NoteTakingController {

	NoteTakingService noteTakingService;

	public NoteTakingController(NoteTakingService noteTakingService) {
		this.noteTakingService = noteTakingService;
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<Note> getNoteById(@PathVariable String id) {
		Note note = noteTakingService.findNoteById(id);
		return ResponseEntity.ok(note);
	}
	
	@GetMapping(params = "author")
	public ResponseEntity<List<Note>> getNoteByUserId(@RequestParam String author) {
		List<Note> notes = noteTakingService.findNoteByAuthor(author);
		return ResponseEntity.ok(notes);
	}
	
	@GetMapping(params = "keyword")
	public ResponseEntity<List<NoteDto>> getNotesByTitleKeyword(@RequestParam String keyword) {
		List<Note> notes = noteTakingService.findNoteByTitleKeyword(keyword);
		List<NoteDto> dtos = notes.stream()
		        .map(NoteMapper::toNoteDto).toList();
		return ResponseEntity.ok(dtos);
	}
	
	@GetMapping
	public ResponseEntity<List<NoteDto>> getAllNotes() {
        List<Note> allNotes = noteTakingService.findAll();
        List<NoteDto> dtos = allNotes.stream()
        		.map(NoteMapper::toNoteDto).toList();
        return ResponseEntity.ok(dtos);
	}
	
	@PostMapping
	public ResponseEntity<NoteDto> createNote(@RequestBody @Valid NoteDto note) {
		Note noteEntity = NoteMapper.toEntity(note);
		Note savedNote = noteTakingService.saveNote(noteEntity);
		return ResponseEntity.ok(NoteMapper.toNoteDto(savedNote));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<NoteDto> updateNote(@PathVariable(required = true) String id, @RequestBody @Valid NoteDto note) {
		Note noteToUpdate = noteTakingService.findNoteById(id);
		if(noteToUpdate != null) {
			noteToUpdate.setTitle(note.getTitle());
			noteToUpdate.setContent(note.getContent());
			noteToUpdate.setAuthor(note.getAuthor());
			noteTakingService.saveNote(noteToUpdate);
		}
		return ResponseEntity.ok(NoteMapper.toNoteDto(noteToUpdate));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSubstation(@PathVariable String id) {
		noteTakingService.deleteNote(id);
		return ResponseEntity.noContent().build();
	}

}
