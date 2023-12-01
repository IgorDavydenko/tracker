package com.github.igordavydenko.tracker.rest.converter;

import com.github.igordavydenko.tracker.persistence.entity.RunEntity;
import com.github.igordavydenko.tracker.rest.dto.RunDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RunEntityToResponseConverter implements Converter<RunEntity, RunDto.RunInfo> {

  @Override
  public RunDto.RunInfo convert(RunEntity source) {
    var runInfo = new RunDto.RunInfo();

    runInfo.setStartDateTime(source.getStartDateTime());
    runInfo.setStartLatitude(source.getStartLatitude());
    runInfo.setStartLongitude(source.getStartLongitude());
    runInfo.setFinishDateTime(source.getFinishDateTime());
    runInfo.setFinishLatitude(source.getFinishLatitude());
    runInfo.setFinishLongitude(source.getFinishLongitude());
    runInfo.setDistance(source.getDistance());

    if (source.getUser() != null) {
      runInfo.setUserId(source.getUser().getId());
    }

    return runInfo;
  }
}
