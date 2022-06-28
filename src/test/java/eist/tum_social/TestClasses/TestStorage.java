package eist.tum_social.TestClasses;

import eist.tum_social.tum_social.datastorage.Database;
import eist.tum_social.tum_social.datastorage.Storage;
import org.springframework.web.multipart.MultipartFile;

/**
 * A storage used for testing.
 */
public class TestStorage extends Storage {
    public boolean fileCopied = false;
    public boolean fileDeleted = false;

    public boolean fileSaved = false;
    public String copySrc;
    public String copyDst;
    public String deletePath;

    public MultipartFile savedFile;
    public String savedFilePath;

    public TestStorage(Database db) {
        super(db);
    }

    @Override
    public void copyFile(String src, String dst) {
        fileCopied = true;
        copySrc = src;
        copyDst = dst;
    }

    @Override
    public void deleteFile(String file) {
        fileDeleted = true;
        deletePath = file;
    }

    @Override
    public void saveMultipartFile(MultipartFile file, String path) {
        fileSaved = true;
        savedFile = file;
        savedFilePath = path;
    }
}
