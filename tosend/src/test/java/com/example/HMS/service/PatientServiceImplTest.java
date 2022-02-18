package com.example.HMS.service;

import com.example.HMS.entity.Patient;
import com.example.HMS.exception.CustomCreatedException;
import com.example.HMS.exception.PatientNotFoundException;
import com.example.HMS.respository.AppointmentRepo;
import com.example.HMS.respository.PatientRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class PatientServiceImplTest {

    @Autowired
    private PatientService patientService;


    @MockBean
    private PatientRepo patientRepo;
    Date d1=new Date(2022-1900,1,12);
    Date d2=new Date(2019-1900,1,12);

    @MockBean
    private AppointmentRepo appointmentRepo;

    @BeforeEach
    void setUp() {
        Patient patient1= Patient.builder()
                .patientId(1L)
                .name("Patient1")
                .age(100)
                .mobile(9999999999L)
                .address("india")
                .gender("MALE")
                .otp(4444)
                .build();
        Patient patient2= Patient.builder()
                .patientId(2L)
                .name("Patient2")
                .age(100)
                .mobile(9999999999L)
                .address("india")
                .gender("MALE")
                .build();

        Mockito.when(patientRepo.findAll()).thenReturn(List.of(patient1,patient2));
        Mockito.doNothing().when(patientRepo).deleteById(4L);
        Mockito.doNothing().when(patientRepo).deleteById(1L);
        Mockito.when(patientRepo.findById(4L)).thenReturn(Optional.empty());
        Mockito.when(patientRepo.findById(2L)).thenReturn(Optional.of(patient2));
        Mockito.when(patientRepo.findById(1L)).thenReturn(Optional.of(patient1));
        Mockito.when(patientRepo.findAllByTimeStamp(d1)).thenReturn(List.of(patient1));
        Mockito.when(patientRepo.findAllByTimeStamp(d2)).thenReturn(List.of());
        Mockito.when(patientRepo.findAllBetweenDate(d2,d1)).thenReturn(List.of(patient1,patient2));
        Mockito.when(patientRepo.save(any(Patient.class))).then(returnsFirstArg());
        Mockito.when(patientRepo.findByPatientIdAndMobile(1L,9999999999L)).thenReturn(patient1);
        Mockito.when(patientRepo.findByPatientIdAndMobile(4L,9999999999L)).thenReturn(null);
        Mockito.when(appointmentRepo.findAllByPatientPatientId(1L)).thenReturn(List.of());
    }

    @Test
    @DisplayName("Updating patient")
    void updatePatient() throws PatientNotFoundException, CustomCreatedException {
        Patient patient= Patient.builder()
                .patientId(2L)
                .name("Patient1")
                .age(100)
                .mobile(9999999999L)
                .address("bangladesh")
                .gender("MALE")
                .build();
        assertEquals("bangladesh",patientService.updatePatient(patient).getAddress());
    }

    @Test
    @DisplayName("Deleting patient by ID")
    void deletePatient() {
        assertEquals(true,patientService.deletePatient(4L));
        assertEquals(false,patientService.deletePatient(1L));
    }

    @Test
    @DisplayName("Getting all patients")
    void getAllPatients() {
        assertEquals(2,patientService.getAllPatients().size());
    }

    @Test
    @DisplayName("Getting patient by date")
    void getPatientsByDate() throws CustomCreatedException {
        assertEquals(1,patientService.getPatientsByDate(d1).size());
        assertEquals(0,patientService.getPatientsByDate(d2).size());
    }

    @Test
    @DisplayName("Getting patients between date")
    void getPatientsBetweenDate() throws CustomCreatedException {
        assertEquals(2,patientService.getPatientsBetweenDate(d2,d1).size());
    }

    @Test
    @DisplayName("Saving patient")
    void savePatient() {
        Patient patient= Patient.builder()
                .name("Patient2")
                .age(100)
                .mobile(9999999999L)
                .address("india")
                .gender("MALE")
                .build();
        assertEquals(patient.getName(),patientService.savePatient(patient).getName());
    }

    @Test
    @DisplayName("Get Patient Restricted")
    void getPatientInternal() throws PatientNotFoundException {
        assertEquals("Patient1",patientService.getPatientInternal(1L).getName());
    }

    @Test
    @DisplayName("Get Patient Restricted, Invalid ID")
    void getPatientInternalInvalidID() throws PatientNotFoundException {
        assertThrows(PatientNotFoundException.class,()->patientService.getPatientInternal(4L));
    }

    @Test
    @DisplayName("Getting Patient OTP")
    void getPatient() throws PatientNotFoundException {
        assertEquals(1L,patientService.getPatientOtp(1L,9999999999L).getPatientId());
    }

    @Test
    @DisplayName("Getting Patient OTP, Invalid Patient ID")
    void getPatientInvalidId() throws PatientNotFoundException {
        assertThrows(PatientNotFoundException.class,()->patientService.getPatientOtp(4L,9999999999L));
    }



    @Test
    @DisplayName("Verifying Patient OTP")
    void getPatientWithOtp() throws CustomCreatedException, PatientNotFoundException {
        Map<String,Object> map=patientService.getPatientDetailsWithOtp(1L,9999999999L,4444);
        Patient patient= (Patient) map.get("patient");
        assertEquals("Patient1",patient.getName());

    }

    @Test
    @DisplayName("Varifying Patient OTP, Invalid OTP")
    void getPatientWithOTPInvalid(){
        assertThrows(CustomCreatedException.class,()->patientService.getPatientDetailsWithOtp(1L,9999999999L,5843));
    }
}