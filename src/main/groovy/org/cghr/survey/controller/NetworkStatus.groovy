package org.cghr.survey.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by ravitej on 14/10/14.
 */
@RestController
@RequestMapping("/NetworkStatus")
class NetworkStatus {
    

    Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces()

    @RequestMapping(value = "", produces = "application/json")
    String isConnectedToWifiNetwork() {

        List hostAddresses = []
        networkInterfaces.each {
            NetworkInterface networkInterface ->
                networkInterface.getInterfaceAddresses().each {
                    InterfaceAddress interfaceAddress ->
                        hostAddresses.add(interfaceAddress.getAddress().getHostAddress())
                }

        }
        hostAddresses.findAll { it == /~192.168*.*/ } ? [status: true].toJson() : [status: false].toJson()
    }


}
