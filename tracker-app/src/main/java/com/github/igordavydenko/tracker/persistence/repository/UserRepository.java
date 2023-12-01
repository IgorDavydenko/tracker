package com.github.igordavydenko.tracker.persistence.repository;

import com.github.igordavydenko.tracker.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

}
