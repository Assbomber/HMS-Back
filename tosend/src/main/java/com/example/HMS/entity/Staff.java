package com.example.HMS.entity;

import com.example.HMS.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long staffId;

    @Pattern(regexp = "^[a-zA-Z ]*$",message = "Name must have only letters")
    @NotBlank(message = "Name cannot be null")
    @Column(
            nullable = false
    )
    private String name;

    @Pattern(regexp = "ADMIN|RECEPTIONIST|DOCTOR", message = "Invalid Role, must be ADMIN,RECEPTIONIST or DOCTOR only.")
    @NotNull(message = "Role cannot be null")
    @Column(
            nullable = false
    )
    private String role;

    @NotNull(message = "Password cannot be null")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(
            nullable = false
    )
    private String password;

    @ManyToMany(
            cascade = {CascadeType.MERGE,CascadeType.DETACH}
    )
    @JoinColumn(
            name="staff_id",
            referencedColumnName = "staffId"
    )
    private List<Department> departments;

    @JsonIgnore
    @OneToMany(
            mappedBy = "staff",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Appointment> appointmentList;



}
