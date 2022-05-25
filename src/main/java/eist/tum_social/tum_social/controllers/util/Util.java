package eist.tum_social.tum_social.controllers.util;

import eist.tum_social.tum_social.model.Person;
import eist.tum_social.tum_social.persistent_data_storage.Storage;
import eist.tum_social.tum_social.persistent_data_storage.StorageFacade;
import org.springframework.ui.Model;

import static eist.tum_social.tum_social.controllers.AuthenticationController.getCurrentUsersTumId;

public class Util {

    public static void addPersonToModel(Model model) {
        StorageFacade db = new Storage();
        Person person = db.getPerson(getCurrentUsersTumId());
        model.addAttribute(person);
    }

}
