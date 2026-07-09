package com.bluestaq.note_taking_service.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bluestaq.note_taking_service.entity.Note;
import com.bluestaq.note_taking_service.repo.NoteRepo;

@Service
public class NoteTakingService {

	NoteRepo noteRepo;
	
	public NoteTakingService(NoteRepo noteRepo) {
		this.noteRepo = noteRepo;
	}
	
	public Optional<Note> findNoteById(String id) {
		return noteRepo.findById(id);
	}

	public List<Note> findNoteByAuthor(String author) {
		return noteRepo.findNoteByAuthor(author);
	}
	
	public List<Note> findNoteByTitleKeyword(String keyword) {
		return noteRepo.findNoteByTitleKeyword(keyword);
	}
	
	public Page<Note> findNotesByCreatedAtBetween(Instant from, Instant to, Pageable pageable) {
		return noteRepo.findNotesByCreatedAtBetween(from, to, pageable);
	}
	
	public Page<Note> findAll(Pageable pageable) {
		return noteRepo.findAll(pageable);
	}
	
	public Note saveNote(Note note) {
		return noteRepo.save(note);
	}	
	
	public void deleteNote(String id) {		
		noteRepo.deleteNoteById(id);
	}	
}
