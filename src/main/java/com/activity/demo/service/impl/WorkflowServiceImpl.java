package com.activity.demo.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.annotation.Resource;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ModelEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.util.io.InputStreamSource;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.activity.demo.dao.ILeaveBillDao;
import com.activity.demo.model.Employee;
import com.activity.demo.model.LeaveBill;
import com.activity.demo.service.IWorkflowService;
import com.activity.demo.web.form.WorkflowBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class WorkflowServiceImpl implements IWorkflowService {

    @Autowired
    private ILeaveBillDao leaveBillDao;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private FormService formService;

    @Resource
    private HistoryService historyService;

    @Resource
    private IdentityService identityService;

    @Resource
    private ObjectMapper objectMapper;
    
    @Resource
    private ManagementService managementService;

    @Override
    public void saveNewDeploye(File file, String filename) {
        try {
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
            repositoryService.createDeployment().name(filename).addZipInputStream(zipInputStream).deploy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Deployment> findDeploymentList() {
        List<Deployment> list = repositoryService.createDeploymentQuery().orderByDeploymenTime().asc().list();
        return list;
    }

    @Override
    public List<ProcessDefinition> findProcessDefinitionList() {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().orderByProcessDefinitionVersion().asc().list();
        return list;
    }

    /** 使用部署对象ID和资源图片名称，获取图片的输入流 */
    @Override
    public InputStream findImageInputStream(String deploymentId, String imageName) {
        return repositoryService.getResourceAsStream(deploymentId, imageName);
    }

    @Override
    public void deleteProcessDefinitionByDeploymentId(String deploymentId) {
        repositoryService.deleteDeployment(deploymentId, true);
    }


    @Override
    public List<Model> findModelDefinitionList() {
        return repositoryService.createModelQuery().list();
    }

    /** 更新请假状态，启动流程实例，让启动的流程实例关联业务 */
    @Override
    public void saveStartProcess(WorkflowBean workflowBean, Employee employee) {
        int id = workflowBean.getId();
        LeaveBill leaveBill = leaveBillDao.findLeaveBillById(id);
        leaveBill.setState(1);
        leaveBillDao.updateLeaveBill(leaveBill);
        // 3：使用当前对象获取到流程定义的key（对象的名称就是流程定义的key）
        String key = leaveBill.getClass().getSimpleName();

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("inputUser", employee.getName());
        /**
         * 5： (1)使用流程变量设置字符串（格式：LeaveBill.id的形式），通过设置，让启动的流程（流程实例）关联业务
         * (2)使用正在执行对象表中的一个字段BUSINESS_KEY（Activiti提供的一个字段），让启动的流程（流程实例）关联业务
         */
        // 格式：LeaveBill.id的形式（使用流程变量）
        String objId = key + "." + id;
        variables.put("objId", objId);
        // 6：使用流程定义的key，启动流程实例，同时设置流程变量，同时向正在执行的执行对象表中的字段BUSINESS_KEY添加业务数据，同时让流程关联业务
        runtimeService.startProcessInstanceByKey(key, objId, variables);

    }

    @Override
    public List<Task> findTaskListByName(String name) {
        List<Task> list = taskService.createTaskQuery().taskAssignee(name).orderByTaskCreateTime().asc().list();
        if (list == null || list.size() <= 0) {
            list = taskService.createTaskQuery().taskCandidateUser(name).orderByTaskCreateTime().asc().list();
        }
        return list;
    }

    @Override
    public String findTaskFormKeyByTaskId(String taskId) {
        TaskFormData formData = formService.getTaskFormData(taskId);
        String url = formData.getFormKey();
        return url;
    }

    @Override
    public LeaveBill findLeaveBillByTaskId(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processInstanceId = task.getProcessInstanceId();

        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        String buniness_key = pi.getBusinessKey();

        String id = "";
        if (StringUtils.isNotBlank(buniness_key)) {

            id = buniness_key.split("\\.")[1];
        }

        LeaveBill leaveBill = leaveBillDao.findLeaveBillById(Integer.parseInt(id));
        return leaveBill;
    }

    /** 二：已知任务ID，查询ProcessDefinitionEntiy对象，从而获取当前任务完成之后的连线名称，并放置到List<String>集合中 */
    @Override
    public List<String> findOutComeListByTaskId(String taskId) {

        List<String> list = new ArrayList<String>();

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processDefinitionId = task.getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);

        String processInstanceId = task.getProcessInstanceId();

        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        /* String activityId = pi.getActivityId(); */
        String activityId = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult().getActivityId();
        // 获取当前的活动
        ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);

        List<PvmTransition> pvmList = activityImpl.getOutgoingTransitions();
        if (pvmList != null && pvmList.size() > 0) {
            for (PvmTransition pvm : pvmList) {
                String name = (String) pvm.getProperty("name");
                if (StringUtils.isNotBlank(name)) {
                    list.add(name);
                } else {
                    list.add("默认提交");
                }
            }
        }
        return list;
    }

    /** 指定连线的名称完成任务 */
    @Override
    public void saveSubmitTask(WorkflowBean workflowBean, Employee employee) {

        String taskId = workflowBean.getTaskId();

        String outcome = workflowBean.getOutcome();

        String message = workflowBean.getComment();

        int id = workflowBean.getId();

        /**
         * 1：在完成之前，添加一个批注信息，向act_hi_comment表中添加数据，用于记录对当前申请人的一些审核信息
         */

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        String processInstanceId = task.getProcessInstanceId();
        /**
         * 注意：添加批注的时候，由于Activiti底层代码是使用： String userId = Authentication.getAuthenticatedUserId();
         * CommentEntity comment = new CommentEntity(); comment.setUserId(userId);
         * 所有需要从Session中获取当前登录人，作为该任务的办理人（审核人），对应act_hi_comment表中的User_ID的字段，不过不添加审核人，该字段为null
         * 所以要求，添加配置执行使用Authentication.setAuthenticatedUserId();添加当前任务的审核人
         * */
        Authentication.setAuthenticatedUserId(employee.getName());
        taskService.addComment(taskId, processInstanceId, message);

        Map<String, Object> variables = new HashMap<String, Object>();
        // if (outcome != null && !outcome.equals("默认提交")) {
        // variables.put("outcome", outcome);
        // }
        variables.put("outcome", outcome);
        if (task.getAssignee() != null && task.getAssignee().equals("张三")) {
            List<String> userList = new ArrayList<String>();
            userList.add(employee.getManager().getName());
            userList.add("李四部门经理");
            variables.put("userGroup", userList);
        }

        if (taskService.getVariable(taskId, "publish") != null) {
            taskService.removeVariable(taskId, "publish");
        }

        if (taskService.getVariable(taskId, "userGroup") != null) {
            Map<String, Object> variableMap = new HashMap<String, Object>();
            ArrayList<String> arrayList = (ArrayList<String>) taskService.getVariable(taskId, "userGroup");
            variableMap.put("userGroup", arrayList);
            taskService.removeVariable(taskId, "userGroup");
        }

        taskService.complete(taskId, variables);

        /**
         * 5：在完成任务之后，判断流程是否结束 如果流程结束了，更新请假单表的状态从1变成2（审核中-->审核完成）
         */
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        if (pi == null) {
            LeaveBill bill = leaveBillDao.findLeaveBillById(id);
            bill.setState(2);
            leaveBillDao.updateLeaveBill(bill);
        }
    }

    @Override
    public List<Comment> findCommentByTaskId(String taskId) {
        List<Comment> list = new ArrayList<Comment>();

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        String processInstanceId = task.getProcessInstanceId();
        // //使用流程实例ID，查询历史任务，获取历史任务对应的每个任务ID
        // List<HistoricTaskInstance> htiList =
        // historyService.createHistoricTaskInstanceQuery()//历史任务表查询
        // .processInstanceId(processInstanceId)//使用流程实例ID查询
        // .list();
        // //遍历集合，获取每个任务ID
        // if(htiList!=null && htiList.size()>0){
        // for(HistoricTaskInstance hti:htiList){
        // //任务ID
        // String htaskId = hti.getId();
        // //获取批注信息
        // List<Comment> taskList = taskService.getTaskComments(htaskId);//对用历史完成后的任务ID
        // list.addAll(taskList);
        // }
        // }
        list = taskService.getProcessInstanceComments(processInstanceId);
        return list;
    }

    @Override
    public List<Comment> findCommentByLeaveBillId(int id) {

        LeaveBill leaveBill = leaveBillDao.findLeaveBillById(id);

        String objectName = leaveBill.getClass().getSimpleName();

        String objId = objectName + "." + id;

        /** 1:使用历史的流程实例查询，返回历史的流程实例对象，获取流程实例ID */
        // HistoricProcessInstance hpi =
        // historyService.createHistoricProcessInstanceQuery()//对应历史的流程实例表
        // .processInstanceBusinessKey(objId)//使用BusinessKey字段查询
        // .singleResult();
        // //流程实例ID
        // String processInstanceId = hpi.getId();
        /** 2:使用历史的流程变量查询，返回历史的流程变量的对象，获取流程实例ID */
        HistoricVariableInstance hvi = historyService.createHistoricVariableInstanceQuery()// 对应历史的流程变量表
                .variableValueEquals("objId", objId)// 使用流程变量的名称和流程变量的值查询
                .singleResult();

        String processInstanceId = hvi.getProcessInstanceId();
        List<Comment> list = taskService.getProcessInstanceComments(processInstanceId);
        return list;
    }

    /** 1：获取任务ID，获取任务对象，使用任务对象获取流程定义ID，查询流程定义对象 */
    @Override
    public ProcessDefinition findProcessDefinitionByTaskId(String taskId) {

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        String processDefinitionId = task.getProcessDefinitionId();

        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        return pd;
    }

    /**
     * 二：查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中
     * map集合的key：表示坐标x,y,width,height map集合的value：表示坐标对应的值
     */
    @Override
    public Map<String, Object> findCoordingByTask(String taskId) {

        Map<String, Object> map = new HashMap<String, Object>();

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        String processDefinitionId = task.getProcessDefinitionId();

        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);

        String processInstanceId = task.getProcessInstanceId();

        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        String activityId = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult().getActivityId();

        /* String activityId = pi.getActivityId(); */

        ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);//
        // 获取坐标
        map.put("x", activityImpl.getX());
        map.put("y", activityImpl.getY());
        map.put("width", activityImpl.getWidth());
        map.put("height", activityImpl.getHeight());
        return map;
    }

    @Override
    public String saveModel(ObjectMapper objectMapper, ObjectNode editorNode, ModelEntity model) throws UnsupportedEncodingException {

        Model modelData = repositoryService.newModel();

        ObjectNode modelObjectNode = objectMapper.createObjectNode();

        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, model.getName());

        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);

        model.setMetaInfo(StringUtils.defaultString(modelObjectNode.toString()));

        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, model.getMetaInfo());

        modelData.setMetaInfo(modelObjectNode.toString());

        modelData.setName(model.getName());

        modelData.setKey(StringUtils.defaultString(model.getKey()));

        repositoryService.saveModel(modelData);

        repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));

        return modelData.getId();
    }

    @Override
    public BpmnModel findBpmnModelById(String modelId) throws JsonProcessingException, IOException {
        Model modelData = repositoryService.getModel(modelId);
        BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
        JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
        BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
        return bpmnModel;
    }

    @Override
    public void deleteModel(String modelId) {
        repositoryService.deleteModel(modelId);
    }

    @Override
    public void saveModelDeploy(String modelId) throws JsonProcessingException, IOException {
        Model modelData = repositoryService.getModel(modelId);
        ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
        byte[] bpmnBytes = null;

        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
        bpmnBytes = new BpmnXMLConverter().convertToXML(model);

        String processName = modelData.getName() + ".bpmn20.xml";
        @SuppressWarnings("unused")
        Deployment deployment = repositoryService.createDeployment().name(modelData.getName()).addString(processName, new String(bpmnBytes)).deploy();
    }

    @Override
    public void saveImportModel(File file) throws UnsupportedEncodingException, FileNotFoundException {

        BpmnModel model = new BpmnXMLConverter().convertToBpmnModel(new InputStreamSource(new FileInputStream(file)), false, false);

        ObjectNode stencilSetNode = new BpmnJsonConverter().convertToJson(model);

        ObjectNode modelObjectNode = objectMapper.createObjectNode();

        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, model.getMainProcess().getName());

        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);

        Model modelData = repositoryService.newModel();

        modelData.setName(model.getMainProcess().getName());

        modelData.setMetaInfo(modelObjectNode.toString());

        repositoryService.saveModel(modelData);

        repositoryService.addModelEditorSource(modelData.getId(), stencilSetNode.toString().getBytes("utf-8"));
    }

    @Override
    public List<Map<String, Object>> findPublishFailMessage() {
        List<Map<String, Object>> messages = new ArrayList<Map<String, Object>>();
        List<Execution> executions = runtimeService.createExecutionQuery().list();
        for (Execution execution : executions) {
            if (execution != null) {
                if (runtimeService.getVariableInstance(execution.getId(), "publish") != null) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    ProcessInstance pi =
                            runtimeService.createProcessInstanceQuery().processInstanceId(execution.getProcessInstanceId()).singleResult();

                    String buniness_key = pi.getBusinessKey();

                    String id = "";
                    if (StringUtils.isNotBlank(buniness_key)) {

                        id = buniness_key.split("\\.")[1];
                    }
                    map.put(id, runtimeService.getVariableInstance(execution.getId(), "publish").getTextValue());
                    messages.add(map);
                }
            }
        }

        return messages;
    }
}
