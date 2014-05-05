package org.cghr.security.controller

import javax.servlet.*
import javax.servlet.http.HttpServletResponse

/**
 * Created by ravitej on 5/5/14.
 */
class CORSFilter implements Filter {

    @Override
    void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {


        HttpServletResponse response = (HttpServletResponse) resp

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");

        chain.doFilter(req, resp);


    }

    @Override
    void destroy() {

    }
}
