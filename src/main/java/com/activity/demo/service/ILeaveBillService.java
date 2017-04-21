package com.activity.demo.service;

import java.util.List;

import com.activity.demo.model.Employee;
import com.activity.demo.model.LeaveBill;

public interface ILeaveBillService {

	List<LeaveBill> findLeaveBillList(int empId);

	void saveLeaveBill(LeaveBill leaveBill, Employee employee);

	LeaveBill findLeaveBillById(int id);

	void deleteLeaveBillById(int id);
	
	String selectleaveBillId(String seqname);
}
