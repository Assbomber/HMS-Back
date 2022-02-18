
package com.example.HMS.service;

import com.example.HMS.Constants;
import com.example.HMS.entity.Department;
import com.example.HMS.entity.Staff;
import com.example.HMS.entity.StaffMapper;
import com.example.HMS.exception.CustomCreatedException;
import com.example.HMS.exception.DepartmentNotFoundException;
import com.example.HMS.exception.StaffNotFoundException;
import com.example.HMS.respository.StaffRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


/**
 * StaffServiceImpl implements StaffService interface and UserDetailsService to implements methods for Staff CRUD functionality
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class StaffServiceImpl implements StaffService, UserDetailsService {


    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final StaffRepo staffRepo;
    private final DepartmentService departmentService;

    /**
     * deleteStaff(Long id) allows you to delete a Staff from the DB when provided valid Staff ID
     * */
    @Override
    public boolean deleteStaff(Long id) {
        log.info("Deleting staff: "+id);
        if(id<1) throw new IllegalArgumentException(Constants.ILLEGAL_ID);
        staffRepo.deleteById(id);
        if(!staffRepo.findById(id).isPresent()) return true;
        else return false;
    }

    /**
     * updateStaff(StaffMapper mapper) allows you to update staff to the DB when StaffMapper object is provided as Body
     * */
    @Override
    @Transactional
    public Staff updateStaff(StaffMapper staff) throws StaffNotFoundException, DepartmentNotFoundException, CustomCreatedException {
        log.info("Updating staff: "+staff.getStaffId());
        if(staff.getStaffId()==null) throw new CustomCreatedException(Constants.ID_NULL);
        if(staff.getStaffId()<1) throw new IllegalArgumentException(Constants.ILLEGAL_ID);
        Optional<Staff> optional=staffRepo.findById(staff.getStaffId());
        List<Department> list=departmentService.getDepartmentsByIDS(staff.getDepartments());
        if(optional.isPresent()){
                optional.get().setName(staff.getName());
                optional.get().setDepartments(list);
                return staffRepo.save(optional.get());
        }else throw new StaffNotFoundException(Constants.STAFF_NOT_FOUND+staff.getStaffId());
    }

    /**
     * saveStaff(StaffMapper mapper) allows you to save staff to the DB when StaffMapper object is provided as Body
     * */
    @Override
    public Staff saveStaff(StaffMapper staffmapper) throws DepartmentNotFoundException {
        log.info("Creating staff {}",staffmapper.getName());
        staffmapper.setPassword(bCryptPasswordEncoder.encode(staffmapper.getPassword()));
        List<Department> list=departmentService.getDepartmentsByIDS(staffmapper.getDepartments());
        Staff staff= Staff.builder()
                .name(staffmapper.getName())
                .password(staffmapper.getPassword())
                .role(staffmapper.getRole())
                .departments(list).build();
        return staffRepo.save(staff);
    }

    /**
     * getAllStaff() allows you to  retrieve all Staff
     * */
    @Override
    public List<Staff> getAllStaff() {
        log.info("Retrieving all staff");
        return staffRepo.findAll();
    }

    /**
     * getStaff(Long id) allows you to retrieve a single Staff when passed in a valid Staff ID
     * */
    public Staff getStaff(Long id) throws StaffNotFoundException {
        log.info("Fetching staff with id:"+id);
        if(id<1) throw new IllegalArgumentException(Constants.ILLEGAL_ID);
        Optional<Staff> optional=staffRepo.findById(id);
        if(optional.isPresent()) return optional.get();
        else throw new StaffNotFoundException(Constants.STAFF_NOT_FOUND+id);

    }

    /**
     * loadUserByUsername(String id) used to load user from database for authentication purpose
     * <h2>MODIFYING MAY BREAK LOGIN FUNCTIONALITY</h2>
     * */
    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        log.info("Loading user :"+id);
        Optional<Staff> optional= staffRepo.findById(Long.parseLong(id));
        if(optional.isPresent()){
            Staff staff=optional.get();
            Collection<SimpleGrantedAuthority> authorities=new ArrayList();
            authorities.add(new SimpleGrantedAuthority(staff.getRole()));
            return new org.springframework.security.core.userdetails.User(staff.getStaffId().toString(),staff.getPassword(),authorities);
        }else{
            throw new UsernameNotFoundException("User not found");
        }
    }

}
