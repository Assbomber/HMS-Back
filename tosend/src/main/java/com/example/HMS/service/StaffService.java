package com.example.HMS.service;

import com.example.HMS.entity.Staff;
import com.example.HMS.entity.StaffMapper;
import com.example.HMS.exception.CustomCreatedException;
import com.example.HMS.exception.DepartmentNotFoundException;
import com.example.HMS.exception.StaffNotFoundException;

import java.util.List;
/**
 * StaffService is an Interface that helps to implement methods for CRUD functionalities
 * */
public interface StaffService {
    public Staff saveStaff(StaffMapper staffmapper) throws DepartmentNotFoundException;
    public Staff getStaff(Long id) throws StaffNotFoundException;
    public List<Staff> getAllStaff();
    public Staff updateStaff(StaffMapper staff) throws StaffNotFoundException, DepartmentNotFoundException, CustomCreatedException;

    boolean deleteStaff(Long id);
}
