package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.controllers.forms.ChangePasswordForm;
import eist.tum_social.tum_social.controllers.forms.Form;
import eist.tum_social.tum_social.controllers.forms.UpdateProfileForm;
import eist.tum_social.tum_social.controllers.util.Util;
import eist.tum_social.tum_social.model.DegreeProgram;
import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    private final Storage storage;
    public ProfileController(@Autowired Storage storage) {
        this.storage = storage;
    }
    public final static String PROFILE_PICTURE_LOCATION = Storage.getResourcePath()+"/profile_pictures/";

    @GetMapping("/profile")
    public String profilePage(Model model) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        addPersonToModel(model);

        List<DegreeProgram> degreePrograms = storage.getDegreePrograms();
        model.addAttribute("degreePrograms", degreePrograms);

        return "profile";
    }

    @GetMapping("/profile/{tumId}")
    public String profilePage(Model model, @PathVariable String tumId) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Person person = getCurrentPerson();
        Person viewedPerson = storage.getPerson(tumId);

        if (person.equals(viewedPerson)) {
            return "redirect:/profile";
        }

        model.addAttribute("person", person);
        model.addAttribute("viewedPerson", viewedPerson);

        return "profile_view";
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

        storage.saveMultipartFile(profilePicture, PROFILE_PICTURE_LOCATION + getCurrentUsersTumId() + ".png");
        return "redirect:/profile";
    }

    @PostMapping("/deleteProfile")
    public String deleteProfilePage(@RequestParam("deleteProfile") String tumId) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        storage.delete(getCurrentPerson());
        logout();
        storage.deleteFile(PROFILE_PICTURE_LOCATION + tumId + ".png");

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
        Person person = getCurrentPerson();
        form.apply(person);
        storage.update(person);
    }

}
