package com.github.igordavydenko.tracker.service;

import com.github.igordavydenko.tracker.persistence.entity.RunEntity;
import com.github.igordavydenko.tracker.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Random;

import static com.github.igordavydenko.tracker.service.RunServiceTest.generateRun;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatisticServiceTest {

  private static final Random random = new Random();

  @Mock
  private RunService runService;

  @InjectMocks
  private StatisticService statisticService;

  @Test
  public void When_GetUserRuns_Success() {
    var userId = random.nextLong();
    var userEntity = new UserEntity();
    userEntity.setId(userId);

    List<RunEntity> foundedRuns = List.of(generateRun(userEntity), generateRun(userEntity));

    when(runService.getUser(userId))
        .thenReturn(userEntity);
    when(runService.findByUserAndPeriod(userEntity, null, null))
        .thenReturn(foundedRuns);

    var result = statisticService.getUserRuns(userId, null, null);

    assertEquals(foundedRuns.size(), result.size());
  }

  @Test
  public void When_GetUserStatistic_Success() {
    var userId = random.nextLong();
    var userEntity = new UserEntity();
    userEntity.setId(userId);

    RunEntity activeRun = generateRun(userEntity);
    activeRun.setDistance(1000);
    RunEntity emptyDistanceRun = generateRun(userEntity);
    emptyDistanceRun.setDistance(0);

    List<RunEntity> foundedRuns = List.of(activeRun, emptyDistanceRun);

    when(runService.getUser(userId))
        .thenReturn(userEntity);
    when(runService.findByUserAndPeriod(userEntity, null, null))
        .thenReturn(foundedRuns);

    var result = statisticService.getUserStatistic(userId, null, null);

    assertEquals(foundedRuns.size(), result.getTotalRuns());
    assertEquals(activeRun.getDistance(), result.getTotalDistance());
    assertEquals(1.0, result.getAverageSpeed());
  }

}
