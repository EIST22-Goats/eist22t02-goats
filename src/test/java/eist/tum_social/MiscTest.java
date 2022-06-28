package eist.tum_social;

import eist.tum_social.TestClasses.TestModel;
import eist.tum_social.tum_social.controllers.*;
import eist.tum_social.tum_social.controllers.util.Util;
import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.datastorage.util.Pair;
import eist.tum_social.tum_social.model.Announcement;
import eist.tum_social.tum_social.model.Course;
import eist.tum_social.tum_social.model.Person;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static eist.tum_social.Util.getStorage;
import static eist.tum_social.tum_social.controllers.AuthenticationController.login;
import static eist.tum_social.tum_social.controllers.util.Util.getCurrentPerson;
import static org.junit.jupiter.api.Assertions.*;

public class MiscTest extends SessionBasedTest {

    @Test
    void indexTest() {
        login("ge47son");
        Person person = getCurrentPerson();
        TestModel model = new TestModel();
        IndexController indexController = new IndexController(getStorage());
        assertEquals("index",indexController.index(model));
        assertTrue(model.asMap().containsValue(person));
        assertEquals(person.getCourses(), model.getAttribute("myCoursesList"));
        List<Pair<String, Announcement>> announcements = new ArrayList<>();
        for (Course course : person.getCourses()) {
            List<Announcement> courseAnnouncement = course.getAnnouncements();
            if (!courseAnnouncement.isEmpty()) {
                announcements.add(new Pair<>(course.getName(), courseAnnouncement.get(0)));
            }
        }
        assertEquals(announcements, model.getAttribute("myAnnouncements"));
    }

    @Test
    void getIframeUrlNothingTest() {
        LocationController locationController = new LocationController();
        assertTrue(locationController.getIframeUrl(null, null).isEmpty());
    }

    @Test
    void getIframeUrlRoomTest() {
        LocationController locationController = new LocationController();
        assertEquals(Map.of(
                "roomImageUrl", "https://nav.tum.sexy/cdn/maps/roomfinder/rf91.webp",
                "markerY", "61.65289256198348",
                "markerX", "49.152542372881356",
                "roomId", "5611.EG.038",
                "iframeUrl", "https://www.openstreetmap.org/export/embed.html?"+
                                "bbox=11.667287010131773%2C48.262924450158145%2C11.667289010131771%2C48.26292645015814&amp&layer=mapnik&"+
                                "marker=48.26292545015814%2C11.667288010131772"
                ),
                locationController.getIframeUrl(null, "00.11.038"));
    }

    @Test
    void getIframeUrlLocationTest() {
        LocationController locationController = new LocationController();
        assertEquals(Map.of(
                "iframeUrl", "https://www.openstreetmap.org/export/embed.html?"+
                                "bbox=11.5656093%2C48.1477055%2C11.5699075%2C48.1514056&amp&layer=mapnik&"+
                                "marker=48.149554550000005%2C11.567753141783761"
                ),
                locationController.getIframeUrl("Arcisstraße 21", null));
    }

    @Test
    void getIframeUrlBothTest() {
        LocationController locationController = new LocationController();
        assertEquals(Map.of(
                        "iframeUrl", "https://www.openstreetmap.org/export/embed.html?"+
                                "bbox=11.5656093%2C48.1477055%2C11.5699075%2C48.1514056&amp&layer=mapnik&"+
                                "marker=48.149554550000005%2C11.567753141783761"
                ),
                locationController.getIframeUrl("Arcisstraße 21", "00.11.038"));
    }

    @Test
    void searchPersonsExistingTest() {
        login("ge47son");
        Storage storage = getStorage();
        Util.storage = storage;
        PersonSearchController personSearchController = new PersonSearchController(storage);
        List<Map<String, String>> data = personSearchController.searchPersons("Flo");
        assertFalse(data.isEmpty());
        assertEquals(Map.of(
                "firstname", "Willi",
                "id", "14",
                "lastname", "Adam",
                "tumId", "go47tum"), data.get(0));
    }

    @Test
    void searchPersonsMisspelledTest() {
        login("ge47son");
        Storage storage = getStorage();
        Util.storage = storage;
        PersonSearchController personSearchController = new PersonSearchController(storage);
        List<Map<String, String>> data = personSearchController.searchPersons("Wli");
        assertFalse(data.isEmpty());
        assertEquals(Map.of(
                "firstname", "Willi",
                "id", "14",
                "lastname", "Adam",
                "tumId", "go47tum"), data.get(0));
    }

    @Test
    void searchPersonsFriendTest() {
        login("ge47son");
        Storage storage = getStorage();
        Util.storage = storage;
        PersonSearchController personSearchController = new PersonSearchController(storage);
        List<Map<String, String>> data = personSearchController.searchPersons("Klillian");
        assertFalse(data.isEmpty());
        for (var entry: data) {
            assertNotEquals(entry.get("tumId"), "ge47son");
            assertNotEquals(entry.get("tumId"), "ge95bit");
        }
    }

    @Test
    void searchPersonsNonExistingTest() {
        login("ge47son");
        Storage storage = getStorage();
        Util.storage = storage;
        PersonSearchController personSearchController = new PersonSearchController(storage);
        List<Map<String, String>> data = personSearchController.searchPersons("Wolfgang");
        assertTrue(data.isEmpty());
    }

    @Test
    void testLoginRedirection() {
        assertEquals("redirect:/login", new FriendController(null, null).friendsPage(null));
        assertEquals("redirect:/login", new FriendController(null, null).createFriendRequest(null));
        assertEquals("redirect:/login", new ChatController(null).chatPage(null, null));
    }

    @Disabled
    @Test
    void testTimestampFormatting() {
        // TODO
    }
}
