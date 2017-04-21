package com.activity.demo.web.action;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.activity.demo.model.Employee;
import com.activity.demo.service.IEmployeeService;
import com.activity.demo.utils.SessionContext;

@Controller
@RequestMapping("/login")
public class LoginAction extends AbstractAction{

    @Resource
	private IEmployeeService employeeService;

    public LoginAction(){
        String prefix = "/views/";
        setPrefix(prefix);
    }

    @RequestMapping("/submit")
	public String login(HttpServletRequest request,String name){
		Employee emp = employeeService.findEmployeeByName(name);

		request.getSession().setAttribute("user", emp);
		request.getSession().setAttribute(SessionContext.GLOBLE_USER_SESSION, emp);
		return getPrefix()+"main";
	}
	
    @RequestMapping("/head")
	public String top() {
		return getPrefix()+"top";
	}
	
    @RequestMapping("/left")
	public String left() {
		return getPrefix()+"left";
	}
	
    @RequestMapping("/index")
	public String welcome() {
		return getPrefix()+"welcome";
	}
	

    @RequestMapping("/logout")
	public String logout(HttpServletRequest request){

		request.getSession().removeAttribute("user");;
		return "/login";
	}
	
}
