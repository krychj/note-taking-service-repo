package com.bluestaq.note_taking_service.exception;

import org.springframework.web.util.HtmlUtils;

public class NoteNotFoundException extends RuntimeException {
   
	private static final long serialVersionUID = 1L;

	public NoteNotFoundException(String id) {
        super("Note not found with id: " + HtmlUtils.htmlEscape(id));
    }
}