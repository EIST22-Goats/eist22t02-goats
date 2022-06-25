package eist.tum_social.TestClasses;

import eist.tum_social.tum_social.datastorage.Database;
import eist.tum_social.tum_social.datastorage.Storage;

public class TestStorage extends Storage {
    public boolean fileCopied = false;
    public String copySrc;
    public String copyDst;

    public TestStorage(Database db) {
        super(db);
    }

    public void copyFile(String src, String dst) {
        fileCopied = true;
        copySrc = src;
        copyDst = dst;
    }
}
