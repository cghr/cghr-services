package org.cghr.survey.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by ravitej on 14/10/14.
 */
@RestController
@RequestMapping("/sync/networkStatus")
class NetworkStatusService {


    @Autowired
    @Qualifier("ipAddressPattern")
    String pattern


    @RequestMapping("")
    Map isConnectedToWifiNetwork() {

        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces()

        List hostAddresses = networkInterfaces
                .collect { NetworkInterface networkInterface -> getHostAddresses(networkInterface) }
                .flatten()

        hostAddresses.find { it.contains(pattern) } ? [status: true] : [status: false]
    }

    List getHostAddresses(NetworkInterface networkInterface) {

        networkInterface.getInterfaceAddresses().collect { InterfaceAddress interfaceAddress ->
            interfaceAddress.getAddress().getHostAddress()

        }
    }


}
