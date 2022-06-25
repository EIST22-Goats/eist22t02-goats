package eist.tum_social;

import eist.tum_social.tum_social.datastorage.Database;
import eist.tum_social.tum_social.datastorage.Storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class StorageMock extends Storage {
    public boolean fileCopied = false;
    public String copySrc;
    public String copyDst;

    public StorageMock(Database db) {
        super(db);
    }

    public void copyFile(String src, String dst) {
        fileCopied = true;
        copySrc = src;
        copyDst = dst;
    }
}
