package eist.tum_social;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

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
        Util.setupMockSession();
    }
}
