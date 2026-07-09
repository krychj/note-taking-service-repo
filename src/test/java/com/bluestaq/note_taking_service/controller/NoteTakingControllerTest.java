package com.bluestaq.note_taking_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bluestaq.note_taking_service.dto.NoteDto;
import com.bluestaq.note_taking_service.entity.Note;
import com.bluestaq.note_taking_service.entity.NoteTestFactory;
import com.bluestaq.note_taking_service.service.NoteTakingService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(NoteTakingController.class)
class NoteTakingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private NoteTakingService noteTakingService;

    private Note sampleNote;

    @BeforeEach
    void setUp() {
        sampleNote = NoteTestFactory.create(
                "64f1a2b3c4d5e6f7a8b9c0d1",
                "Bluestaq Department ID Project 1",
                "When designing and implementing software, keep things as simple as possible.",
                "user-1",
                Instant.parse("2026-07-01T14:32:00Z"),
                Instant.parse("2026-07-01T14:32:00Z"));
    }

    // Test GET /{id}   
    @Test
    @DisplayName("Happy path: valid id value supplied, and one note should be returned.")
    void getNoteById_whenFound_returns200WithNote() throws Exception {
        when(noteTakingService.findNoteById("64f1a2b3c4d5e6f7a8b9c0d1"))
                .thenReturn(Optional.of(sampleNote));

        mockMvc.perform(get("/api/v1/notes/{id}", "64f1a2b3c4d5e6f7a8b9c0d1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("64f1a2b3c4d5e6f7a8b9c0d1"))
                .andExpect(jsonPath("$.title").value("Bluestaq Department ID Project 1"))
                .andExpect(jsonPath("$.author").value("user-1"));
    }

    @Test
    @DisplayName("Alternative path: invalid id value supplied, and returned response status should be 'not found'.")
    void getNoteById_whenNotFound_returns404() throws Exception {
        when(noteTakingService.findNoteById("nonexistent-id"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/notes/{id}", "nonexistent-id"))
                .andExpect(status().isNotFound());
    }

    // Test GET ?author=
    @Test
    @DisplayName("Happy path: valid author value supplied, and one note should be returned.")
    void getNotesByAuthor_returnsMatchingNotes() throws Exception {
        when(noteTakingService.findNoteByAuthor("user-1"))
                .thenReturn(List.of(sampleNote));

        mockMvc.perform(get("/api/v1/notes").param("author", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value("user-1"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    // Test GET ?keyword=
    @Test
    @DisplayName("Happy path: valid keyword value supplied, and one note should be returned.")
    void getNotesByTitleKeyword_returnsMatchingNotes() throws Exception {
        when(noteTakingService.findNoteByTitleKeyword("Bluestaq"))
                .thenReturn(List.of(sampleNote));

        mockMvc.perform(get("/api/v1/notes").param("keyword", "Bluestaq"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Bluestaq Department ID Project 1"));
    }

    // Test GET ?from=&to=
    @Test
    @DisplayName("Happy path: valid from & to date values supplied, and one note should be returned.")
    void getNotesByCreatedAtBetween_returnsPagedResult() throws Exception {
        Page<Note> page = new PageImpl<>(List.of(sampleNote), PageRequest.of(0, 20), 1);
        when(noteTakingService.findNotesByCreatedAtBetween(any(Instant.class), any(Instant.class), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/notes")
                        .param("from", "2026-07-01T00:00:00Z")
                        .param("to", "2026-07-09T23:59:59Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("64f1a2b3c4d5e6f7a8b9c0d1"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    // Test GET / (all, paginated)
    @DisplayName("Happy path: valid request, and one note should be returned.")
    @Test
    void getAllNotes_returnsPagedResult() throws Exception {
        Page<Note> page = new PageImpl<>(List.of(sampleNote), PageRequest.of(0, 20), 1);
        when(noteTakingService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    // Test POST
    @Test
    @DisplayName("Happy path: valid note supplied to the endpoint, and one note should be returned.")
    void createNote_withValidBody_returns200() throws Exception {
        NoteDto request = new NoteDto();
        request.setTitle("Bluestaq Department ID Project 1");
        request.setContent("When designing and implementing software, keep things as simple as possible.");
        request.setAuthor("user-1");

        when(noteTakingService.saveNote(any(Note.class))).thenReturn(sampleNote);

        mockMvc.perform(post("/api/v1/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Bluestaq Department ID Project 1"));
    }

    @Test
    @DisplayName("Alternative path: invalid note supplied to the endpoint, and bad response status should be returned.")
    void createNote_withMissingFields_returns400() throws Exception {
        NoteDto invalidRequest = new NoteDto(); // all fields null

        mockMvc.perform(post("/api/v1/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(noteTakingService, never()).saveNote(any());
    }

    // Test PUT
    @Test
    @DisplayName("Happy path: valid note supplied to the endpoint, and one updated note should be returned.")
    void updateNote_whenFound_returns200WithUpdatedNote() throws Exception {
    	NoteDto updateRequest = new NoteDto();
        updateRequest.setTitle("Updated title");
        updateRequest.setContent("Updated content");
        updateRequest.setAuthor("user-2");

        when(noteTakingService.findNoteById("64f1a2b3c4d5e6f7a8b9c0d1"))
                .thenReturn(Optional.of(sampleNote));
        when(noteTakingService.saveNote(any(Note.class))).thenReturn(sampleNote);

        mockMvc.perform(put("/api/v1/notes/{id}", "64f1a2b3c4d5e6f7a8b9c0d1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        ArgumentCaptor<Note> noteCaptor = ArgumentCaptor.forClass(Note.class);
        verify(noteTakingService).saveNote(noteCaptor.capture());

        Note capturedNote = noteCaptor.getValue();
        assertEquals(capturedNote.getTitle(), "Updated title");
        assertEquals(capturedNote.getContent(), "Updated content");
        assertEquals(capturedNote.getAuthor(), "user-2");
        assertEquals(capturedNote.getId(), "64f1a2b3c4d5e6f7a8b9c0d1"); 
    }

    @Test
    @DisplayName("Alternative path: one note supplied to the endpoint but it "
    		+ "cannot be found for the update, and system should return response with the 'not found' status.")
    void updateNote_whenNotFound_returns404() throws Exception {
        NoteDto updateRequest = new NoteDto();
        updateRequest.setTitle("Updated title");
        updateRequest.setContent("Updated content");
        updateRequest.setAuthor("user-2");

        when(noteTakingService.findNoteById("nonexistent-id")).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/notes/{id}", "nonexistent-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        verify(noteTakingService, never()).saveNote(any());
    }

    // Test DELETE
    @Test
    @DisplayName("Happy path: valid id supplied to the endpoint, and system should call delete note exactly one time")
    void deleteNote_returns204() throws Exception {
    	doNothing().when(noteTakingService).deleteNote("64f1a2b3c4d5e6f7a8b9c0d1");

        mockMvc.perform(delete("/api/v1/notes/{id}", "64f1a2b3c4d5e6f7a8b9c0d1"))
                .andExpect(status().isNoContent());

        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);       
        verify(noteTakingService, times(1)).deleteNote(idCaptor.capture());

        assertEquals(idCaptor.getValue(), "64f1a2b3c4d5e6f7a8b9c0d1");
    }
}