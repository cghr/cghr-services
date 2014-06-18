package org.cghr.startupTasks

import spock.lang.IgnoreRest
import spock.lang.Specification
/**
 * Created by ravitej on 18/6/14.
 */
class MetaClassEnhancementSpec extends Specification {

    MetaClassEnhancement metaClassEnhancement

    def setup() {
        metaClassEnhancement = new MetaClassEnhancement()
    }

    def "should  attach toJson method to List class containing maps"() {
        given:
        List list = [[id: 1, name: 'india'], [id: 2, name: 'pakistan']]

        when:
        metaClassEnhancement.toJsonConversions()

        then:
        list.toJson() == '[{"id":1,"name":"india"},{"id":2,"name":"pakistan"}]'
    }

    def "should  attach toJson method to Map Class"() {
        given:
        Map map = [id: 1, name: 'india']

        when:
        metaClassEnhancement.toJsonConversions()

        then:
        map.toJson() == '{"id":1,"name":"india"}'
    }

    def "should attach jsonToMap method to String Class"() {

        given:
        String json = '{"id":1,"name":"india"}'

        when:
        metaClassEnhancement.fromJsonConversions()

        then:
        json.jsonToMap() == [id: 1, name: 'india']


    }

}
