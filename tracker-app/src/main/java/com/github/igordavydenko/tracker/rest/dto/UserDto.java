package com.github.igordavydenko.tracker.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDto implements Serializable {

  @NoArgsConstructor
  @Getter
  @Setter
  public abstract static class AbstractUser implements Serializable {

    @NotBlank(message = "Field 'firstName' must be filled")
    private String firstName;

    @NotBlank(message = "Field 'lastName' must be filled")
    private String lastName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "Field 'birthDate' must be filled")
    private LocalDate birthDate;

    @NotNull(message = "Field 'sex' must be filled")
    private Boolean sex;
  }

  @NoArgsConstructor
  public static class UserRequest extends AbstractUser {
  }

  @NoArgsConstructor
  @Getter
  @Setter
  public static class UserResponse extends AbstractUser {
    @NotNull(message = "Field 'id' must be filled")
    private Long id;
  }
}
