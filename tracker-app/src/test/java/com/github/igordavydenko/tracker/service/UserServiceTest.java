package com.github.igordavydenko.tracker.service;

import com.github.igordavydenko.tracker.exception.UserNotFoundException;
import com.github.igordavydenko.tracker.persistence.entity.UserEntity;
import com.github.igordavydenko.tracker.persistence.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  private static final Random random = new Random();
  private static final String EXCEPTION_USER_NOT_FOUND = "User by id '%s' not found";

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  @Captor
  private ArgumentCaptor<UserEntity> userEntityCaptor;

  @Test
  public void When_GetAllUsers_Success() {
    List<UserEntity> repositoryResponse = List.of(
        generateUserEntity(),
        generateUserEntity(),
        generateUserEntity()
    );

    when(userRepository.findAll()).thenReturn(repositoryResponse);

    var result = userService.getAllUsers();

    assertEquals(repositoryResponse.size(), result.size());
    result.forEach(resultUser -> {
      var founded = repositoryResponse.stream()
          .filter(user -> user.getId().equals(resultUser.getId()))
          .findFirst();
      assertTrue(founded.isPresent());
      assertUser(founded.get(), resultUser);
    });
  }

  @Test
  public void When_GetUserById_Success() {
    var id = random.nextLong();
    var foundedUser = generateUserEntity();
    foundedUser.setId(id);

    when(userRepository.findById(id))
        .thenReturn(Optional.of(foundedUser));

    var resultUser = userService.getUserById(id);
    assertEquals(id, resultUser.getId());
    assertUser(foundedUser, resultUser);
  }

  @Test
  public void When_GetUserById_Exception() {
    var id = random.nextLong();

    when(userRepository.findById(id))
        .thenReturn(Optional.empty());

    var exception = assertThrows(
        UserNotFoundException.class,
        () -> userService.getUserById(id));
    assertEquals(
        String.format(EXCEPTION_USER_NOT_FOUND, id),
        exception.getMessage()
    );
  }

  @Test
  public void When_Create_Success() {
    var newUser = generateUserEntity();
    newUser.setId(null);

    var createdUser = new UserEntity();
    createdUser.setId(random.nextLong());
    createdUser.setFirstName(newUser.getFirstName());
    createdUser.setLastName(newUser.getLastName());
    createdUser.setBirthDate(newUser.getBirthDate());
    createdUser.setSex(newUser.getSex());

    when(userRepository.save(newUser))
        .thenReturn(createdUser);

    var resultUser = userService.save(newUser);

    assertEquals(createdUser.getId(), resultUser.getId());
    assertUser(createdUser, resultUser);

    verify(userRepository).save(userEntityCaptor.capture());
    assertUser(newUser, userEntityCaptor.getValue());
  }

  @Test
  public void When_Update_Success() {
    var id = random.nextLong();
    var existUser = generateUserEntity();
    existUser.setId(id);

    var updateUser = generateUserEntity();
    updateUser.setId(id);

    when(userRepository.findById(id))
        .thenReturn(Optional.of(existUser));
    when(userRepository.save(any(UserEntity.class)))
        .thenReturn(updateUser);

    var resultUser = userService.update(id, updateUser);
    assertEquals(updateUser.getId(), resultUser.getId());
    assertUser(updateUser, resultUser);

    verify(userRepository).save(userEntityCaptor.capture());
    var captureUser = userEntityCaptor.getValue();
    assertEquals(updateUser.getId(), resultUser.getId());
    assertUser(updateUser, captureUser);
  }

  @Test
  public void When_Update_Exception() {
    var id = random.nextLong();

    when(userRepository.findById(id))
        .thenReturn(Optional.empty());

    var exception = assertThrows(
        UserNotFoundException.class,
        () -> userService.update(id, new UserEntity()));
    assertEquals(
        String.format(EXCEPTION_USER_NOT_FOUND, id),
        exception.getMessage()
    );
  }

  @Test
  public void When_Delete_Success() {
    var id = random.nextLong();
    var deletedUser = generateUserEntity();
    deletedUser.setId(id);

    when(userRepository.findById(id))
        .thenReturn(Optional.of(deletedUser));

    assertDoesNotThrow(() -> userService.delete(id));

    verify(userRepository).delete(userEntityCaptor.capture());
    var captureUser = userEntityCaptor.getValue();
    assertEquals(id, captureUser.getId());
    assertEquals(deletedUser, captureUser);
  }

  @Test
  public void whenDeleteUserNotExists_thenUserNotFoundException() {
    var id = random.nextLong();

    when(userRepository.findById(id))
        .thenReturn(Optional.empty());

    var exception = assertThrows(
        UserNotFoundException.class,
        () -> userService.delete(id));
    assertEquals(
        String.format(EXCEPTION_USER_NOT_FOUND, id),
        exception.getMessage()
    );
  }

  private void assertUser(
      final UserEntity expectedUser,
      final UserEntity actualUser
  ) {
    assertEquals(expectedUser.getFirstName(), actualUser.getFirstName());
    assertEquals(expectedUser.getLastName(), actualUser.getLastName());
    assertEquals(expectedUser.getBirthDate(), actualUser.getBirthDate());
    assertEquals(expectedUser.getSex(), actualUser.getSex());
  }

  public static UserEntity generateUserEntity() {
    var userEntity = new UserEntity();
    userEntity.setId(random.nextLong(0L, Long.MAX_VALUE));
    userEntity.setFirstName(RandomStringUtils.randomAlphabetic(10));
    userEntity.setLastName(RandomStringUtils.randomAlphabetic(10));
    userEntity.setBirthDate(generateLocalDate());
    userEntity.setSex(random.nextBoolean());
    return userEntity;
  }

  private static LocalDate generateLocalDate() {
    return LocalDate.ofEpochDay(ThreadLocalRandom
        .current()
        .nextLong(
            LocalDate.of(1900, 1, 1).toEpochDay(),
            LocalDate.now().toEpochDay()
        ));
  }
}
