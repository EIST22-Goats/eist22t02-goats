package eist.tum_social;

import eist.tum_social.TestClasses.TestDatabase;
import eist.tum_social.TestClasses.TestModel;
import eist.tum_social.TestClasses.TestStorage;
import eist.tum_social.tum_social.controllers.AuthenticationController;
import eist.tum_social.tum_social.controllers.forms.RegistrationForm;
import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import static eist.tum_social.Util.getDatabase;
import static eist.tum_social.Util.getStorage;
import static eist.tum_social.tum_social.controllers.AuthenticationController.*;
import static eist.tum_social.tum_social.controllers.ProfileController.PROFILE_PICTURE_LOCATION;
import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationTest extends SessionBasedTest {
    private AuthenticationController authenticationController;
    @BeforeEach
    void setupAuthenticationController() {
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
    void registerPersonValidTest() {
        String tumId = "go42tum";
        Person person = new Person();
        person.setTumId(tumId);
        String password = "password";
        person.setPassword(password);
        TestDatabase database = new TestDatabase("jdbc:sqlite:test_copy.db");
        TestStorage storage = new TestStorage(database);
        authenticationController = new AuthenticationController(storage);
        authenticationController.registerPerson(person);
        assertTrue(authenticationController.isLoginValid(tumId, password));
        assertTrue(database.updateCalled);
        assertEquals(database.calledOn, person);
        assertTrue(storage.fileCopied);
        assertEquals( PROFILE_PICTURE_LOCATION+"default.png", storage.copySrc);
        assertEquals( PROFILE_PICTURE_LOCATION+tumId+".png", storage.copyDst);
    }

    @Test
    void registerPersonInvalidTest() {
        Person person = new Person();
        person.setTumId("hallo welt");
        String password = "password";
        person.setPassword(password);
        TestDatabase database = new TestDatabase("jdbc:sqlite:test_copy.db");
        Storage storage = new TestStorage(database);
        authenticationController = new AuthenticationController(storage);
        authenticationController.registerPerson(person);
        assertFalse(database.updateCalled);
    }

    @Test
    void registerPersonDuplicateTest() {
        Person person = new Person();
        person.setTumId("ge47son");
        TestDatabase database = new TestDatabase("jdbc:sqlite:test_copy.db");
        Storage storage = new TestStorage(database);
        authenticationController = new AuthenticationController(storage);
        authenticationController.registerPerson(person);
        assertFalse(database.updateCalled);
    }
    @Test
    void createDefaultProfilePictureTest() {
        String tumId = "te31tes";

        TestStorage storage = new TestStorage(getDatabase());

        authenticationController = new AuthenticationController(storage);
        authenticationController.createDefaultProfilePicture(tumId);
        assertTrue(storage.fileCopied);
        assertEquals( PROFILE_PICTURE_LOCATION+"default.png", storage.copySrc);
        assertEquals( PROFILE_PICTURE_LOCATION+tumId+".png", storage.copyDst);
    }

    @Test
    void registrationPageGetTest() {
        assertEquals("register", authenticationController.registrationPage());
        login("ge47son");
        assertEquals("redirect:/", authenticationController.registrationPage());
    }

    @Test
    void registrationPagePostSuccessTest() {
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setTumId("ge74nos");
        TestModel model = new TestModel();
        Person person = new Person();
        registrationForm.apply(person);
        assertEquals("redirect:/", authenticationController.registrationPage(model, registrationForm));
        assertTrue(isLoggedIn());
        assertEquals("ge74nos", getCurrentUsersTumId());
        assertNull(model.getAttribute("registrationFailed"));
        assertNull(model.getAttribute("errorMessage"));
    }
    @Test
    void registrationPagePostFailureTest() {
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setTumId("ge47son");
        TestModel model = new TestModel();
        Person person = new Person();
        registrationForm.apply(person);
        assertEquals("register", authenticationController.registrationPage(model, registrationForm));
        assertFalse(isLoggedIn());
        assertEquals(true, model.getAttribute("registrationFailed"));
        assertNotNull(model.getAttribute("errorMessage"));
    }

    @Test
    void registrationPagePostLoggedInTest() {
        Model model = new TestModel();
        RegistrationForm registrationForm = new RegistrationForm();
        login("go42tum");
        assertEquals("redirect:/", authenticationController.registrationPage(model, registrationForm));
    }
}
