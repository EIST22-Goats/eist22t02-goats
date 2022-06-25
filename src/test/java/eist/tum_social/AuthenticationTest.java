package eist.tum_social;

import eist.tum_social.tum_social.controllers.AuthenticationController;
import eist.tum_social.tum_social.datastorage.SqliteDatabase;
import eist.tum_social.tum_social.datastorage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static eist.tum_social.tum_social.controllers.AuthenticationController.isTumIDInvalid;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthenticationTest {

    private AuthenticationController authenticationController;

    @BeforeEach
    void setupMockSession() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes attr = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attr);
    }

    @BeforeEach
    void setupAuthenticationControllern() {
        Storage storage = new Storage(new SqliteDatabase("jdbc:sqlite:tum_social.db"));
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
    void testLogin() {
        assertFalse(AuthenticationController.isLoggedIn());
        AuthenticationController.login("ge47son");
        assertTrue(AuthenticationController.isLoggedIn());
        AuthenticationController.logout();
        assertFalse(AuthenticationController.isLoggedIn());
    }

    // TODO: registration
}
