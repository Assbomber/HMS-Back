package com.example.HMS.service;

import com.example.HMS.entity.Department;
import com.example.HMS.entity.StaffDepartmentMap;
import com.example.HMS.exception.DepartmentNotFoundException;
import com.example.HMS.respository.DepartmentRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class DepartmentServiceImplTest {

    @Autowired
    private DepartmentService departmentService;

    @MockBean
    private DepartmentRepo departmentRepo;

    @BeforeEach
    void setUp() {
        Department department1= Department.builder()
                .name("Something")
                .departmentId(1L).build();
        Department department2= Department.builder()
                .name("Something2")
                .departmentId(2L).build();

        List<StaffDepartmentMap>list=new ArrayList<>();
        list.add(new StaffDepartmentMap() {
            @Override
            public String getDoctor() {
                return "Aman";
            }

            @Override
            public String getStaffId() {
                return "1";
            }
        });

        Mockito.when(departmentRepo.save(any(Department.class))).then(returnsFirstArg());
        Mockito.when(departmentRepo.findById(2L)).thenReturn(Optional.of(department2));
        Mockito.when(departmentRepo.findById(3L)).thenThrow(RuntimeException.class);
        Mockito.when(departmentRepo.findAll()).thenReturn(List.of(department1,department2));
        Mockito.when(departmentRepo.findDoctorsByDepartmentId(4L)).thenReturn(list);
        Mockito.when(departmentRepo.findDoctorsByDepartmentId(2L)).thenReturn(List.of());


    }

    @Test
    @DisplayName("Saving a Department")
    void saveDepartment() {
        Department department1= Department.builder()
                .name("Something")
                .departmentId(1L).build();
        assertEquals(department1,departmentService.saveDepartment(department1));
    }

    @Test
    @DisplayName("Get Department by ID")
    void getDepartment() throws DepartmentNotFoundException {
        Department department2= Department.builder()
                .name("Something2")
                .departmentId(2L).build();
        assertEquals(department2,departmentService.getDepartment(2L));
        assertThrows(RuntimeException.class,()->departmentService.getDepartment(3L));
    }

    @Test
    @DisplayName("Get all departments")
    void getAllDepartment() {
        assertEquals(2,departmentService.getAllDepartment().size());
        assertEquals("Something",departmentService.getAllDepartment().get(0).getName());
    }

    @Test
    @DisplayName("Get Doctors by Department ID")
    void getDoctorsByDepartmentId() {
        assertEquals(0,departmentService.getDoctorsByDepartmentId(2L).size());
        assertEquals(1,departmentService.getDoctorsByDepartmentId(4L).size());
        assertEquals("Aman",departmentService.getDoctorsByDepartmentId(4L).get(0).getDoctor());
    }
}