package com.github.igordavydenko.tracker.rest.converter;

import com.github.igordavydenko.tracker.persistence.entity.RunEntity;
import com.github.igordavydenko.tracker.persistence.entity.UserEntity;
import com.github.igordavydenko.tracker.rest.dto.RunDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RunFinishToEntityConverter implements Converter<RunDto.RunFinish, RunEntity> {

  @Override
  public RunEntity convert(RunDto.RunFinish source) {
    var runEntity = new RunEntity();

    runEntity.setFinishDateTime(source.getFinishDateTime());
    runEntity.setFinishLatitude(source.getFinishLatitude());
    runEntity.setFinishLongitude(source.getFinishLongitude());
    runEntity.setDistance(source.getDistance());

    UserEntity user = new UserEntity();
    user.setId(source.getUserId());
    runEntity.setUser(user);

    return runEntity;
  }
}
