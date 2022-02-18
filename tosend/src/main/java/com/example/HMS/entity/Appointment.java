package com.example.HMS.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long appointmentId;

    @NotNull(message = "Department cannot be null")
    @OneToOne(
            cascade = CascadeType.MERGE
    )
    @JoinColumn(
            nullable = false,
            name="department_id",
            referencedColumnName = "departmentId"

    )
    private Department department;
    @NotNull(message = "Date cannot be null")
    @Column(
            nullable = false
    )
    private Date date;

    @NotNull(message = "Staff cannot be null")
    @OneToOne(
            cascade = CascadeType.MERGE
    )
    @JoinColumn(
            nullable = false,
            name="staff_id",
            referencedColumnName = "staffId"
    )
    private Staff staff;

    @NotNull(message = "Patient cannot be null")
    @ManyToOne(
            cascade = CascadeType.MERGE
    )
    @JoinColumn(
            nullable = false,
            name="patient_id",
            referencedColumnName = "patientId"
    )
    private Patient patient;
    private String comments;

    @NotBlank(message = "Status cannot be blank")
    @JoinColumn(
            nullable = false
    )
    private String Status;

    private String docName;
    private String docType;

    @Lob
    private byte[] data;

    @JsonIgnore
    private Date timeStamp;

    @PrePersist
    void preInsert() {
        if (this.timeStamp == null)
            this.timeStamp=new Date(LocalDate.now().getYear()-1900,LocalDate.now().getMonth().getValue()-1,LocalDate.now().getDayOfMonth());
    }
}
