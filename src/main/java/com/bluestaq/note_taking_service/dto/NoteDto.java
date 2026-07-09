package com.bluestaq.note_taking_service.dto;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NoteDto {	
	
	@Schema(hidden = true)
	private String id;
	
	@NotBlank
	@Size(max = 200)
	private String title;	

	@NotBlank
	@Size(max = 3000)
	private String content;
	
	@NotBlank
	@Size(max = 200)
	private String author;
	
	@Schema(hidden = true)
	Instant createdAt;
	@Schema(hidden = true)
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
