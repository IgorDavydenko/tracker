package com.github.igordavydenko.tracker.rest.converter;

import com.github.igordavydenko.tracker.persistence.entity.UserEntity;
import com.github.igordavydenko.tracker.rest.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserEntityToResponseConverter implements Converter<UserEntity, UserDto.UserResponse> {

  @Override
  public UserDto.UserResponse convert(UserEntity source) {
    final var target = new UserDto.UserResponse();

    if (source.getId() != null) {
      target.setId(source.getId());
    }
    if (source.getFirstName() != null) {
      target.setFirstName(source.getFirstName());
    }
    if (source.getLastName() != null) {
      target.setLastName(source.getLastName());
    }
    if (source.getBirthDate() != null) {
      target.setBirthDate(source.getBirthDate());
    }
    if (source.getSex() != null) {
      target.setSex(source.getSex());
    }

    return target;
  }
}
