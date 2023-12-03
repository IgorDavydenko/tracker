package com.github.igordavydenko.tracker.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RunDto implements Serializable {

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Setter
  public abstract static class AbstractRun implements Serializable {
    @NotNull(message = "Field 'userId' must not be null")
    private Long userId;
  }


  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Setter
  public static class RunStart extends AbstractRun {
    @NotNull(message = "Field 'startDateTime' must not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime startDateTime;

    @NotNull(message = "Field 'startLatitude' must not be null")
    @DecimalMin(value = "-90.0", message = "Field 'startLatitude' must be greater than or equal to -90.0")
    @DecimalMax(value = "90.0", message = "Field 'startLatitude' must be less than or equal to 90.0")
    private Double startLatitude;

    @NotNull(message = "Field 'startLongitude' must not be null")
    @DecimalMin(value = "-180.0", message = "Field 'startLongitude' must be greater than or equal to -180.0")
    @DecimalMax(value = "180.0", message = "Field 'startLongitude' must be less than or equal to 180.0")
    private Double startLongitude;
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Setter
  public static class RunFinish extends AbstractRun {

    @NotNull(message = "Field 'finishDateTime' must not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime finishDateTime;

    @NotNull(message = "Field 'finishDateTime' must not be null")
    @DecimalMin(value = "-90.0", message = "Field 'finishDateTime' must be greater than or equal to -90.0")
    @DecimalMax(value = "90.0", message = "Field 'finishDateTime' must be less than or equal to 90.0")
    private Double finishLatitude;

    @NotNull(message = "Finish longitude must not be null")
    @DecimalMin(value = "-180.0", message = "Field 'finishLongitude' must be greater than or equal to -180.0")
    @DecimalMax(value = "180.0", message = "Field 'finishLongitude' must be less than or equal to 180.0")
    private Double finishLongitude;

    @NotNull(message = "Field 'distance' must not be null")
    @Positive(message = "Field 'distance' must be a positive number")
    private Integer distance;
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Setter
  public static class RunInfo extends AbstractRun {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime startDateTime;
    private Double startLatitude;
    private Double startLongitude;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime finishDateTime;
    private Double finishLatitude;
    private Double finishLongitude;
    private Integer distance;
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Setter
  public static class RunStatistic extends RunInfo {
    private Double averageSpeed;
  }

}
