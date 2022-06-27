package eist.tum_social;

import eist.tum_social.TestClasses.TestModel;
import eist.tum_social.tum_social.controllers.ChatController;
import eist.tum_social.tum_social.controllers.ChatRestController;
import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.model.Person;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static eist.tum_social.Util.getStorage;
import static eist.tum_social.tum_social.controllers.AuthenticationController.login;
import static eist.tum_social.tum_social.controllers.util.Util.getCurrentPerson;
import static org.junit.jupiter.api.Assertions.*;

public class ChatTest extends SessionBasedTest {

    @SuppressWarnings("unckecked")
    @Test
    void chatPageTest() {
        login("ge47son");
        ChatController chatController = new ChatController(getStorage());
        TestModel model = new TestModel();
        Person person = getCurrentPerson();
        assertEquals("chat", chatController.chatPage(model, "ge95bit"));

        assertEquals(person, model.getAttribute("person"));
        List<Map<String, String>> currentChats = (List<Map<String, String>>) model.getAttribute("currentChats");
        assertNotNull(currentChats);
        assertEquals(1, currentChats.size());
        currentChats = currentChats.stream().sorted(Comparator.comparing(e -> e.get("tumId"))).collect(java.util.stream.Collectors.toList());
        assertEquals(Map.of(
                "tumId", "ge95bit",
                "name", "Kilian Northoff",
                "time", currentChats.get(0).get("time")
                ),
            currentChats.get(0));

        assertEquals("ge95bit", model.getAttribute("currentTumId"));
        assertEquals("Kilian Northoff", model.getAttribute("currentName"));
    }

    @Test
    void getMessagesTest() {
        login("ge47son");
        Storage storage = getStorage();
        ChatRestController chatRestController = new ChatRestController(storage);
        List<Map<String, String>> messages = chatRestController.getMessages("ge95bit");
        assertEquals(40, messages.size());
    }
    @Test
    void getMessagesEmptyTest() {
        login("ge47son");
        Storage storage = getStorage();
        ChatRestController chatRestController = new ChatRestController(storage);
        List<Map<String, String>> messages = chatRestController.getMessages("go47tum");
        assertEquals(0, messages.size());
    }

    @Test
    void getMessagesNotLoggedInTest() {
        Storage storage = getStorage();
        ChatRestController chatRestController = new ChatRestController(storage);
        List<Map<String, String>> messages = chatRestController.getMessages("should not matter");
        assertNull(messages);
    }
    @Test
    void addMessageInitialTest() {
        login("ge47son");
        Storage storage = getStorage();
        ChatRestController chatRestController = new ChatRestController(storage);
        chatRestController.addMessage("go47tum", "hello world");
        List<Map<String, String>> messages = chatRestController.getMessages("go47tum");
        assertEquals(1, messages.size());
        assertEquals(Map.of(
                "id", messages.get(0).get("id"),
                "sender", "ge47son",
                "receiver", "go47tum",
                "message", "hello world",
                "time", messages.get(0).get("time")
        ), messages.get(0));
    }

    @Test
    void addMessageTest() {
        login("ge47son");
        Storage storage = getStorage();
        ChatRestController chatRestController = new ChatRestController(storage);
        chatRestController.addMessage("ge95bit", "hello world 2");
        List<Map<String, String>> messages = chatRestController.getMessages("ge95bit");
        messages = messages.stream().sorted(Comparator.comparing(a -> a.get("id"))).toList();
        assertEquals(41, messages.size());
        assertEquals(Map.of(
                "id", messages.get(40).get("id"),
                "sender", "ge47son",
                "receiver", "ge95bit",
                "message", "hello world 2",
                "time", messages.get(40).get("time")
        ), messages.get(40));
    }
    @Test
    void addMessageNotLoggedInTest() {
        Storage storage = getStorage();
        ChatRestController chatRestController = new ChatRestController(storage);
        chatRestController.addMessage("ge95bit", "hello world 3");
        assertEquals(40, storage.getChatMessages(17).size());
    }

}
