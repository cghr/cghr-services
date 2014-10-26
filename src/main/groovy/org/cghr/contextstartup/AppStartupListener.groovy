package org.cghr.contextstartup
import groovy.transform.CompileStatic

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

        ServletContext servletContext = sce.getServletContext();
        String basepath = getRealPath(servletContext)

        String userHome = getUserHome()

        setBasePath(basepath)
        setUserHome(userHome)
        configureLogger(basepath)

    }

    @Override
    void contextDestroyed(ServletContextEvent sce) {
    }

    String getRealPath(ServletContext servletContext) {

        String path = servletContext.getRealPath("/")
        resolvePath(path)

    }

    String getUserHome() {
        String path = System.getProperty("user.home")
        resolvePath(path)
    }

    void setBasePath(String path) {
        System.setProperty("basePath", path);

    }

    void setUserHome(String path) {
        System.setProperty("userHome", path);
    }

    void configureLogger(String path) {

        //PropertyConfigurator.configure(path + "/WEB-INF/log4j.properties");
    }

    String resolvePath(String path) {
        path.endsWith("/") ? path : path + "/"
    }
}
