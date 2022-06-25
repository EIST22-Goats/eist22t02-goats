package eist.tum_social;

import eist.tum_social.tum_social.datastorage.Storage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static eist.tum_social.Util.getStorage;

public abstract class SessionBasedTest {
    @BeforeEach
    void copyTestDatabase() {
        Util.copyTestDatabase();
    }

    @AfterEach
    void removeTestDatabase() {
        Util.removeTestDatabase();
    }
    @BeforeEach
    void setupMockSession() {
        Storage storage = getStorage();
        eist.tum_social.tum_social.controllers.util.Util.storage = storage;
        Util.setupMockSession();
    }
}
