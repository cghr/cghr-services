package org.cghr.security.controller

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.cghr.security.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class AuthFilter implements Filter {

	@Autowired UserService userService
	@Autowired RequestParser requestParser

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {


		def request=(HttpServletRequest)req
		def response=(HttpServletResponse)resp


		def token=requestParser.getAuthTokenFromCookies(request)
		boolean isValidToken=(token==null)?false:(userService.isValidToken(token))


		if(!isValidToken)
			response.setStatus(HttpStatus.UNAUTHORIZED.value)
		else
			chain.doFilter(request, response)
	}

	public void destroy() {
	}
	AuthFilter() {
	}
	AuthFilter(UserService userService,RequestParser parser) {
		this.userService=userService
		this.requestParser=parser
	}
}
