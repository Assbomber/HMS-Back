package com.example.HMS.service;

import com.example.HMS.entity.Patient;
import com.example.HMS.exception.CustomCreatedException;
import com.example.HMS.exception.PatientNotFoundException;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * PatientService is an Interface that helps to implement following functions
 * */
public interface PatientService {
    public Patient savePatient(Patient patient);
    Patient getPatientInternal(Long id) throws PatientNotFoundException;
    Patient getPatientOtp(Long id,Long mobile) throws PatientNotFoundException;
    Map<String,Object> getPatientDetailsWithOtp(Long id, Long mobile, Integer otp) throws PatientNotFoundException, CustomCreatedException;
    List<Patient> getPatientsByDate(Date date) throws CustomCreatedException;

    List<Patient> getPatientsBetweenDate(Date minDate, Date maxDate) throws CustomCreatedException;

    List<Patient> getAllPatients();

    Patient updatePatient(Patient patient) throws PatientNotFoundException, CustomCreatedException;

    boolean deletePatient(Long id);
}
