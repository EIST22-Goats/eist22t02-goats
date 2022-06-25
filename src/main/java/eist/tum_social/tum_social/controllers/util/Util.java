package eist.tum_social.tum_social.controllers.util;

import eist.tum_social.tum_social.model.Person;
import eist.tum_social.tum_social.datastorage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import static eist.tum_social.tum_social.controllers.AuthenticationController.getCurrentUsersTumId;

@Component
public class Util {

    public static Storage storage;
    @Autowired
    private Util(Storage storage) {
        Util.storage = storage;
    }

    public static void addPersonToModel(Model model) {
        Person person = getCurrentPerson();
        model.addAttribute(person);
    }

    public static Person getCurrentPerson() {
        return storage.getPerson(getCurrentUsersTumId());
    }

}
