package org.cghr.startupTasks

import groovy.sql.Sql

import javax.annotation.PostConstruct

/**
 * Created by ravitej on 25/4/14.
 */
class DbImport {

    String dbScriptsPath
    Sql gSql

    DbImport(String dbScriptsPath, Sql gSql) {
        this.dbScriptsPath = dbScriptsPath
        this.gSql = gSql
    }

    @PostConstruct
    void importSqlScripts() {

        List sortedFilesByName = getSqlFiles()?.sort { it.name }
        importAndDeleteFiles(sortedFilesByName)

    }

    List getSqlFiles() {
        new File(dbScriptsPath).listFiles() as List
    }

    void importAndDeleteFiles(List files) {

        files.each { importSqlFile(it); deleteFile(it) }
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
