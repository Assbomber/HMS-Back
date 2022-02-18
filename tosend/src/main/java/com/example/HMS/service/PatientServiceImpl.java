package com.example.HMS.service;

import com.example.HMS.Constants;
import com.example.HMS.entity.Appointment;
import com.example.HMS.entity.Patient;
import com.example.HMS.exception.CustomCreatedException;
import com.example.HMS.exception.PatientNotFoundException;
import com.example.HMS.respository.AppointmentRepo;
import com.example.HMS.respository.PatientRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * PatientServiceImpl implements StaffService interface and UserDetailsService to implements methods for Staff CRUD functionality
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl implements PatientService{

    private final PatientRepo patientRepo;
    private final AppointmentRepo appointmentRepo;

    /**
     * updatePatient(Patient patient) allows you to update a patient if valid Schema is provided
     * */
    @Override
    public Patient updatePatient(Patient patient) throws PatientNotFoundException, CustomCreatedException {
        log.info("Updating patient :"+patient.getPatientId());
        if(patient.getPatientId()==null) throw new CustomCreatedException(Constants.ID_NULL);
        if(patient.getPatientId()<1) throw new IllegalArgumentException(Constants.ILLEGAL_ID);
        Optional<Patient> optional=patientRepo.findById(patient.getPatientId());
        if(optional.isPresent()){
            optional.get().setName(patient.getName());
            optional.get().setAge(patient.getAge());
            optional.get().setAddress(patient.getAddress());
            optional.get().setMobile(patient.getMobile());
            optional.get().setGender(patient.getGender());
            log.info("final save");
            patientRepo.save(optional.get());
            log.info("returning");
            return optional.get();
        }else throw new PatientNotFoundException(Constants.PATIENT_NOT_FOUND+patient.getPatientId());
    }

    /**
     * deletePatient(Patient patient) allows you to delete a patient if valid Schema is provided
     * */
    @Override
    public boolean deletePatient(Long id) {
        log.info("Deleting patient :"+id);
        if(id<1) throw new IllegalArgumentException(Constants.ILLEGAL_ID);
        patientRepo.deleteById(id);
        if(!patientRepo.findById(id).isPresent()) return true;
        else return false;
    }

    /**
     * getAllPatients(Patient patient) allows you to retrieve all patients in the DB
     * */
    @Override
    public List<Patient> getAllPatients() {
        log.info("Fetching all Patients");
        return patientRepo.findAll();
    }

    /**
     * getPatientsByDate(Date date) allows you to retrieve all patients created on that date.
     * */
    @Override
    public List<Patient> getPatientsByDate(Date date) throws CustomCreatedException {
        log.info("fetching patients by joining date: "+date.toString());
        if(date==null) throw new CustomCreatedException(Constants.DATE_NULL);
        return patientRepo.findAllByTimeStamp(date);
    }

    /**
     * getPatientsBetweenDate(Date minDate,Date maxDate) allows you to retrieve all patients created in the provided date Range.
     * */
    @Override
    public List<Patient> getPatientsBetweenDate(Date minDate, Date maxDate) throws CustomCreatedException {
        log.info("Fetching patient by joining date in range"+minDate.toString()+" - "+maxDate.toString());
        if(minDate==null || maxDate==null) throw new CustomCreatedException(Constants.DATE_NULL);
        return patientRepo.findAllBetweenDate(minDate,maxDate);
    }

    /**
     * savePatient(Patient patient) allows you to save a patient when valid schema is provided.
     * */
    @Override
    public Patient savePatient(Patient patient) {
        log.info("Saving patient :"+patient.getName());
        return patientRepo.save(patient);
    }

    /**
     * getPatientInternal(Long id) allows you to retrieve patient with the associated ID
     * */
    @Override
    public Patient getPatientInternal(Long id) throws PatientNotFoundException {
        log.info("Fetching patient by iD:"+id);
        if(id<1) throw new IllegalArgumentException(Constants.ILLEGAL_ID);
        Optional<Patient> optional= patientRepo.findById(id);
        if(optional.isPresent()) return optional.get();
        else throw new PatientNotFoundException(Constants.PATIENT_NOT_FOUND+id);
    }

    /**
     * getPatientsOtp(Long id,Long mobile) allows you to retrieve patient OTP with the provided Id and Mobile.
     * */
    public Patient getPatientOtp(Long id,Long mobile) throws PatientNotFoundException {
        log.info("Fetching patient by ID:"+id+" and mobile:"+mobile);
        if(id<1) throw new IllegalArgumentException(Constants.ILLEGAL_ID);
        Patient patient= patientRepo.findByPatientIdAndMobile(id,mobile);
        if(patient!=null){
            Integer random=(int)(Math.random()*(9999-1000+1)+1000);
            patient.setOtp(random);
            patientRepo.save(patient);
            setTimeout(()->{
                patient.setOtp(null);
                patientRepo.save(patient);
            },100000);
            return patient;
        }
        else throw new PatientNotFoundException(Constants.PATIENT_NOT_FOUND+id);
    }

    /**
     * getPatientsDetailsWithOtp(Date date) allows you to retrieve patient details and Appointments if valid OTP is provided.
     * */
    public Map<String,Object> getPatientDetailsWithOtp(Long id, Long mobile, Integer otp) throws PatientNotFoundException, CustomCreatedException {
        log.info("verify patient by OTP. Patient Id:"+id+" , mobile:"+mobile+" OTP: "+otp);
        if(id<1) throw new IllegalArgumentException(Constants.ILLEGAL_ID);
        Patient patient= patientRepo.findByPatientIdAndMobile(id,mobile);
        if(patient!=null){
            if(otp.equals(patient.getOtp())){
                List<Appointment> appointments=appointmentRepo.findAllByPatientPatientId(id);
                Map<String,Object> map=new HashMap<>();
                map.put("patient",patient);
                map.put("appointments",appointments);
                return map;
            }
            else throw new CustomCreatedException("Incorrect OTP");
        }
        else throw new PatientNotFoundException(Constants.PATIENT_NOT_FOUND+id);
    }


    // setTimeout
    public static void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();
    }
}
