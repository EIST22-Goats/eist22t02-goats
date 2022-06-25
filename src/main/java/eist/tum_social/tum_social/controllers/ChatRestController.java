package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.model.ChatMessage;
import eist.tum_social.tum_social.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
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
import static eist.tum_social.tum_social.controllers.util.NotificationType.NEW_FRIEND_REQUEST;
import static eist.tum_social.tum_social.controllers.util.NotificationType.NEW_MESSAGE;
import static eist.tum_social.tum_social.controllers.util.Util.getCurrentPerson;

@RestController
public class ChatRestController {

    private final Storage storage;
    public ChatRestController(@Autowired Storage storage) {
        this.storage = storage;
    }

    @PostMapping("/messages/{tumId}")
    public List<Map<String, String>> getMessages(@PathVariable String tumId) {
        if (!isLoggedIn()) {
            return null;
        }

        Person person = getCurrentPerson();
        Person otherPerson = storage.getPerson(tumId);
        List<ChatMessage> messages = storage.getChatMessages(person.getId(), otherPerson.getId());

        List<Map<String, String>> result = new ArrayList<>();

        for (ChatMessage message : messages) {
            result.add(Map.of(
                    "id", String.valueOf(message.getId()),
                    "receiver", message.getReceiver().getTumId(),
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

        Person person = getCurrentPerson();
        Person otherPerson = storage.getPerson(tumId);

/*        NotificationController.sendNotification(
                otherPerson,
                "Nachricht von " + person.getFirstname() + " " + person.getLastname(),
                text,
                "/chat/" + person.getTumId(),
                NEW_MESSAGE
        );
*/
        ChatMessage chatMessage = new ChatMessage();

        chatMessage.setTime(LocalTime.now());
        chatMessage.setDate(LocalDate.now());
        chatMessage.setMessage(text);
        chatMessage.setSender(person);
        chatMessage.setReceiver(otherPerson);

        storage.update(chatMessage);

        return Map.of(
                "id", String.valueOf(chatMessage.getId()),
                "message", text,
                "time", formatTimestamp(chatMessage.getDate(), chatMessage.getTime())
        );
    }
}
