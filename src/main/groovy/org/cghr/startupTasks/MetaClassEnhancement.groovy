package org.cghr.startupTasks

import com.google.gson.Gson

import javax.annotation.PostConstruct
/**
 * Created by ravitej on 11/6/14.
 */
class MetaClassEnhancement {



    @PostConstruct
    void toJsonConversions() {

        def toJson = { new Gson().toJson(delegate) }
        Map.metaClass.toJson = toJson
        List.metaClass.toJson = toJson

    }
    @PostConstruct
    void fromJsonConversions() {

        def fromJson = {
            new Gson().fromJson(delegate, Map)
        }
        String.metaClass.jsonToMap = fromJson
    }

}
