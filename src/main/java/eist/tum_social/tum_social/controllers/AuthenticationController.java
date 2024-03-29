package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.controllers.forms.RegistrationForm;
import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.datastorage.StorageFacade;
import eist.tum_social.tum_social.controllers.util.Status;
import eist.tum_social.tum_social.model.Person;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

import static eist.tum_social.tum_social.controllers.ProfileController.PROFILE_PICTURE_LOCATION;
import static eist.tum_social.tum_social.controllers.util.Status.ERROR;
import static eist.tum_social.tum_social.controllers.util.Status.SUCCESS;

@Controller
public class AuthenticationController {

    private final Storage storage;
    public AuthenticationController(@Autowired Storage storage) {
        this.storage = storage;
    }
    public static final String TUM_ID_REGEX =
            "^[^aeiouAEIOU\\d\\W][aeiou]\\d\\d[^aeiouAEIOU\\d\\W][aeiou][^aeiouAEIOU\\d\\W]$";

    private static final String LOGGED_IN_TOKEN = "loggedIn";

    @GetMapping("/login")
    public String loginPage() {
        if (isLoggedIn()) {
            return "redirect:/";
        } else {
            return "login";
        }
    }

    @PostMapping("/login")
    public String loginPage(Model model, Person person) {
        if (isLoggedIn()) {
            return "redirect:/";
        }

        if (isLoginValid(person.getTumId(), person.getPassword())) {
            login(person.getTumId());
            return "redirect:/";
        } else {
            model.addAttribute("wrongInput", true);
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logoutPage() {
        logout();
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registrationPage() {
        if (isLoggedIn()) {
            return "redirect:/";
        }
        return "register";
    }

    @PostMapping("/register")
    public String registrationPage(Model model, RegistrationForm form) {
        if (isLoggedIn()) {
            return "redirect:/";
        }

        Person person = new Person();
        form.apply(person);

        Status status = registerPerson(person);
        if (status == SUCCESS) {
            login(form.getTumId());
            return "redirect:/";
        } else {
            model.addAttribute("registrationFailed", true);
            model.addAttribute("errorMessage", status.getErrorMessage());
            return "register";
        }
    }

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public Status registerPerson(Person person) {
        if (isTumIDInvalid(person.getTumId())) {
            return ERROR("Tum ID ist ungültig");
        }

        if (storage.getPerson(person.getTumId()) != null) {
            return ERROR("Account für " + person.getTumId() + " existiert bereits");
        }

        String hashedPassword = hashPassword(person.getPassword());
        person.setPassword(hashedPassword);

        storage.update(person);

        createDefaultProfilePicture(person.getTumId());

        return SUCCESS;
    }

    public void createDefaultProfilePicture(String tumId) {
        String copied = PROFILE_PICTURE_LOCATION + tumId + ".png";
        String defaultProfile = PROFILE_PICTURE_LOCATION + "default.png";
        storage.copyFile(defaultProfile, copied);
    }

    public boolean isLoginValid(String tumId, String password) {
        if (isTumIDInvalid(tumId)) {
            return false;
        }

        Person person = storage.getPerson(tumId);
        return person != null && BCrypt.checkpw(password, person.getPassword());
    }

    public static boolean isTumIDInvalid(String tumId) {
        Pattern p = Pattern.compile(TUM_ID_REGEX);
        Matcher m = p.matcher(tumId);
        return !m.matches();
    }

    public static boolean isLoggedIn() {
        return getCurrentUsersTumId() != null;
    }

    public static String getCurrentUsersTumId() {
        return (String) session().getAttribute(LOGGED_IN_TOKEN);
    }

    public static void login(String tumId) {
        session().setAttribute(LOGGED_IN_TOKEN, tumId);
    }

    public static void logout() {
        session().removeAttribute(LOGGED_IN_TOKEN);
    }

    public static HttpSession session() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true);
    }
}
