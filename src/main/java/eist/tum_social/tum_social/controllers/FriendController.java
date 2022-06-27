package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static eist.tum_social.tum_social.controllers.AuthenticationController.isLoggedIn;
import static eist.tum_social.tum_social.controllers.util.NotificationType.*;
import static eist.tum_social.tum_social.controllers.util.Util.getCurrentPerson;

@Controller
public class FriendController {
    private final Storage storage;

    private final NotificationController notificationController;
    public FriendController(@Autowired Storage storage, @Autowired NotificationController notificationController) {
        this.storage = storage;
        this.notificationController = notificationController;
    }
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

    @PostMapping("/createFriendRequest/{tumId}")
    public String createFriendRequest(@PathVariable String tumId) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Person person = getCurrentPerson();
        Person receiver = storage.getPerson(tumId);

        if (!receiver.getTumId().equals(person.getTumId()) &&
                !person.getFriends().contains(receiver) &&
                !person.getOutgoingFriendRequests().contains(receiver)
        ) {
            person.getOutgoingFriendRequests().add(receiver);
            storage.update(person);

            notificationController.sendNotification(
                    receiver,
                    "Neue Freundschaftsanfrage",
                    person.getFirstname() + " " + person.getLastname() + " hat dir eine Freundschaftsanfrage gesendet",
                    "/friends",
                    NEW_FRIEND_REQUEST);
        }

        return "redirect:/friends";
    }

    @PostMapping("/deleteFriendRequest/{otherId}")
    public String deleteFriendRequest(@PathVariable int otherId) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Person person = getCurrentPerson();
        List<Person> outgoingFriendRequests = person.getOutgoingFriendRequests();
        List<Person> incomingFriendRequests = person.getIncomingFriendRequests();
        Optional<Person> outgoing = outgoingFriendRequests.stream().filter(p -> p.getId() == otherId).findFirst();
        Optional<Person> incoming = incomingFriendRequests.stream().filter(p -> p.getId() == otherId).findFirst();

        if (outgoing.isPresent()) {
            outgoingFriendRequests.remove(outgoing.get());
        } else if (incoming.isPresent()) {
            notificationController.sendNotification(
                    incoming.get(),
                    "Freundschaftsanfrage abgelehnt",
                    person.getFirstname() + " " + person.getLastname() + " hat deine Freundschaftsanfrage abgelehnt",
                    "/friends",
                    FRIEND_REQUEST_DENIED);
            incomingFriendRequests.remove(incoming.get());
        }

        storage.update(person);

        return "redirect:/friends";
    }

    @PostMapping("/acceptFriendRequest/{otherId}")
    public String acceptFriendRequest(@PathVariable int otherId) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Person person = getCurrentPerson();
        List<Person> incomingFriendRequests = person.getIncomingFriendRequests();
        Optional<Person> incoming = incomingFriendRequests.stream().filter(p -> p.getId() == otherId).findFirst();

        if (incoming.isPresent()) {
            incomingFriendRequests.remove(incoming.get());
            person.getFriends().add(incoming.get());

            notificationController.sendNotification(
                    incoming.get(),
                    "Freundschaftsanfrage angenommen",
                    person.getFirstname() + " " + person.getLastname() + " hat deine Freundschaftsanfrage angenommen!",
                    "/friends",
                    FRIEND_REQUEST_ACCEPTED);
        }

        storage.update(person);

        return "redirect:/friends";
    }

    @PostMapping("/bringBalanceToYourLife")
    public String bringBalanceToYourLife() {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Person person = getCurrentPerson();
        List<Person> friends = person.getFriends();
        int c = (friends.size() + 1) / 2;
        for (int i = 0; i < c; i++) {
            int index = (int) Math.round(Math.random() * (friends.size() - 1));
            friends.remove(friends.get(index));
        }

        storage.update(person);

        return "redirect:/friends";
    }

}
