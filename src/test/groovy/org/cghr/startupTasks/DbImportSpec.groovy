package org.cghr.startupTasks

import groovy.sql.Sql
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification


/**
 * Created by ravitej on 25/4/14.
 */
@ContextConfiguration(locations = "classpath:spring-context.xml")
class DbImportSpec extends Specification {

    DbImport dbImport

    @Autowired
    Sql gSql
    String sqlDir = File.createTempDir().absolutePath

    def setupSpec() {

    }

    def setup() {

        dbImport = new DbImport(sqlDir, gSql)


    }

    def "should execute sql scripts in a given directory "() {
        given:
        File file1 = new File(sqlDir + File.separator + 'a.sql')
        file1.setText("""DROP TABLE IF EXISTS country;
                         |DROP TABLE IF EXISTS state;
                         |CREATE TABLE  country(id int,name varchar(100),continent varchar(100));
                         |CREATE TABLE  state(id int,name varchar(100),country varchar(100)); """.stripMargin())

        File file2 = new File(sqlDir + File.separator + 'b.sql')
        file2.setText("""INSERT INTO country values(1,'india','asia');
                         |INSERT INTO country values(1,'pakistan','asia');
                         |INSERT INTO state values(1,'Maharastra','india');
                         |INSERT INTO state values(1,'Karnataka','india');""".stripMargin())


        when:
        dbImport.importSqlScripts()

        then:
        gSql.rows("select * from country").size() == 2;


    }

}