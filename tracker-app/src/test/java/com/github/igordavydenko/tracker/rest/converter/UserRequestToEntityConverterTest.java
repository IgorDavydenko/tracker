package com.github.igordavydenko.tracker.rest.converter;

import com.github.igordavydenko.tracker.persistence.entity.UserEntity;
import com.github.igordavydenko.tracker.rest.dto.UserDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserRequestToEntityConverterTest {

  private static final Random random = new Random();

  private final UserRequestToEntityConverter converter = new UserRequestToEntityConverter();

  @Test
  public void TestFullFilledData() {
    var user = new UserDto.UserRequest();
    user.setFirstName(RandomStringUtils.randomAlphabetic(10));
    user.setLastName(RandomStringUtils.randomAlphabetic(10));
    user.setBirthDate(LocalDate.of(1234, 1, 1));
    user.setSex(random.nextBoolean());

    UserEntity result = converter.convert(user);

    assertNotNull(result);
    assertEquals(user.getFirstName(), result.getFirstName());
    assertEquals(user.getLastName(), result.getLastName());
    assertEquals(user.getBirthDate(), result.getBirthDate());
    assertEquals(user.getSex(), result.getSex());
  }

  @Test
  public void TestEmptyData() {
    var user = new UserDto.UserRequest();

    UserEntity result = converter.convert(user);

    assertNotNull(result);
    assertNull(result.getFirstName());
    assertNull(result.getLastName());
    assertNull(result.getBirthDate());
    assertNull(result.getSex());
  }
}
