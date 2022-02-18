package com.example.HMS.service;

import com.example.HMS.exception.CustomCreatedException;

import java.util.Map;

/**
 * AdminService is an interface that allows you to implement method required for retrieving dashboard data
 * */
public interface AdminService {
    public Map<String,Object> getDashboardData() throws CustomCreatedException;
}
