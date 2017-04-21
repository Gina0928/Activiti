package com.activity.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.activity.demo.dao.ILeaveBillDao;
import com.activity.demo.model.Employee;
import com.activity.demo.model.LeaveBill;
import com.activity.demo.service.ILeaveBillService;

@Service
public class LeaveBillServiceImpl implements ILeaveBillService {

	@Autowired
	private ILeaveBillDao leaveBillDao;
	
	@Override
	public List<LeaveBill> findLeaveBillList(int empId) {
		List<LeaveBill> list = leaveBillDao.selectLeaveBillListByEmpId(empId);
		return list;
	}
	
	@Override
	public void saveLeaveBill(LeaveBill leaveBill, Employee employee) {
		int id = leaveBill.getId();
		if(id==0){
			leaveBill.setUser(employee);
			leaveBillDao.saveLeaveBill(leaveBill);
		}
		else{
			leaveBillDao.updateLeaveBill(leaveBill);
		}
		
	}
	
	@Override
	public LeaveBill findLeaveBillById(int id) {
		LeaveBill bill = leaveBillDao.findLeaveBillById(id);
		return bill;
	}
	
	@Override
	public void deleteLeaveBillById(int id) {
		leaveBillDao.deleteLeaveBillById(id);
	}

    @Override
    public String selectleaveBillId(String seqname) {
        return leaveBillDao.selectSequenceId(seqname);
    }

}
