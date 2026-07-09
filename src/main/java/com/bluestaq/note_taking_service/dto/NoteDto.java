package com.bluestaq.note_taking_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class NoteDto {	
	
	String id;
	
	@NotNull
	@NotEmpty
	private String title;	

	@NotNull
	@NotEmpty
	private String content;
	
	@NotNull
	@NotEmpty
	private String author;
	
	public NoteDto() {
		super();
	}
	
	public NoteDto(String id, @NotNull @NotEmpty String title, @NotNull @NotEmpty String content, @NotNull @NotEmpty String author) {
		super();
		this.id = id;
		this.title = title;
		this.content = content;
		this.author = author;
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
}
