package org.cghr.startupTasks

import javax.annotation.PostConstruct

/**
 * Created by ravitej on 25/4/14.
 */

class DirCreator {

    List<String> dirs

    DirCreator(List<String> dirs) {
        this.dirs = dirs
    }

    @PostConstruct
    void create() {
        dirs.each { createDir(new File(it)) }
    }

    void createDir(File dir) {
        dir.mkdirs()
    }


}
