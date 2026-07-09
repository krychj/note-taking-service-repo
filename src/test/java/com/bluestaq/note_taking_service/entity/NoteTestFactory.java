package com.bluestaq.note_taking_service.entity;

import java.time.Instant;

public class NoteTestFactory {
	public static Note create(String id, String title, String content, String author, 
			Instant createdAt, Instant lastUpdatedAt) {
		
		return new Note(id, title, content, author, createdAt, lastUpdatedAt);
	}
}
