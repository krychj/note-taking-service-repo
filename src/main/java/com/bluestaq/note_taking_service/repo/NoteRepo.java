package com.bluestaq.note_taking_service.repo;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.bluestaq.note_taking_service.entity.Note;

public interface NoteRepo extends MongoRepository<Note, String> {
	
	@Query(value = "{'author': ?0}")
	public List<Note> findNoteByAuthor(String username);
	
	@Query("{ 'title': { $regex: ?0, $options: 'i' } }")
	public List<Note> findNoteByTitleKeyword(String keyword);
	
	@Query(value = "{ '_id': ?0 }", delete = true)
	public List<Note> deleteNoteById(String id);
	
	@Query("{ 'createdAt': { $gte: ?0, $lte: ?1 } }")
	Page<Note> findNotesByCreatedAtBetween(Instant from, Instant to, Pageable pageable);
}
