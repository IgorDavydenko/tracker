package com.github.igordavydenko.tracker.rest.converter;

import com.github.igordavydenko.tracker.persistence.entity.RunEntity;
import com.github.igordavydenko.tracker.persistence.entity.UserEntity;
import com.github.igordavydenko.tracker.rest.dto.RunDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RunStartToEntityConverter implements Converter<RunDto.RunStart, RunEntity> {

  @Override
  public RunEntity convert(RunDto.RunStart source) {
    var runEntity = new RunEntity();

    runEntity.setStartDateTime(source.getStartDateTime());
    runEntity.setStartLatitude(source.getStartLatitude());
    runEntity.setStartLongitude(source.getStartLongitude());

    var user = new UserEntity();
    user.setId(source.getUserId());
    runEntity.setUser(user);

    return runEntity;
  }
}
