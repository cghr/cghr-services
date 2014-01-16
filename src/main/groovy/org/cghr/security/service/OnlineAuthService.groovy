package org.cghr.security.service

import org.cghr.security.exception.NoSuchUserFound
import org.cghr.security.exception.ServerNotFoundException
import org.cghr.security.model.User
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

import com.google.gson.Gson


class OnlineAuthService {

	String serverAuthUrl
	RestTemplate restTemplate

	public OnlineAuthService(String serverAuthUrl,RestTemplate restTemplate) {
		this.serverAuthUrl=serverAuthUrl
		this.restTemplate=restTemplate
	}


	public User authenticate(User user) {


		Gson gson=new Gson()
		User serverRespUser

		restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter())
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());



		HttpHeaders headers=new HttpHeaders()
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON))


		try{

			println restTemplate.postForObject(serverAuthUrl,user,User.class)
			serverRespUser=	restTemplate.postForObject(serverAuthUrl,user,User.class)
			return serverRespUser
		}
		catch(HttpClientErrorException ex) {


			def status=ex.statusCode

			if(status==ex.statusCode.NOT_FOUND)
				throw new ServerNotFoundException()

			else if(status==ex.statusCode.UNAUTHORIZED)
				throw new NoSuchUserFound()
		}
	}
}
