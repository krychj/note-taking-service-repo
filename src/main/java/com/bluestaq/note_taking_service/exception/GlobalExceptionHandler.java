package com.bluestaq.note_taking_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
      
    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoteNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder(ex, HttpStatus.NOT_FOUND, "Note not found").build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
