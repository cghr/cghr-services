package org.cghr.dataSync.util

/**
 * Created by ravitej on 21/2/14.
 */
class FileManager {

    String inboxPath
    String outboxPath

    FileManager(String inboxPath,String outboxPath)
    {
        this.inboxPath=inboxPath
        this.outboxPath=outboxPath

    }

    File getInboxFile(String filename)
    {
        new File(inboxPath+filename)

    }

    File getOutboxFile(String filename)
    {
        new File(outboxPath+filename)
    }

    void createInboxFile(String filename,String fileContents)
    {

    }
    void createOutboxFile(String filename,String fileContents)
    {


    }

}
