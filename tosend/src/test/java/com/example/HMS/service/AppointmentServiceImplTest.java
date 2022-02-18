package com.example.HMS.service;

import com.example.HMS.Role;
import com.example.HMS.entity.*;
import com.example.HMS.exception.*;
import com.example.HMS.respository.AppointmentRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class AppointmentServiceImplTest {

    @Autowired
    private AppointmentService appointmentService;
    @MockBean
    private AppointmentRepo appointmentRepo;
    @MockBean
    private DepartmentService departmentService;
    @MockBean
    private PatientService patientService;
    @MockBean
    private StaffService staffService;
    @MockBean
    private UserDetailsService userDetailsService;
    private Date date=new Date(2022-1900,02,03);

    @BeforeEach
    void setUp() throws PatientNotFoundException, DepartmentNotFoundException, StaffNotFoundException {
        Patient patient= Patient.builder()
                .patientId(1L)
                .name("Patient1")
                .age(100)
                .mobile(9999999999L)
                .address("india")
                .gender("MALE")
                .build();
        Department department= Department.builder()
                .departmentId(1L)
                .name("Department").build();
        Staff staff= Staff.builder()
                .staffId(1L)
                .name("Staff")
                .password("x")
                .role(Role.DOCTOR.name())
                .departments(List.of(department)).build();
        Appointment appointment=Appointment.builder()
                .date(date)
                .staff(staff)
                .department(department)
                .patient(patient)
                .appointmentId(1L)
                .build();
        Mockito.when(patientService.getPatientInternal(1L)).thenReturn(patient);
        Mockito.when(patientService.getPatientInternal(4L)).thenThrow(PatientNotFoundException.class);
        Mockito.when(departmentService.getDepartment(1L)).thenReturn(department);
        Mockito.when(staffService.getStaff(1L)).thenReturn(staff);
        Mockito.when(appointmentRepo.save(any(Appointment.class))).then(returnsFirstArg());
        Mockito.when(appointmentRepo.findById(4L)).thenReturn(Optional.empty());
        Mockito.when(appointmentRepo.findById(1L)).thenReturn(Optional.of(appointment));
        Mockito.when(appointmentRepo.findAllByPatientPatientId(1L)).thenReturn(List.of(appointment));
        Mockito.when(appointmentRepo.findAllByStaffStaffIdAndDate(1L,date)).thenReturn(List.of(appointment));
        Mockito.when(appointmentRepo.findAllByStaffStaffId(1L)).thenReturn(List.of(appointment));
        Mockito.when(appointmentRepo.findAllByTimeStamp(date)).thenReturn(List.of(appointment));
        Mockito.when(appointmentRepo.findAll()).thenReturn(List.of(appointment));
        Mockito.when(appointmentRepo.findByDateBetween(date,date)).thenReturn(List.of(appointment));
    }

    @Test
    @DisplayName("Get appointments between Date")
    void getAppointmentsBetween() throws CustomCreatedException {
        assertEquals(1,appointmentService.getAppointmentsBetween(date,date).size());
    }

    @Test
    @DisplayName("Get all appointments")
    void getAllAppointments() {
        assertEquals(1,appointmentService.getAllAppointments().size());
    }

    @Test
    @DisplayName("Get appointments by Date")
    void getAppointmentsByDate() throws CustomCreatedException {
        assertEquals(1,appointmentService.getAppointmentsByDate(date).size());
    }

    @Test
    @DisplayName("Get appointments by Doctor ID")
    void getAppointmentsByDoctorId() {
        assertEquals(1,appointmentService.getAppointmentsByDoctorId(1L).size());
    }

    @Test
    @DisplayName("Get appointments by Staff ID and date")
    void getAppointmentsByDoctorIdAndDate() throws CustomCreatedException {
        assertEquals(1,appointmentService.getAppointmentsByDoctorIdAndDate(1L,date).size());
    }

    @Test
    @DisplayName("Create an Appointment")
    void createAppointment() throws StaffNotFoundException, PatientNotFoundException, DepartmentNotFoundException {
        AppointmentMapper mapper=AppointmentMapper.builder()
                .date(date)
                .departmentId(1L)
                .doctorId(1L)
                .patientId(1L)
                .build();
        Appointment appointment=appointmentService.createAppointment(mapper);
        assertEquals("Patient1",appointment.getPatient().getName());
        assertEquals("Department",appointment.getDepartment().getName());
    }

    @Test
    @DisplayName("Create an appointment, Invalid Patient")
    void createAppointmentInvalidPatient() throws StaffNotFoundException, PatientNotFoundException, DepartmentNotFoundException {
        AppointmentMapper mapper=AppointmentMapper.builder()
                .date(date)
                .departmentId(1L)
                .doctorId(1L)
                .patientId(4L)
                .build();
        assertThrows(PatientNotFoundException.class,()->appointmentService.createAppointment(mapper));
    }

    @Test
    @DisplayName("Get appointment")
    void getAppointment() throws AppointmentNotFoundException {
        assertEquals("Staff",appointmentService.getAppointment(1L).getStaff().getName());
    }
    @Test
    @DisplayName("Get Appointment By Invalid ID")
    void getAppointmentInvalidId() throws AppointmentNotFoundException {
        assertThrows(AppointmentNotFoundException.class,()->appointmentService.getAppointment(4L));
    }

    @Test
    @DisplayName("Get appointments by Patient Id")
    void getAppointmentsByPatientId() {
        assertEquals(1,appointmentService.getAppointmentsByPatientId(1L).size());
    }

    @Test
    @DisplayName("Saving Prescription")
    void saveFile() throws AppointmentNotFoundException, CustomCreatedException {
        MockMultipartFile mockMultipartFile=new MockMultipartFile("name","name.txt", MediaType.TEXT_PLAIN_VALUE,"helloworld".getBytes());
        assertEquals("name.txt",appointmentService.saveFile(1L,mockMultipartFile).getDocName());
    }
}