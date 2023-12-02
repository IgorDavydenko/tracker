package com.github.igordavydenko.tracker.rest.converter;

import com.github.igordavydenko.tracker.persistence.entity.RunEntity;
import com.github.igordavydenko.tracker.rest.dto.RunDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RunStartToEntityConverterTest {

  private static final Random random = new Random();

  private final RunStartToEntityConverter converter = new RunStartToEntityConverter();

  @Test
  public void TestFullFilledData() {
    var startRun = new RunDto.RunStart();
    startRun.setUserId(random.nextLong());
    startRun.setStartDateTime(LocalDateTime.of(1234, 1, 1, 0, 0));
    startRun.setStartLatitude(random.nextDouble());
    startRun.setStartLongitude(random.nextDouble());

    RunEntity result = converter.convert(startRun);

    assertNotNull(result);
    assertEquals(startRun.getStartDateTime(), result.getStartDateTime());
    assertEquals(startRun.getStartLatitude(), result.getStartLatitude());
    assertEquals(startRun.getStartLongitude(), result.getStartLongitude());
    assertNull(result.getFinishDateTime());
    assertNull(result.getFinishLatitude());
    assertNull(result.getFinishLongitude());
    assertNull(result.getDistance());
    assertNotNull(result.getUser());
    assertEquals(startRun.getUserId(), result.getUser().getId());
  }

  @Test
  public void TestEmptyData() {
    var startRun = new RunDto.RunStart();

    RunEntity result = converter.convert(startRun);

    assertNotNull(result);
    assertNull(result.getStartDateTime());
    assertNull(result.getStartLatitude());
    assertNull(result.getStartLongitude());
    assertNull(result.getFinishDateTime());
    assertNull(result.getFinishLatitude());
    assertNull(result.getFinishLongitude());
    assertNull(result.getDistance());
    assertNotNull(result.getUser());
    assertNull(result.getUser().getId());
  }
}
