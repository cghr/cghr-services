package org.cghr.contextstartup

import spock.lang.Specification

import javax.servlet.ServletContext
import javax.servlet.ServletContextEvent


/**
 * Created by ravitej on 26/10/14.
 */
class AppStartupListenerSpec extends Specification {

    AppStartupListener appStartupListener
    String path = File.createTempDir().absolutePath
    ServletContext servletContext
    ServletContextEvent servletContextEvent


    def setupSpec() {

    }

    def setup() {

        appStartupListener = new AppStartupListener()
        servletContext = Stub() {
            getRealPath("/") >> path
        }
        servletContextEvent = Stub() {
            getServletContext() >> servletContext
        }

    }

    def "should set basePath in system property"() {

        given:
        File log4j=new File("./log4j.properties")
        File webInf=new File(path+"/WEB-INF")
        webInf.mkdirs()
        File file=new File(path+"/WEB-INF/log4j.properties")
        file.setText(log4j.text)


        when:
        appStartupListener.contextInitialized(servletContextEvent)

        then:
        System.getProperty("basePath") == path+'/'

    }


}