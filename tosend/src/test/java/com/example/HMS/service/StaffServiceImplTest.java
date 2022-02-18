package com.example.HMS.service;

import com.example.HMS.Role;
import com.example.HMS.entity.Department;
import com.example.HMS.entity.Staff;
import com.example.HMS.entity.StaffMapper;
import com.example.HMS.exception.CustomCreatedException;
import com.example.HMS.exception.DepartmentNotFoundException;
import com.example.HMS.exception.StaffNotFoundException;
import com.example.HMS.respository.StaffRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class StaffServiceImplTest {

    @Autowired
    private StaffServiceImpl staffService;
    @MockBean
    private DepartmentService departmentService;
    @MockBean
    private StaffRepo staffRepo;


    @BeforeEach
    void setUp() throws DepartmentNotFoundException {


        Department department1= Department.builder()
                .name("Something")
                .departmentId(1L).build();
        Department department2= Department.builder()
                .name("Something2")
                .departmentId(2L).build();
        Staff staff1=Staff.builder()
                .departments(List.of(department1,department2))
                .role(Role.DOCTOR.name())
                .staffId(1L)
                .password("xxxxx")
                .name("Doctor")
                .build();
        Staff staff2=Staff.builder()
                .departments(List.of(department1,department2))
                .role(Role.ADMIN.name())
                .staffId(2L)
                .password("xxxxx")
                .name("Doctor2")
                .build();

        Mockito.when(departmentService.getDepartment(1L)).thenReturn(department1);
        Mockito.when(departmentService.getDepartment(2L)).thenReturn(department2);
        Mockito.when(departmentService.getDepartmentsByIDS(new Long[]{2L})).thenReturn(List.of(department2));
        Mockito.when(staffRepo.save(any(Staff.class))).then(returnsFirstArg());
        Mockito.when(staffRepo.findAll()).thenReturn(List.of(staff1,staff2));
        Mockito.when(staffRepo.findById(3L)).thenThrow(RuntimeException.class);
        Mockito.when(staffRepo.findById(2L)).thenReturn(Optional.of(staff2));
        Mockito.doNothing().when(staffRepo).deleteById(4L);
        Mockito.when(staffRepo.findById(4L)).thenReturn(Optional.empty());

    }

    @Test
    @DisplayName("Deleting staff")
    void deleteStaff() {
        assertEquals(true,staffService.deleteStaff(4L));
        assertEquals(false,staffService.deleteStaff(2L));

    }

    @Test
    @DisplayName("Updating staff")
    void updateStaff() throws StaffNotFoundException, DepartmentNotFoundException, CustomCreatedException {
        StaffMapper staffMapper= StaffMapper.builder()
                .staffId(2L)
                .role(Role.DOCTOR.name())
                .password("Doctor")
                .name("Doctor")
                .departments(new Long[]{2L})
                .build();
        Staff returnedStaff=staffService.updateStaff(staffMapper);
        assertEquals(1,returnedStaff.getDepartments().size());
        assertEquals(2L,returnedStaff.getStaffId());
        assertEquals("Something2",returnedStaff.getDepartments().get(0).getName());
        staffMapper.setStaffId(3L);
        assertThrows(RuntimeException.class,()->staffService.updateStaff(staffMapper));
    }

    @Test
    @DisplayName("Updating staff, Invalid ID")
    void updateStaffInvalidId()  {
        StaffMapper staffMapper= StaffMapper.builder()
                .staffId(3L)
                .role(Role.DOCTOR.name())
                .password("Doctor")
                .name("Doctor")
                .departments(new Long[]{2L})
                .build();
        assertThrows(RuntimeException.class,()->staffService.updateStaff(staffMapper));
    }

    @Test
    @DisplayName("Saving a new Staff")
    void saveStaff() throws DepartmentNotFoundException {
        StaffMapper staffMapper= StaffMapper.builder()
                .role(Role.DOCTOR.name())
                .password("Doctor")
                .name("Doctor")
                .departments(new Long[]{1L,2L})
                .build();

        Staff returnedStaff=staffService.saveStaff(staffMapper);
        assertEquals(staffMapper.getName(),returnedStaff.getName());
        assertEquals(staffMapper.getRole(),returnedStaff.getRole());
//        assertEquals(staffMapper.getDepartments()[0],returnedStaff.getDepartments().get(0).getDepartmentId());
    }

    @Test
    @DisplayName("Get all staff")
    void getAllStaff() {
        List<Staff> list=staffService.getAllStaff();
        assertEquals(2,list.size());
        assertEquals(Role.ADMIN.name(),list.get(1).getRole());
        assertEquals(1L,list.get(0).getStaffId());
    }

    @Test
    @DisplayName("Get staff by ID")
    void getStaff() throws StaffNotFoundException {

        assertEquals(2L,staffService.getStaff(2L).getStaffId());
    }

    @Test
    @DisplayName("Get Staff by Invalid Id")
    void getStaffInvalidId(){
        assertThrows(RuntimeException.class,()->staffService.getStaff(3L));
    }

    @Test
    @DisplayName("Load user for auth")
    void loadUserByUsername() {
        assertEquals("2",staffService.loadUserByUsername("2").getUsername());
    }

    @Test
    @DisplayName("Load user for auth Invalid Id")
    void loadUserByInvalidUsername(){
        assertThrows(RuntimeException.class,()->staffService.loadUserByUsername("3"));
    }
}