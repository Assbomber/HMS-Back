package com.example.HMS.controller;

import com.example.HMS.entity.Patient;
import com.example.HMS.exception.CustomCreatedException;
import com.example.HMS.exception.PatientNotFoundException;
import com.example.HMS.service.DepartmentService;
import com.example.HMS.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(PatientController.class)
@AutoConfigureMockMvc(addFilters = false)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PatientService patientService;
    @MockBean
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() throws PatientNotFoundException, CustomCreatedException {
        Patient patient1= Patient.builder()
                .patientId(1L)
                .name("Patient1")
                .age(100)
                .mobile(9999999999L)
                .address("india")
                .gender("MALE")
                .otp(4444)
                .build();
        Map<String,Object> map=new HashMap<>();
        map.put("patient",patient1);
        Mockito.when(patientService.savePatient(any(Patient.class))).then(returnsFirstArg());
        Mockito.when(patientService.updatePatient(any(Patient.class))).then(returnsFirstArg());
        Mockito.when(patientService.getPatientInternal(1L)).thenReturn(patient1);
        Mockito.when(patientService.getPatientInternal(4L)).thenThrow(PatientNotFoundException.class);
        Mockito.when(patientService.getPatientOtp(1L,9999999999L)).thenReturn(patient1);
        Mockito.when(patientService.getPatientDetailsWithOtp(1L,9999999999L,4444)).thenReturn(map);
        Mockito.when(patientService.getPatientOtp(2L,9999999999L)).thenThrow(PatientNotFoundException.class);
        Mockito.when(patientService.getPatientDetailsWithOtp(1L,9999999999L,4844)).thenThrow(CustomCreatedException.class);
        Mockito.when(patientService.getAllPatients()).thenReturn(List.of());
        Mockito.when(patientService.deletePatient(1L)).thenReturn(true);
        Mockito.when(patientService.deletePatient(4L)).thenReturn(false);
    }

    @Test
    @DisplayName("Saving patient")
    void savePatient() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/hmsapi/patients/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +

                        "    \"name\":\"Patient\",\n" +
                        "    \"age\":12,\n" +
                        "    \"gender\":\"MALE\",\n" +
                        "    \"mobile\":9999999999\n" +
                        "}")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Saving patient, Invalid schema")
    void savePatientInvalidSchema() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/hmsapi/patients/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +

                        "    \"name\":\"Patient\",\n" +
                        "    \"age\":121,\n" +
                        "    \"gender\":\"MALE\",\n" +
                        "    \"mobile\":9999999999\n" +
                        "}")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Updating patient")
    void updatePatient() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/hmsapi/patients/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"patientId\":1,\n" +
                        "    \"name\":\"Patient\",\n" +
                        "    \"age\":120,\n" +
                        "    \"gender\":\"MALE\",\n" +
                        "    \"mobile\":9999999999\n" +
                        "}")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Get Patient by Id, Restricted")
    void getPatientInternal() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/patients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Get Patient by Id, Invalid Id provided, Restricted")
    void getPatientInternalInvalidId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/patients/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test
    @DisplayName("Get patient OTP")
    void getPatientOtp() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/patients/open/1/9999999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Get patient OTP, invalid credentials")
    void getPatientOtpInvalidCredentials() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/patients/open/2/9999999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Verifying patient OTP")
    void getPatientDetailsWithOtp() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/patients/open/1/9999999999/4444")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Veriying Patient OTP, Invalid OTP")
    void getPatientDetailsWithOtpInvalidOTP() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/patients/open/1/9999999999/4844")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test
    @DisplayName("Get all patients")
    void getAllPatients() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/patients/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Delete patient")
    void deletePatient() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/hmsapi/patients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isAccepted());
    }
    @Test
    @DisplayName("Delete patient, Bad request")
    void deletePatientBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/hmsapi/patients/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}