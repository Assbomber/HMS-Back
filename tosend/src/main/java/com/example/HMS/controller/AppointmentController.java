package com.example.HMS.controller;

import com.example.HMS.entity.Appointment;
import com.example.HMS.entity.AppointmentMapper;
import com.example.HMS.entity.Patient;
import com.example.HMS.exception.*;
import com.example.HMS.service.AppointmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.sql.Date;
import java.util.List;

/**
 * AppointmentController is a Rest Controller that allows following operations:
 * <li>Creating a new Appointment</li>
 * <li>Retrieving all Appointments</li>
 * <li>Get Appointment by ID</li>
 * <li>Get Appointment by Patient ID</li>
 * <li>Get Appointment by Doctor ID</li>
 * <li>Get Appointment by Doctor ID and Date</li>
 * <li>Save Prescription for an Appointment</li>
 * <li>Get Prescription for an Appointment</li>
 * */
@RestController
@RequiredArgsConstructor
@RequestMapping("/hmsapi/appointments")
@Slf4j
@Api(tags = "Appointment APIs", description = "Allows Appointment CRUD operations")
public class AppointmentController {
    private final AppointmentService appointmentService;

    /**
     * createAppointment(AppointmentMapper appointmentMapper) allows to create a new Appointment
     * when a valid Appointment Mapper schema is provided as a body.
     * */
    @ApiOperation(value = "Create an Appointment",
            notes = "allows to create a new Appointment when a valid Appointment Mapper schema is provided as a body.",
            response = Appointment.class
    )
    @PostMapping("/")
    public Appointment createAppointment(@Valid @RequestBody AppointmentMapper appointmentMapper) throws StaffNotFoundException, DepartmentNotFoundException, PatientNotFoundException {
        log.info("Creating a New appointment for patient ID:"+appointmentMapper.getPatientId());
        return appointmentService.createAppointment(appointmentMapper);
    }

    /**
     * getAppointment(Long id) allows to retrieve an appointment associated with that ID
     * */
    @ApiOperation(value = "Get an Appointment",
            notes = "allows to retrieve an appointment associated with that ID",
            response = Appointment.class
    )
    @GetMapping("/{id}")
    public Appointment getAppointment(@PathVariable("id") Long id) throws AppointmentNotFoundException {
        log.info("Retrieving appointment by id: "+id);
        return appointmentService.getAppointment(id);
    }

    /**
     * getAppointmentsByPatientId(Long id) allows to retrieve all appointments associated with
     * the provided patient Id
     *
     * */
    @ApiOperation(value = "Get Appointments By Patient ID",
            notes = "allows to retrieve all appointments associated with the provided patient Id",
            response = List.class
    )
    @GetMapping("/patient/{id}")
    public List<Appointment> getAppointmentsByPatientId(@PathVariable("id") Long id){
        log.info("Retrieving appointments by Patient id:"+id);
        List<Appointment> appointments=appointmentService.getAppointmentsByPatientId(id);
        return appointments;
    }

    /**
     * getAppointmentsByDoctorId(Long id) allows to retrieve all appointments associated with
     * the provided Doctor Id
     * */
    @ApiOperation(value = "Get Appointments By Doctor ID",
            notes = "allows to retrieve all appointments associated with the provided Doctor Id",
            response = List.class
    )
    @GetMapping("/doctor/{id}")
    public List<Appointment> getAppointmentsByDoctorId(@PathVariable("id") Long id){
        log.info("Retrieving appointments by doctor id: "+id);
        List<Appointment> appointments=appointmentService.getAppointmentsByDoctorId(id);
        return appointments;
    }

    /**
     * ggetAppointmentsByDoctorIdAndDate(Long id, Date date) allows to retrieve all appointments associated with
     * the provided Doctor Id and matching Date.
     * */
    @ApiOperation(value = "Get Appointments By Doctor ID and Date",
            notes = "allows to retrieve all appointments associated with the provided Doctor Id and matching Date.",
            response = List.class
    )
    @GetMapping("/doctor/{id}/{date}")
    public List<Appointment> getAppointmentsByDoctorIdAndDate(@PathVariable("id") Long id,@PathVariable("date") Date date) throws CustomCreatedException {
        log.info("Retrieving appointments for doctor ID:"+id+" and date: "+date);
        List<Appointment> appointments=appointmentService.getAppointmentsByDoctorIdAndDate(id,date);
        return appointments;
    }

    /**
     * updateAppointment(Long id, Multipart file) allows you to upload and save the prescription
     * to already existing appointment.
     * */
    @ApiOperation(value = "Update an Appointment",
            notes = " allows you to upload and save the prescription to already existing appointment.",
            response = Appointment.class
    )
    @PutMapping("/{id}")
    public Appointment updateAppointment(@PathVariable("id") Long id, @RequestPart("file") MultipartFile file) throws AppointmentNotFoundException, CustomCreatedException {
        log.info("Saving prescription to appointment id:"+id);
        return appointmentService.saveFile(id,file);
    }

    /**
     * getAppointmentsByDate(Date date) allows you to retrieve all appointments matching with
     * Date provided.
     * */
    @ApiOperation(value = "Get Appointments By Date",
            notes = " allows you to retrieve all appointments matching with  Date provided.",
            response = List.class
    )
    @GetMapping("/date/{date}")
    public List<Appointment> getAppointmentsByDate(@PathVariable("date") Date date) throws CustomCreatedException {
        log.info("Get Appointments by Date:"+date);
        return appointmentService.getAppointmentsByDate(date);
    }

    /**
     * downloadFile(Long id) allows you to retrieve the prescription using the appointment ID.
     * */
    @ApiOperation(value = "Download a Prescription",
            notes = " allows you to retrieve the prescription using the appointment ID.",
            response = ResponseEntity.class
    )
    @GetMapping("/download/{id}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable("id") Long id) throws AppointmentNotFoundException {
        log.info("Downloading prescription with appointment Id:"+id);
        Appointment appointment=appointmentService.getAppointment(id);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(appointment.getDocType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment:filename=\""+appointment.getDocName()+"\"")
                .body(new ByteArrayResource(appointment.getData()));
    }

    /**
     * getAppointmentList(Date start?,Date end?) allows you to retrieve all appointments
     * or appointments in the date range if  start and end dates are provided.
     * */
    @ApiOperation(value = "Get Appointment List",
            notes = " allows you to retrieve all appointments or appointments in the date range if  start and end dates are provided",
            response = ResponseEntity.class
    )
    @GetMapping("/")
    public List<Appointment> getAppointmentsList(@RequestParam(name="start",required = false) Date start,@RequestParam(name="end",required = false) Date end) throws CustomCreatedException {
        log.info("Retrieving appointment List. Start Date:"+start+", end Date:"+end);
        if(start==null || end==null)
            return appointmentService.getAllAppointments();
        else return appointmentService.getAppointmentsBetween(start,end);
    }



}

