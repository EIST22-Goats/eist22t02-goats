package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.model.ChatMessage;
import eist.tum_social.tum_social.model.Person;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static eist.tum_social.tum_social.controllers.util.DateUtils.formatTimestamp;
import static eist.tum_social.tum_social.controllers.util.Util.getCurrentPerson;

@RestController
public class ChatRestController {

    @PostMapping("/chat/{tumId}")
    public List<Map<String, String>> getNotifications(@PathVariable String tumId) {
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

//    @PostMapping("/currentChats")
//    public List<Map<String, String>> getCurrentChats() {
//        Storage storage = new Storage();
//
//        Person person = getCurrentPerson(storage);
//
//        List<ChatMessage> messages = storage.getChatMessages(person.getId());
//
//
//        List<Map<String, String>> result = new ArrayList<>();
//
//        for (ChatMessage message : messages) {
//
//            Person otherPerson = (message.getReceiver().equals(person) ? message.getReceiver() : message.getSender());
//
//            result.add(Map.of(
//                    "name", otherPerson.getFirstname(),
//                    "tumId", otherPerson.getTumId(),
//                    "time", formatTimestamp(message.getDate(), message.getTime())
//            ));
//        }
//        return result;
//    }
}
