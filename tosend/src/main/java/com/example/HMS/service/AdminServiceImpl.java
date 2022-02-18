package com.example.HMS.service;

import com.example.HMS.exception.CustomCreatedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

/**
 * AdminServiceImpl implements AdminService
 * interface to implement the getDashboardData method.
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements  AdminService {

    public final AppointmentService appointmentService;
    public final StaffService staffService;
    public final PatientService patientService;

    /**
     * getDashboardData() allows one to retrieve required data for dashboard, like:
     * <li>Today's Appointment Count</li>
     * <li>Today's Patient Count</li>
     * <li>Last Six months Patient Count</li>
     * */
    public Map<String,Object> getDashboardData() throws CustomCreatedException {
        log.info("Accumulating dashboard Data");
        Date date=new Date(LocalDate.now().getYear()-1900,LocalDate.now().getMonth().getValue()-1,LocalDate.now().getDayOfMonth());
        Map<String,Object> map=new HashMap();
        map.put("TodayAppointmentCount",appointmentService.getAppointmentsByDate(date).size());
        map.put("TodayPatientCount",patientService.getPatientsByDate(date).size());
        Map<String,Integer> lastSixMonthsPatientCount=new TreeMap();
        int minusDays=30;
        Date maxDate=date;
        Date minDate=new Date(LocalDate.now().minusDays(30).getYear()-1900,LocalDate.now().minusDays(30).getMonth().getValue()-1,LocalDate.now().minusDays(30).getDayOfMonth());
        for(int i=0;i<6;i++){
            lastSixMonthsPatientCount.put(
                    minDate.toString(),
                    patientService.getPatientsBetweenDate(minDate,maxDate).size()
            );
            maxDate=minDate;
            minusDays+=30;
            minDate=new Date(LocalDate.now().minusDays(minusDays).getYear()-1900,LocalDate.now().minusDays(minusDays).getMonth().getValue()-1,LocalDate.now().minusDays(minusDays).getDayOfMonth());
        }
        map.put("LastSixMonthsPatientCount",lastSixMonthsPatientCount);
        return map;
    }
}
