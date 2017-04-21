/**
 * 
 */
package com.activity.demo.web.action;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.impl.persistence.entity.ModelEntity;
import org.activiti.engine.repository.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.activity.demo.service.IWorkflowService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Gina.Ai
 *
 */
@Controller
@RequestMapping("/processEditor")
public class ProcessEditorAction extends AbstractAction {

    @Resource
    private IWorkflowService workflowService;

    public ProcessEditorAction() {
        setPrefix("/views/processModel/");
    }

    @RequestMapping("/index")
    public String index(HttpServletRequest request) {
        List<Model> models = workflowService.findModelDefinitionList();
        request.setAttribute("models", models);
        return getPrefix() + "processModel";
    }

    @RequestMapping("/create")
    public String create(ModelEntity model) {
        return getPrefix() + "create";
    }

    @RequestMapping("/submit")
    public void submit(ModelEntity model, HttpServletRequest request, HttpServletResponse response) {
        try {

            ObjectMapper objectMapper = new ObjectMapper();

            ObjectNode editorNode = objectMapper.createObjectNode();

            editorNode.put("id", "canvas");

            editorNode.put("resourceId", "canvas");

            ObjectNode stencilSetNode = objectMapper.createObjectNode();

            stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");

            editorNode.put("stencilset", stencilSetNode);

            String modelId = workflowService.saveModel(objectMapper, editorNode, model);

            /*response.sendRedirect("/service/editor?id=" + modelId);*/
            response.sendRedirect("/processEditor/index");

        } catch (Exception e) {

        }
    }
    
    @RequestMapping("/down")
    public void export(String modelId, HttpServletResponse response){
        try {  
            BpmnModel bpmnModel = workflowService.findBpmnModelById(modelId);
            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();  
            byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);  
            ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);  
            /*IOUtils.copy(in, response.getOutputStream());*/
            String filename = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
            response.setHeader("Content-Disposition", "attachment; filename=" + filename); 
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            toClient.write(bpmnBytes);
            toClient.flush();
            toClient.close();
            in.close();
            response.flushBuffer();  
        } catch (Exception e) {
        }  
    }
    
    @RequestMapping("/delete")
    public void delete(String modelId, HttpServletResponse response) throws IOException{
        workflowService.deleteModel(modelId);
        response.sendRedirect("/processEditor/index");
    }
    
    @RequestMapping("/deploy")
    public void deploy(String modelId, HttpServletResponse response) throws IOException{ 
        workflowService.saveModelDeploy(modelId);
        response.sendRedirect("/processEditor/index");
    }
    
    @RequestMapping("/import/index")
    public String importIndex(){
        return getPrefix() + "import";
    }
    
    @RequestMapping("/import/submit")
    public void importSubmit(MultipartFile file, HttpServletResponse response, HttpServletRequest request) throws IOException{
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
        workflowService.saveImportModel(targetFile);
        response.sendRedirect("/processEditor/index");
    }

}
