/**
 * 
 */
package com.activity.demo.web.action;
/**
 * @author Gina.Ai
 *
 */
public abstract class AbstractAction {
    
    private String prefix;

    public AbstractAction() {
        super();
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}
