package org.cghr.context

import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericGroovyApplicationContext

/**
 * Created by ravitej on 28/4/14.
 */
class SpringContext {

    static ApplicationContext ctx = new GenericGroovyApplicationContext("file:config/AppContext.groovy")


    static getBean(String bean) {
        ctx.getBean(bean)
    }

    static getSql() {
        ctx.getBean('gSql')
    }

    static getDbTester() {
        ctx.getBean('dt')
    }

    static getDbAccess() {

        ctx.getBean('dbAccess')
    }

    static getDbStore() {

        ctx.getBean('dbStore')
    }
}
