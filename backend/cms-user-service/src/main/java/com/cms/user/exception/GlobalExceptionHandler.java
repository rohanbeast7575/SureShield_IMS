package com.cms.user.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.cms.user.dto.CommonApiResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<CommonApiResponse> handleUserNotFoundException(UserNotFoundException e) {
		CommonApiResponse response = new CommonApiResponse();
		log.error("User not found: {}", e.getMessage()); // Logging the error message and stack trace

		response.setResponseMessage("User not found: " + e.getMessage());
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<CommonApiResponse> handleIllegalArgumentException(IllegalArgumentException e) {
		CommonApiResponse response = new CommonApiResponse();
		log.error("Invalid input: {}", e.getMessage()); // Logging the error message and stack trace

		response.setResponseMessage("Invalid input: " + e.getMessage());
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			// Logging the error message and stack trace

			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			log.error("Invalid input: {} {}", fieldName, errorMessage);
			errors.put(fieldName, errorMessage);
		});
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<CommonApiResponse> handleGeneralException(Exception e) {
		CommonApiResponse response = new CommonApiResponse();
		response.setResponseMessage("An error occurred: " + e.getMessage());
		log.error("An error Occured {} ", e.getMessage());

		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
