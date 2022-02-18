package com.example.HMS.service;

import com.example.HMS.Constants;
import com.example.HMS.Status;
import com.example.HMS.entity.*;
import com.example.HMS.exception.*;
import com.example.HMS.respository.AppointmentRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

/**
 * AppointmentServiceImpl implements AppointmentService
 * interface to implement the Jpa crud operation for Appointments.
 * */
@Service @RequiredArgsConstructor @Slf4j
public class AppointmentServiceImpl implements AppointmentService{


    private final AppointmentRepo appointmentRepo;
    private final DepartmentService departmentService;

    private final PatientService patientService;
    private final StaffService staffService;


    /**
     * getAppointmentList(Date start,Date end) allows you to retrieve all appointments in the date range provided.
     * */
    @Override
    public List<Appointment> getAppointmentsBetween(Date start, Date end) throws CustomCreatedException {
        log.info("Fetching appointment between "+start.toString()+" - "+end.toString());
        if(start==null || end== null) throw new CustomCreatedException(Constants.DATE_NULL);
        if(start.compareTo(end)>0) throw new IllegalArgumentException(Constants.STARTDATE_GTR_THAN_ENDDATE);
        return appointmentRepo.findByDateBetween(start,end);
    }

    /**
     * getAppointmentList() allows you to retrieve all appointments
     * */
    @Override
    public List<Appointment> getAllAppointments() {
        log.info("Fetching all appointments");
        return appointmentRepo.findAll();
    }

    /**
     * getAppointmentsByDate(Date date) allows you to retrieve all appointments matching with
     * Date provided.
     * */
    @Override
    public List<Appointment> getAppointmentsByDate(Date date) throws CustomCreatedException {
        log.info("Fetching appointments for date:"+date.toString());
        if(date==null) throw new CustomCreatedException(Constants.DATE_NULL);
        return appointmentRepo.findAllByTimeStamp(date);
    }

    /**
     * getAppointmentsByDoctorId(Long id) allows to retrieve all appointments associated with
     * the provided Doctor Id
     * */
    @Override
    public List<Appointment> getAppointmentsByDoctorId(Long id) {
        log.info("Fetching appointments for Doctor ID:"+id);
        if(id<1) throw new IllegalArgumentException(Constants.ILLEGAL_ID);
        return appointmentRepo.findAllByStaffStaffId(id);
    }

    /**
     * getAppointmentsByDoctorIdAndDate(Long id, Date date) allows to retrieve all appointments associated with
     * the provided Doctor Id and matching Date.
     * */
    public List<Appointment> getAppointmentsByDoctorIdAndDate(Long id, Date date) throws CustomCreatedException {
        log.info("Fetching appointments for Doctor Id:"+id+" and date: "+date.toString());
        if(id<1) throw new IllegalArgumentException(Constants.ILLEGAL_ID);
        if(date==null) throw new CustomCreatedException(Constants.DATE_NULL);
        return appointmentRepo.findAllByStaffStaffIdAndDate(id,date);
    }

    /**
     * createAppointment(AppointmentMapper appointmentMapper) allows to create a new Appointment
     * when a valid Appointment Mapper schema is provided as a body.
     * */
    @Override
    @Transactional
    public Appointment createAppointment(AppointmentMapper appointmentMapper) throws DepartmentNotFoundException, StaffNotFoundException, PatientNotFoundException {
        log.info("Creating new appointment for Patient ID:"+appointmentMapper.getPatientId());
        Patient patient=patientService.getPatientInternal(appointmentMapper.getPatientId());
        Department department=departmentService.getDepartment(appointmentMapper.getDepartmentId());
        Staff staff=staffService.getStaff(appointmentMapper.getDoctorId());
        Appointment appointment=Appointment.builder()
                .Status(Status.NEW.name())
                .department(department)
                .patient(patient)
                .date(appointmentMapper.getDate())
                .comments(appointmentMapper.getComments())
                .staff(staff).build();
        return appointmentRepo.save(appointment);
    }

    /**
     * getAppointment(Long id) allows to retrieve an appointment associated with that ID
     * */
    public Appointment getAppointment(Long id) throws AppointmentNotFoundException {
        log.info("Fetching appointment by ID:"+id);
        if(id<1) throw new IllegalArgumentException(Constants.ILLEGAL_ID);
        Optional<Appointment> optional=appointmentRepo.findById(id);
        if(optional.isPresent()) return optional.get();
        else throw new AppointmentNotFoundException("No appointment exists with id:"+id);
    }

    /**
     * getAppointmentsByPatientId(Long id) allows to retrieve all appointments associated with
     * the provided patient Id
     *
     * */
    @Override
    public List<Appointment> getAppointmentsByPatientId(Long id) {
        log.info("Fetching appointment by Patient Id:"+id);
        if(id<1) throw new IllegalArgumentException(Constants.ILLEGAL_ID);
        return appointmentRepo.findAllByPatientPatientId(id);
    }

    /**
     * saveFile(Long id, Multipart file) allows you to upload and save the prescription
     * to already existing appointment.
     * */
    @Override
    public Appointment saveFile(Long id,MultipartFile file) throws AppointmentNotFoundException, CustomCreatedException {
        log.info("Adding prescription for appointment Id:"+id);
        if(id<1) throw new IllegalArgumentException(Constants.ILLEGAL_ID);
        if(file==null) throw new CustomCreatedException(Constants.FILE_NULL);
        String filename=file.getOriginalFilename();
        try{
            Optional<Appointment> optional=appointmentRepo.findById(id);
            if(optional.isPresent()) {
                Appointment appointment=optional.get();
                appointment.setDocName(filename);
                appointment.setDocType(file.getContentType());
                appointment.setData(file.getBytes());
                appointment.setStatus(Status.CLOSED.name());
                return appointmentRepo.save(appointment);
            }
            else throw new AppointmentNotFoundException("No appointment exists with id:"+id);
        }catch(Exception e){
            return null;
        }
    }
}
