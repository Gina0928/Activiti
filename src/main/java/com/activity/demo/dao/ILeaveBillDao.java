package com.activity.demo.dao;

import java.util.List;

import com.activity.demo.model.LeaveBill;



public interface ILeaveBillDao {

	/**查询自己的请假单的信息*/
	List<LeaveBill> selectLeaveBillListByEmpId(int empId);
	
	/**保存请假单*/
	void saveLeaveBill(LeaveBill leaveBill);
	
	/**使用请假单ID，查询请假单的对象*/
	LeaveBill findLeaveBillById(int id);
	
	/**更新请假单*/
	void updateLeaveBill(LeaveBill leaveBill);
	
	/**使用请假单ID，删除请假单*/
	void deleteLeaveBillById(int id);
	
	String selectSequenceId(String seqname);
}
