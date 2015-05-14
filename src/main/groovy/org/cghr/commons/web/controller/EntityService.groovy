package org.cghr.commons.web.controller

import org.cghr.commons.entity.Entity
import org.cghr.survey.controller.SurveyRandomizer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

import javax.servlet.http.HttpServletRequest
import java.text.SimpleDateFormat

/**
 * Created by ravitej on 25/11/14.
 */
@RestController
@RequestMapping("/entity")
class EntityService {

    @Autowired
    Entity entity
    @Autowired
    String serverBaseUrl
    @Autowired
    SurveyRandomizer randomizer


    @RequestMapping("/{entityName}/{entityId}")
    Map getEntity(
            @PathVariable String entityName,
            @PathVariable String entityId) {

        entity.findById(entityName, entityId)

    }


    @RequestMapping("/{entityName}")
    List getEntityList(@PathVariable String entityName) {

        entity.findAll(entityName)

    }

    @RequestMapping("/{entityName}/{property}/{propertyValue}")
    List getEntityListWithCriteria(
            @PathVariable String entityName, @PathVariable String property, @PathVariable String propertyValue) {

        entity.findByCriteria(entityName, property, propertyValue)
    }

    @RequestMapping(value = "/{entityName}", method = RequestMethod.POST, consumes = "application/json")
    String freshSave(@RequestBody Map entityData, @PathVariable String entityName) {

        if (entityName == 'death') {
            entityData.put("surveytype", randomizer.getRandomSurveyType())
        }

        entity.freshSave(entityName, entityData)

        Map logEntity = entityData.clone()
        logEntity.putAll(getTimeLogIfExists(entityName))
        entity.log(entityName, logEntity)
    }

    Map getTimeLogIfExists(String entityName) {

        Map time = [:]
        if (entity.hasTimeLog(entityName))
            time.put('timelog', getNow())
        else if (entity.hasEndTime(entityName))
            time.put('endtime', getNow())

        return time
    }

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
    String saveVariantEntities(@RequestBody final Map[] data, HttpServletRequest request) {


        List changelogs = data.toList()
        entity.saveVariantEntities(changelogs)


        String requestHost = request.serverName
        if (isNotSeverHost(requestHost))
            entity.saveChangeLogs(changelogs)

    }


    @RequestMapping(value = "/{entityName}/{entityId}", method = RequestMethod.DELETE)
    String deleteEntity(@PathVariable String entityName,
                        @PathVariable String entityId) {

        entity.delete(entityName, entityId)
    }

    String getServerHost() {
        serverBaseUrl.toURL().getHost()
    }

    boolean isNotSeverHost(String requestHost) {
        !(requestHost == serverHost && requestHost != 'localhost')
    }

    String getNow() {
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
    }

}
