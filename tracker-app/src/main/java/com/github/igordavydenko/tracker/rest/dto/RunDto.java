package com.github.igordavydenko.tracker.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RunDto implements Serializable {

  @NoArgsConstructor
  @Getter
  @Setter
  public abstract static class AbstractRun implements Serializable {
    @NotNull(message = "Field 'userId' must not be null")
    private Long userId;
  }

  @NoArgsConstructor
  @Getter
  @Setter
  public static class RunStart extends AbstractRun {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime;
    private Double startLatitude;
    private Double startLongitude;
  }

  @NoArgsConstructor
  @Getter
  @Setter
  public static class RunFinish extends AbstractRun {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime finishDateTime;
    private Double finishLatitude;
    private Double finishLongitude;
    private Double distance;
  }

  @NoArgsConstructor
  @Getter
  @Setter
  public static class RunInfo extends AbstractRun {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime;
    private Double startLatitude;
    private Double startLongitude;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime finishDateTime;
    private Double finishLatitude;
    private Double finishLongitude;
    private Double distance;
  }

  @NoArgsConstructor
  @Getter
  @Setter
  public static class RunStatistic extends RunInfo {
    private Double averageSpeed;
  }

}
