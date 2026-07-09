package com.bluestaq.note_taking_service.mapper;

import java.time.Instant;

import com.bluestaq.note_taking_service.dto.NoteDto;
import com.bluestaq.note_taking_service.entity.Note;

public class NoteMapper {
	
	public static Note toEntity(NoteDto dto) {
		Note entity = new Note();		
		entity.setTitle(dto.getTitle());		
		entity.setContent(dto.getContent());
		entity.setAuthor(dto.getAuthor());
		
		Instant now = Instant.now();
		Instant createdAt = dto.getCreatedAt();
		if(createdAt != null) {
			entity.setCreatedAt(createdAt);
		} else {
			entity.setCreatedAt(now);
		}
		entity.setLastUpdatedAt(now);
		return entity;
	}

	public static NoteDto toNoteDto(Note entity) {
		return new NoteDto(entity.getId(), entity.getTitle(), 
				entity.getContent(), entity.getAuthor(), entity.getCreatedAt(), entity.getLastUpdatedAt());
	}
}