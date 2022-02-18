package com.example.HMS.respository;

import com.example.HMS.Role;
import com.example.HMS.entity.Department;
import com.example.HMS.entity.Staff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StaffRepoTest {

    @Autowired
    private StaffRepo staffRepo;
    @Autowired
    private TestEntityManager testEntityManager;


    @Test
    @DisplayName("Find by Id")
    void findById(){
        Department department1= Department.builder()
                .name("Something")
                .departmentId(1L).build();
        Department department2= Department.builder()
                .name("Something2")
                .departmentId(2L).build();
        Staff staff1=Staff.builder()
                .departments(List.of(department1,department2))
                .role(Role.DOCTOR.name())
                .password("xxxxx")
                .name("Doctor")
                .build();
        Staff savedStaff=testEntityManager.persist(staff1);
        assertEquals("Doctor",staffRepo.findById(savedStaff.getStaffId()).get().getName());
        testEntityManager.clear();
        assertThrows(RuntimeException.class,()->staffRepo.findById(savedStaff.getStaffId()).get().getName());
    }

}