package com.github.igordavydenko.tracker.persistence.repository;

import com.github.igordavydenko.tracker.persistence.entity.RunEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RunRepository extends JpaRepository<RunEntity, Long> {

  List<RunEntity> findByUserIdAndStartDateTimeBetween(Long userId, LocalDateTime fromDateTime, LocalDateTime toDateTime);


}
