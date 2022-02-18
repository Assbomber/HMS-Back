package com.example.HMS.controller;

import com.example.HMS.entity.Department;
import com.example.HMS.entity.StaffDepartmentMap;
import com.example.HMS.exception.DepartmentNotFoundException;
import com.example.HMS.service.DepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Department Controller is a Rest Controller that allows following operations:
 * <li>Creating a new Department</li>
 * <li>Retrieving all Departments</li>
 * <li>Get Department by ID</li>
 * <li>Retrieve Staff(s) by Department ID</li>
 * */
@RestController
@RequiredArgsConstructor
@RequestMapping("/hmsapi/departments")
@Slf4j
@Api(tags = "Department APIs", description = "Allows CRUD Operations for Department")
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * createDepartment(Department department) allows you to create a new Department when valid Department Object is provided as a Body
     * */
    @ApiOperation(value = "Create a Department",
            notes = " allows you to create a new Department when valid Department Object is provided as a Body",
            response = Department.class
    )
    @PostMapping("/")
    public Department createDepartment(@Valid @RequestBody Department department){
        log.info("Creating department :"+department.getName());
        return departmentService.saveDepartment(department);
    }

    /**
     * getAllDepartments() allows you to retrieve all Departments in the DB
     * */
    @ApiOperation(value = "Get all Departments",
            notes = "allows you to retrieve all Departments in the DB",
            response = List.class
    )
    @GetMapping("/")
    public List<Department> getAllDepartments(){
        log.info("Retrieving all departments");
        return departmentService.getAllDepartment();
    }

    /**
     * getDepartment(Long id) allows you to retrieve a Department when provided valid ID
     * */
    @ApiOperation(value = "Get a Department",
            notes = "allows you to retrieve a Department when provided valid ID",
            response = Department.class
    )
    @GetMapping("/{id}")
    public Department getDepartment(@PathVariable("id") Long id) throws DepartmentNotFoundException {
        log.info("Retrieving department by id:"+id);
        return departmentService.getDepartment(id);
    }

    /**
     * getStaffByDepartmentId(Long id) allows you to retrieve a List of StaffDepartmentMap when provided valid ID
     * */
    @ApiOperation(value = "Get staff By department ID",
            notes = " allows you to retrieve a List of StaffDepartmentMap when provided valid ID",
            response = List.class
    )
    @GetMapping("/{id}/staff")
    public List<StaffDepartmentMap> getStaffByDepartmentId(@PathVariable("id") Long id){
        log.info("Retrieving staff by Department id:"+id);
        return departmentService.getDoctorsByDepartmentId(id);
    }


}
