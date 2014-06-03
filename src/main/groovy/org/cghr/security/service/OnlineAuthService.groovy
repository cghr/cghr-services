package org.cghr.security.service

import com.google.gson.Gson
import org.cghr.security.exception.NoSuchUserFound
import org.cghr.security.exception.ServerNotFoundException
import org.cghr.security.model.User
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
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

        Map serverRespUser
        String onlinAuthHostname = serverAuthUrl.toURL().getHost()

        if (hostname == onlinAuthHostname && onlinAuthHostname != 'localhost')
            throw new ServerNotFoundException()

        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        HttpEntity<String> request = new HttpEntity<String>(new Gson().toJson(user), headers)

        try {

            serverRespUser = restTemplate.postForObject(serverAuthUrl, request, Map.class);
            return serverRespUser
        }

        catch (HttpClientErrorException ex) {

            def status = ex.statusCode
            if (status == HttpStatus.NOT_FOUND)
                throw new ServerNotFoundException()

            else if (status == HttpStatus.FORBIDDEN)
                throw new NoSuchUserFound()
        }
        catch (ResourceAccessException ex) {
            throw new ServerNotFoundException()
        }

    }
}
