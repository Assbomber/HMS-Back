package com.example.HMS.controller;


import com.example.HMS.exception.CustomCreatedException;
import com.example.HMS.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * AdminController is a Rest Controller that allows admin to
 * retrieve the required dashboard data.
 * */
@RestController
@RequestMapping("/hmsapi/admin/")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "Admin APIs", description = "Helps accumulate birds eye view for HMS data")
public class AdminController {

    public final AdminService adminService;

    /**
     * getDashBoardData() allows to
     * retrieve the dashboard data.
     * */
    @ApiOperation(value = "Get Admin Dashboard Data",
            notes = "This end point lets you retrieve all required data to fill up the admin dashboard",
            response = ResponseEntity.class
    )
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashBoardData() throws CustomCreatedException {
        log.info("Retrieving Dashboard Data");
        return ResponseEntity.ok().body(adminService.getDashboardData()
        );
    }
}
