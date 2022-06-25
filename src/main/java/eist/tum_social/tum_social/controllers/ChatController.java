package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.model.ChatMessage;
import eist.tum_social.tum_social.model.Notification;
import eist.tum_social.tum_social.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.processing.Messager;

import static eist.tum_social.tum_social.controllers.util.Util.getCurrentPerson;
import static eist.tum_social.tum_social.controllers.util.DateUtils.formatTimestamp;

import java.util.*;
import java.util.stream.Collectors;

import static eist.tum_social.tum_social.controllers.AuthenticationController.isLoggedIn;
import static eist.tum_social.tum_social.controllers.util.Util.addPersonToModel;

@Controller
public class ChatController {

    private final Storage storage;
    public ChatController(@Autowired Storage storage) {
        this.storage = storage;
    }

    @GetMapping(value = {"/chat", "/chat/{tumId}"})
    public String chatPage(Model model, @PathVariable(required = false) String tumId) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Person person = getCurrentPerson();

        List<ChatMessage> messages = storage.getChatMessages(person.getId());

        Map<String, ChatMessage> latestMessages = new HashMap<>();

        for (ChatMessage message : messages) {
            Person otherPerson = (message.getReceiver().equals(person) ? message.getSender() : message.getReceiver());

            ChatMessage currentLastMessage = latestMessages.getOrDefault(otherPerson.getTumId(), null);
            if (currentLastMessage == null ||
                    message.getDate().isAfter(currentLastMessage.getDate()) ||
                    (message.getDate().isEqual(currentLastMessage.getDate()) &&
                    message.getTime().isAfter(currentLastMessage.getTime()))) {
                latestMessages.put(otherPerson.getTumId(), message);
            }
        }

        List<ChatMessage> latestMessagesList = latestMessages.values().stream().sorted(Comparator.comparing(ChatMessage::getDate).thenComparing(ChatMessage::getTime).reversed()).toList();

        List<Map<String, String>> currentChats = new ArrayList<>();

        List<Person> friends = person.getFriends();

        for (ChatMessage message : latestMessagesList) {
            Person otherPerson = (message.getReceiver().equals(person) ? message.getSender() : message.getReceiver());
            friends.remove(otherPerson);

            currentChats.add(Map.of(
                    "name", otherPerson.getFirstname() + " " + otherPerson.getLastname(),
                    "tumId", otherPerson.getTumId(),
                    "time", formatTimestamp(message.getDate(), message.getTime())
            ));
        }

        for (Person friend : friends) {
            currentChats.add(Map.of(
                    "name", friend.getFirstname() + " " + friend.getLastname(),
                    "tumId", friend.getTumId(),
                    "time", "Fange einen neuen Chat an"
            ));
        }

        String currentName;

        if (tumId == null) {
            currentName = currentChats.get(0).get("name");
            tumId = currentChats.get(0).get("tumId");
        } else {
            Person otherPerson = storage.getPerson(tumId);
            if (otherPerson == null) {
                return "redirect:/chat";
            }

            currentName = otherPerson.getFirstname()+" "+otherPerson.getLastname();
        }

        model.addAttribute("person", person);
        model.addAttribute("currentChats", currentChats);

        model.addAttribute("currentName", currentName);
        model.addAttribute("currentTumId", tumId);

        return "chat";
    }
}
