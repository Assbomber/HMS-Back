package com.example.HMS.controller;

import com.example.HMS.Role;
import com.example.HMS.entity.Department;
import com.example.HMS.entity.Staff;
import com.example.HMS.entity.StaffMapper;
import com.example.HMS.exception.CustomCreatedException;
import com.example.HMS.exception.DepartmentNotFoundException;
import com.example.HMS.exception.StaffNotFoundException;
import com.example.HMS.service.StaffService;
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

@WebMvcTest(StaffController.class)
@AutoConfigureMockMvc(addFilters = false)

class StaffControllerTest {

    @MockBean
    private StaffService staffService;
    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws DepartmentNotFoundException, StaffNotFoundException, CustomCreatedException {

        Department department1= Department.builder()
                .name("Something")
                .departmentId(1L).build();

        StaffMapper staffMapper= StaffMapper.builder()
                .role(Role.DOCTOR.name())
                .password("Doctor")
                .name("Doctor")
                .departments(new Long[]{1L})
                .build();
        StaffMapper staffMapper3= StaffMapper.builder()
                .staffId(4L)
                .role(Role.DOCTOR.name())
                .password("Doctor")
                .name("Doctor")
                .departments(new Long[]{1L})
                .build();
        Staff staff= Staff.builder()
                .staffId(1L)
                .role(Role.DOCTOR.name())
                .password("Doctor")
                .name("Doctor")
                .departments(List.of(department1))
                .build();
        Staff staff2= Staff.builder()
                .staffId(2L)
                .role(Role.DOCTOR.name())
                .password("Doctor2")
                .name("Doctor2")
                .departments(List.of(department1))
                .build();
        Staff staff3= Staff.builder()
                .staffId(4L)
                .role(Role.DOCTOR.name())
                .password("Doctor2")
                .name("Doctor2")
                .departments(List.of(department1))
                .build();



        Mockito.when(staffService.saveStaff(staffMapper)).thenReturn(staff);
//        Mockito.when(staffService.updateStaff(staffMapper)).thenThrow(RuntimeException.class);
        Mockito.when(staffService.getAllStaff()).thenReturn(List.of(staff,staff2));
        Mockito.when(staffService.updateStaff(staffMapper3)).thenReturn(staff3);
        Mockito.when(staffService.getStaff(4L)).thenReturn(staff3);
        Mockito.when(staffService.getStaff(1L)).thenThrow(StaffNotFoundException.class);
        Mockito.when(staffService.deleteStaff(4L)).thenReturn(true);
        Mockito.when(staffService.deleteStaff(1L)).thenReturn(false);
    }

    @Test
    @DisplayName("Saving staff")
    void saveStaff() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/hmsapi/staff/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +

                        "    \"name\":\"Doctor\",\n" +
                        "    \"role\":\"DOCTOR\",\n" +
                        "    \"password\":\"Doctor\",\n" +
                        "    \"departments\":[1]\n" +
                        "}")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Saving Doctor, department not provided")
    void saveStaffInvalidSchema() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/hmsapi/staff/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"name\":\"Doctor\",\n" +
                        "    \"password\":\"sfsfsdfsdf\",\n" +
                        "    \"role\":\"DOCTOR\"\n" +
                        "}")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Updating staff")
    void updateStaff() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/hmsapi/staff/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"staffId\":\"4\",\n" +
                        "    \"name\":\"Doctor\",\n" +
                        "    \"role\":\"DOCTOR\",\n" +
                        "    \"password\":\"Doctor\",\n" +
                        "    \"departments\":[1]\n" +
                        "}")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Updating staff, staffId not provided")
    void updateStaffInvalidSchema() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/hmsapi/staff/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"name\":\"Doctor\",\n" +
                        "    \"role\":\"DOCTOR\",\n" +
                        "    \"password\":\"Doctor\",\n" +
                        "    \"departments\":[1]\n" +
                        "}")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getAllStaff() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/staff/")
                .content("")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Getting staff")
    void getStaff() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/staff/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Getting staff, Invalid Id provided")
    void getStaffInvalidId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/staff/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Deleting Staff")
    void deleteStaff() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/hmsapi/staff/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isAccepted());
    }

    @Test
    @DisplayName("Deleting staff, invalid id provided")
    void deleteStaffInvalidId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/hmsapi/staff/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}