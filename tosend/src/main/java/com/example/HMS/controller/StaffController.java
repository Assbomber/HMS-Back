
package com.example.HMS.controller;

import com.example.HMS.Constants;
import com.example.HMS.Role;
import com.example.HMS.entity.Staff;
import com.example.HMS.entity.StaffMapper;
import com.example.HMS.exception.CustomCreatedException;
import com.example.HMS.exception.DepartmentNotFoundException;
import com.example.HMS.exception.StaffNotFoundException;
import com.example.HMS.service.StaffService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Staff Controller is a Rest controller class that provides functionalities like:
 * <li>Creating a new staff member</li>
 * <li>Getting a Staff by ID</li>
 * <li>Deleting a Staff by ID</li>
 * <li>Update a Staff by ID</li>
 */
@RestController
@RequestMapping("hmsapi/staff")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
@Api(tags="Staff APIs", description = "Allows CRUD Operations for Staff")
public class StaffController {

    private final StaffService staffService;

    /**
     * savestaff(StaffMapper mapper) allows you to save staff to the DB when StaffMapper object is provided as Body
     * */
    @ApiOperation(value = "Save Staff",
            notes = " allows you to save staff to the DB when StaffMapper object is provided as Body",
            response = ResponseEntity.class
    )
    @PostMapping("/")
    public ResponseEntity<?> saveStaff(@Valid @RequestBody StaffMapper staffmapper) throws DepartmentNotFoundException {
        log.info("Saving Staff :"+staffmapper.getName());
        if(staffmapper.getRole().equals(Role.DOCTOR.name()) && (staffmapper.getDepartments()==null || staffmapper.getDepartments().length==0)){
            log.error("Failed to save staff. Trying to save a Doctor without providing Department");
            Map<String,String> map=new HashMap<>();
            map.put("status", HttpStatus.BAD_REQUEST.toString());
            map.put("error","Doctor must have a Department");
            return ResponseEntity.badRequest().body(map);
        }
        return ResponseEntity.ok().body(staffService.saveStaff(staffmapper));
    }

    /**
     * updateStaff(StaffMapper mapper) allows you to update staff to the DB when StaffMapper object is provided as Body
     * */
    @ApiOperation(value = "Update Staff",
            notes = " allows you to update staff to the DB when StaffMapper object is provided as Body",
            response = ResponseEntity.class
    )
    @PutMapping("/")
    public ResponseEntity<?> updateStaff(@Valid @RequestBody StaffMapper staff) throws StaffNotFoundException, DepartmentNotFoundException, CustomCreatedException {
        log.info("Updating staff with ID :"+staff.getStaffId());
        if(staff.getStaffId()==null) throw new CustomCreatedException(Constants.ID_MISSING);
        return ResponseEntity.ok().body(staffService.updateStaff(staff));
    }

    /**
     * getAllStaff() allows you to  retrieve all Staff
     * */
    @ApiOperation(value = "Get all Staff",
            notes = " allows you to  retrieve all Staff",
            response = List.class
    )
    @GetMapping("/")
    public List<Staff>  getAllStaff(){
        log.info("Retrieving all Staff");
        return staffService.getAllStaff();
    }

    /**
     * getStaff(Long id) allows you to retrieve a single Staff when passed in a valid Staff ID
     * */
    @ApiOperation(value = "Get a Staff",
            notes = " allows you to retrieve a single Staff when passed in a valid Staff ID",
            response = Staff.class
    )
    @GetMapping("/{id}")
    public Staff getStaff(@PathVariable("id") Long id) throws StaffNotFoundException {
        log.info("Retrieving staff with ID:"+id);
        return staffService.getStaff(id);
    }

    /**
     * deleteStaff(Long id) allows you to delete a Staff from the DB when provided valid Staff ID
     * */
    @ApiOperation(value = "Delete Staff",
            notes = " allows you to delete a Staff from the DB when provided valid Staff ID",
            response = ResponseEntity.class
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable("id") Long id){
        log.info("Deleting staff with ID:"+id);
        Map<String,String> map=new HashMap<>();
        if(staffService.deleteStaff(id)){
            log.info("Staff deletion successful for id:"+id);
            map.put("status",HttpStatus.ACCEPTED.name());
            map.put("message","The User was Deleted");
            return ResponseEntity.accepted().body(map);
        }else{
            log.error("Staff deletion unsuccessful for id:"+id);
            map.put("status",HttpStatus.BAD_REQUEST.name());
            map.put("message","Error deleting the user");
            return ResponseEntity.badRequest().body(map);
        }
    }






}
