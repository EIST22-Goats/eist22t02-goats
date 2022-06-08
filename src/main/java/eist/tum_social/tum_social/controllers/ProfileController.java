package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.controllers.forms.ChangePasswordForm;
import eist.tum_social.tum_social.controllers.forms.Form;
import eist.tum_social.tum_social.controllers.forms.UpdateProfileForm;
import eist.tum_social.tum_social.model.DegreeProgram;
import eist.tum_social.tum_social.datastorage.Storage;
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
import java.util.List;

import static eist.tum_social.tum_social.controllers.AuthenticationController.*;
import static eist.tum_social.tum_social.controllers.util.Util.addPersonToModel;
import static eist.tum_social.tum_social.controllers.util.Util.getCurrentPerson;

@Controller
public class ProfileController {

    public final static String PROFILE_PICTURE_LOCATION =
            new File("src/main/resources/static/profile_pictures/").getAbsolutePath();

    @GetMapping("/profile")
    public String profilePage(Model model) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        addPersonToModel(model);

        List<DegreeProgram> degreePrograms = new Storage().getDegreePrograms();
        model.addAttribute("degreePrograms", degreePrograms);

        return "profile";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(UpdateProfileForm form) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        updatePerson(form);

        return "redirect:/profile";
    }

    @PostMapping("/setProfilePicture")
    public String setProfilePicture(@RequestParam("profilePicture") MultipartFile profilePicture) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        try {
            profilePicture.transferTo(
                    new File(PROFILE_PICTURE_LOCATION + "/" + getCurrentUsersTumId() + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/profile";
    }

    @PostMapping("/deleteProfile")
    public String deleteProfilePage(@RequestParam("deleteProfile") String tumId) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Storage db = new Storage();
        db.delete(getCurrentPerson(db));
        logout();
        try {
            Files.delete(Path.of(PROFILE_PICTURE_LOCATION + "/" + tumId + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/login";
    }

    @PostMapping("/changePassword")
    public String changePassword(ChangePasswordForm form) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }
        updatePerson(form);
        return "redirect:/profile";
    }

    private void updatePerson(Form<Person> form) {
        Storage db = new Storage();
        Person person = db.getPerson(getCurrentUsersTumId());
        form.apply(person);
        db.update(person);
    }

}
