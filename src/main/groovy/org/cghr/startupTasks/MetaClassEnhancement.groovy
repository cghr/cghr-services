package org.cghr.startupTasks

import groovy.json.JsonOutput

import javax.annotation.PostConstruct
/**
 * Created by ravitej on 11/6/14.
 */
class MetaClassEnhancement {


    @PostConstruct
    void toJsonConversions() {

       def toJson = { JsonOutput.toJson(delegate) }
        Map.metaClass.toJson = toJson
        List.metaClass.toJson = toJson

    }

    @PostConstruct
    void fromJsonConversions() {

        def fromJson = {
            new groovy.json.JsonSlurper().parseText(delegate)
        }
        String.metaClass.jsonToMap = fromJson
    }

}
