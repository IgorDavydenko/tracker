package com.github.igordavydenko.tracker.service;

import com.github.igordavydenko.tracker.exception.UserNotFoundException;
import com.github.igordavydenko.tracker.persistence.entity.UserEntity;
import com.github.igordavydenko.tracker.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public List<UserEntity> getAllUsers() {
    return userRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Optional<UserEntity> getUserById(Long id) {
    return userRepository.findById(id);
  }

  @Transactional
  public UserEntity create(UserEntity source) {
    return save(source);
  }

  @Transactional
  public Optional<UserEntity> update(Long id, UserEntity source) {
    return getUserById(id)
        .map(target -> {
          setProperties(source, target);
          return save(target);
        });
  }

  public void delete(Long id) {
    var user = getUserById(id)
        .orElseThrow(() -> new UserNotFoundException(String.format("User by id '%s' not found", id)));
    userRepository.delete(user);
  }

  @Transactional
  public UserEntity save(final UserEntity userEntity) {
    return userRepository.save(userEntity);
  }

  private void setProperties(
      final UserEntity source,
      final UserEntity target
  ) {
    target.setFirstName(source.getFirstName());
    target.setLastName(source.getLastName());
    target.setBirthDate(source.getBirthDate());
    target.setSex(source.getSex());
  }
}
