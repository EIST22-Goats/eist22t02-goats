package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.controllers.util.NotificationType;
import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.model.Notification;
import eist.tum_social.tum_social.model.Person;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Array;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
                        .comparing(Notification::getDate)
                        .thenComparing(Notification::getTime))
                .toList();
        
        List<Map<String, String>> result = new ArrayList<>();

        for (Notification notification:notifications) {
            result.add(Map.of(
                "title", notification.getTitle(),
                "description", notification.getDescription(),
                "link", notification.getLink(),
                "date", formatNotificationTimestamp(notification.getDate(), notification.getTime())
            ));
        }
        return result;
    }

    private static String formatNotificationTimestamp(LocalDate date, LocalTime time) {
        String dateString = "";

        if (time.isAfter(LocalTime.now().minusHours(1))) {
            long minDelta = time.until(LocalTime.now(), ChronoUnit.MINUTES);
            if (minDelta <= 1) {
                dateString += "Gerade eben";
            } else {
                dateString += "Vor " + minDelta + " Minuten";
            }
        } else if (time.isAfter(LocalTime.now().minusHours(12))) {
            dateString += "Vor " + time.until(LocalTime.now(), ChronoUnit.HOURS) + " Stunden";
        } else {
            dateString += time.format(DateTimeFormatter.ofPattern("HH:mm"));
            if (date.isEqual(LocalDate.now())) {
                dateString += ", Heute";
            } else if (date.isEqual(LocalDate.now().minusDays(1))) {
                dateString +=  ", Gestern";
            } else {
                dateString += ", " + date.format(DateTimeFormatter.ofPattern("dd.MM."));
            }
            if (LocalDate.now().getYear() != date.getYear()) {
                dateString += date.format(DateTimeFormatter.ofPattern("yyyy"));
            }
        }

        return dateString;
    }

    @PostMapping("/clearNotifications")
    public void clearNotifications() {
        Storage storage = new Storage();
        Person person = getCurrentPerson(storage);

        for (Notification notification:person.getNotifications()) {
            storage.delete(notification);
        }
    }

    public static void sendNotification(Person person, String title, String description, String link, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setDescription(description);
        notification.setDate(LocalDate.now());
        notification.setTime(LocalTime.now());
        notification.setLink(link);

        Storage storage = new Storage();
        storage.update(notification);

        person.getNotifications().add(notification);
        storage.update(person);
    }

}
