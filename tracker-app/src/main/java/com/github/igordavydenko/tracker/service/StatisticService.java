package com.github.igordavydenko.tracker.service;

import com.github.igordavydenko.tracker.persistence.entity.RunEntity;
import com.github.igordavydenko.tracker.rest.dto.RunDto;
import com.github.igordavydenko.tracker.rest.dto.StatisticDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticService {

  private final RunService runService;

  @Transactional(readOnly = true)
  public List<RunDto.RunStatistic> getUserRuns(
      final Long userId,
      final LocalDateTime fromDateTime,
      final LocalDateTime toDateTime
  ) {
    var user = runService.getUser(userId);
    var userRuns = runService.findByUserAndPeriod(user, fromDateTime, toDateTime);
    return userRuns.stream()
        .map(this::convertToRunStatistic)
        .collect(Collectors.toList());
  }


  @Transactional(readOnly = true)
  public StatisticDto getUserStatistic(
      final Long userId,
      final LocalDateTime fromDateTime,
      final LocalDateTime toDateTime
  ) {
    var userRuns = getUserRuns(userId, fromDateTime, toDateTime);

    double totalDistance = 0.0;
    double totalTime = 0.0;

    for (RunDto.RunStatistic run : userRuns) {
      if (run.getDistance() != null
          && run.getDistance() > 0
          && run.getStartDateTime() != null
          && run.getFinishDateTime() != null
      ) {
        totalDistance += run.getDistance();
        totalTime += Duration.between(run.getStartDateTime(), run.getFinishDateTime()).toHours();
      }
    }

    return StatisticDto.builder()
        .totalRuns(userRuns.size())
        .totalDistance(totalDistance)
        .averageSpeed((totalTime > 0) ? calculateAverageSpeed(totalDistance, totalTime) : 0.0)
        .build();
  }

  private double calculateAverageSpeed(final RunEntity run) {
    if (run.getDistance() == null || run.getDistance() == 0) {
      return 0;
    }
    var duration = Duration.between(run.getStartDateTime(), run.getFinishDateTime());
    if (duration.isZero()) {
      return 0;
    }

    return calculateAverageSpeed(run.getDistance(), duration.toHours());
  }

  private double calculateAverageSpeed(
      final double distanceMeter,
      final double durationHour) {
    return distanceMeter / (durationHour * 1000);
  }

  private RunDto.RunStatistic convertToRunStatistic(final RunEntity runEntity) {
    var runStatistic = new RunDto.RunStatistic();

    runStatistic.setUserId(runEntity.getUser().getId());
    runStatistic.setStartDateTime(runEntity.getStartDateTime());
    runStatistic.setFinishDateTime(runEntity.getFinishDateTime());
    runStatistic.setStartLatitude(runEntity.getStartLatitude());
    runStatistic.setFinishLatitude(runEntity.getFinishLatitude());
    runStatistic.setStartLongitude(runEntity.getStartLongitude());
    runStatistic.setFinishLongitude(runEntity.getFinishLongitude());
    runStatistic.setDistance(runEntity.getDistance());
    runStatistic.setAverageSpeed(calculateAverageSpeed(runEntity));

    return runStatistic;
  }
}
