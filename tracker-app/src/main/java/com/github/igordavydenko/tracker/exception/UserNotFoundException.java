package com.github.igordavydenko.tracker.exception;

public class UserNotFoundException extends RuntimeException {

  private static final String MESSAGE_TEMPLATE = "User by id '%s' not found";

  public UserNotFoundException(Long userId) {
    super(String.format(MESSAGE_TEMPLATE, userId));
  }

}
