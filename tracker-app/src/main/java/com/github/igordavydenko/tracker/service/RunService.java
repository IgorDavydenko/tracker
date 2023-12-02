package com.github.igordavydenko.tracker.service;

import com.github.igordavydenko.tracker.exception.RunBusinessLogicException;
import com.github.igordavydenko.tracker.persistence.entity.RunEntity;
import com.github.igordavydenko.tracker.persistence.entity.UserEntity;
import com.github.igordavydenko.tracker.persistence.repository.RunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RunService {

  private static final int EARTH_RADIUS = 6371000;

  private final RunRepository runRepository;
  private final UserService userService;

  @Transactional(readOnly = true)
  public UserEntity getUser(Long id) {
    return userService.getUserById(id);
  }

  @Transactional
  public UserEntity getUserByRun(RunEntity run) {
    var user = run.getUser();
    if (user == null) {
      throw new IllegalArgumentException("Field user must be filled with a valid user");
    }
    return getUser(user.getId());
  }

  @Transactional(readOnly = true)
  public List<RunEntity> findByUserAndPeriod(
      final UserEntity user,
      final LocalDateTime fromDateTime,
      final LocalDateTime toDateTime
  ) {
    return runRepository.findByUserIdAndPeriod(user.getId(), fromDateTime, toDateTime);
  }

  @Transactional
  public RunEntity finishRun(final RunEntity source) {
    var user = getUserByRun(source);
    var activeRunOptional = getActiveRun(user);

    if (activeRunOptional.isPresent()) {
      var activeRun = activeRunOptional.get();
      updateProperties(source, activeRun);
      validateRun(activeRun);
      return save(activeRun);
    } else {
      throw new RunBusinessLogicException(String.format("No active run found for user with id: '%s'", user.getId()));
    }
  }

  @Transactional
  public RunEntity startRun(final RunEntity source) {
    var user = getUserByRun(source);
    var activeRunOptional = getActiveRun(user);
    if (activeRunOptional.isPresent()) {
      throw new RunBusinessLogicException("Other run in progress");
    }

    var target = new RunEntity();
    setProperties(source, target);
    target.setUser(user);
    validateStartRun(target);
    return save(target);
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

  private double calculateDistance(final RunEntity runEntity) {
    if (runEntity.getStartLatitude() == null
        || runEntity.getFinishLatitude() == null
        || runEntity.getStartLongitude() == null
        || runEntity.getFinishLongitude() == null
    ) {
      throw new RunBusinessLogicException("Can't calculate distance. Some latitude or longitude params is null");
    }
    double latitude = Math.toRadians(runEntity.getFinishLatitude() - runEntity.getStartLatitude());
    double longitude = Math.toRadians(runEntity.getFinishLongitude() - runEntity.getStartLongitude());

    double a = Math.cos(Math.toRadians(runEntity.getStartLatitude()))
        * Math.cos(Math.toRadians(runEntity.getFinishLatitude()))
        * Math.sin(longitude / 2) * Math.sin(longitude / 2)
        + Math.sin(latitude / 2) * Math.sin(latitude / 2);
    return EARTH_RADIUS * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
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
  }

  private void updateProperties(
      final RunEntity source,
      final RunEntity target
  ) {
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
    } else {
        target.setDistance((int) calculateDistance(target));
    }
  }

  private void validateStartRun(RunEntity runEntity) {
    if (runEntity.getStartDateTime() == null) {
      throw new RunBusinessLogicException("Run startDateTime can't be null");
    }
    if (runEntity.getStartLatitude() == null) {
      throw new RunBusinessLogicException("Run startLatitude can't be null");
    }
    if (runEntity.getStartLongitude() == null) {
      throw new RunBusinessLogicException("Run startLongitude can't be null");
    }
  }

  private void validateRun(RunEntity runEntity) {
    validateStartRun(runEntity);
    if (runEntity.getFinishDateTime() == null) {
      throw new RunBusinessLogicException("Run finishDateTime can't be null");
    }
    if (runEntity.getFinishLatitude() == null) {
      throw new RunBusinessLogicException("Run finishLatitude can't be null");
    }
    if (runEntity.getFinishLongitude() == null) {
      throw new RunBusinessLogicException("Run finishLongitude can't be null");
    }
    if (runEntity.getFinishDateTime().compareTo(runEntity.getStartDateTime()) < 0) {
      throw new RunBusinessLogicException("Finish time should be after start time");
    }
    if (runEntity.getDistance() < 0) {
      throw new RunBusinessLogicException("Run distance must be positive or zero");
    }
  }

}
