package com.github.igordavydenko.tracker.handler;

import ch.qos.logback.classic.Logger;
import com.github.igordavydenko.tracker.exception.RunBusinessLogicException;
import com.github.igordavydenko.tracker.exception.UserNotFoundException;
import com.github.igordavydenko.tracker.rest.dto.ErrorResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ResponseExceptionHandler {

  private static final Logger log = (Logger) LoggerFactory.getLogger(ResponseExceptionHandler.class);

  @ExceptionHandler(value = {IllegalArgumentException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
    log.warn("IllegalArgumentException: {}", ex.getMessage());

    return ErrorResponse.builder()
        .errorCode(HttpStatus.BAD_REQUEST.value())
        .errorMessage("Illegal argument")
        .errorDetails(Collections.singletonList(
            ErrorResponse.ErrorDetails.builder()
                .message(ex.getMessage())
                .build()
        ))
        .build();
  }

  @ExceptionHandler(value = {UserNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleNotFound(UserNotFoundException ex) {
    log.warn("Not found exception: {}", ex.getMessage());

    return ErrorResponse.builder()
        .errorCode(HttpStatus.NOT_FOUND.value())
        .errorMessage("Data not found")
        .errorDetails(Collections.singletonList(
            ErrorResponse.ErrorDetails.builder()
                .message(ex.getMessage())
                .build()
        ))
        .build();
  }

  @ExceptionHandler(value = {RunBusinessLogicException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleBusinessLogicException(RunBusinessLogicException ex) {
    log.warn("Business logic exception: {}", ex.getMessage());

    return ErrorResponse.builder()
        .errorCode(HttpStatus.BAD_REQUEST.value())
        .errorMessage("Business logic exception")
        .errorDetails(Collections.singletonList(
            ErrorResponse.ErrorDetails.builder()
                .message(ex.getMessage())
                .build()
        ))
        .build();
  }

  @ExceptionHandler(value = {MethodArgumentNotValidException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleMethodArgValid(MethodArgumentNotValidException ex) {
    log.warn("Request validation error: {}", ex.getMessage());

    return ErrorResponse.builder()
        .errorCode(HttpStatus.BAD_REQUEST.value())
        .errorMessage("Business logic exception")
        .errorDetails(getValidationErrorResponse(ex.getBindingResult()))
        .build();
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse unhandledException(Exception ex) {
    log.error("Internal error: {}", ex.getMessage());

    return ErrorResponse.builder()
        .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .errorMessage("Internal server error")
        .errorDetails(Collections.singletonList(
            ErrorResponse.ErrorDetails.builder()
                .message(ExceptionUtils.getStackTrace(ex))
                .build()
        ))
        .build();
  }

  private List<ErrorResponse.ErrorDetails> getValidationErrorResponse(final Errors errors) {

    return errors.getAllErrors().stream()
        .filter(error -> StringUtils.isNotBlank(error.getDefaultMessage()))
        .filter(error -> error instanceof FieldError)
        .map(error -> (FieldError) error)
        .map(error -> ErrorResponse.ErrorDetails.builder()
            .message(error.getDefaultMessage())
            .fieldName(error.getField())
            .rejectedValue(error.getRejectedValue())
            .build())
        .collect(Collectors.toList());
  }
}
