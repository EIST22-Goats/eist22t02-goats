package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.data_storage.StorageFacade;
import eist.tum_social.tum_social.data_storage.Storage;
import eist.tum_social.tum_social.model.Person;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static eist.tum_social.tum_social.controllers.AuthenticationController.getCurrentUsersTumId;
import static eist.tum_social.tum_social.controllers.AuthenticationController.isLoggedIn;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(Model model) {
        if (!isLoggedIn()) {
            return "redirect:/anmelden";
        }

        StorageFacade db = new Storage();
        Person person = db.getPerson(getCurrentUsersTumId());
        model.addAttribute("person", person);

        return "index";

    }

}
