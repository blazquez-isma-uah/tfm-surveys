package com.tfm.bandas.surveys.exception;

import feign.FeignException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

  @ExceptionHandler(NoSuchElementException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, Object> handleNotFound(NoSuchElementException ex) {
    return errorBody("No Encontrado", ex.getMessage());
  }

  @ExceptionHandler({ IllegalArgumentException.class })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleBadRequest(RuntimeException ex) {
    return errorBody("Petición Inválida", ex.getMessage());
  }

  @ExceptionHandler({ IllegalStateException.class })
  @ResponseStatus(HttpStatus.CONFLICT)
  public Map<String, Object> handleConflict(RuntimeException ex) {
    return errorBody("Conflicto", ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, String> details = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(FieldError::getField,
                    DefaultMessageSourceResolvable::getDefaultMessage,
                    (a,b) -> a, LinkedHashMap::new));
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("error", "Errores de Validación");
    body.put("message", "Uno o más campos no son válidos.");
    body.put("details", details);
    return body;
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleConstraintViolation(ConstraintViolationException ex) {
    Map<String, String> details = ex.getConstraintViolations().stream()
            .collect(Collectors.toMap(v -> v.getPropertyPath().toString(),
                    ConstraintViolation::getMessage,
                    (a,b)->a, LinkedHashMap::new));
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("error", "Violación de Restricciones");
    body.put("message", "Uno o más parámetros no son válidos.");
    body.put("details", details);
    return body;
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleNotReadable(HttpMessageNotReadableException ex) {
    logger.warn("Malformed request body: {}", ex.getMessage());
    return errorBody("JSON Malformado", "El cuerpo de la petición no es un JSON válido o no tiene el formato esperado.");
  }

  @ExceptionHandler({AccessDeniedException.class })
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Map<String, Object> handleDenied(AccessDeniedException ex) {
    return errorBody("Acceso Denegado", "No tienes permisos para realizar esta operación.");
  }

  @ExceptionHandler(EventNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, Object> handleEventNotFound(EventNotFoundException ex) {
    return errorBody("Evento No Encontrado", "EVENT_NOT_FOUND", ex.getMessage());
  }

  @ExceptionHandler(EventsServiceUnavailableException.class)
  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  public Map<String, Object> handleEventsServiceUnavailable(EventsServiceUnavailableException ex) {
    return errorBody("Servicio No Disponible", "EVENTS_SERVICE_UNAVAILABLE", ex.getMessage());
  }

  @ExceptionHandler(FeignException.class)
  public ResponseEntity<Map<String, Object>> handleFeign(FeignException ex) {
    HttpStatus status;
    try {
      status = HttpStatus.valueOf(ex.status());
    } catch (Exception e) {
      status = HttpStatus.BAD_GATEWAY;
    }
    logger.error("Error en la comunicación con un servicio remoto (status {})", ex.status(), ex);

    String msg = switch (status) {
      case NOT_FOUND -> "El recurso solicitado no existe en el servicio remoto.";
      case BAD_REQUEST -> "Los datos enviados no son válidos según el servicio remoto.";
      case UNAUTHORIZED, FORBIDDEN -> "No tienes permisos para realizar esta operación en el servicio remoto.";
      case CONFLICT -> "Existe un conflicto con los datos proporcionados en el servicio remoto.";
      default -> "El servicio remoto no está disponible en este momento. Inténtalo de nuevo más tarde.";
    };
    return ResponseEntity.status(status)
            .body(errorBody("Error de Servicio Externo", msg));
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, Object> handleGeneric(Exception ex) {
    logger.error("Unhandled exception", ex);
    return errorBody("Error Interno", "Ha ocurrido un error inesperado. Inténtalo de nuevo más tarde.");
  }

  @ExceptionHandler(PreconditionRequiredException.class)
  @ResponseStatus(HttpStatus.PRECONDITION_REQUIRED) // 428
  public Map<String, Object> handlePreconditionRequired(RuntimeException ex) {
    return errorBody("Precondición Requerida", ex.getMessage());
  }

  @ExceptionHandler(PreconditionFailedException.class)
  @ResponseStatus(HttpStatus.PRECONDITION_FAILED) // 412
  public Map<String, Object> handlePreconditionFailed(RuntimeException ex) {
    return errorBody("Precondición Fallida", ex.getMessage());
  }

  @ExceptionHandler(OptimisticLockingFailureException.class)
  @ResponseStatus(HttpStatus.CONFLICT) // 409
  public Map<String, Object> handleOptimisticLock(OptimisticLockingFailureException ex) {
    return errorBody("Conflicto de Concurrencia", "OPTIMISTIC_LOCK_CONFLICT",
            "Se ha detectado una modificación concurrente de este recurso. Recupera la versión más reciente e inténtalo de nuevo.");
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.CONFLICT) // 409
  public Map<String, Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
    logger.error("Data integrity violation", ex);
    return errorBody("Conflicto de Datos",
            "No se ha podido completar la operación porque los datos entran en conflicto con información ya existente.");
  }

  private Map<String, Object> errorBody(String error, String message) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("error", error);
    body.put("message", message);
    return body;
  }

  private Map<String, Object> errorBody(String error, String errorCode, String message) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("error", error);
    body.put("errorCode", errorCode);
    body.put("message", message);
    return body;
  }

}
