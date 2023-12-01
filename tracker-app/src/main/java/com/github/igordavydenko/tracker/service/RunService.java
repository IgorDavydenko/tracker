package com.github.igordavydenko.tracker.service;

import com.github.igordavydenko.tracker.exception.RunBusinessLogicException;
import com.github.igordavydenko.tracker.exception.UserNotFoundException;
import com.github.igordavydenko.tracker.persistence.entity.RunEntity;
import com.github.igordavydenko.tracker.persistence.entity.UserEntity;
import com.github.igordavydenko.tracker.persistence.repository.RunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RunService {

  private static final int EARTH_RADIUS = 6371000;

  private final RunRepository runRepository;
  private final UserService userService;

  @Transactional
  public RunEntity startRun(final RunEntity source) {
    var sourceUser = source.getUser();
    if (sourceUser == null) {
      throw new IllegalArgumentException("Field user must be filled with a valid user");
    }

    Optional<UserEntity> foundedUser = userService.getUserById(sourceUser.getId());
    if (foundedUser.isPresent()) {
      var user = foundedUser.get();

      var activeRunOptional = getActiveRun(user);
      if (activeRunOptional.isPresent()) {
        throw new RunBusinessLogicException("Other run in progress");
      }

      var target = new RunEntity();
      setProperties(source, target);
      target.setUser(user);

      return save(target);
    } else {
      throw new UserNotFoundException(String.format("User by id '%s' not found", sourceUser.getId()));
    }
  }

  @Transactional
  public RunEntity finishRun(final RunEntity source) {
    var sourceUser = source.getUser();
    if (sourceUser == null) {
      throw new IllegalArgumentException("Field user must be filled with a valid user");
    }

    Optional<UserEntity> foundedUser = userService.getUserById(sourceUser.getId());
    if (foundedUser.isPresent()) {
      var user = foundedUser.get();

      var activeRunOptional = getActiveRun(user);

      if (activeRunOptional.isPresent()) {
        var activeRun = activeRunOptional.get();
        setProperties(source, activeRun);
        calculateDistance(activeRun);
        validate(activeRun);
        return save(activeRun);
      } else {
        throw new RunBusinessLogicException("No active run found for user with id: " + sourceUser.getId());
      }
    } else {
      throw new UserNotFoundException(String.format("User by id '%s' not found", sourceUser.getId()));
    }
  }

  @Transactional
  public RunEntity save(RunEntity runEntity) {
    return runRepository.save(runEntity);
  }

  private Optional<RunEntity> getActiveRun(UserEntity user) {
    return user.getRuns().stream()
        .filter(run -> run.getFinishDateTime() == null)
        .findFirst();
  }

  private void setProperties(
      final RunEntity source,
      final RunEntity target
  ) {
    if (source.getStartDateTime() != null) {
      target.setStartDateTime(source.getStartDateTime());
    }
    if (source.getStartLatitude() != null) {
      target.setStartLatitude(source.getStartLatitude());
    }
    if (source.getStartLongitude() != null) {
      target.setStartLongitude(source.getStartLongitude());
    }

    if (source.getFinishDateTime() != null) {
      target.setFinishDateTime(source.getFinishDateTime());
    }
    if (source.getFinishLatitude() != null) {
      target.setFinishLatitude(source.getFinishLatitude());
    }
    if (source.getFinishLongitude() != null) {
      target.setFinishLongitude(source.getFinishLongitude());
    }
    if (source.getDistance() != null) {
      target.setDistance(source.getDistance());
    }
  }

  private void calculateDistance(final RunEntity runEntity) {
    if (runEntity.getDistance() != null
        || runEntity.getStartLatitude() == null
        || runEntity.getFinishLatitude() == null
        || runEntity.getStartLongitude() == null
        || runEntity.getFinishLongitude() == null
    ) {
      return;
    }
    double latitude = Math.toRadians(runEntity.getFinishLatitude() - runEntity.getStartLatitude());
    double longitude = Math.toRadians(runEntity.getFinishLongitude() - runEntity.getStartLongitude());

    double a = Math.cos(Math.toRadians(runEntity.getStartLatitude()))
        * Math.cos(Math.toRadians(runEntity.getFinishLatitude()))
        * Math.sin(longitude / 2) * Math.sin(longitude / 2)
        + Math.sin(latitude / 2) * Math.sin(latitude / 2);

    runEntity.setDistance(EARTH_RADIUS * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
  }

  private void validate(RunEntity runEntity) {
    if (runEntity.getStartDateTime() == null) {
      throw new RunBusinessLogicException("Run startDateTime can't be null");
    }
    if (runEntity.getFinishDateTime() == null) {
      throw new RunBusinessLogicException("Run finishDateTime can't be null");
    }
    if (runEntity.getFinishDateTime().isBefore(runEntity.getStartDateTime())) {
      throw new RunBusinessLogicException("Finish time should be after start time");
    }
    if (runEntity.getDistance() < 0) {
      throw new RunBusinessLogicException("Run distance must be positive or zero");
    }
  }

}
