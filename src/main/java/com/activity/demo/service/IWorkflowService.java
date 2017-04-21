package com.activity.demo.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.impl.persistence.entity.ModelEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import com.activity.demo.model.Employee;
import com.activity.demo.model.LeaveBill;
import com.activity.demo.web.form.WorkflowBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;



public interface IWorkflowService {

	void saveNewDeploye(File file, String filename);
	
	void saveModelDeploy(String modelId) throws JsonProcessingException, IOException ;

	List<Deployment> findDeploymentList();

	List<ProcessDefinition> findProcessDefinitionList();

	InputStream findImageInputStream(String deploymentId, String imageName);

	void deleteProcessDefinitionByDeploymentId(String deploymentId);

	void saveStartProcess(WorkflowBean workflowBean, Employee employee);

	List<Task> findTaskListByName(String name);

	String findTaskFormKeyByTaskId(String taskId);

	LeaveBill findLeaveBillByTaskId(String taskId);

	List<String> findOutComeListByTaskId(String taskId);

	void saveSubmitTask(WorkflowBean workflowBean, Employee employee);

	List<Comment> findCommentByTaskId(String taskId);

	List<Comment> findCommentByLeaveBillId(int id);

	ProcessDefinition findProcessDefinitionByTaskId(String taskId);

	Map<String, Object> findCoordingByTask(String taskId);
	
	List<Model> findModelDefinitionList();
	
	String saveModel(ObjectMapper objectMapper, ObjectNode editorNode, ModelEntity model) throws UnsupportedEncodingException;
	
	BpmnModel findBpmnModelById(String modelId) throws JsonProcessingException, IOException ;
	
	void deleteModel(String modelId);
	
	void saveImportModel(File file) throws UnsupportedEncodingException, FileNotFoundException ;
	
	List<Map<String,Object>> findPublishFailMessage();
}
