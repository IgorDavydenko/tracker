package com.github.igordavydenko.tracker.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
public class ErrorResponse implements Serializable {

  private final int errorCode;
  private final String errorMessage;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final List<ErrorDetails> errorDetails;

  @Builder
  @Getter
  @Setter
  public static class ErrorDetails implements Serializable {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fieldName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String rejectedValue;
  }

}
