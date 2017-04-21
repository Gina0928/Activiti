package com.activity.demo.utils;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.activity.demo.model.Employee;

/**
 * 员工经理任务分配
 *
 */
@SuppressWarnings("serial")
public class ManagerTaskHandler implements TaskListener {
    
	@Override
	public void notify(DelegateTask delegateTask) {
	    
	    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	    
	    Employee employee = (Employee) request.getSession().getAttribute(SessionContext.GLOBLE_USER_SESSION);
	    
	    delegateTask.setAssignee(employee.getManager().getName());
	}

}
 