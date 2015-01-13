package org.cghr.survey.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by ravitej on 14/10/14.
 */
@RestController
@RequestMapping("/NetworkStatus")
class NetworkStatusService {


    @Autowired
    @Qualifier("ipAddressPattern")
    String pattern


    @RequestMapping("")
    String isConnectedToWifiNetwork() {

        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces()
        List hostAddresses = []

        networkInterfaces.each { NetworkInterface networkInterface ->

            networkInterface.getInterfaceAddresses().each { InterfaceAddress interfaceAddress ->
                hostAddresses << interfaceAddress.getAddress().getHostAddress()
            }

        }
        hostAddresses.findAll { it.contains(pattern) } ? [status: true].toJson() : [status: false].toJson()
    }


}
