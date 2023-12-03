package com.github.igordavydenko.tracker.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.igordavydenko.tracker.rest.dto.RunDto;
import com.github.igordavydenko.tracker.rest.dto.StatisticDto;
import com.github.igordavydenko.tracker.service.StatisticService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {StatisticController.class})
public class StatisticControllerTest {

  private static final String PATH_PREFIX = "/api/v1/statistics";
  private static final Random random = new Random();

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private MockMvc mockMvc;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private StatisticService statisticService;

  @Test
  public void Whet_GetUserStatistics_Success() throws Exception {
    var id = random.nextLong(0, Long.MAX_VALUE);

    StatisticDto statistic = StatisticDto.builder()
        .totalRuns(random.nextInt())
        .totalDistance(random.nextDouble())
        .averageSpeed(random.nextDouble())
        .build();

    when(statisticService.getUserStatistic(id, null, null))
        .thenReturn(statistic);

    mockMvc.perform(get(PATH_PREFIX + "/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalRuns").value(statistic.getTotalRuns()))
        .andExpect(jsonPath("$.totalDistance").value(statistic.getTotalDistance()))
        .andExpect(jsonPath("$.averageSpeed").value(statistic.getAverageSpeed()));
  }

  @Test
  public void Whet_GetUserRuns_Success() throws Exception {
    var id = random.nextLong(0, Long.MAX_VALUE);

    when(statisticService.getUserRuns(id, null, null))
        .thenReturn(List.of(new RunDto.RunStatistic(), new RunDto.RunStatistic()));

    mockMvc.perform(get(PATH_PREFIX + "/" + id + "/runs"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }
}
