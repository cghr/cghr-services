package org.cghr.startupTasks

import com.google.gson.Gson

import javax.annotation.PostConstruct

/**
 * Created by ravitej on 11/6/14.
 */
class MetaClassEnhancement {

    Gson gson = new Gson()

    @PostConstruct
    void toJsonConversions() {

        def toJson = {
            gson.toJson(delegate)
        }
        Map.metaClass.toJson = toJson
        List.metaClass.toJson = toJson

    }

    @PostConstruct
    void fromJsonConversions() {

        def fromJson={
            gson.fromJson(delegate,Map)
        }
        String.metaClass.jsonToMap=fromJson
    }

}
