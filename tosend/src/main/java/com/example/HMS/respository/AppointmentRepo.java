package com.example.HMS.respository;

import com.example.HMS.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
/**
 * AppointmentRepo extends JpaRepository to provided Jpa functionalities
 * */

@Repository
public interface AppointmentRepo extends JpaRepository<Appointment,Long> {

    public List<Appointment> findAllByPatientPatientId(Long id);
    public List<Appointment> findAllByStaffStaffId(Long id);
    public List<Appointment> findAllByStaffStaffIdAndDate(Long id, Date date);
    public List<Appointment> findAllByTimeStamp(Date date);

    List<Appointment> findByDateBetween(Date start, Date end);
}
