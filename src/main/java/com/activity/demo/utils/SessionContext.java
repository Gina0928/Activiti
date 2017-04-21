package com.activity.demo.utils;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.activity.demo.model.Employee;

public class SessionContext implements HttpSessionListener{

	public static final String GLOBLE_USER_SESSION = "globle_user";

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        Employee user = (Employee) se.getSession().getAttribute(GLOBLE_USER_SESSION);
        if(user!=null){
            se.getSession().removeAttribute(GLOBLE_USER_SESSION);
        }        
    }
}
