package com.activity.demo.web.action;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.activity.demo.model.Employee;
import com.activity.demo.model.LeaveBill;
import com.activity.demo.service.ILeaveBillService;
import com.activity.demo.utils.SessionContext;

@Controller
@RequestMapping("/leaveBill")
public class LeaveBillAction extends AbstractAction {

    @Resource
    private ILeaveBillService leaveBillService;

    public LeaveBillAction() {
        String prefix = "/views/leaveBill/";
        setPrefix(prefix);
    }

    @RequestMapping("/index")
    public String home(HttpServletRequest request) {

        Employee emp = (Employee) request.getSession().getAttribute("user");

        List<LeaveBill> list = leaveBillService.findLeaveBillList(emp.getId());

        request.setAttribute("list", list);

        return getPrefix() + "list";
    }


    @RequestMapping("/input")
    public String input(HttpServletRequest request, @RequestParam(value = "id", required = false) String id) {
        if (id != null && !StringUtils.isBlank(id)) {
            LeaveBill bill = leaveBillService.findLeaveBillById(Integer.valueOf(id));
            request.setAttribute("bill", bill);
        }
        return getPrefix() + "input";
    }

    @RequestMapping("/save")
    public void save(HttpServletRequest request, HttpServletResponse response, LeaveBill leaveBill) throws IOException {
        if(leaveBill==null){
            leaveBill = new LeaveBill();
            leaveBill.setId(Integer.valueOf(leaveBillService.selectleaveBillId("SEQ_BILL")));
        }

        leaveBillService.saveLeaveBill(leaveBill, (Employee) request.getSession().getAttribute(SessionContext.GLOBLE_USER_SESSION));
        response.sendRedirect("/leaveBill/index");
    }

    @RequestMapping("/delete")
    public void delete(@RequestParam(value="id", required=false) String id, HttpServletResponse response) throws IOException {
        if (id != null && !StringUtils.isBlank(id)) {
            leaveBillService.deleteLeaveBillById(Integer.valueOf(id));
        }
        response.sendRedirect("/leaveBill/index");
    }
    
    @RequestMapping("/generateId")
    @ResponseBody
    public String generateBillId(HttpServletResponse response) throws IOException{
        String id = leaveBillService.selectleaveBillId("SEQ_BILL");
        return "{\"id\":"+id+"}";
    }

}
