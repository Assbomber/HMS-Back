package com.example.HMS.respository;

import com.example.HMS.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
/**
 * PatientRepo extends JpaRepository to provided Jpa functionalities
 * */
@Repository
public interface PatientRepo extends JpaRepository<Patient,Long> {
    public Patient findByPatientIdAndMobile(Long id,Long mobile);

    List<Patient> findAllByTimeStamp(Date date);

    @Query(value="SELECT * FROM patient\n" +
            "WHERE time_stamp>:minDate AND time_stamp<=:maxDate",nativeQuery = true)
    List<Patient> findAllBetweenDate(Date minDate, Date maxDate);
}
