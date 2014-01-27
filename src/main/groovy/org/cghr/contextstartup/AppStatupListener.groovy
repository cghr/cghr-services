package org.cghr.contextstartup;

import javax.servlet.ServletContext
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener


class AppStartupListener implements ServletContextListener {

    @Override
    void contextInitialized(ServletContextEvent sce) {

        setBasePath(sce);
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

    void setBasePath(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        String path = sc.getRealPath("/");

        System.setProperty("basePath", path);
        PropertyConfigurator.configure(path + "/WEB-INF/log4j.properties");
    }
}