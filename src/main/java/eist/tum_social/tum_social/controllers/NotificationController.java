package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.model.Notification;
import eist.tum_social.tum_social.model.Person;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Array;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static eist.tum_social.tum_social.controllers.AuthenticationController.isLoggedIn;
import static eist.tum_social.tum_social.controllers.util.Util.getCurrentPerson;

@RestController
public class NotificationController {

    @PostMapping("/getNotifications")
    public List<Map<String, String>> getNotifications() {
        if (!isLoggedIn()) {
            return new ArrayList<>();
        }

        Storage storage = new Storage();
        Person currentPerson = getCurrentPerson(storage);
        List<Notification> notifications = currentPerson.getNotifications();
        notifications = notifications.stream()
                .sorted(Comparator
                        .comparing(Notification::getDay)
                        .thenComparing(Notification::getTime))
                .toList();
        
        List<Map<String, String>> result = new ArrayList<>();
        for (Notification notification:notifications) {
            result.add(Map.of(
                    "title", notification.getTitle(),
                    "description", notification.getDescription()
            ));
        }
        return result;
    }

}
