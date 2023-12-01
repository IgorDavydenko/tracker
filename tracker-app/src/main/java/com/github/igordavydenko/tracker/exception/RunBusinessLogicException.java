package com.github.igordavydenko.tracker.exception;

public class RunBusinessLogicException extends RuntimeException {

  public RunBusinessLogicException(String message) {
    super(message);
  }

  public RunBusinessLogicException(String message, Throwable cause) {
    super(message, cause);
  }
}
