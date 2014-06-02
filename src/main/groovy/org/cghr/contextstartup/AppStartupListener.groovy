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
            path = path + '/'
        }
        String userHome=System.getProperty('user.home')
        if(!userHome.endsWith('/'))
            userHome=userHome+'/'

        setBasePath(path)
        setUserHome(userHome)
        configureLogger(path)

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
