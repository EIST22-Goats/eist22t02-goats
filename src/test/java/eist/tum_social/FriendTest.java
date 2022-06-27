package eist.tum_social;

import eist.tum_social.TestClasses.TestModel;
import eist.tum_social.tum_social.controllers.FriendController;
import eist.tum_social.tum_social.controllers.NotificationController;
import eist.tum_social.tum_social.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

import static eist.tum_social.Util.getStorage;
import static eist.tum_social.tum_social.controllers.AuthenticationController.isLoggedIn;
import static eist.tum_social.tum_social.controllers.AuthenticationController.login;
import static eist.tum_social.tum_social.controllers.util.NotificationType.*;
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

        // TODO: check Notification
        // TODO: check Friend request to Friend or self
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
        System.out.println(person.getFriends());
        assertEquals("redirect:/friends", friendController.createFriendRequest("ge95bit"));
        assertFalse(person.getOutgoingFriendRequests().contains(getStorage().getPerson("ge95bit")));
    }

    @Disabled
    @Test
    void deleteFriendRequestTest() {
        // TODO
    }
    @Disabled
    @Test
    void acceptFriendRequestTest() {
        // TODO
    }
    @Disabled
    @Test
    void bringBalanceToYourLifeTest() {
        // TODO
    }
}
