package com.github.igordavydenko.tracker.rest.controller;

import com.github.igordavydenko.tracker.persistence.entity.RunEntity;
import com.github.igordavydenko.tracker.rest.dto.RunDto;
import com.github.igordavydenko.tracker.service.RunService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/runs")
@Validated
public class RunController {

  private final ConversionService conversionService;
  private final RunService runService;

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/start")
  public RunDto.RunInfo startRun(
      @RequestBody
      @Valid final RunDto.RunStart runStart
  ) {
    return toResponse(
        runService.startRun(
            conversionService.convert(runStart, RunEntity.class)));
  }

  @PostMapping("/finish")
  public RunDto.RunInfo finishRun(
      @RequestBody
      @Valid final RunDto.RunFinish runFinish
  ) {
    return toResponse(
        runService.finishRun(
            conversionService.convert(runFinish, RunEntity.class)));
  }

  private RunDto.RunInfo toResponse(final RunEntity runEntity) {
    return conversionService.convert(runEntity, RunDto.RunInfo.class);
  }
}
