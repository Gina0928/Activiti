package com.activity.demo.dao;

import java.util.List;

import com.activity.demo.model.Employee;

public interface IEmployeeDao {

	Employee selectEmployeeById(int id);
	
	/**使用用户名作为查询条件，查询用户对象*/
	List<Employee> selectEmployeeListByName(String name);
}
