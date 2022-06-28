package eist.tum_social;

import eist.tum_social.TestClasses.TestModel;
import eist.tum_social.TestClasses.TestStorage;
import eist.tum_social.tum_social.controllers.AuthenticationController;
import eist.tum_social.tum_social.controllers.ProfileController;
import eist.tum_social.tum_social.controllers.forms.ChangePasswordForm;
import eist.tum_social.tum_social.controllers.forms.UpdateProfileForm;
import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.model.DegreeProgram;
import eist.tum_social.tum_social.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static eist.tum_social.Util.getDatabase;
import static eist.tum_social.Util.getStorage;
import static eist.tum_social.tum_social.controllers.AuthenticationController.*;
import static eist.tum_social.tum_social.controllers.ProfileController.PROFILE_PICTURE_LOCATION;
import static eist.tum_social.tum_social.controllers.util.Util.getCurrentPerson;
import static org.junit.jupiter.api.Assertions.*;

public class ProfileTest extends SessionBasedTest {

    private Storage storage;
    private ProfileController profileController;

    @BeforeEach
    public void setUpProfileController() {
        storage = getStorage();
        profileController = new ProfileController(storage);
    }

    @SuppressWarnings("unchecked")
    @Test
    void ownProfilePageTest() {
        login("ge47son");
        TestModel model = new TestModel();
        Person person = getCurrentPerson();
        assertEquals("profile", profileController.profilePage(model));
        assertTrue(model.asMap().containsValue(person));
        List<DegreeProgram> degreePrograms = (List<DegreeProgram>) model.getAttribute("degreePrograms");
        assertNotNull(degreePrograms);
        degreePrograms = degreePrograms.stream().sorted(Comparator.comparing(DegreeProgram::getId)).toList();
        assertEquals(List.of(
                storage.getDegreeProgram("Informatik"),
                storage.getDegreeProgram("Maschinenbau")
        ), degreePrograms);
    }

    @Test
    void otherProfilePageTest() {
        login("ge47son");
        TestModel model = new TestModel();
        Person person = getCurrentPerson();
        Person viewedPerson = storage.getPerson("ge95bit");
        assertEquals("profile_view", profileController.profilePage(model, "ge95bit"));
        assertEquals(person, model.getAttribute("person"));
        assertEquals(viewedPerson, model.getAttribute("viewedPerson"));
    }

    @Test
    void otherProfileRedirectTest() {
        login("ge47son");
        assertEquals("redirect:/profile", profileController.profilePage(new TestModel(), "ge47son"));
    }

    @Test
    void updateProfileTest() {
        login("ge47son");
        UpdateProfileForm form = new UpdateProfileForm();
        form.setFirstname("Max");
        form.setLastname("Mustermann");
        form.setEmail("muster@mail.com");
        form.setBiography("lorem ipsume dolor sit amet");
        form.setBirthdate("1993-10-01");
        form.setSemesterNr(9);
        form.setDegreeProgramName("Maschinenbau");
        profileController.updateProfile(form);
        Person person = getCurrentPerson();
        assertEquals("Max", person.getFirstname());
        assertEquals("Mustermann", person.getLastname());
        assertEquals("muster@mail.com", person.getEmail());
        assertEquals("lorem ipsume dolor sit amet", person.getBiography());
        assertEquals(LocalDate.of(1993, 10, 1), person.getBirthdate());
        assertEquals(9, person.getSemesterNr());
        assertEquals(storage.getDegreeProgram("Maschinenbau"), person.getDegreeProgram());
    }

    @Test
    void updateProfileInvalidDegreeProgram() {
        login("ge47son");
        UpdateProfileForm form = new UpdateProfileForm();
        form.setFirstname("Max");
        form.setLastname("Mustermann");
        form.setEmail("muster@mail.com");
        form.setBiography("lorem ipsume dolor sit amet");
        form.setBirthdate("1993-10-01");
        form.setSemesterNr(9);
        form.setDegreeProgramName("Maschinenbau");
        form.setDegreeProgramName("Wirtschaftsinformatik");
        profileController.updateProfile(form);
        assertEquals(storage.getDegreeProgram("Informatik"), getCurrentPerson().getDegreeProgram());
    }

    @Test
    void setProfilePicture() {
        login("ge47son");
        Path imagePath = Path.of(PROFILE_PICTURE_LOCATION + "ge47son.png");
        String name = "ge47son.png";
        String contentType = "image/png";
        byte[] content = null;
        try {
            content = Files.readAllBytes(imagePath);
        } catch (IOException e) {
            fail("io exception");
        }
        MultipartFile result = new MockMultipartFile(name, name, contentType, content);
        TestStorage storage = new TestStorage(getDatabase());
        profileController = new ProfileController(storage);
        assertEquals("redirect:/profile", profileController.setProfilePicture(result));
        assertTrue(storage.fileSaved);
        assertEquals(result, storage.savedFile);
        assertEquals(imagePath.toString(), storage.savedFilePath);
    }

    @Test
    void deleteProfilePage() {
        login("ge47son");
        TestStorage storage = new TestStorage(getDatabase());
        profileController = new ProfileController(storage);
        assertEquals("redirect:/login", profileController.deleteProfilePage("ge47son"));
        assertFalse(isLoggedIn());
        assertTrue(storage.fileDeleted);
        assertEquals(PROFILE_PICTURE_LOCATION + "ge47son.png", storage.deletePath);
    }
    @Test
    void changePassword() {
        login("ge47son");
        ChangePasswordForm form = new ChangePasswordForm();
        form.setPassword("password");
        assertEquals("redirect:/profile", profileController.changePassword(form));
        AuthenticationController authenticationController = new AuthenticationController(getStorage());
        assertTrue(authenticationController.isLoginValid("ge47son", "password"));
    }
}
