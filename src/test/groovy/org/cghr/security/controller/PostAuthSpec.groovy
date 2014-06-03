package org.cghr.security.controller

import org.cghr.GenericGroovyContextLoader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.servlet.http.HttpServletResponse

/**
 * Created by ravitej on 3/6/14.
 */
@ContextConfiguration(value = "classpath:appContext.groovy", loader = GenericGroovyContextLoader.class)
class PostAuthSpec extends Specification {

    @Autowired
    PostAuth postAuth

    def "should create cookie for a given data"() {

        given:
        HttpServletResponse response = new MockHttpServletResponse()
        String myCookie = '{"id":1,"key":"value"}'

        when:
        postAuth.addCookie('mycookie', myCookie, response)

        then:
        response.getCookie("mycookie")?.getValue() == myCookie
        response.getCookie("mycookie")?.getPath() == '/'


    }
}
