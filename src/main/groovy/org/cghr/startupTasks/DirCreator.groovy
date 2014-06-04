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

        dirs.each {
            String dir ->
                File newDir = new File(dir)
                if (!newDir.exists()) {
                    newDir.mkdirs()
                    println 'created dir ' + newDir
                }


        }

    }


}
