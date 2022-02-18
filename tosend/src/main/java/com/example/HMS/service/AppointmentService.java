package com.example.HMS.service;

import com.example.HMS.entity.AppointmentMapper;
import com.example.HMS.entity.Appointment;
import com.example.HMS.exception.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

/**
 * AppointmentService is an interface that allows you to implement methods required for CRUD operations
 * */
public interface AppointmentService {
    public Appointment createAppointment(AppointmentMapper appointmentMapper) throws DepartmentNotFoundException, StaffNotFoundException, PatientNotFoundException;
    public Appointment getAppointment(Long id) throws AppointmentNotFoundException;
    public List<Appointment> getAppointmentsByPatientId(Long id);
    public List<Appointment> getAppointmentsByDoctorId(Long id);
    public Appointment saveFile(Long id,MultipartFile file) throws AppointmentNotFoundException, CustomCreatedException;
    public List<Appointment> getAppointmentsByDoctorIdAndDate(Long id, Date date) throws CustomCreatedException;

    public List<Appointment> getAppointmentsByDate(Date date) throws CustomCreatedException;

    List<Appointment> getAllAppointments();

    List<Appointment> getAppointmentsBetween(Date start, Date end) throws CustomCreatedException;
}
