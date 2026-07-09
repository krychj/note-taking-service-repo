package com.bluestaq.note_taking_service.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NoteDto {	
	
	String id;
	
	@NotNull
	@NotEmpty
	@Size(max = 200)
	private String title;	

	@NotNull
	@NotEmpty
	@Size(max = 3000)
	private String content;
	
	@NotNull
	@NotEmpty
	@Size(max = 200)
	private String author;
	
	Instant createdAt;
	Instant lastUpdatedAt;
	
	public NoteDto() {
		super();
	}
	
	public NoteDto(String id, @NotNull @NotEmpty String title, @NotNull @NotEmpty String content, 
			@NotNull @NotEmpty String author, Instant createdAt, Instant lastUpdatedAt) {
		
		super();
		this.id = id;
		this.title = title;
		this.content = content;
		this.author = author;
		this.createdAt = createdAt;
		this.lastUpdatedAt = lastUpdatedAt;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getId() {
		return id;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getLastUpdatedAt() {
		return lastUpdatedAt;
	}
}
