package com.example.HMS.respository;

import com.example.HMS.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * StaffRepo extends JpaRepository to provided Jpa functionalities
 * */
@Repository
public interface StaffRepo extends JpaRepository<Staff,Long> {
}
