package com.activity.demo.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.activity.demo.model.Employee;


/**
 * 登录验证拦截器
 *
 */
public class LoginInteceptor implements HandlerInterceptor {
    
    private String[] allowUrls;

	/**每次访问Action类之前，先执行intercept方法*/
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
      //获取当前访问Action的URL
        String actionName = request.getRequestURI();
        for(int i=0; i<allowUrls.length; i++){
            if(actionName.equals(allowUrls[i])){
                return true;
            }
        }
        if(!"/login/submit".equals(actionName)){
            //从Session中获取当前用户对象
            Employee employee = (Employee) request.getSession().getAttribute("user");
            //如果Session不存在，跳转到登录页面
            if(employee==null){
                response.sendRedirect("/");
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {      
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {        
    }

    public void setAllowUrls(String[] allowUrls) {
        this.allowUrls = allowUrls;
    }

}
