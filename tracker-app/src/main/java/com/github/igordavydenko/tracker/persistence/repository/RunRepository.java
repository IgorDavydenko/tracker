package com.github.igordavydenko.tracker.persistence.repository;

import com.github.igordavydenko.tracker.persistence.entity.RunEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RunRepository extends JpaRepository<RunEntity, Long> {

  default List<RunEntity> findByUserIdAndPeriod(Long userId, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
    if (fromDateTime != null && toDateTime != null) {
      return findByUserIdAndStartDateTimeBetween(userId, fromDateTime, toDateTime);
    } else if (fromDateTime != null) {
      return findByUserIdAndStartDateTimeAfter(userId, fromDateTime);
    } else if (toDateTime != null) {
      return findByUserIdAndStartDateTimeBefore(userId, toDateTime);
    } else {
      return findByUserId(userId);
    }
  }

  List<RunEntity> findByUserIdAndStartDateTimeBetween(Long userId, LocalDateTime fromDateTime, LocalDateTime toDateTime);

  List<RunEntity> findByUserIdAndStartDateTimeAfter(Long userId, LocalDateTime fromDateTime);

  List<RunEntity> findByUserIdAndStartDateTimeBefore(Long userId, LocalDateTime toDateTime);

  List<RunEntity> findByUserId(Long userId);

}
