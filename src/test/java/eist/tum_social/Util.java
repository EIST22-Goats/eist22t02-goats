package eist.tum_social;

import eist.tum_social.tum_social.datastorage.Database;
import eist.tum_social.tum_social.datastorage.SqliteDatabase;
import eist.tum_social.tum_social.datastorage.Storage;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Util {

    private static String getBaseDir() {
        return System.getProperty("user.dir") + "/";
    }

    public static Database getDatabase() {
        return new SqliteDatabase("jdbc:sqlite:test_copy.db");
    }
    public static Storage getStorage() {
        return new Storage(getDatabase());
    }

    public static void copyTestDatabase() {
        String workingDir = getBaseDir();
        Path srcPath = Paths.get(workingDir + "test.db");
        Path dstPath = Paths.get(workingDir + "test_copy.db");
        try {
            Files.copy(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeTestDatabase() {
        String workingDir = getBaseDir();
        Path dbPath = Paths.get(workingDir + "test_copy.db");
        try {
            Files.delete(dbPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
