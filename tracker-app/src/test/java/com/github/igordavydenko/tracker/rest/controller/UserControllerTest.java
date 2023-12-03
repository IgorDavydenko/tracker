package com.github.igordavydenko.tracker.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.igordavydenko.tracker.exception.UserNotFoundException;
import com.github.igordavydenko.tracker.persistence.entity.UserEntity;
import com.github.igordavydenko.tracker.rest.dto.UserDto;
import com.github.igordavydenko.tracker.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static com.github.igordavydenko.tracker.service.UserServiceTest.generateUserEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class})
public class UserControllerTest {

  private static final String PATH_PREFIX = "/api/v1/users";
  private static final Random random = new Random();

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private MockMvc mockMvc;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserService userService;

  @Test
  public void Whet_FindAllUsers_Success() throws Exception {
    when(userService.getAllUsers())
        .thenReturn(List.of(new UserEntity(), new UserEntity()));

    mockMvc.perform(get(PATH_PREFIX))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  public void Whet_FindUserById_Success() throws Exception {
    var userEntity = generateUserEntity();

    when(userService.getUserById(userEntity.getId()))
        .thenReturn(userEntity);
    mockMvc.perform(get(PATH_PREFIX + "/" + userEntity.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userEntity.getId()))
        .andExpect(jsonPath("$.firstName").value(userEntity.getFirstName()))
        .andExpect(jsonPath("$.lastName").value(userEntity.getLastName()))
        .andExpect(jsonPath("$.birthDate").value(userEntity.getBirthDate().toString()))
        .andExpect(jsonPath("$.sex").value(userEntity.getSex()));
  }

  @Test
  public void Whet_CreateUser_Success() throws Exception {
    var id = random.nextLong(0, Long.MAX_VALUE);

    UserDto.UserRequest request = generateUserRequest();

    when(userService.create(any(UserEntity.class)))
        .thenAnswer(answer -> {
          var userEntity = answer.getArgument(0, UserEntity.class);
          userEntity.setId(id);
          return userEntity;
        });

    mockMvc.perform(post(PATH_PREFIX)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.firstName").value(request.getFirstName()))
        .andExpect(jsonPath("$.lastName").value(request.getLastName()))
        .andExpect(jsonPath("$.birthDate").value(request.getBirthDate().toString()))
        .andExpect(jsonPath("$.sex").value(request.getSex()));
  }

  @Test
  public void Whet_DeleteUser_Success() throws Exception {
    var id = random.nextLong(0L, Long.MAX_VALUE);
    mockMvc.perform(delete(PATH_PREFIX + "/" + id))
        .andExpect(status().isNoContent());
    verify(userService, times(1)).delete(id);
  }

  @Test
  public void Whet_UpdateUser_Success() throws Exception {
    UserDto.UserRequest request = generateUserRequest();

    UserEntity userEntity = generateUserEntity();

    when(userService.update(anyLong(), any(UserEntity.class)))
        .thenReturn(userEntity);

    mockMvc.perform(put(PATH_PREFIX + "/" + userEntity.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userEntity.getId()))
        .andExpect(jsonPath("$.firstName").value(userEntity.getFirstName()))
        .andExpect(jsonPath("$.lastName").value(userEntity.getLastName()))
        .andExpect(jsonPath("$.birthDate").value(userEntity.getBirthDate().toString()))
        .andExpect(jsonPath("$.sex").value(userEntity.getSex()));
  }

  @Test
  public void Whet_FindUserById_ExceptionUserNotFound() throws Exception {
    var id = 1L;

    when(userService.getUserById(id))
        .thenThrow(new UserNotFoundException(id));
    mockMvc.perform(get(PATH_PREFIX + "/" + id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value(HttpStatus.NOT_FOUND.value()))
        .andExpect(jsonPath("$.errorMessage").value("Data not found"))
        .andExpect(jsonPath("$.errorDetails.length()").value(1))
        .andExpect(jsonPath("$.errorDetails[0].message").value("User by id '1' not found"));
  }

  @Test
  public void Whet_CreateUser_ValidationError() throws Exception {
    UserDto.UserRequest request = generateUserRequest();
    request.setFirstName(null);
    mockMvc.perform(post(PATH_PREFIX)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.errorMessage").value("Business logic exception"))
        .andExpect(jsonPath("$.errorDetails[0].message").value("Field 'firstName' must be filled"))
        .andExpect(jsonPath("$.errorDetails[0].fieldName").value("firstName"));

    request = generateUserRequest();
    request.setLastName(null);
    mockMvc.perform(post(PATH_PREFIX)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.errorMessage").value("Business logic exception"))
        .andExpect(jsonPath("$.errorDetails[0].message").value("Field 'lastName' must be filled"))
        .andExpect(jsonPath("$.errorDetails[0].fieldName").value("lastName"));

    request = generateUserRequest();
    request.setBirthDate(null);
    mockMvc.perform(post(PATH_PREFIX)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.errorMessage").value("Business logic exception"))
        .andExpect(jsonPath("$.errorDetails[0].message").value("Field 'birthDate' must be filled"))
        .andExpect(jsonPath("$.errorDetails[0].fieldName").value("birthDate"));

    request = generateUserRequest();
    request.setSex(null);
    mockMvc.perform(post(PATH_PREFIX)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.errorMessage").value("Business logic exception"))
        .andExpect(jsonPath("$.errorDetails[0].message").value("Field 'sex' must be filled"))
        .andExpect(jsonPath("$.errorDetails[0].fieldName").value("sex"));
  }

  private UserDto.UserRequest generateUserRequest() {
    UserDto.UserRequest request = new UserDto.UserRequest();
    request.setFirstName(RandomStringUtils.randomAlphabetic(10));
    request.setLastName(RandomStringUtils.randomAlphabetic(10));
    request.setBirthDate(LocalDate.EPOCH);
    request.setSex(random.nextBoolean());
    return request;
  }
}
