package com.github.igordavydenko.tracker.rest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Builder
@Setter
@Getter
public class ErrorResponse implements Serializable {

  private final int errorCode;
  private final List<String> errorDetails;

}
