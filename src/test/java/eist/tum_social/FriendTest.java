package eist.tum_social;

import eist.tum_social.TestClasses.TestModel;
import eist.tum_social.tum_social.controllers.FriendController;
import eist.tum_social.tum_social.controllers.NotificationController;
import eist.tum_social.tum_social.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static eist.tum_social.Util.getStorage;
import static eist.tum_social.tum_social.controllers.AuthenticationController.login;
import static eist.tum_social.tum_social.controllers.AuthenticationController.logout;
import static eist.tum_social.tum_social.controllers.util.Util.getCurrentPerson;
import static org.junit.jupiter.api.Assertions.*;

public class FriendTest extends SessionBasedTest {

    private FriendController friendController;

    @BeforeEach
    void setupFriendController() {
        NotificationController notificationController = new NotificationController(getStorage());
        friendController = new FriendController(getStorage(), notificationController);
    }

    @Test
    void friendsPageTest() {
        login("ge47son");
        TestModel testModel = new TestModel();
        Person person = getCurrentPerson();
        assertEquals("friends", friendController.friendsPage(testModel));
        assertEquals(person, testModel.getAttribute("person"));
        assertEquals(person.getFriends(), testModel.getAttribute("friends"));
        assertEquals(person.getIncomingFriendRequests(), testModel.getAttribute("incomingFriendRequests"));
        assertEquals(person.getOutgoingFriendRequests(), testModel.getAttribute("outgoingFriendRequests"));
    }

    @Test
    void createFriendRequestTest() {
        login("ge47son");
        Person person = getCurrentPerson();
        assertEquals("redirect:/friends", friendController.createFriendRequest("go47tum"));
        assertTrue(person.getOutgoingFriendRequests().contains(getStorage().getPerson("go47tum")));
    }

    @Test
    void createFriendRequestToSelfTest() {
        login("ge47son");
        Person person = getCurrentPerson();
        assertEquals("redirect:/friends", friendController.createFriendRequest("ge47son"));
        assertFalse(person.getOutgoingFriendRequests().contains(getStorage().getPerson("ge47son")));
    }

    @Test
    void createFriendRequestToFriendTest() {
        login("ge47son");
        Person person = getCurrentPerson();
        assertEquals("redirect:/friends", friendController.createFriendRequest("ge95bit"));
        assertFalse(person.getOutgoingFriendRequests().contains(getStorage().getPerson("ge95bit")));
    }

    @Test
    void deleteFriendRequestTest() {
        login("ge47son");
        friendController.createFriendRequest("go47tum");
        Person person = getCurrentPerson();
        assertTrue(person.getOutgoingFriendRequests().contains(getStorage().getPerson("go47tum")));
        assertEquals("redirect:/friends", friendController.deleteFriendRequest(14));
        person.getOutgoingFriendRequestEntities().set(null);
        assertFalse(person.getOutgoingFriendRequests().contains(getStorage().getPerson("go47tum")));
    }

    @Test
    void acceptFriendRequestTest() {
        login("ge47son");
        friendController.createFriendRequest("go47tum");
        logout();
        login("go47tum");
        assertEquals("redirect:/friends", friendController.acceptFriendRequest(3));
        Person person = getCurrentPerson();
        assertTrue(person.getFriends().contains(getStorage().getPerson("ge47son")));
    }
}
