package eist.tum_social;

import eist.tum_social.tum_social.controllers.AuthenticationController;
import eist.tum_social.tum_social.controllers.util.Status;
import eist.tum_social.tum_social.datastorage.Database;
import eist.tum_social.tum_social.datastorage.SqliteDatabase;
import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static eist.tum_social.Util.getDatabase;
import static eist.tum_social.Util.getStorage;
import static eist.tum_social.tum_social.controllers.AuthenticationController.getCurrentUsersTumId;
import static eist.tum_social.tum_social.controllers.AuthenticationController.isTumIDInvalid;
import static eist.tum_social.tum_social.controllers.ProfileController.PROFILE_PICTURE_LOCATION;
import static eist.tum_social.tum_social.controllers.util.Status.ERROR;
import static eist.tum_social.tum_social.controllers.util.Status.SUCCESS;
import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationTest {
    private AuthenticationController authenticationController;

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
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes attr = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attr);
    }

    @BeforeEach
    void setupAuthenticationControllern() {
        Storage storage = getStorage();
        authenticationController = new AuthenticationController(storage);
    }

    @Test
    void isTumIdValidValidTest() {
        assertFalse(isTumIDInvalid("ge47son"));
        assertFalse(isTumIDInvalid("ba42run"));
    }

    @Test
    void isTumIdValidInvalidTest() {
        assertTrue(isTumIDInvalid("hallo welt :)"));
        assertTrue(isTumIDInvalid("ge477son"));
    }

    @Test
    void isTumIdValidAlmostValidTest() {
        assertTrue(isTumIDInvalid("ge47snn"));
        assertTrue(isTumIDInvalid("gg47son"));
        assertTrue(isTumIDInvalid("ee47son"));
        assertTrue(isTumIDInvalid("ge47oon"));
        assertTrue(isTumIDInvalid("ge47soo"));
        assertTrue(isTumIDInvalid("ge4nson"));
        assertTrue(isTumIDInvalid("gen7son"));
    }

    @Test
    void isLoginValidCorrectTest() {
        assertTrue(authenticationController.isLoginValid("ge47son", "12345"));
    }

    @Test
    void isLoginValidWrongPasswordTest() {
        assertFalse(authenticationController.isLoginValid("ge47son", "123456"));
    }

    @Test
    void isLoginValidUnknownUsernameTest() {
        assertFalse(authenticationController.isLoginValid("ge50son", "12345"));
    }

    @Test
    void loginLogoutTest() {
        assertFalse(AuthenticationController.isLoggedIn());
        AuthenticationController.login("ge47son");
        assertTrue(AuthenticationController.isLoggedIn());
        AuthenticationController.logout();
        assertFalse(AuthenticationController.isLoggedIn());
    }

    @Test
    void getCurrentUsersTumIdTest() {
        String tumId = "ge47son";
        String tumId2 = "ge52bit";
        assertNull(getCurrentUsersTumId());
        AuthenticationController.login(tumId);
        assertEquals(tumId, getCurrentUsersTumId());
        AuthenticationController.logout();
        assertNull(getCurrentUsersTumId());
        AuthenticationController.login(tumId2);
        assertEquals(tumId2, getCurrentUsersTumId());
    }
    @Test
    void registerPersonTest() {

    }
    @Test
    void createDefaultProfilePictureTest() {
        String tumId = "te31tes";

        StorageMock storage = new StorageMock(getDatabase());

        authenticationController = new AuthenticationController(storage);
        authenticationController.createDefaultProfilePicture(tumId);
        assertTrue(storage.fileCopied);
        assertEquals( PROFILE_PICTURE_LOCATION+"default.png", storage.copySrc);
        assertEquals( PROFILE_PICTURE_LOCATION+tumId+".png", storage.copyDst);
    }

}
