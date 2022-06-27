package eist.tum_social;

import eist.tum_social.tum_social.controllers.NotificationController;
import eist.tum_social.tum_social.controllers.util.NotificationType;
import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.model.Person;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static eist.tum_social.Util.getStorage;
import static eist.tum_social.tum_social.controllers.AuthenticationController.login;
import static eist.tum_social.tum_social.controllers.util.Util.getCurrentPerson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class NotificationTest extends SessionBasedTest  {

    @Test
    void getNotificationsNotLoggedInTest() {
        NotificationController notificationController = new NotificationController(getStorage());
        List<Map<String, String>> data = notificationController.getNotifications();
        assertEquals(0, data.size());
    }

    @Test
    void getNotificationsLoggedInEmptyTest() {
        login("ge95bit");
        NotificationController notificationController = new NotificationController(getStorage());
        List<Map<String, String>> data = notificationController.getNotifications();
        assertEquals(0, data.size());
    }

    @Test
    void getNotificationsLoggedInTest() {
        login("ge47son");
        NotificationController notificationController = new NotificationController(getStorage());
        List<Map<String, String>> data = notificationController.getNotifications();
        assertEquals(2, data.size());
        data = data.stream().sorted(Comparator.comparing(e -> e.get("title"))).toList();
        assertEquals(Map.of(
                "title", "Test Notification 1",
                "description", "the first test notification",
                "link", "/chat/ge95bit",
                "date", data.get(0).get("date")
        ), data.get(0));
        assertEquals(Map.of(
                "title", "Test Notification 2",
                "description", "the second test notification",
                "link", "/friends",
                "date", data.get(1).get("date")
        ), data.get(1));
    }

    @Test
    public void clearNotificationsTest() {
        login("ge47son");
        NotificationController notificationController = new NotificationController(getStorage());
        List<Map<String, String>> data = notificationController.getNotifications();
        assertNotEquals(0, data.size());
        notificationController.clearNotifications();
        data = notificationController.getNotifications();
        assertEquals(0, data.size());
    }

    @Test
    public void sendNotificationTest() {
        login("ge47son");
        Storage storage = getStorage();
        NotificationController notificationController = new NotificationController(storage);
        Person receiver = getCurrentPerson();
        notificationController.sendNotification(
                receiver,
                "Test Notification",
                "the test notification",
                "/chat/ge95bit",
                NotificationType.NEW_FRIEND_REQUEST
                );
        List<Map<String, String>> data = notificationController.getNotifications();
        assertEquals(3, data.size());
        data = data.stream().sorted(Comparator.comparing(e -> e.get("title"))).toList();
        assertEquals(Map.of(
                "title", "Test Notification",
                "description", "the test notification",
                "link", "/chat/ge95bit",
                "date", data.get(0).get("date")
        ), data.get(0));
    }
}
