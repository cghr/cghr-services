package org.cghr.commons.db

import com.google.gson.Gson
import groovy.sql.Sql
import org.cghr.commons.entity.Entity
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by ravitej on 19/1/15.
 */
@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader.class)
class EntitySpec extends Specification {

    @Autowired
    Sql gSql

    @Autowired
    Entity entity

    @Shared
    List countries
    @Shared
    List countryChangelogs

    @Autowired
    DbTester dt

    def setupSpec() {
        countries = new MockData().sampleData.get('country')
        countryChangelogs = new MockData().sampleData.get('countryBatchData')
    }

    def setup() {

        dt.cleanInsert('country')
    }

    def "should find an entity by entityName,id"() {

        expect:
        entity.findById(entityName, entityId) == result

        where:
        entityName | entityId || result
        'country'  | '1'      || countries[0]
        'country'  | '999'    || [:]

    }

    def "should find the entityList by entityName"() {
        expect:
        entity.findAll(entityName) == result

        where:
        entityName || result
        'country'  || countries

    }

    def "should find the entityList by criteria"() {
        expect:
        entity.findByCriteria(entityName, searchKey, searchValue) == result

        where:
        entityName | searchKey   | searchValue || result
        'country'  | 'continent' | 'asia'      || countries
        'country'  | 'continent' | 'africa'    || []

    }

    def "should save a new entity"() {
        setup:
        dt.clean('country')

        when:
        entity.saveOrUpdate('country', countries[0])

        then:
        gSql.firstRow("select * from country where id=1") == countries[0]

    }

    def "should update an existing entity"() {

        given:
        Map updatedEntity = [id: 1, name: 'india updated', continent: 'asia']

        when:
        entity.saveOrUpdate('country', updatedEntity)

        then:
        gSql.firstRow("select * from country where id=1") == updatedEntity

    }

    def "should freshly create a new entity by cleaning up existing entity for a given entityId"() {

        given:
        Map updatedEntity = [id: 1, name: 'india updated']

        when:
        entity.freshSave('country', updatedEntity)

        then:
        gSql.firstRow("select * from country where id=1") == [id:1,name: 'india updated',continent: null]

    }

    def "should save an entityList for a given entityName"() {
        setup:
        dt.clean('country')

        when:
        entity.saveList('country', countries)

        then:
        gSql.rows("select * from country") == countries

    }

    def "should delete an entity with id"() {
        when:
        entity.delete('country', '1')

        then:
        gSql.firstRow("select * from country where id=1") == null
    }

    def "should log a given entity to datachangelog"() {
        setup:
        Map log = [datastore: 'country', data: countries[0]]
        String expectedLog
        dt.clean('datachangelog')


        when:
        entity.log('country', countries[0])

        then:
        gSql.rows("select * from datachangelog").size() == 1
        gSql.eachRow("select log from datachangelog") {
            expectedLog = it.log.getAsciiStream().getText();
        }
        expectedLog == new Gson().toJson(log)

    }

    def "should save list of variant entities"() {
        setup:
        dt.clean('country')

        when:
        entity.saveVariantEntities(countryChangelogs)

        then:
        gSql.rows("select * from country") == countries

    }

    def "should save changeLogs"() {
        setup:
        dt.clean('datachangelog')
        List changelogs = [
                [datastore: 'country', data: countries[0]].toJson(),
                [datastore: 'country', data: countries[1]].toJson(),
                [datastore: 'country', data: countries[2]].toJson()
        ]
        List result=[]

        when:
        entity.saveChangeLogs(changelogs)

        then:
        gSql.rows("select * from datachangelog").size()==3

        gSql.eachRow("select log from datachangelog"){
            result << it.log.getAsciiStream().getText()
        }
        result[0]==[datastore: 'country', data: countries[0]].toJson()
    }


}
