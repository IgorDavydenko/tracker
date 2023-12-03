package com.github.igordavydenko.tracker.service;

import com.github.igordavydenko.tracker.exception.RunBusinessLogicException;
import com.github.igordavydenko.tracker.persistence.entity.RunEntity;
import com.github.igordavydenko.tracker.persistence.entity.UserEntity;
import com.github.igordavydenko.tracker.persistence.repository.RunRepository;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RunServiceTest {

  private static final Random random = new Random();
  private static final RandomDataGenerator numericGenerator = new RandomDataGenerator();

  @InjectMocks
  private RunService runService;

  @Mock
  private RunRepository runRepository;

  @Mock
  private UserService userService;

  @Captor
  private ArgumentCaptor<RunEntity> runEntityCaptor;

  @Test
  public void When_GetUser_Success() {
    var userId = random.nextLong();

    when(userService.getUserById(userId))
        .thenAnswer(answer -> {
          var user = new UserEntity();
          user.setId(answer.getArgument(0));
          return user;
        });

    var resultUser = runService.getUser(userId);

    assertNotNull(resultUser);
    assertEquals(userId, resultUser.getId());
  }

  @Test
  public void When_GetUser_NoExceptionHandler() {
    Long userId = 1L;

    when(userService.getUserById(userId))
        .thenThrow(new RuntimeException());

    assertThrows(
        RuntimeException.class,
        () -> userService.getUserById(userId));
  }

  @Test
  public void When_GetUserByRun_Success() {
    var userId = random.nextLong();
    var userEntity = new UserEntity();
    userEntity.setId(userId);

    var runEntity = new RunEntity();
    runEntity.setUser(userEntity);

    when(userService.getUserById(userId))
        .thenReturn(userEntity);

    var result = runService.getUserByRun(runEntity);

    assertEquals(userId, result.getId());
  }

  @Test
  public void When_GetUserByRun_AndUserIsNull_Exception() {
    var exception = assertThrows(
        IllegalArgumentException.class,
        () -> runService.getUserByRun(new RunEntity()));

    assertEquals(
        "Field user must be filled with a valid user",
        exception.getMessage()
    );
  }

  @Test
  public void When_FindByUserAndPeriod_Success() {
    UserEntity user = new UserEntity();
    user.setId(random.nextLong());

    var fromDateTime = LocalDateTime.of(2000, 1, 1, 0, 0);
    var toDateTime = LocalDateTime.of(2100, 12, 31, 23, 59);

    List<RunEntity> expectedRuns = List.of(
        new RunEntity(), new RunEntity(), new RunEntity()
    );

    when(runRepository.findByUserIdAndPeriod(user.getId(), fromDateTime, toDateTime))
        .thenReturn(expectedRuns);

    var result = runService.findByUserAndPeriod(user, fromDateTime, toDateTime);
    assertEquals(expectedRuns, result);
  }

  @Test
  public void When_FinishRun_Success() {
    var user = new UserEntity();
    user.setId(random.nextLong());

    var activeRun = generateStartRun(user);
    var fullRun = generateRun(user);
    user.setRuns(List.of(activeRun, fullRun));

    var finishRun = generateRun(user);
    finishRun.setFinishDateTime(activeRun.getStartDateTime().plusHours(1L));

    when(userService.getUserById(user.getId()))
        .thenReturn(user);
    when(runRepository.save(any(RunEntity.class)))
        .thenAnswer(answer -> answer.getArgument(0));

    var result = runService.finishRun(finishRun);

    assertEquals(activeRun.getStartDateTime(), result.getStartDateTime());
    assertEquals(activeRun.getStartLatitude(), result.getStartLatitude());
    assertEquals(activeRun.getStartLongitude(), result.getStartLongitude());
    assertEquals(finishRun.getFinishDateTime(), result.getFinishDateTime());
    assertEquals(finishRun.getFinishLatitude(), result.getFinishLatitude());
    assertEquals(finishRun.getFinishLongitude(), result.getFinishLongitude());
    assertEquals(finishRun.getDistance(), result.getDistance());

    verify(runRepository).save(runEntityCaptor.capture());
    var runCaptor = runEntityCaptor.getValue();
    assertEquals(activeRun.getStartDateTime(), runCaptor.getStartDateTime());
    assertEquals(activeRun.getStartLatitude(), runCaptor.getStartLatitude());
    assertEquals(activeRun.getStartLongitude(), runCaptor.getStartLongitude());
    assertEquals(finishRun.getFinishDateTime(), runCaptor.getFinishDateTime());
    assertEquals(finishRun.getFinishLatitude(), runCaptor.getFinishLatitude());
    assertEquals(finishRun.getFinishLongitude(), runCaptor.getFinishLongitude());
    assertEquals(finishRun.getDistance(), runCaptor.getDistance());
  }

  @Test
  public void When_FinishRun_NoActiveRunException() {
    var user = new UserEntity();
    user.setId(random.nextLong());

    var finishedRun = generateRun(user);
    user.setRuns(List.of(finishedRun));

    var sourceRun = generateRun(user);

    when(userService.getUserById(user.getId()))
        .thenReturn(user);

    var exception = assertThrows(
        RunBusinessLogicException.class,
        () -> runService.finishRun(sourceRun)
    );
    assertEquals(
        String.format("No active run found for user with id: '%s'", user.getId()),
        exception.getMessage());
  }

  @Test
  public void When_StartRun_Success() {
    var user = new UserEntity();
    user.setId(random.nextLong());

    var newRun = generateRun(user);

    when(userService.getUserById(user.getId()))
        .thenReturn(user);
    when(runRepository.save(any(RunEntity.class)))
        .thenAnswer(answer -> answer.getArgument(0));

    var result = runService.startRun(newRun);

    assertEquals(newRun.getStartDateTime(), result.getStartDateTime());
    assertEquals(newRun.getStartLatitude(), result.getStartLatitude());
    assertEquals(newRun.getStartLongitude(), result.getStartLongitude());
    assertNull(result.getFinishDateTime());
    assertNull(result.getFinishLatitude());
    assertNull(result.getFinishLongitude());
    assertNull(result.getDistance());

    verify(runRepository).save(runEntityCaptor.capture());
    var runCaptor = runEntityCaptor.getValue();
    assertEquals(newRun.getStartDateTime(), runCaptor.getStartDateTime());
    assertEquals(newRun.getStartLatitude(), runCaptor.getStartLatitude());
    assertEquals(newRun.getStartLongitude(), runCaptor.getStartLongitude());
    assertNull(result.getFinishDateTime());
    assertNull(result.getFinishLatitude());
    assertNull(result.getFinishLongitude());
    assertNull(result.getDistance());
  }

  @Test
  public void When_StartRun_OtherRunActiveException() {
    var user = new UserEntity();
    user.setId(random.nextLong());

    var activeRun = generateStartRun(user);
    user.setRuns(List.of(activeRun));

    var newRun = generateRun(user);

    when(userService.getUserById(user.getId()))
        .thenReturn(user);

    var exception = assertThrows(
        RunBusinessLogicException.class,
        () -> runService.startRun(newRun)
    );
    assertEquals(
        "Other run in progress",
        exception.getMessage());
  }

  public static RunEntity generateStartRun(UserEntity user) {
    var run = new RunEntity();
    run.setUser(user);

    run.setStartDateTime(generateDateTime());
    run.setStartLatitude(generateLatitude());
    run.setStartLongitude(generateLongitude());

    return run;
  }

  public static RunEntity generateRun(UserEntity user) {
    var run = generateStartRun(user);

    run.setFinishDateTime(run.getStartDateTime().plusHours(1L));
    run.setFinishLatitude(generateLatitude());
    run.setFinishLongitude(generateLongitude());
    run.setDistance(numericGenerator.nextInt(0, Integer.MAX_VALUE));
    return run;
  }

  public static LocalDateTime generateDateTime() {
    return LocalDateTime.of(
        LocalDate.ofEpochDay(ThreadLocalRandom
            .current()
            .nextLong(
                LocalDate.of(1900, 1, 1).toEpochDay(),
                LocalDate.now().toEpochDay()
            )),
        LocalTime.MIDNIGHT
    );
  }

  public static Double generateLatitude() {
    return numericGenerator.nextUniform(-90L, 90L);
  }

  public static Double generateLongitude() {
    return numericGenerator.nextUniform(-180L, 180L);

  }

}
