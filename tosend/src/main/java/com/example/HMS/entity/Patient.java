package com.example.HMS.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long patientId;
    @Pattern(regexp = "^[a-zA-Z ]*$",message = "Name must have only letters")
    @NotBlank(message = "Name cannot be blank")
    @Column(
            nullable = false
    )
    private String name;
    @Min(value = 1,message = "Age must be equal to or greater than 1")
    @Max(value = 120,message = "Age must be less than equal to 120")
    @NotNull(message = "Age should not be blank")
    @Column(
            nullable = false
    )
    private Integer age;
    @Min(value = 1000000000L,message = "Invalid mobile number")
    @Max(value = 9999999999L,message = "Invalid mobile number")
    @NotNull(message = "Mobile number should not be blank")
    @Column(
            nullable = false
    )
    private Long mobile;
    @Pattern(regexp = "MALE|FEMALE|OTHERS", message = "Invalid gender, must be MALE,FEMALE or OTHERS only.")
    @NotNull(message = "Gender should not be blank")
    @Column(
            nullable = false
    )
    private String gender;
    private String address;
    private Integer otp;

    @JsonIgnore
    private Date timeStamp;

    @PrePersist
    void preInsert() {
        if (this.timeStamp == null)
            this.timeStamp=new Date(LocalDate.now().getYear()-1900,LocalDate.now().getMonth().getValue()-1,LocalDate.now().getDayOfMonth());
    }

    @JsonIgnore
    @OneToMany(
            mappedBy = "patient",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )

    private List<Appointment> appointmentList;

}
