package com.github.igordavydenko.tracker.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.igordavydenko.tracker.persistence.entity.RunEntity;
import com.github.igordavydenko.tracker.rest.dto.RunDto;
import com.github.igordavydenko.tracker.service.RunService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {RunController.class})
public class RunControllerTest {

  private static final String PATH_PREFIX = "/api/v1/runs";
  private static final Random random = new Random();
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private MockMvc mockMvc;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private RunService runService;

  @Captor
  private ArgumentCaptor<RunEntity> runEntityCaptor;

  @Test
  public void Whet_StartRun_Success() throws Exception {
    var id = random.nextLong(0, Long.MAX_VALUE);

    RunDto.RunStart runStart = new RunDto.RunStart();
    runStart.setUserId(random.nextLong(1L, Long.MAX_VALUE));
    runStart.setStartDateTime(LocalDateTime.now());
    runStart.setStartLatitude(random.nextDouble(-90, 90));
    runStart.setStartLongitude(random.nextDouble(-180, 180));

    when(runService.startRun(any(RunEntity.class)))
        .thenAnswer(answer -> {
          var runEntity = answer.getArgument(0, RunEntity.class);
          runEntity.setId(id);
          return runEntity;
        });

    mockMvc.perform(post(PATH_PREFIX + "/start")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(runStart)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.startDateTime").value(runStart.getStartDateTime().format(formatter)))
        .andExpect(jsonPath("$.startLatitude").value(runStart.getStartLatitude()))
        .andExpect(jsonPath("$.startLongitude").value(runStart.getStartLongitude()))
        .andExpect(jsonPath("$.finishDateTime").isEmpty())
        .andExpect(jsonPath("$.finishLatitude").isEmpty())
        .andExpect(jsonPath("$.finishLongitude").isEmpty())
        .andExpect(jsonPath("$.distance").isEmpty());


    verify(runService).startRun(runEntityCaptor.capture());
    var runCaptor = runEntityCaptor.getValue();
    assertEquals(runStart.getStartDateTime().format(formatter), runCaptor.getStartDateTime().format(formatter));
    assertEquals(runStart.getStartLatitude(), runCaptor.getStartLatitude());
    assertEquals(runStart.getStartLongitude(), runCaptor.getStartLongitude());
    assertNull(runCaptor.getFinishDateTime());
    assertNull(runCaptor.getFinishLatitude());
    assertNull(runCaptor.getFinishLongitude());
    assertNull(runCaptor.getDistance());
  }

  @Test
  public void Whet_FinishRun_Success() throws Exception {
    RunDto.RunFinish runFinish = new RunDto.RunFinish();
    runFinish.setUserId(random.nextLong(1L, Long.MAX_VALUE));
    runFinish.setFinishDateTime(LocalDateTime.now());
    runFinish.setFinishLatitude(random.nextDouble(-90, 90));
    runFinish.setFinishLongitude(random.nextDouble(-180, 180));
    runFinish.setDistance(random.nextInt(0, Integer.MAX_VALUE));

    RunEntity runEntity = new RunEntity();
    runEntity.setStartDateTime(LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0));
    runEntity.setStartLatitude(random.nextDouble(-90, 90));
    runEntity.setStartLongitude(random.nextDouble(-180, 180));
    runEntity.setFinishDateTime(LocalDateTime.of(1970, 1, 1, 1, 0, 0, 0));
    runEntity.setFinishLatitude(random.nextDouble(-90, 90));
    runEntity.setFinishLongitude(random.nextDouble(-180, 180));
    runEntity.setDistance(random.nextInt(0, Integer.MAX_VALUE));

    when(runService.finishRun(any(RunEntity.class)))
        .thenReturn(runEntity);

    mockMvc.perform(post(PATH_PREFIX + "/finish")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(runFinish)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.startDateTime").value(runEntity.getStartDateTime().format(formatter)))
        .andExpect(jsonPath("$.startLatitude").value(runEntity.getStartLatitude()))
        .andExpect(jsonPath("$.startLongitude").value(runEntity.getStartLongitude()))
        .andExpect(jsonPath("$.finishDateTime").value(runEntity.getFinishDateTime().format(formatter)))
        .andExpect(jsonPath("$.finishLatitude").value(runEntity.getFinishLatitude()))
        .andExpect(jsonPath("$.finishLongitude").value(runEntity.getFinishLongitude()))
        .andExpect(jsonPath("$.distance").value(runEntity.getDistance()));

    verify(runService).finishRun(runEntityCaptor.capture());
    var runCaptor = runEntityCaptor.getValue();
    assertNull(runCaptor.getStartDateTime());
    assertNull(runCaptor.getStartLatitude());
    assertNull(runCaptor.getStartLongitude());
    assertEquals(runFinish.getFinishDateTime().format(formatter), runCaptor.getFinishDateTime().format(formatter));
    assertEquals(runFinish.getFinishLatitude(), runCaptor.getFinishLatitude());
    assertEquals(runFinish.getFinishLongitude(), runCaptor.getFinishLongitude());
    assertEquals(runFinish.getDistance(), runCaptor.getDistance());
  }

  @Test
  public void Whet_StartRun_ValidationError() throws Exception {
    RunDto.RunStart runStart = generateRunStart();
    runStart.setUserId(null);
    mockMvc.perform(post(PATH_PREFIX + "/start")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(runStart)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.errorMessage").value("Business logic exception"))
        .andExpect(jsonPath("$.errorDetails[0].message").value("Field 'userId' must be filled"))
        .andExpect(jsonPath("$.errorDetails[0].fieldName").value("userId"));

    runStart = generateRunStart();
    runStart.setUserId(-10L);
    mockMvc.perform(post(PATH_PREFIX + "/start")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(runStart)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.errorMessage").value("Business logic exception"))
        .andExpect(jsonPath("$.errorDetails[0].message").value("Field 'userId' must be a positive integer"))
        .andExpect(jsonPath("$.errorDetails[0].fieldName").value("userId"));

    runStart = generateRunStart();
    runStart.setStartDateTime(null);
    mockMvc.perform(post(PATH_PREFIX + "/start")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(runStart)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.errorMessage").value("Business logic exception"))
        .andExpect(jsonPath("$.errorDetails[0].message").value("Field 'startDateTime' must be filled"))
        .andExpect(jsonPath("$.errorDetails[0].fieldName").value("startDateTime"));

    runStart = generateRunStart();
    runStart.setStartLatitude(null);
    mockMvc.perform(post(PATH_PREFIX + "/start")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(runStart)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.errorMessage").value("Business logic exception"))
        .andExpect(jsonPath("$.errorDetails[0].message").value("Field 'startLatitude' must be filled"))
        .andExpect(jsonPath("$.errorDetails[0].fieldName").value("startLatitude"));

    runStart = generateRunStart();
    runStart.setStartLatitude(1000D);
    mockMvc.perform(post(PATH_PREFIX + "/start")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(runStart)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.errorMessage").value("Business logic exception"))
        .andExpect(jsonPath("$.errorDetails[0].message").value("Field 'startLatitude' must be less than or equal to 90.0"))
        .andExpect(jsonPath("$.errorDetails[0].fieldName").value("startLatitude"));


    runStart = generateRunStart();
    runStart.setStartLongitude(null);
    mockMvc.perform(post(PATH_PREFIX + "/start")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(runStart)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.errorMessage").value("Business logic exception"))
        .andExpect(jsonPath("$.errorDetails[0].message").value("Field 'startLongitude' must be filled"))
        .andExpect(jsonPath("$.errorDetails[0].fieldName").value("startLongitude"));

    runStart = generateRunStart();
    runStart.setStartLongitude(1000D);
    mockMvc.perform(post(PATH_PREFIX + "/start")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(runStart)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.errorMessage").value("Business logic exception"))
        .andExpect(jsonPath("$.errorDetails[0].message").value("Field 'startLongitude' must be less than or equal to 180.0"))
        .andExpect(jsonPath("$.errorDetails[0].fieldName").value("startLongitude"));
  }

  @Test
  public void Whet_FinishRun_ValidationError() throws Exception {
    RunDto.RunFinish runFinish = generateRunFinish();
    runFinish.setUserId(null);
    mockMvc.perform(post(PATH_PREFIX + "/finish")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(runFinish)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.errorMessage").value("Business logic exception"))
        .andExpect(jsonPath("$.errorDetails[0].message").value("Field 'userId' must be filled"))
        .andExpect(jsonPath("$.errorDetails[0].fieldName").value("userId"));

    runFinish = generateRunFinish();
    runFinish.setUserId(-10L);
    mockMvc.perform(post(PATH_PREFIX + "/finish")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(runFinish)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.errorMessage").value("Business logic exception"))
        .andExpect(jsonPath("$.errorDetails[0].message").value("Field 'userId' must be a positive integer"))
        .andExpect(jsonPath("$.errorDetails[0].fieldName").value("userId"));

    runFinish = generateRunFinish();
    runFinish.setFinishDateTime(null);
    mockMvc.perform(post(PATH_PREFIX + "/finish")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(runFinish)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.errorMessage").value("Business logic exception"))
        .andExpect(jsonPath("$.errorDetails[0].message").value("Field 'finishDateTime' must be filled"))
        .andExpect(jsonPath("$.errorDetails[0].fieldName").value("finishDateTime"));

    runFinish = generateRunFinish();
    runFinish.setFinishLatitude(null);
    mockMvc.perform(post(PATH_PREFIX + "/finish")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(runFinish)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.errorMessage").value("Business logic exception"))
        .andExpect(jsonPath("$.errorDetails[0].message").value("Field 'finishLatitude' must be filled"))
        .andExpect(jsonPath("$.errorDetails[0].fieldName").value("finishLatitude"));

    runFinish = generateRunFinish();
    runFinish.setFinishLatitude(1000D);
    mockMvc.perform(post(PATH_PREFIX + "/finish")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(runFinish)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.errorMessage").value("Business logic exception"))
        .andExpect(jsonPath("$.errorDetails[0].message").value("Field 'finishLatitude' must be less than or equal to 90.0"))
        .andExpect(jsonPath("$.errorDetails[0].fieldName").value("finishLatitude"));

    runFinish = generateRunFinish();
    runFinish.setFinishLongitude(null);
    mockMvc.perform(post(PATH_PREFIX + "/finish")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(runFinish)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.errorMessage").value("Business logic exception"))
        .andExpect(jsonPath("$.errorDetails[0].message").value("Field 'finishLongitude' must be filled"))
        .andExpect(jsonPath("$.errorDetails[0].fieldName").value("finishLongitude"));

    runFinish = generateRunFinish();
    runFinish.setFinishLongitude(1000D);
    mockMvc.perform(post(PATH_PREFIX + "/finish")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(runFinish)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.errorMessage").value("Business logic exception"))
        .andExpect(jsonPath("$.errorDetails[0].message").value("Field 'finishLongitude' must be less than or equal to 180.0"))
        .andExpect(jsonPath("$.errorDetails[0].fieldName").value("finishLongitude"));

    runFinish = generateRunFinish();
    runFinish.setDistance(null);
    mockMvc.perform(post(PATH_PREFIX + "/finish")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(runFinish)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.errorMessage").value("Business logic exception"))
        .andExpect(jsonPath("$.errorDetails[0].message").value("Field 'distance' must be filled"))
        .andExpect(jsonPath("$.errorDetails[0].fieldName").value("distance"));

    runFinish = generateRunFinish();
    runFinish.setDistance(-1000);
    mockMvc.perform(post(PATH_PREFIX + "/finish")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(runFinish)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.errorMessage").value("Business logic exception"))
        .andExpect(jsonPath("$.errorDetails[0].message").value("Field 'distance' must be a positive number or zero"))
        .andExpect(jsonPath("$.errorDetails[0].fieldName").value("distance"));
  }

  private RunDto.RunStart generateRunStart() {
    RunDto.RunStart runStart = new RunDto.RunStart();
    runStart.setUserId(random.nextLong(1, Long.MAX_VALUE));
    runStart.setStartDateTime(LocalDateTime.now());
    runStart.setStartLatitude(random.nextDouble(-90, 90));
    runStart.setStartLongitude(random.nextDouble(-180, 180));
    return runStart;
  }

  private RunDto.RunFinish generateRunFinish() {
    RunDto.RunFinish runFinish = new RunDto.RunFinish();
    runFinish.setUserId(random.nextLong(1, Long.MAX_VALUE));
    runFinish.setFinishDateTime(LocalDateTime.now());
    runFinish.setFinishLatitude(random.nextDouble(-90, 90));
    runFinish.setFinishLongitude(random.nextDouble(-180, 180));
    runFinish.setDistance(random.nextInt(0, Integer.MAX_VALUE));
    return runFinish;
  }

}
