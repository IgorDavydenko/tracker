package com.github.igordavydenko.tracker.rest.converter;

import com.github.igordavydenko.tracker.persistence.entity.RunEntity;
import com.github.igordavydenko.tracker.persistence.entity.UserEntity;
import com.github.igordavydenko.tracker.rest.dto.RunDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RunEntityToResponseConverterTest {

  private static final Random random = new Random();

  private final RunEntityToResponseConverter converter = new RunEntityToResponseConverter();

  @Test
  public void TestFullFilledData() {
    var runEntity = new RunEntity();
    runEntity.setId(random.nextLong());
    runEntity.setStartDateTime(LocalDateTime.of(1234, 1, 1, 0, 0));
    runEntity.setStartLatitude(random.nextDouble());
    runEntity.setStartLongitude(random.nextDouble());
    runEntity.setFinishDateTime(LocalDateTime.of(5678, 12, 31, 23, 59));
    runEntity.setFinishLatitude(random.nextDouble());
    runEntity.setFinishLongitude(random.nextDouble());
    runEntity.setDistance(random.nextInt());

    var userEntity = new UserEntity();
    userEntity.setId(random.nextLong());
    runEntity.setUser(userEntity);

    RunDto.RunInfo result = converter.convert(runEntity);

    assertNotNull(result);
    assertEquals(runEntity.getStartDateTime(), result.getStartDateTime());
    assertEquals(runEntity.getStartLatitude(), result.getStartLatitude());
    assertEquals(runEntity.getStartLongitude(), result.getStartLongitude());
    assertEquals(runEntity.getFinishDateTime(), result.getFinishDateTime());
    assertEquals(runEntity.getFinishLatitude(), result.getFinishLatitude());
    assertEquals(runEntity.getFinishLongitude(), result.getFinishLongitude());
    assertEquals(runEntity.getDistance(), result.getDistance());
    assertEquals(runEntity.getUser().getId(), result.getUserId());
  }

  @Test
  public void TestEmptyData() {
    var runEntity = new RunEntity();

    RunDto.RunInfo result = converter.convert(runEntity);

    assertNotNull(result);
    assertNull(result.getStartDateTime());
    assertNull(result.getStartLatitude());
    assertNull(result.getStartLongitude());
    assertNull(result.getFinishDateTime());
    assertNull(result.getFinishLatitude());
    assertNull(result.getFinishLongitude());
    assertNull(result.getDistance());
    assertNull(result.getUserId());
  }
}
