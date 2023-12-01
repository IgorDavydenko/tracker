package com.github.igordavydenko.tracker.rest.controller;

import com.github.igordavydenko.tracker.rest.dto.RunDto;
import com.github.igordavydenko.tracker.rest.dto.StatisticDto;
import com.github.igordavydenko.tracker.service.StatisticService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static com.github.igordavydenko.tracker.rest.util.ValidationMessage.ERROR_POSITIVE_ID;

@RestController
@RequestMapping("/api/v1/statistics/{userId}")
@RequiredArgsConstructor
@Validated
public class StatisticController {

  private final StatisticService statisticService;

  @GetMapping
  public StatisticDto getUserStatistics(
      @PathVariable
      @Positive(message = ERROR_POSITIVE_ID) Long userId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDateTime,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDateTime
  ) {
    return statisticService.getUserStatistic(userId, fromDateTime, toDateTime);
  }

  @GetMapping("/runs")
  public List<RunDto.RunStatistic> getUserRuns(
      @PathVariable
      @Positive(message = ERROR_POSITIVE_ID) Long userId,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDateTime,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDateTime
  ) {
    return statisticService.getUserRuns(userId, fromDateTime, toDateTime);
  }

}
