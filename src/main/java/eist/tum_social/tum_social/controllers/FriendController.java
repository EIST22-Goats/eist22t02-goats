package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.model.Person;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import static eist.tum_social.tum_social.controllers.AuthenticationController.isLoggedIn;
import static eist.tum_social.tum_social.controllers.util.Util.addPersonToModel;
import static eist.tum_social.tum_social.controllers.util.Util.getCurrentPerson;

@Controller
public class FriendController {

    @GetMapping("/friends")
    public String friendsPage(Model model) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Person person = getCurrentPerson();
        List<Person> friends = person.getFriends();
        List<Person> outgoingFriendRequests = person.getOutgoingFriendRequests();
        List<Person> incomingFriendRequests = person.getIncomingFriendRequests();

        model.addAttribute("person", person);
        model.addAttribute("friends", friends);
        model.addAttribute("incomingFriendRequests", incomingFriendRequests);
        model.addAttribute("outgoingFriendRequests", outgoingFriendRequests);

        return "friends";
    }

    @PostMapping("/createFriendRequest")
    public String createFriendRequest() {
        return "";
    }

    @PostMapping("/deleteFriendRequest")
    public String deleteFriendRequest() {
        return "";
    }

}
