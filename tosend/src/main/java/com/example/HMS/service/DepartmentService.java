package com.example.HMS.service;

import com.example.HMS.entity.Department;
import com.example.HMS.entity.StaffDepartmentMap;
import com.example.HMS.exception.DepartmentNotFoundException;

import java.util.List;

/**
 * DepartmentService is an interface that allows you to implement methods required for CRUD operations
 * */
public interface DepartmentService {
    public Department saveDepartment(Department department);
    public Department getDepartment(Long id) throws DepartmentNotFoundException;
    public List<Department> getAllDepartment();
    public List<StaffDepartmentMap> getDoctorsByDepartmentId(Long id);
    public List<Department> getDepartmentsByIDS(Long [] list);
}
