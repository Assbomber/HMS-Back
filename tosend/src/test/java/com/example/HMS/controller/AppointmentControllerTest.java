package com.example.HMS.controller;

import com.example.HMS.entity.*;
import com.example.HMS.exception.*;
import com.example.HMS.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebMvcTest(AppointmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AppointmentService appointmentService;
    @MockBean
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() throws StaffNotFoundException, PatientNotFoundException, DepartmentNotFoundException, AppointmentNotFoundException, IOException, CustomCreatedException {
        AppointmentMapper mapper=AppointmentMapper.builder()
                .patientId(1L)
                .doctorId(2L)
                .departmentId(3L)
                .date(new Date(2022+1900,02,12)).build();
        MockMultipartFile mockMultipartFile=new MockMultipartFile("name","name.txt", MediaType.TEXT_PLAIN_VALUE,"helloworld".getBytes());
        Appointment appointment=Appointment.builder()
                .appointmentId(4L)
                .patient(Patient.builder().patientId(5L).build())
                .department(Department.builder().departmentId(6L).build())
                .staff(Staff.builder().staffId(7L).build())
                .docName(mockMultipartFile.getName())
                .docType(mockMultipartFile.getContentType())
                .data(mockMultipartFile.getBytes()).build();
        Mockito.when(appointmentService.getAllAppointments()).thenReturn(List.of(appointment));
        Mockito.when(appointmentService.getAppointmentsByDate(new Date(2022+1900,02,12))).thenReturn(List.of(appointment));
        Mockito.when(appointmentService.saveFile(eq(4L),any(MultipartFile.class))).thenReturn(appointment);
        Mockito.when(appointmentService.getAppointmentsByDoctorIdAndDate(7L,new Date(2022+1900,02,12))).thenReturn(List.of(appointment));
        Mockito.when(appointmentService.getAppointmentsByDoctorId(7L)).thenReturn(List.of(appointment));
        Mockito.when(appointmentService.getAppointmentsByPatientId(5L)).thenReturn(List.of(appointment));
        Mockito.when(appointmentService.getAppointment(4L)).thenReturn(appointment);
        Mockito.when(appointmentService.getAppointment(1L)).thenThrow(AppointmentNotFoundException.class);
        Mockito.when(appointmentService.createAppointment(any(AppointmentMapper.class))).thenAnswer(i->{
           AppointmentMapper mapper1=i.getArgument(0);
           if(mapper1.getPatientId()==1L) return appointment;
           else if(mapper1.getPatientId()==2L) throw new PatientNotFoundException("Patient Not found");
           else throw new DepartmentNotFoundException("Department Not found");
        });
    }

    @Test
    @DisplayName("Saving an appointment")
    void createAppointment() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/hmsapi/appointments/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"patientId\":1,\n" +
                        "    \"doctorId\":2,\n" +
                        "    \"date\":\"2022-02-12\",\n" +
                        "    \"departmentId\":6\n" +
                        "}")).andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    @DisplayName("Saving an appointment, invalid Patient ID")
    void createAppointmentInvalidPatient() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/hmsapi/appointments/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"patientId\":2,\n" +
                        "    \"doctorId\":2,\n" +
                        "    \"date\":\"2022-02-12\",\n" +
                        "    \"departmentId\":6\n" +
                        "}")).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Saving an appointment, invalid Department ID")
    void createAppointmentInvalidDepartment() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/hmsapi/appointments/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"patientId\":3,\n" +
                        "    \"doctorId\":2,\n" +
                        "    \"date\":\"2022-02-12\",\n" +
                        "    \"departmentId\":6\n" +
                        "}")).andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test
    @DisplayName("Get Department by Id")
    void getAppointment() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/appointments/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    @DisplayName("Get Department by Invalid Id")
    void getAppointmentInvalidId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/appointments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("get appointments by Patient Id")
    void getAppointmentsByPatientId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/appointments/patient/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    @DisplayName("Get appointment by Doctor ID")
    void getAppointmentsByDoctorId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/appointments/doctor/7")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Get appointment by Doctor Id and Date")
    void getAppointmentsByDoctorIdAndDate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/appointments/doctor/7/2022-02-12")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Upload prescription")
    void updateAppointment() throws Exception {
        MockMultipartFile mockMultipartFile=new MockMultipartFile("name","name.txt", MediaType.TEXT_PLAIN_VALUE,"helloworld".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.put("/hmsapi/appointments/4")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE).content(mockMultipartFile.getBytes())
                ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Get appointments by Date")
    void getAppointmentsByDate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/appointments/date/2022-02-12")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Get prescription")
    void downloadFile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/appointments/download/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Get all appointments")
    void getAppointmentsList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/appointments/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isOk());
    }
}