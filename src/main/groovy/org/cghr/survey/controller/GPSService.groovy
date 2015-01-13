package org.cghr.survey.controller
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletResponse
/**
 * Created by ravitej on 12/11/14.
 */
@RestController
@RequestMapping("/gps")
class GPSService {

    @Autowired
    @Qualifier("gpsSocketPort")
    Integer gpsSocketPort

    @RequestMapping("")
    String getGps(HttpServletResponse response) {

        try {
            def socket = new Socket("localhost", gpsSocketPort);

            socket.withStreams { input, output ->
                output << "requesting gps socket server ...\n"
                def gps = input.newReader().readLine()

                List points = gps.split(";")
                return [latitude: points[0], longitude: points[1]].toJson()

            }
        }

        catch (any) {
            println "GPS Server not running"
            response.sendError(500)
        }
    }

}
