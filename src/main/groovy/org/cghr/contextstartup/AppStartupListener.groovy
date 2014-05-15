package org.cghr.contextstartup

import groovy.transform.CompileStatic
import org.apache.log4j.PropertyConfigurator

import javax.servlet.ServletContext
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener

/**
 * Created by ravitej on 25/4/14.
 */
@CompileStatic
class AppStartupListener implements ServletContextListener {

    @Override
    void contextInitialized(ServletContextEvent sce) {

        ServletContext sc = sce.getServletContext();
        String path = sc.getRealPath("/");
        if (!path.endsWith("/")) {
            path = path + File.separator
        }
        String userHome=System.getProperty('user.home')
        if(!userHome.endsWith('/'))
            userHome=userHome+File.separator

        setBasePath(path)
        setUserHome(userHome)
        configureLogger(path)

        // Get all AppStartupTasks and execute one by one
        // ApplicationContext appContext = WebApplicationContextUtils
        // .getWebApplicationContext(sce.getServletContext());
        // List<String> startupTasks = (List<String>) appContext
        // .getBean("startupTasks");
        // for (String taskClass : startupTasks) {
        // try {
        // AppStartupTask task = (AppStartupTask) Class.forName(taskClass)
        // .newInstance();
        // task.doStartupTask();
        // } catch (Exception e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // }

    }

    @Override
    void contextDestroyed(ServletContextEvent sce) {
    }

    void setBasePath(String path) {
        System.setProperty("basePath", path);

    }
    void setUserHome(String path){
        System.setProperty("userHome",path);
    }

    void configureLogger(String path) {

        PropertyConfigurator.configure(path + "/WEB-INF/log4j.properties");
    }
}
