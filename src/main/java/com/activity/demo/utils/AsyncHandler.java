/**
 * 
 */
package com.activity.demo.utils;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

/**
 * @author Gina.Ai
 *
 */
public class AsyncHandler implements ExecutionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        execution.setVariable("asynVar", "success");
    }

}
