package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.model.ChatMessage;
import eist.tum_social.tum_social.model.Person;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static eist.tum_social.tum_social.controllers.AuthenticationController.isLoggedIn;
import static eist.tum_social.tum_social.controllers.util.DateUtils.formatTimestamp;
import static eist.tum_social.tum_social.controllers.util.Util.getCurrentPerson;

@RestController
public class ChatRestController {

    @PostMapping("/messages/{tumId}")
    public List<Map<String, String>> getMessages(@PathVariable String tumId) {
        if (!isLoggedIn()) {
            return null;
        }

        Storage storage = new Storage();
        Person person = getCurrentPerson(storage);
        Person otherPerson = storage.getPerson(tumId);
        List<ChatMessage> messages = storage.getChatMessages(person.getId(), otherPerson.getId());

        List<Map<String, String>> result = new ArrayList<>();

        for (ChatMessage message : messages) {
            result.add(Map.of(
                    "reciever", message.getReceiver().getTumId(),
                    "sender", message.getSender().getTumId(),
                    "message", message.getMessage(),
                    "time", formatTimestamp(message.getDate(), message.getTime())
            ));
        }
        return result;
    }

    @PostMapping("/addMessage/{tumId}")
    public Map<String, String> addMessage(@PathVariable String tumId, @RequestParam String text) {
        if (!isLoggedIn()) {
            return null;
        }

        Storage storage = new Storage();
        Person person = getCurrentPerson(storage);
        Person otherPerson = storage.getPerson(tumId);

        ChatMessage chatMessage = new ChatMessage();

        chatMessage.setTime(LocalTime.now());
        chatMessage.setDate(LocalDate.now());
        chatMessage.setMessage(text);
        chatMessage.setSender(person);
        chatMessage.setReceiver(otherPerson);

        storage.update(chatMessage);

        return Map.of(
                "message", text,
                "time", formatTimestamp(chatMessage.getDate(), chatMessage.getTime())
        );
    }
}
