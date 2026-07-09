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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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

	@Operation(summary = "Get a note by ID", description = "Retrieves a single note using its unique identifier.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Note found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = NoteDto.class),
                examples = @ExampleObject(name = "Found note", value = """
                    {
                      "id": "64f1a2b3c4d5e6f7a8b9c0d1",
                      "title": "Bluestaq Project 1",
                      "content": "Keep things as simple as possible.",
                      "author": "user-1",
                      "createdAt": "2026-07-01T14:32:00Z",
                      "lastUpdatedAt": "2026-07-01T14:32:00Z"
                    }
                    """))),
        @ApiResponse(responseCode = "404", description = "Note not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "Not found", value = """
                    {
                      "code": "NOT_FOUND",
                      "message": "Note not found with id: 64f1a2b3c4d5e6f7a8b9c0d1"
                    }
                    """)))
    })
	@GetMapping(value = "/{id}")
	public ResponseEntity<NoteDto> getNoteById(
			@Parameter(description = "24-character Id", example = "64f1a2b3c4d5e6f7a8b9c0d1")
            @PathVariable String id) {
		Note note = noteTakingService.findNoteById(id).orElseThrow(() -> new NoteNotFoundException(id));
		NoteDto dto = NoteMapper.toNoteDto(note);
		return ResponseEntity.ok(dto);
	}
	
	@Operation(summary = "Get notes by author", description = "Returns all notes written by a specific author (not paginated).")
    @ApiResponse(responseCode = "200", description = "Notes retrieved",
        content = @Content(mediaType = "application/json", array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @Schema(implementation = NoteDto.class))))
	@GetMapping(params = "author")
	public ResponseEntity<List<NoteDto>> getNoteByAuthor(
			@Parameter(description = "Author/User ID", example = "user-1")
			@RequestParam(required = false) String author) {
		List<Note> notes = noteTakingService.findNoteByAuthor(author);
		List<NoteDto> dtos = notes.stream()
		        .map(NoteMapper::toNoteDto).toList();
		return ResponseEntity.ok(dtos);
	}
	
	@Operation(summary = "Search notes by title keyword", description = "Returns a list of notes (not paginated) whose title contains the given keyword.")
	@ApiResponse(responseCode = "200", description = "Matching notes retrieved")
	@GetMapping(params = "keyword")
	public ResponseEntity<List<NoteDto>> getNotesByTitleKeyword(
			@Parameter(description = "Keyword to search for within note titles", example = "Project 1")
			@RequestParam(required = false) String keyword) {
		List<Note> notes = noteTakingService.findNoteByTitleKeyword(keyword);
		List<NoteDto> dtos = notes.stream()
		        .map(NoteMapper::toNoteDto).toList();
		return ResponseEntity.ok(dtos);
	}
	
	@Operation(summary = "List notes by date range", description = "Returns a paginated list of notes created within the given date/time range.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notes retrieved"),
        @ApiResponse(responseCode = "400", description = "Invalid or missing date parameters")
    })
	@GetMapping(params = {"from", "to"})
	public ResponseEntity<Page<NoteDto>> getNotesByCreatedAtBetween(
			@Parameter(description = "Start of date range (ISO-8601, UTC)", example = "2026-07-01T00:00:00Z")
            @RequestParam(required = false) Instant from,
            @Parameter(description = "End of date range (ISO-8601, UTC)", example = "2026-07-09T23:59:59Z")
            @RequestParam(required = false) Instant to,
            @PageableDefault(size = 20, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
		
		Page<Note> notes = noteTakingService.findNotesByCreatedAtBetween(from, to, pageable);
		Page<NoteDto> dtos = notes.map(NoteMapper::toNoteDto);
		return ResponseEntity.ok(dtos);
	}
	
	@Operation(summary = "List all notes", description = "Returns a paginated list of all notes, sorted by creation date (newest first) by default.")
    @ApiResponse(responseCode = "200", description = "Paginated notes retrieved")
	@GetMapping
	public ResponseEntity<Page<NoteDto>> getAllNotes(@PageableDefault(size = 20, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
		Page<Note> notes = noteTakingService.findAll(pageable);
        Page<NoteDto> dtos = notes.map(NoteMapper::toNoteDto);
        return ResponseEntity.ok(dtos);
	}
	
	@Operation(summary = "Create a note", description = "Creates a new note and returns the saved resource.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Note created"),
        @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = NoteDto.class),
            examples = @ExampleObject(name = "New note", value = """
                {
                  "title": "Bluestaq Project 1",
                  "content": "Keep things as simple as possible",
                  "author": "user-1"
                }
                """)))
	@PostMapping
	public ResponseEntity<NoteDto> createNote(@RequestBody @Valid NoteDto note) {
		Note noteEntity = NoteMapper.toEntity(note);
		Note savedNote = noteTakingService.saveNote(noteEntity);
		return ResponseEntity.ok(NoteMapper.toNoteDto(savedNote));
	}
	
	@Operation(summary = "Update a note", description = "Replaces an existing note's title, content, and author.")
		@ApiResponses({
	        @ApiResponse(responseCode = "200", description = "Note updated"),
	        @ApiResponse(responseCode = "404", description = "Note not found")
	    })
	@PutMapping("/{id}")
	public ResponseEntity<NoteDto> updateNote(
			@Parameter(description = "24-character Id", example = "64f1a2b3c4d5e6f7a8b9c0d1")
			@PathVariable(required = true) String id, @RequestBody @Valid NoteDto note) {
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
	
	@Operation(summary = "Delete a note", description = "Permanently deletes a note by ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Note deleted"),
        @ApiResponse(responseCode = "404", description = "Note not found")
    })
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSubstation(
			@Parameter(description = "24-character Id", example = "64f1a2b3c4d5e6f7a8b9c0d1")
			@PathVariable String id) {
		noteTakingService.deleteNote(id);
		return ResponseEntity.noContent().build();
	}

}
