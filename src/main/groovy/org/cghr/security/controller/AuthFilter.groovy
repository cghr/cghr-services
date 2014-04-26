package org.cghr.security.controller
import org.cghr.security.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.support.WebApplicationContextUtils

import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthFilter implements Filter {

    UserService userService

    RequestParser requestParser

    public void init(FilterConfig filterConfig) throws ServletException {

        ServletContext servletContext = filterConfig.getServletContext();
        WebApplicationContext webApplicationContext =
                WebApplicationContextUtils.getWebApplicationContext(servletContext);

        userService= webApplicationContext.getBean("userService")
        requestParser=webApplicationContext.getBean("requestParser")
//        AutowireCapableBeanFactory autowireCapableBeanFactory =
//                webApplicationContext.getAutowireCapableBeanFactory();
//
//        autowireCapableBeanFactory.configureBean(userService,"userService");
//        autowireCapableBeanFactory.configureBean(requestParser,"requestParser")
    }

    public void doFilter(ServletRequest req, ServletResponse resp,
                         FilterChain chain) throws IOException, ServletException {


        def request = (HttpServletRequest) req
        def response = (HttpServletResponse) resp


        def token = requestParser.getAuthTokenFromCookies(request)
        boolean isValidToken = (token == null) ? false : (userService.isValidToken(token))

        if (!isValidToken)
            response.setStatus(HttpStatus.UNAUTHORIZED.value)
        else
            chain.doFilter(request, response)
    }

    public void destroy() {
    }

    AuthFilter() {
    }

    AuthFilter(UserService userService, RequestParser parser) {
        this.userService = userService
        this.requestParser = parser
    }
}
