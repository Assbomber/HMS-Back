package com.example.HMS.controller;

import com.example.HMS.exception.CustomCreatedException;
import com.example.HMS.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @MockBean
    private AdminService adminService;

    @MockBean
    private UserDetailsService userDetailsService;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() throws CustomCreatedException {
        Mockito.when(adminService.getDashboardData()).thenReturn(new HashMap<String,Object>());
    }
    @Test
    void getDashBoardData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/admin/dashboard"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}