package org.cghr.security.service

import groovy.transform.TupleConstructor
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

@TupleConstructor
class OnlineAuthService {

    String serverAuthUrl
    RestTemplate restTemplate


    public Map authenticate(User user, String hostname) {

        if (isServerHost(hostname))
            throw new ServerNotFoundException()

        HttpEntity<String> request = constructJsonRequest(user)
        postRequest(request)

    }

    Map postRequest(HttpEntity<String> request) {

        Map response
        try {
            response = restTemplate.postForObject(serverAuthUrl, request, Map.class)
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

    HttpEntity<String> constructJsonRequest(User user) {

        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        new HttpEntity<String>(user.toJson(), headers)
    }

    boolean isServerHost(String hostname) {

        String serverHost = getServerHostName()
        hostname == serverHost && serverHost != 'localhost'
    }

    String getServerHostName() {
        serverAuthUrl.toURL().host
    }
}
