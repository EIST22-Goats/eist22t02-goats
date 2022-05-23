package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.database.DatabaseFacade;
import eist.tum_social.tum_social.database.SqliteFacade;
import eist.tum_social.tum_social.model.Person;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

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

        return getProfilePage(model);
    }

    @PostMapping("/profil")
    public String profilePage(Model model, @RequestParam("profilePicture") MultipartFile profilePicture) {
        if (!isLoggedIn()) {
            return "redirect:/anmelden";
        }
        try {
            profilePicture.transferTo(
                    new File(PROFILE_PICTURE_LOCATION + "/" + getCurrentUsersTumId() + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return getProfilePage(model);
    }

    @PostMapping("/deleteProfile")
    public String deleteProfilePage(@RequestParam("deleteProfile") String tumId) {
        if (!isLoggedIn()) {
            return "redirect:/anmelden";
        }
        deleteProfile(tumId);
        return "redirect:/anmelden";
    }

    private void deleteProfile(String tumId) {
        DatabaseFacade db = new SqliteFacade();
        db.removePerson(tumId);
        logout();
        // TODO delete profile picture
    }

    private String getProfilePage(Model model) {
        DatabaseFacade db = new SqliteFacade();
        Person person = db.getPerson(getCurrentUsersTumId());

        model.addAttribute("person", person);

        return "profil";
    }

}