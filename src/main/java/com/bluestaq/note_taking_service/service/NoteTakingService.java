package com.bluestaq.note_taking_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bluestaq.note_taking_service.entity.Note;
import com.bluestaq.note_taking_service.repo.NoteRepo;

@Service
public class NoteTakingService {

	NoteRepo noteRepo;
	
	public NoteTakingService(NoteRepo noteRepo) {
		this.noteRepo = noteRepo;
	}
	
	public Note findNoteById(String id) {
		return noteRepo.findById(id).orElse(null);
	}

	public List<Note> findNoteByAuthor(String author) {
		return noteRepo.findNoteByAuthor(author);
	}
	
	public List<Note> findNoteByTitleKeyword(String keyword) {
		return noteRepo.findNoteByTitleKeyword(keyword);
	}
	
	public List<Note> findAll() {
		return noteRepo.findAll();
	}
	
	public Note saveNote(Note note) {
		return noteRepo.save(note);
	}	
	
	public void deleteNote(String id) {		
		noteRepo.deleteNoteById(id);
	}	
}
