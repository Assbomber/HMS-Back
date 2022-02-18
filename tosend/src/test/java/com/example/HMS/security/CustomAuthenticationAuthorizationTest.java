package com.example.HMS.security;

import com.example.HMS.controller.StaffController;
import com.example.HMS.entity.Staff;
import com.example.HMS.service.StaffService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@WebMvcTest(StaffController.class)
class CustomAuthenticationAuthorizationTest {

    @MockBean
    private StaffService staffService;
    @MockBean
    private UserDetailsService userDetailsService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp() throws Exception {
        Staff staff= Staff.builder()
                .role("ADMIN")
                .staffId(1L)
                .password(bCryptPasswordEncoder.encode("xxx")).build();
        Collection<SimpleGrantedAuthority> authorities=new ArrayList();
        authorities.add(new SimpleGrantedAuthority(staff.getRole()));
        UserDetails userDetails=new org.springframework.security.core.userdetails.User(staff.getStaffId().toString(),staff.getPassword(),authorities);
        Mockito.when(userDetailsService.loadUserByUsername("1")).thenReturn(userDetails);
    }

    @Test
    @DisplayName("Successfull Login")
    public void loginIn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login").servletPath("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("id","1")
                        .param("password","xxx")
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test
    @DisplayName("Unsuccessful Login")
    public void loginInInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login").servletPath("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("id","1")
                .param("password","xx")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @DisplayName("Authorization, Valid token")
    public void Authorization() throws Exception {
        MvcResult result=mockMvc.perform(MockMvcRequestBuilders.post("/login").servletPath("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("id","1")
                .param("password","xxx")
        ).andReturn();

        Map<String,String> map=new HashMap<>();
        Map<String,String> resMap=new ObjectMapper().readValue(result.getResponse().getContentAsString(),map.getClass());
        Mockito.when(staffService.getAllStaff()).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/staff/")
                        .header("Authorization","Bearer "+resMap.get("token"))
        ).andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    @DisplayName("Authorization, Invalid token")
    public void AuthorizationInvalid() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/staff/").servletPath("/hmsapi/staff")
                .header("Authorization","Bearer "+"token")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized());

    }

    @Test
    @DisplayName("Authorization, No token")
    public void AuthorizationNoToken() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/hmsapi/staff/").servletPath("/hmsapi/staff")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized());

    }
}