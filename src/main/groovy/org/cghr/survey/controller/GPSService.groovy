package org.cghr.survey.controller

import groovy.util.logging.Log4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletResponse

/**
 * Created by ravitej on 12/11/14.
 */
@Log4j
@RestController
@RequestMapping("/gps")
class GPSService {

    @Autowired
    @Qualifier("gpsSocketPort")
    Integer gpsSocketPort

    @RequestMapping("")
    Map getGps(HttpServletResponse response) {

        try {
            def socket = new Socket("localhost", gpsSocketPort);
            Map gpsFix

            socket.withStreams { input, output ->
                println "Request gps socket server"
                output << "requesting gps socket server ...\n"
                def gps = input.newReader().readLine()
                println "received response "
                println gps

                List points = gps.split(";")
                gpsFix = [latitude: points[0], longitude: points[1]]

            }
            return gpsFix
        }

        catch (any) {
            log.error("GPS Server not running")
            response.sendError(500)
        }
    }

}
