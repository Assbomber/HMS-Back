package com.example.HMS.service;

import com.example.HMS.entity.Appointment;
import com.example.HMS.entity.Patient;
import com.example.HMS.exception.CustomCreatedException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class AdminServiceImplTest {

    @Autowired
    private AdminService adminService;

    @MockBean
    private AppointmentService appointmentService;
    @MockBean
    private PatientService patientService;


    private Map<String,Integer> lastSixMonthsPatientCount=new TreeMap();

    void dateInitializer(){
        int minusDays=30;
        Date maxDate=new Date(LocalDate.now().getYear()-1900,LocalDate.now().getMonth().getValue()-1,LocalDate.now().getDayOfMonth());
        Date minDate=new Date(LocalDate.now().minusDays(30).getYear()-1900,LocalDate.now().minusDays(30).getMonth().getValue()-1,LocalDate.now().minusDays(30).getDayOfMonth());
        for(int i=0;i<6;i++){
            lastSixMonthsPatientCount.put(
                    minDate.toString(),
                    0
            );
            maxDate=minDate;
            minusDays+=30;
            minDate=new Date(LocalDate.now().minusDays(minusDays).getYear()-1900,LocalDate.now().minusDays(minusDays).getMonth().getValue()-1,LocalDate.now().minusDays(minusDays).getDayOfMonth());
        }
    }

    @BeforeEach
    void setUp() throws CustomCreatedException {
        List<Appointment> sampleListAppointments=new ArrayList();
        List<Patient> sampleListPatient=new ArrayList();

        Date date=new Date(LocalDate.now().getYear()-1900,LocalDate.now().getMonth().getValue()-1,LocalDate.now().getDayOfMonth());

        Mockito.when(appointmentService.getAppointmentsByDate(date)).thenReturn(sampleListAppointments);
        Mockito.when(patientService.getPatientsByDate(date)).thenReturn(sampleListPatient);
        Mockito.when(patientService.getPatientsBetweenDate(any(Date.class),any(Date.class))).thenReturn(sampleListPatient);
    }

    @Test
    @DisplayName("Getting dashboard Data for Admin")
    void getDashboardData() throws CustomCreatedException {
        dateInitializer();
        Map<String,Object> shouldReturnMap=new HashMap<>();
        shouldReturnMap.put("TodayAppointmentCount",0);
        shouldReturnMap.put("TodayPatientCount",0);
        shouldReturnMap.put("LastSixMonthsPatientCount",lastSixMonthsPatientCount);
        assertEquals(shouldReturnMap,adminService.getDashboardData());
    }
}