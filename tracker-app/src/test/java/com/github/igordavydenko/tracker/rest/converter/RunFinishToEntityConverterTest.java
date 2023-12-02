package com.github.igordavydenko.tracker.rest.converter;

import com.github.igordavydenko.tracker.persistence.entity.RunEntity;
import com.github.igordavydenko.tracker.rest.dto.RunDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RunFinishToEntityConverterTest {

  private static final Random random = new Random();

  private final RunFinishToEntityConverter converter = new RunFinishToEntityConverter();

  @Test
  public void TestFullFilledData() {
    var finishRun = new RunDto.RunFinish();
    finishRun.setFinishDateTime(LocalDateTime.of(5678, 12, 31, 23, 59));
    finishRun.setFinishLatitude(random.nextDouble());
    finishRun.setFinishLongitude(random.nextDouble());
    finishRun.setDistance(random.nextInt());

    RunEntity result = converter.convert(finishRun);

    assertNotNull(result);
    assertNull(result.getStartDateTime());
    assertNull(result.getStartLatitude());
    assertNull(result.getStartLongitude());
    assertEquals(finishRun.getFinishDateTime(), result.getFinishDateTime());
    assertEquals(finishRun.getFinishLatitude(), result.getFinishLatitude());
    assertEquals(finishRun.getFinishLongitude(), result.getFinishLongitude());
    assertEquals(finishRun.getDistance(), result.getDistance());
    assertNotNull(result.getUser());
    assertNull(result.getUser().getId());
  }

  @Test
  public void TestEmptyData() {
    var finishRun = new RunDto.RunFinish();

    RunEntity result = converter.convert(finishRun);

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
