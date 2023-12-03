package com.github.igordavydenko.tracker.rest.controller;

import com.github.igordavydenko.tracker.persistence.entity.UserEntity;
import com.github.igordavydenko.tracker.rest.dto.UserDto;
import com.github.igordavydenko.tracker.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.github.igordavydenko.tracker.rest.util.ValidationMessage.ERROR_POSITIVE_ID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

  private final ConversionService conversionService;
  private final UserService userService;

  @GetMapping
  public List<UserDto.UserResponse> findAll() {
    return toResponseList(userService.getAllUsers());
  }

  @GetMapping("/{id}")
  public UserDto.UserResponse findById(
      @PathVariable
      @Positive(message = ERROR_POSITIVE_ID) Long id
  ) {
    return toResponse(userService.getUserById(id));
  }

  @PostMapping
  public ResponseEntity<UserDto.UserResponse> createUser(
      @RequestBody
      @Valid final UserDto.UserRequest user
  ) {
    var savedUserEntity = userService.create(conversionService.convert(user, UserEntity.class));
    return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(savedUserEntity));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteUser(
      @PathVariable
      @Positive(message = ERROR_POSITIVE_ID) Long id
  ) {
    userService.delete(id);
  }

  @PutMapping(value = "/{id}")
  public UserDto.UserResponse updateUser(
      @PathVariable
      @Positive(message = ERROR_POSITIVE_ID) final Long id,
      @RequestBody
      @Valid final UserDto.UserRequest user
  ) {
    return toResponse(userService.update(id, conversionService.convert(user, UserEntity.class)));
  }

  private List<UserDto.UserResponse> toResponseList(final Iterable<UserEntity> userEntityIterable) {
    return StreamSupport
        .stream(userEntityIterable.spliterator(), false)
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  private UserDto.UserResponse toResponse(final UserEntity userEntity) {
    return conversionService.convert(userEntity, UserDto.UserResponse.class);
  }

}
