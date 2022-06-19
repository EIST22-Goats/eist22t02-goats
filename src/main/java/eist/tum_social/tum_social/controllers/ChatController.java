package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.model.ChatMessage;
import eist.tum_social.tum_social.model.Notification;
import eist.tum_social.tum_social.model.Person;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/chat")
    public String chatPage(Model model) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Storage storage = new Storage();

        Person person = getCurrentPerson(storage);

        List<ChatMessage> messages = storage.getChatMessages(person.getId());

        Map<String, ChatMessage> latestMessages = new HashMap<>();

        for (ChatMessage message : messages) {
            Person otherPerson = (message.getReceiver().equals(person) ? message.getReceiver() : message.getSender());

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

        for (ChatMessage message : latestMessagesList) {
            Person otherPerson = (message.getReceiver().equals(person) ? message.getReceiver() : message.getSender());

            currentChats.add(Map.of(
                    "name", otherPerson.getFirstname() + " " + otherPerson.getLastname(),
                    "tumId", otherPerson.getTumId(),
                    "time", formatTimestamp(message.getDate(), message.getTime())
            ));
        }

        model.addAttribute("person", person);
        model.addAttribute("currentChats", currentChats);
        model.addAttribute("currentChat", currentChats.get(0));

        return "chat";
    }
}
