package com.tfm.bandas.surveys.exception;

import feign.FeignException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(NoSuchElementException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, Object> handleNotFound(NoSuchElementException ex) {
    return Map.of("error", "Not Found", "message", ex.getMessage());
  }

  @ExceptionHandler({ IllegalArgumentException.class })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleBadRequest(RuntimeException ex) {
    return Map.of("error", "Bad Request", "message", ex.getMessage());
  }

  @ExceptionHandler({ IllegalStateException.class })
  @ResponseStatus(HttpStatus.CONFLICT)
  public Map<String, Object> handleConflict(RuntimeException ex) {
    return Map.of("error", "Conflict", "message", ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, String> details = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(FieldError::getField,
                    DefaultMessageSourceResolvable::getDefaultMessage,
                    (a,b) -> a, LinkedHashMap::new));
    return Map.of("error", "Validation Failed", "details", details);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleConstraintViolation(ConstraintViolationException ex) {
    Map<String, String> details = ex.getConstraintViolations().stream()
            .collect(Collectors.toMap(v -> v.getPropertyPath().toString(),
                    ConstraintViolation::getMessage,
                    (a,b)->a, LinkedHashMap::new));
    return Map.of("error", "Constraint Violation", "details", details);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleNotReadable(HttpMessageNotReadableException ex) {
    return Map.of("error", "Malformed JSON", "message", ex.getMostSpecificCause().getMessage());
  }

  @ExceptionHandler({AccessDeniedException.class })
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Map<String, Object> handleDenied(AccessDeniedException ex) {
    return Map.of("error", "Forbidden", "message", "Insufficient permissions");
  }

  @ExceptionHandler(FeignException.class)
  public ResponseEntity<Map<String, Object>> handleWebClient(FeignException ex) {
    HttpStatus status = HttpStatus.valueOf(ex.status());
    String msg = ex.contentUTF8();
    return ResponseEntity.status(status)
            .body(Map.of("error", "Upstream Error", "status", status.value(), "message", msg));
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, Object> handleGeneric(Exception ex) {
    return Map.of("error", "Internal Error", "message", ex.getMessage());
  }
}
