package com.example.HMS.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentMapper {
    @NotNull(message = "Patient id cannot be null")
    private Long patientId;
    @NotNull(message = "doctor id cannot be null")
    private Long doctorId;
    @NotNull(message = "Department id cannot be null")
    private Long departmentId;
    @NotNull(message = "Date cannot be null")
    private Date date;
    private String comments;
}
