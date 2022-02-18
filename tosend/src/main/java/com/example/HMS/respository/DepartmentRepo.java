package com.example.HMS.respository;

import com.example.HMS.entity.Department;
import com.example.HMS.entity.StaffDepartmentMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * DepartmentRepo extends JpaRepository to provided Jpa functionalities
 * */
@Repository
public interface DepartmentRepo extends JpaRepository<Department,Long> {

    @Query(value = "SELECT s.staff_id as staffId,s.name as doctor\n" +
            "FROM staff_departments sd\n" +
            "JOIN staff s\n" +
            "\tON sd.staff_staff_id=s.staff_id\n" +
            "JOIN department d\n" +
            "\tON sd.departments_department_id=d.department_id\n" +
            "WHERE d.department_id=:id",nativeQuery = true)
    public List<StaffDepartmentMap> findDoctorsByDepartmentId(Long id);

}
