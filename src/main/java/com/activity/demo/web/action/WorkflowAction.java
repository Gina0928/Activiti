package com.activity.demo.web.action;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.activity.demo.model.Employee;
import com.activity.demo.model.LeaveBill;
import com.activity.demo.service.ILeaveBillService;
import com.activity.demo.service.IWorkflowService;
import com.activity.demo.utils.SessionContext;
import com.activity.demo.web.form.WorkflowBean;

@Controller
@RequestMapping("/workflow")
public class WorkflowAction extends AbstractAction {

    @Resource
    private IWorkflowService workflowService;

    @Resource
    private ILeaveBillService leaveBillService;

    public WorkflowAction() {
        String prefix = "/views/workflow/";
        setPrefix(prefix);
    }

    @RequestMapping("/index")
    public String deployHome(HttpServletRequest request) {

        List<Deployment> depList = workflowService.findDeploymentList();

        List<ProcessDefinition> pdList = workflowService.findProcessDefinitionList();
        
        request.setAttribute("depList", depList);
        request.setAttribute("pdList", pdList);
        return getPrefix() + "workflow";
    }

    @RequestMapping("/deploy")
    public void newdeploy(HttpServletResponse response, HttpServletRequest request, String filename, MultipartFile file) throws IOException {
        String path = request.getSession().getServletContext().getRealPath("upload");
        String name = file.getOriginalFilename();
        File targetFile = new File(path, name);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }

        try {
            file.transferTo(targetFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (targetFile != null) {
            workflowService.saveNewDeploye(targetFile, filename);
        }
        response.sendRedirect("/workflow/index");;
    }


    @RequestMapping("/delete")
    public void delDeployment(String deploymentId, HttpServletResponse response) throws IOException {
        // 使用部署对象ID，删除流程定义
        workflowService.deleteProcessDefinitionByDeploymentId(deploymentId);
        response.sendRedirect("/workflow/index");
    }

    @RequestMapping("/viewImage")
    public String viewImage(HttpServletResponse response, String deploymentId, String imageName) throws Exception {
        // 2：获取资源文件表（act_ge_bytearray）中资源图片输入流InputStream
        InputStream in = workflowService.findImageInputStream(deploymentId, imageName);

        OutputStream out = response.getOutputStream();

        for (int b = -1; (b = in.read()) != -1;) {
            out.write(b);
        }
        out.close();
        in.close();

        return null;
    }

    @RequestMapping("/startProcess")
    public void startProcess(HttpServletRequest request, String id, HttpServletResponse response) throws IOException {
        WorkflowBean workflowBean = new WorkflowBean();
        workflowBean.setId(Integer.valueOf(id));

        workflowService.saveStartProcess(workflowBean, (Employee) request.getSession().getAttribute(SessionContext.GLOBLE_USER_SESSION));
        response.sendRedirect("/workflow/taskList");
    }


    @RequestMapping("/taskList")
    public String listTask(HttpServletRequest request) {

        Employee employee = (Employee) request.getSession().getAttribute(SessionContext.GLOBLE_USER_SESSION);

        List<Task> list = workflowService.findTaskListByName(employee.getName());
        
        if(employee.getRole().equals("manager")){
            List<Map<String, Object>> message = workflowService.findPublishFailMessage();
            if(!message.equals("")){
                request.setAttribute("publish", message);
            }
        }
        
        request.setAttribute("list", list);

        return getPrefix() + "task";
    }


    @RequestMapping("/viewTask")
    public void viewTaskForm(HttpServletRequest request, String taskId, HttpServletResponse response) throws IOException {
        // 获取任务表单中任务节点的url连接
        String url = workflowService.findTaskFormKeyByTaskId(taskId);
        url += "?taskId=" + taskId;
        request.setAttribute("taskId", taskId);
        request.setAttribute("url", url);
        response.sendRedirect(url);
    }


    @RequestMapping("/audit")
    public String audit(String taskId, HttpServletRequest request) {

        LeaveBill leaveBill = workflowService.findLeaveBillByTaskId(taskId);
        request.setAttribute("leaveBill", leaveBill);

        List<String> outcomeList = workflowService.findOutComeListByTaskId(taskId);
        request.setAttribute("outcomeList", outcomeList);

        List<Comment> commentList = workflowService.findCommentByTaskId(taskId);
        
        request.setAttribute("taskId", taskId);
        request.setAttribute("commentList", commentList);
        return getPrefix()+"taskForm";
    }


    @RequestMapping("/submitTask")
    public void submitTask(HttpServletResponse response, HttpServletRequest request, WorkflowBean workflowBean) throws IOException {
        if (workflowBean == null) {
            workflowBean = new WorkflowBean();
        }
        workflowService.saveSubmitTask(workflowBean, (Employee) request.getSession().getAttribute(SessionContext.GLOBLE_USER_SESSION));
        response.sendRedirect("/workflow/taskList");
    }

    @RequestMapping("/current")
    public String viewCurrentImage(HttpServletRequest request, String taskId) {

        ProcessDefinition pd = workflowService.findProcessDefinitionByTaskId(taskId);
        request.setAttribute("deploymentId", pd.getDeploymentId());
        request.setAttribute("imageName", pd.getDiagramResourceName());
        /** 二：查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中 */
        Map<String, Object> map = workflowService.findCoordingByTask(taskId);
        request.setAttribute("acs", map);
        return getPrefix() + "image";
    }

    @RequestMapping("/his")
    public String viewHisComment(HttpServletRequest request, String id) {
        if (id != null && StringUtils.isNotBlank(id)) {
            LeaveBill leaveBill = leaveBillService.findLeaveBillById(Integer.valueOf(id));
            request.setAttribute("leaveBill", leaveBill);
            List<Comment> commentList = workflowService.findCommentByLeaveBillId(Integer.valueOf(id));
            request.setAttribute("commentList", commentList);
        }
        return getPrefix() + "taskFormHis";
    }
}
