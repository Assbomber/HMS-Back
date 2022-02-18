package com.example.HMS.controller;

import com.example.HMS.entity.Department;
import com.example.HMS.entity.StaffDepartmentMap;
import com.example.HMS.exception.DepartmentNotFoundException;
import com.example.HMS.service.DepartmentService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(DepartmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class DepartmentControllerTest {

    @MockBean
    private DepartmentService departmentService;
    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws DepartmentNotFoundException {
        Department department=Department.builder()
                .name("Department").build();
        Department department1=Department.builder()
                .departmentId(1L)
                .name("Department").build();
        Department department2=Department.builder()
                .departmentId(2L)
                .name("Department2").build();


        Mockito.when(departmentService.saveDepartment(department)).thenReturn(department1);
        Mockito.when(departmentService.getAllDepartment()).thenReturn(List.of(department1,department2));
        Mockito.when(departmentService.getDepartment(1L)).thenReturn(department1);
        Mockito.when(departmentService.getDepartment(4L)).thenThrow(DepartmentNotFoundException.class);
        Mockito.when(departmentService.getDoctorsByDepartmentId(1L)).thenReturn(List.of(
                new StaffDepartmentMap() {
                    @Override
                    public String getDoctor() {
                        return "Aman";
                    }

                    @Override
                    public String getStaffId() {
                        return "1";
                    }
                },
                new StaffDepartmentMap() {
                    @Override
                    public String getDoctor() {
                        return "Amania";
                    }

                    @Override
                    public String getStaffId() {
                        return "2";
                    }
                }
        ));


    }

    @Test
    @DisplayName("Creating a department")
    void createDepartment() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/hmsapi/departments/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"name\":\"HELLO\"\n" +
                        "}")).andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    @DisplayName("Creating a department, No name provided")
    void createDepartmentNoName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/hmsapi/departments/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "}")).andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    @DisplayName("Get all department")
    void getAllDepartments() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/departments/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Get department by ID")
    void getDepartment() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/departments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Get Department by ID, invalid id provided")
    void getDepartmentInvalidId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/departments/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Getting Staff by Department Id")
    void getStaffByDepartmentId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/departments/1/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isOk());
    }

}