package com.github.igordavydenko.tracker.handler;

import ch.qos.logback.classic.Logger;
import com.github.igordavydenko.tracker.exception.RunBusinessLogicException;
import com.github.igordavydenko.tracker.exception.UserNotFoundException;
import com.github.igordavydenko.tracker.rest.dto.ErrorResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedList;
import java.util.List;

@RestControllerAdvice
public class ResponseExceptionHandler {

  private static final Logger log = (Logger) LoggerFactory.getLogger(ResponseExceptionHandler.class);

  @ExceptionHandler(value
      = { IllegalArgumentException.class, UserNotFoundException.class, RunBusinessLogicException.class })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleBodyFieldError(RuntimeException ex) {
    log.error("IllegalArgumentException: {}", ex.getMessage());

    List<String> errorMessage = new LinkedList<>();
    errorMessage.add(ex.getMessage());

    return ErrorResponse.builder()
        .errorCode(HttpStatus.BAD_REQUEST.value())
        .errorDetails(errorMessage)
        .build();
  }


  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse unhandledException(Exception ex) {

    List<String> errorMessage = new LinkedList<>();
    errorMessage.add(ExceptionUtils.getStackTrace(ex));

    return ErrorResponse.builder()
        .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .errorDetails(errorMessage)
        .build();
  }

}
