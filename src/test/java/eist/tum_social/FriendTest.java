package eist.tum_social;

import eist.tum_social.tum_social.model.Person;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

import static eist.tum_social.tum_social.controllers.AuthenticationController.isLoggedIn;
import static eist.tum_social.tum_social.controllers.util.NotificationType.*;
import static eist.tum_social.tum_social.controllers.util.Util.getCurrentPerson;

public class FriendTest extends SessionBasedTest {
    @Disabled
    @Test
    void friendsPageTest() {
        // TODO
    }
    @Disabled
    @Test
    void createFriendRequestTest() {
        // TODO
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
