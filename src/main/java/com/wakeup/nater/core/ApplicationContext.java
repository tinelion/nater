package com.wakeup.nater.core;

/**
 * @Description
 * @Author Alon
 * @Date 2019/6/26 0:12
 */
public class ApplicationContext {
    private String contextName;

    private String contextPath;

    public ApplicationContext(){

    }


    public ApplicationContext(String contextName, String contextPath){
        this.contextName = contextName;
        this.contextPath = contextPath;
    }


    public void setContextName(String contextName){
        this.contextName = contextName;
    }


    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
}
