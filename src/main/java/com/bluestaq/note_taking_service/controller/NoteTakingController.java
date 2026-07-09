package com.bluestaq.note_taking_service.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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

import com.bluestaq.note_taking_service.dto.NoteDto;
import com.bluestaq.note_taking_service.entity.Note;
import com.bluestaq.note_taking_service.exception.NoteNotFoundException;
import com.bluestaq.note_taking_service.mapper.NoteMapper;
import com.bluestaq.note_taking_service.service.NoteTakingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Notes", description = "Operations provided by the note-taking service, available to users to capture and work with their notes.")
@RestController
@RequestMapping("api/v1/notes")
public class NoteTakingController {
	
	NoteTakingService noteTakingService;	

	public NoteTakingController(NoteTakingService noteTakingService) {
		this.noteTakingService = noteTakingService;
	}

	@Operation(summary = "Get a note by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Note found"),
        @ApiResponse(responseCode = "404", description = "Note not found")
    })
	@GetMapping(value = "/{id}")
	public ResponseEntity<NoteDto> getNoteById(@PathVariable String id) {		
		Note note = noteTakingService.findNoteById(id).orElseThrow(() -> new NoteNotFoundException(id));
		NoteDto dto = NoteMapper.toNoteDto(note);
		return ResponseEntity.ok(dto);
	}
	
	@GetMapping(params = "author")
	public ResponseEntity<List<NoteDto>> getNoteByUserId(@RequestParam String author) {
		List<Note> notes = noteTakingService.findNoteByAuthor(author);
		List<NoteDto> dtos = notes.stream()
		        .map(NoteMapper::toNoteDto).toList();
		return ResponseEntity.ok(dtos);
	}
	
	@Operation(summary = "List notes", description = "Returns a list of notes (not paginated), optionally filtered by keyword")
	@GetMapping(params = "keyword")
	public ResponseEntity<List<NoteDto>> getNotesByTitleKeyword(@RequestParam String keyword) {
		List<Note> notes = noteTakingService.findNoteByTitleKeyword(keyword);
		List<NoteDto> dtos = notes.stream()
		        .map(NoteMapper::toNoteDto).toList();
		return ResponseEntity.ok(dtos);
	}
	
	@Operation(summary = "List notes", description = "Returns a paginated list of notes, optionally filtered by date and time range")
	@GetMapping(params = {"from", "to"})
	public ResponseEntity<Page<NoteDto>> getNotesByCreatedAtBetween(
			@RequestParam Instant from,
			@RequestParam Instant to,
			@PageableDefault(size = 20, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
		
		Page<Note> notes = noteTakingService.findNotesByCreatedAtBetween(from, to, pageable);
		Page<NoteDto> dtos = notes.map(NoteMapper::toNoteDto);
		return ResponseEntity.ok(dtos);
	}
	
	@Operation(summary = "List notes", description = "Returns a paginated list of notes, optionally filtered by page attributes")
	@GetMapping
	public ResponseEntity<Page<NoteDto>> getAllNotes(@PageableDefault(size = 20, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
		Page<Note> notes = noteTakingService.findAll(pageable);
        Page<NoteDto> dtos = notes.map(NoteMapper::toNoteDto);
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
		Note noteToUpdate = noteTakingService.findNoteById(id).orElseThrow(() -> new NoteNotFoundException(id));
		if(noteToUpdate != null) {
			noteToUpdate.setTitle(note.getTitle());
			noteToUpdate.setContent(note.getContent());
			noteToUpdate.setAuthor(note.getAuthor());
			noteToUpdate.setLastUpdatedAt(Instant.now());
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
