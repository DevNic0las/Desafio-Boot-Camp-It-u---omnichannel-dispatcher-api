package com.github.devnic0las.omnichannel_dispatcher_api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidation(
      MethodArgumentNotValidException ex,
      HttpServletRequest request
  ) {
    Map<String, String> fieldErrors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.toMap(
            error -> error.getField(),
            error -> error.getDefaultMessage() == null ? "Valor inválido." : error.getDefaultMessage(),
            (first, second) -> first
        ));

    return buildResponse(
        HttpStatus.BAD_REQUEST,
        "Erro de validação na requisição.",
        request.getRequestURI(),
        fieldErrors
    );
  }

  @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
  public ResponseEntity<ApiErrorResponse> handleBusinessRuntime(
      RuntimeException ex,
      HttpServletRequest request
  ) {
    return buildResponse(
        HttpStatus.BAD_REQUEST,
        ex.getMessage(),
        request.getRequestURI(),
        null
    );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleUnexpected(
      Exception ex,
      HttpServletRequest request
  ) {
    return buildResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Erro interno inesperado.",
        request.getRequestURI(),
        null
    );
  }

  private ResponseEntity<ApiErrorResponse> buildResponse(
      HttpStatus status,
      String message,
      String path,
      Map<String, String> fieldErrors
  ) {
    ApiErrorResponse response = new ApiErrorResponse(
        Instant.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        path,
        fieldErrors
    );
    return ResponseEntity.status(status).body(response);
  }
}
