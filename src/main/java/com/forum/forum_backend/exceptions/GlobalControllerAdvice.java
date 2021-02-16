package com.forum.forum_backend.exceptions;

import com.forum.forum_backend.dtos.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalControllerAdvice {

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ExceptionDto> handleUnauthorizedException (UnauthorizedException ex, HttpServletRequest request) {
		ExceptionDto exception = new ExceptionDto();
		exception.setStatus(ex.getHttpStatus().value());
		exception.setError(ex.getHttpStatus().toString().split(" ")[1]);
		exception.setMessage(ex.getMessage());
		exception.setPath(request.getRequestURI());
		return new ResponseEntity<>(exception, ex.getHttpStatus());
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ExceptionDto> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
		ExceptionDto exception = new ExceptionDto();
		exception.setStatus(ex.getHttpStatus().value());
		exception.setError(ex.getHttpStatus().toString().split(" ")[1]);
		exception.setMessage(ex.getMessage());
		exception.setPath(request.getRequestURI());
		return new ResponseEntity<>(exception, ex.getHttpStatus());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
		ExceptionDto exception = new ExceptionDto();
		exception.setStatus(HttpStatus.BAD_REQUEST.value());
		exception.setError(HttpStatus.BAD_REQUEST.toString().split(" ")[1]);
		exception.setMessage(ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage());
		exception.setPath(request.getRequestURI());
		return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
	}
}
