package org.cghr.startupTasks

import groovy.transform.TupleConstructor

import javax.annotation.PostConstruct

/**
 * Created by ravitej on 25/4/14.
 */

@TupleConstructor
class DirCreator {

    List<String> dirs


    @PostConstruct
    void create() {
        dirs.each { createDir(new File(it)) }
    }

    void createDir(File dir) {
        dir.mkdirs()
    }


}
