package com.example.HMS.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StaffMapper {

    Long staffId;
    @Pattern(regexp = "^[a-zA-Z ]*$",message = "Name must have only letters")
    @NotBlank(message = "Name cannot be null")
    String name;
    @NotBlank(message = "Password cannot be null")
    String password;
    @Pattern(regexp = "ADMIN|RECEPTIONIST|DOCTOR", message = "Invalid Role, must be ADMIN,RECEPTIONIST or DOCTOR only.")
    @NotBlank(message = "Role cannot be null")
    String role;
    Long [] departments;
}
