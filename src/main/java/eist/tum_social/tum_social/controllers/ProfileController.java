package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.controllers.forms.ChangePasswordForm;
import eist.tum_social.tum_social.controllers.forms.UpdateProfileForm;
import eist.tum_social.tum_social.database.DatabaseFacade;
import eist.tum_social.tum_social.database.SqliteFacade;
import eist.tum_social.tum_social.model.DegreeProgram;
import eist.tum_social.tum_social.model.Person;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static eist.tum_social.tum_social.controllers.AuthenticationController.*;

@Controller
public class ProfileController {

    public final static String PROFILE_PICTURE_LOCATION =
            new File("src/main/resources/static/profile_pictures/").getAbsolutePath();

    @GetMapping("/profil")
    public String profilePage(Model model) {
        if (!isLoggedIn()) {
            return "redirect:/anmelden";
        }

        DatabaseFacade db = new SqliteFacade();
        Person person = db.select(Person.class, "tumId='" + getCurrentUsersTumId() + "'", true).get(0);
        model.addAttribute("person", person);
        model.addAttribute("degreePrograms", db.select(DegreeProgram.class));

        return "profil";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(UpdateProfileForm form) {
        if (!isLoggedIn()) {
            return "redirect:/anmelden";
        }

        SqliteFacade db = new SqliteFacade();
        Person person = db.select(Person.class, "tumId='" + getCurrentUsersTumId() + "'", true).get(0);
        form.apply(person);
        db.update(person);

        return "redirect:/profil";
    }

    @PostMapping("/setProfilePicture")
    public String setProfilePicture(@RequestParam("profilePicture") MultipartFile profilePicture) {
        if (!isLoggedIn()) {
            return "redirect:/anmelden";
        }

        try {
            profilePicture.transferTo(
                    new File(PROFILE_PICTURE_LOCATION + "/" + getCurrentUsersTumId() + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/profil";
    }

    @PostMapping("/deleteProfile")
    public String deleteProfilePage(@RequestParam("deleteProfile") String tumId) {
        if (!isLoggedIn()) {
            return "redirect:/anmelden";
        }
        deleteProfile(tumId);
        return "redirect:/anmelden";
    }

    @PostMapping("/changePassword")
    public String changePassword(ChangePasswordForm form) {
        if (!isLoggedIn()) {
            return "redirect:/anmelden";
        }
        SqliteFacade db = new SqliteFacade();
        Person person = db.select(Person.class, "tumId='" + getCurrentUsersTumId() + "'", true).get(0);
        form.apply(person);
        db.update(person);
        return "redirect:/profil";
    }

    private void deleteProfile(String tumId) {
        SqliteFacade db = new SqliteFacade();
        db.removePerson(tumId);
        logout();
        try {
            Files.delete(Path.of(PROFILE_PICTURE_LOCATION + "/" + tumId + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
