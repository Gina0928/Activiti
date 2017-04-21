package com.activity.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.activity.demo.dao.IEmployeeDao;
import com.activity.demo.model.Employee;
import com.activity.demo.service.IEmployeeService;

@Service
public class EmployeeServiceImpl implements IEmployeeService {

	@Autowired
	private IEmployeeDao employeeDao;
	
	@Override
	public Employee findEmployeeByName(String name) {
		List<Employee> lists = employeeDao.selectEmployeeListByName(name);
		
		if(lists!=null && lists.size()>0){
			return lists.get(0);
		}else{
			return null;
		}
		
	}
}
