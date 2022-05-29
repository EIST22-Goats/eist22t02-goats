package eist.tum_social.tum_social.controllers.util;

import eist.tum_social.tum_social.model.Person;
import eist.tum_social.tum_social.persistent_data_storage.Storage;
import org.springframework.ui.Model;

import static eist.tum_social.tum_social.controllers.AuthenticationController.getCurrentUsersTumId;

public class Util {

    public static void addPersonToModel(Model model) {
        Person person = getCurrentPerson();
        model.addAttribute(person);
    }

    public static Person getCurrentPerson() {
        return getCurrentPerson(new Storage());
    }

    public static Person getCurrentPerson(Storage db) {
        return db.getPerson(getCurrentUsersTumId());
    }

}
