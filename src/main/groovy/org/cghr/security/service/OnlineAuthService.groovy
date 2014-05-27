package org.cghr.security.service

import com.google.gson.Gson
import org.cghr.security.exception.NoSuchUserFound
import org.cghr.security.exception.ServerNotFoundException
import org.cghr.security.model.User
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate

class OnlineAuthService {

    String serverAuthUrl
    RestTemplate restTemplate

    public OnlineAuthService(String serverAuthUrl, RestTemplate restTemplate) {
        this.serverAuthUrl = serverAuthUrl
        this.restTemplate = restTemplate
    }


    public Map authenticate(User user, String hostname) {


        Gson gson = new Gson()
        Map serverRespUser


        String onlinAuthHostname = serverAuthUrl.toURL().getHost()

        println "hostname $hostname"
        println "Server auth host $onlinAuthHostname"

        if (hostname == onlinAuthHostname && onlinAuthHostname != 'localhost')
            throw new ServerNotFoundException()


        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter())
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());



        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)



        HttpEntity<String> request = new HttpEntity<String>(gson.toJson(user), headers)


        try {

            serverRespUser = restTemplate.postForObject(serverAuthUrl, request, Map.class);
            println 'Online Server Available'
            return serverRespUser
        }
        catch (HttpClientErrorException ex) {

            def status = ex.statusCode
            if (status == ex.statusCode.NOT_FOUND)
                throw new ServerNotFoundException()

            else if (status == ex.statusCode.FORBIDDEN)
                throw new NoSuchUserFound()
        }
        catch (ResourceAccessException ex) {
            throw new ServerNotFoundException()
        }

    }
}
