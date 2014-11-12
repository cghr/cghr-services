package org.cghr.security.service

import groovy.transform.TupleConstructor
import org.cghr.security.model.User
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate

@TupleConstructor
class OnlineAuthService {

    String serverAuthUrl
    RestTemplate restTemplate


    Map authenticate(User user) {

        HttpEntity<String> httpEntity = buildHttpEntity(user)
        restTemplate.postForObject(serverAuthUrl, httpEntity, Map.class)

    }

    HttpEntity<String> buildHttpEntity(User user) {

        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        new HttpEntity<String>(user.toJson(), headers)
    }


}
