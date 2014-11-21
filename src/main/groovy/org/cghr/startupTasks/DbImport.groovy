package org.cghr.startupTasks

import groovy.sql.Sql
import groovy.transform.TupleConstructor

import javax.annotation.PostConstruct

/**
 * Created by ravitej on 25/4/14.
 */
@TupleConstructor
class DbImport {

    String dbScriptsPath
    Sql gSql

    @PostConstruct
    void importSqlScripts() {
        importAndDeleteFiles sortedSqlFilesByName
    }

    void importSqlScriptsWithoutDeleting() {
        importFiles sortedSqlFilesByName
    }

    List getSortedSqlFilesByName() {
        getSqlFiles(dbScriptsPath)?.sort { it.name }
    }

    List getSqlFiles(dbScriptsPath) {
        new File(dbScriptsPath).listFiles() as List
    }

    void importAndDeleteFiles(List files) {

        files.each { importSqlFile(it); deleteFile(it) }
    }

    void importFiles(List files) {

        files.each { importSqlFile(it); }
    }

    void importSqlFile(File file) {

        String[] sqls = file.text.split(";")
        executeSqlBatch(sqls)
    }

    void deleteFile(File file) {
        file.delete()
    }

    void executeSqlBatch(String[] sqls) {

        gSql.withBatch {
            stmt ->
                sqls.each { String sql -> stmt.addBatch(sql) }
        }

    }
}
