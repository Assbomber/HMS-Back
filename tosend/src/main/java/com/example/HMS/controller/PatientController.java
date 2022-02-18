package com.example.HMS.controller;

import com.example.HMS.entity.Appointment;
import com.example.HMS.entity.Patient;
import com.example.HMS.exception.CustomCreatedException;
import com.example.HMS.exception.PatientNotFoundException;
import com.example.HMS.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PatientController is a Rest controller class that provides functionalities like:
 * <li>Creating a new patient</li>
 * <li>Getting a patient by ID</li>
 * <li>Deleting a patient by ID</li>
 * <li>Update a patient by ID</li>
 * <li>Getting all patients</li>
 * <li>Get patient OTP</li>
 * <li>Get Patient details based on OTP</li>
 */
@Slf4j
@RestController
@RequestMapping("hmsapi/patients")
@RequiredArgsConstructor
@Api(tags = "Patient APIs", description = "Allows CRUD Operations on Patient")
public class PatientController {

    private final PatientService patientService;

   /**
    * savePatient(Patient patient) allows you to save patient to the DB when provided valid schema
    * */
   @ApiOperation(value = "Create a Patient",
           notes = "allows you to save patient to the DB when provided valid schema",
           response = Patient.class
   )
    @PostMapping("/")
    public Patient savePatient(@Valid @RequestBody Patient patient){
        log.info("Creating a Patient :"+patient.getName());
        return patientService.savePatient(patient);
    }

    /**
     * updatePatient(Patient patient) allows you to update patient already existing in the DB when provided valid schema
     * */
    @ApiOperation(value = "Update a Patient",
            notes = " allows you to update patient already existing in the DB when provided valid schema",
            response = Patient.class
    )
    @PutMapping("/")
    public Patient updatePatient(@Valid @RequestBody Patient patient) throws PatientNotFoundException, CustomCreatedException {
        log.info("Updating patient with ID :"+patient.getPatientId());
        return patientService.updatePatient(patient);
    }

    /**
     * getPatientInternal(Long Id) allows you to retrieve patient details when provided valid patient ID.
     * This endpoint has restricted access to only staff members
     * */
    @ApiOperation(value = "Get Patient (Restricted Version)",
            notes = " allows you to retrieve patient details when provided valid patient ID. This endpoint has restricted access to only staff members",
            response = Patient.class
    )
    @GetMapping("/{id}")
    public Patient getPatientInternal(@PathVariable("id") Long id) throws PatientNotFoundException {
        log.info("Retrieving Patient with ID:"+id);
        return patientService.getPatientInternal(id);
    }

    /**
     * getPatientOtp(Long id,Long mobile) allows you to retrieve patient OTP and details.
     * This has open access.
     * */
    @ApiOperation(value = "Get Patient OTP (Open Version)",
            notes = " allows you to retrieve patient OTP when provided valid patient ID and Mobile. This endpoint has open access",
            response = Patient.class
    )
    @GetMapping("/open/{id}/{mobile}")
    public Patient getPatientOtp(@PathVariable("id")Long id, @PathVariable("mobile") Long mobile) throws PatientNotFoundException{
        log.info("Retrieving patient OTP with Id: "+id+" and mobile: "+mobile);
        Patient patient= patientService.getPatientOtp(id,mobile);
        return patient;
    }

    /**
     * getPatientDetailsWithOtp(Long id,Long mobile,Integer Otp) allows you to verify patient OTP and get List of appointments
     * This has open access.
     * */
    @ApiOperation(value = "Get Patient Details using OTP (Open Version)",
            notes = " allows you to verify patient OTP and get List of appointments. This has open access.",
            response = ResponseEntity.class
    )
    @GetMapping("/open/{id}/{mobile}/{otp}")
    public ResponseEntity<?> getPatientDetailsWithOtp(@PathVariable("id")Long id, @PathVariable("mobile") Long mobile,@PathVariable("otp") Integer otp) throws PatientNotFoundException, CustomCreatedException {
        log.info("Retrieving patient details with ID:"+ id+" , mobile:"+mobile+", OTP:"+otp);
        return ResponseEntity.ok().body(patientService.getPatientDetailsWithOtp(id,mobile,otp));
    }

    /**
     * getAllPatients() allows you to retrieve all patients in the DB
     * */
    @ApiOperation(value = "Get All Patients",
            notes = " allows you to retrieve all patients in the DB",
            response = List.class
    )
    @GetMapping("/")
    public List<Patient> getAllPatients(){
        log.info("Retrieving all Patients");
        return patientService.getAllPatients();
    }

    /**
     * deletePatient(Long id) allows you to delete a patient when valid Id is provided
     * */
    @ApiOperation(value = "Delete Patient",
            notes = " allows you to delete a patient when valid Id is provided",
            response = ResponseEntity.class
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable("id") Long id){
        log.info("Deleting patient with ID: "+id);
        Map<String,String> map=new HashMap<>();
        if(patientService.deletePatient(id)){
            log.info("Deleting Patient successful with id:"+id);
            map.put("status", HttpStatus.ACCEPTED.name());
            map.put("message","The User was Deleted");
            return ResponseEntity.accepted().body(map);
        }else{
            log.error("Deleting Patient unsuccessful with id:"+id);
            map.put("status",HttpStatus.BAD_REQUEST.name());
            map.put("message","Error deleting the user");
            return ResponseEntity.badRequest().body(map);
        }
    }


}
