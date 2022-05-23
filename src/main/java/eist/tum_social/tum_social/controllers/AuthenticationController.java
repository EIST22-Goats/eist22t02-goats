package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.database.DatabaseFacade;
import eist.tum_social.tum_social.database.SqliteFacade;
import eist.tum_social.tum_social.model.Person;
import org.mindrot.jbcrypt.BCrypt;
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
import static eist.tum_social.tum_social.controllers.Status.ERROR;
import static eist.tum_social.tum_social.controllers.Status.SUCCESS;

@Controller
public class AuthenticationController {
    public static final String TUM_ID_REGEX =
            "^[^aeiouAEIOU\\d\\W][aeiou]\\d\\d[^aeiouAEIOU\\d\\W][aeiou][^aeiouAEIOU\\d\\W]$";

    private static final String LOGGED_IN_TOKEN = "loggedIn";

    @GetMapping("/anmelden")
    public String loginPage() {
        if (isLoggedIn()) {
            return "redirect:/";
        } else {
            return "anmelden";
        }
    }

    @PostMapping("/anmelden")
    public String loginPage(Model model, Person person) {
        if (isLoggedIn()) {
            return "redirect:/";
        }

        if (isLoginValid(person.getTumId(), person.getPassword())) {
            login(person.getTumId());
            return "redirect:/";
        } else {
            model.addAttribute("wrongInput", true);
            return "anmelden";
        }
    }

    @GetMapping("/abmelden")
    public String logoutPage() {
        logout();
        return "redirect:/anmelden";
    }

    @GetMapping("/registrieren")
    public String registrationPage() {
        if (isLoggedIn()) {
            return "redirect:/";
        } else {
            return "registrieren";
        }
    }

    @PostMapping("/registrieren")
    public String registrationPage(Model model, Person person) {
        if (isLoggedIn()) {
            return "redirect:/";
        }

        Status status = registerPerson(person);
        if (status == SUCCESS) {
            login(person.getTumId());
            return "redirect:/";
        } else {
            model.addAttribute("registrationFailed", true);
            model.addAttribute("errorMessage", status.getErrorMessage());
            return "registrieren";
        }
    }

    public Status registerPerson(Person person) {
        if (isTumIDInvalid(person.getTumId())) {
            return ERROR("Tum ID ist ungültig");
        }

        DatabaseFacade db = new SqliteFacade();
        if (db.getPerson(person.getTumId()) != null) {
            return ERROR("Account für " + person.getTumId() + " existiert bereits");
        }

        String hashedPassword = BCrypt.hashpw(person.getPassword(), BCrypt.gensalt());
        person.setPassword(hashedPassword);

        db.addPerson(person);

        createDefaultProfilePicture(person.getTumId());

        return SUCCESS;
    }

    private void createDefaultProfilePicture(String tumId) {
        try {
            Path copied = Paths.get(PROFILE_PICTURE_LOCATION + "/" + tumId + ".png");
            Path defaultProfile = Paths.get(PROFILE_PICTURE_LOCATION + "/default.png");
            Files.copy(defaultProfile, copied, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isLoginValid(String tumId, String password) {
        if (isTumIDInvalid(tumId)) {
            return false;
        }

        DatabaseFacade db = new SqliteFacade();
        Person person = db.getPerson(tumId);
        if (person != null) {
            return BCrypt.checkpw(password, person.getPassword());
        } else {
            return false;
        }
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