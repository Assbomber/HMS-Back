package com.example.HMS;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.HMS.entity.StaffMapper;
import com.example.HMS.exception.DepartmentNotFoundException;
	
@SpringBootTest
class HmsApplicationTests {

	@Autowired
	private StaffServiceImpl staffservice;
	
	@Test
	void contextLoads() throws DepartmentNotFoundException {
		StaffMapper staffMapper=StaffMapper.builder()
			.role(Role.ADMIN.name())
			.password("Admin")
			.name("Admin")
			.build();
		System.out.println(staffservice.saveStaff(staffMapper));
	}

}
