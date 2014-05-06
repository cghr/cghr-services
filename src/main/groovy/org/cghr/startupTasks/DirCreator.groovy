package org.cghr.startupTasks

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.PostConstruct

/**
 * Created by ravitej on 25/4/14.
 */
@CompileStatic
class DirCreator {

    @Autowired
    List<String> dirs

    DirCreator(List<String> dirs) {
        this.dirs = dirs

    }

    @PostConstruct
    void create() {

        dirs.each {
           String dir ->
                File myDir = new File(dir)
                if (!myDir.exists())
                    myDir.mkdirs()


        }

    }


}
