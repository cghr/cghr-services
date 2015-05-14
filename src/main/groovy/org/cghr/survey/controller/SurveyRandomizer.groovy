package org.cghr.survey.controller

import groovy.transform.TupleConstructor
import org.cghr.commons.db.DbAccess

/**
 * Created by ravitej on 13/3/15.
 */
@TupleConstructor
class SurveyRandomizer {

    DbAccess dbAccess
    List surveys
    int balanceDiff

    String getRandomSurveyType() {

        int va = getCount('va')
        int esl = getCount('esl')

        if (va > esl && va - esl >= balanceDiff)
            return 'esl'
        else if (esl > va && esl - va >= balanceDiff)
            return 'va'
        else getRandom()

    }


    String getRandom() {

        Collections.shuffle(surveys)
        surveys.first()
    }

    Integer getCount(String type) {

        String sql = (type == 'va') ? "SELECT COUNT(*) count FROM feedback WHERE surveytype='va'" : "SELECT COUNT(*) count FROM feedback WHERE surveytype IS NULL "
        dbAccess.firstRow(sql).count

    }


}
