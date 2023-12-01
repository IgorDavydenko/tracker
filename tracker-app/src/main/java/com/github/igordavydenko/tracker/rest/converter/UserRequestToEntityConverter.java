package com.github.igordavydenko.tracker.rest.converter;

import com.github.igordavydenko.tracker.persistence.entity.UserEntity;
import com.github.igordavydenko.tracker.rest.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserRequestToEntityConverter implements Converter<UserDto.UserRequest, UserEntity> {

  @Override
  public UserEntity convert(UserDto.UserRequest source) {
    return UserEntity.builder()
        .firstName(source.getFirstName())
        .lastName(source.getLastName())
        .birthDate(source.getBirthDate())
        .sex(source.getSex())
        .build();
  }
}
