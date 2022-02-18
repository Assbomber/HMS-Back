package com.example.HMS.service;

import com.example.HMS.Constants;
import com.example.HMS.entity.Department;
import com.example.HMS.entity.StaffDepartmentMap;
import com.example.HMS.exception.DepartmentNotFoundException;
import com.example.HMS.respository.DepartmentRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * DepartmentServiceImpl implements DepartmentService
 * interface to implement CRUD options for Department Repository
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService{

    private final DepartmentRepo departmentRepo;

    /**
     * saveDepartment(Department department) allows you to create a new Department when valid Department Object is provided as a Body
     * */
    @Override
    public Department saveDepartment(Department department) {
        log.info("Creating a department :"+department.getName());
        return departmentRepo.save(department);
    }

    /**
     * getDepartment(Long id) allows you to retrieve a Department when provided valid ID
     * */
    @Override
    public Department getDepartment(Long id) throws DepartmentNotFoundException {
        log.info("fetching department by iD:"+id);
        if(id<1) throw new IllegalArgumentException(Constants.ILLEGAL_ID);
        Optional<Department> optional= departmentRepo.findById(id);
        if(optional.isPresent()) return optional.get();
        else throw new DepartmentNotFoundException(Constants.DEPARTMENT_NOT_FOUND+id);
    }

    /**
     * getAllDepartment() allows you to retrieve all Departments in the DB
     * */
    @Override
    public List<Department> getAllDepartment() {
        log.info("Fetching all departments");
        return departmentRepo.findAll();
    }

    /**
     * getDepartmentsByIDs(Long [] list) allows you to retrieve a List of Deparments associated with IDs
     * */
    @Override
    public List<Department> getDepartmentsByIDS(Long [] list){
        return departmentRepo.findAllById(Arrays.asList(list));
    }

    /**
     * getDoctorsByDepartmentId(Long id) allows you to retrieve a List of StaffDepartmentMap when provided valid ID
     * */
    @Override
    public List<StaffDepartmentMap> getDoctorsByDepartmentId(Long id) {
        log.info("Fetching doctors by department Id");
        if(id<1) throw new IllegalArgumentException(Constants.ILLEGAL_ID);
        return departmentRepo.findDoctorsByDepartmentId(id);
    }
}
