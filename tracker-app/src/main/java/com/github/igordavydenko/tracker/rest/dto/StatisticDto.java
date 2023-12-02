package com.github.igordavydenko.tracker.rest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@Builder
public class StatisticDto implements Serializable {

  private int totalRuns;
  private double totalDistance;
  private double averageSpeed;

}
