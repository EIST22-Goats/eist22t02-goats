package eist.tum_social;

import eist.tum_social.tum_social.controllers.CommentController;
import eist.tum_social.tum_social.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static eist.tum_social.Util.getStorage;
import static eist.tum_social.tum_social.controllers.AuthenticationController.login;
import static org.junit.jupiter.api.Assertions.*;

public class CommentTest extends SessionBasedTest {

    private CommentController commentController;

    @BeforeEach
    void setupCommentController() {
        commentController = new CommentController(getStorage());
    }

    @Test
    void getRootCommentsEmptyTest(){
        List<Map<String, String>> data = commentController.getRootComments(15);
        assertEquals(0, data.size());
    }
    @Test
    void getRootCommentsTest() {
        login("ge47son");
        List<Map<String, String>> data = commentController.getRootComments(11);
        assertEquals(3, data.size());
        data = data.stream().sorted(Comparator.comparing(a -> a.get("commentId"))).toList();
        assertEquals(Map.of(
            "commentId", "82",
            "text", "uwu",
            "authorTumId", "ge95bit",
            "authorFirstname", "Kilian",
            "authorLastname", "Northoff",
            "date", data.get(0).get("date"),
            "likes", "2",
            "liked", "true",
            "deletable", "false",
            "numComments", "1"
        ), data.get(0));
        assertEquals(Map.of(
            "commentId", "86",
            "text", "test root comment 2",
            "authorTumId", "ge95bit",
            "authorFirstname", "Kilian",
            "authorLastname", "Northoff",
            "date", data.get(1).get("date"),
            "likes", "3",
            "liked", "true",
            "deletable", "false",
            "numComments", "0"
        ), data.get(1));
        assertEquals(Map.of(
            "commentId", "87",
            "text", "test root comment 3",
            "authorTumId", "ge47son",
            "authorFirstname", "Florian",
            "authorLastname", "Adam",
            "date", data.get(2).get("date"),
            "likes", "0",
            "liked", "false",
            "deletable", "true",
            "numComments", "3"
        ), data.get(2));
    }
    @Test
    void addRootCommentTest() {
        login("ge47son");
        commentController.addRootComment(11, "test root comment 4");
        List<Map<String, String>> data = commentController.getRootComments(11);
        assertEquals(4, data.size());
        data = data.stream().sorted(Comparator.comparing(a -> a.get("commentId"))).toList();
        assertEquals(
            Map.of(
                "commentId", "92",
                "text", "test root comment 4",
                "authorTumId", "ge47son",
                "authorFirstname", "Florian",
                "authorLastname", "Adam",
                "date", data.get(3).get("date"),
                "likes", "0",
                "liked", "false",
                "deletable", "true",
                "numComments", "0"
            ), data.get(3)
        );
    }

    @Test
    void getCommentsTest(){
        login("ge47son");
        List<Map<String, String>> data = commentController.getComments(87);
        assertEquals(3, data.size());
        data = data.stream().sorted(Comparator.comparing(a -> a.get("commentId"))).toList();
        assertEquals(Map.of(
            "commentId", "88",
            "text", "test comment 1",
            "authorTumId", "go47tum",
            "authorFirstname", "Willi",
            "authorLastname", "Adam",
            "date", data.get(0).get("date"),
            "likes", "0",
            "liked", "false",
            "deletable", "false",
            "numComments", "0"
        ), data.get(0));
        assertEquals(Map.of(
            "commentId", "89",
            "text", "test comment 2",
            "authorTumId", "ge95bit",
            "authorFirstname", "Kilian",
            "authorLastname", "Northoff",
            "date", data.get(1).get("date"),
            "likes", "0",
            "liked", "false",
            "deletable", "false",
            "numComments", "1"
        ), data.get(1));
        assertEquals(Map.of(
            "commentId", "90",
            "text", "test comment 3",
            "authorTumId", "ge47son",
            "authorFirstname", "Florian",
            "authorLastname", "Adam",
            "date", data.get(2).get("date"),
            "likes", "0",
            "liked", "false",
            "deletable", "true",
            "numComments", "0"
        ), data.get(2));
        data = commentController.getComments(89);
        assertEquals(1, data.size());
        assertEquals(Map.of(
            "commentId", "91",
            "text", "test comment 1.1",
            "authorTumId", "ge95bit",
            "authorFirstname", "Kilian",
            "authorLastname", "Northoff",
            "date", data.get(0).get("date"),
            "likes", "0",
            "liked", "false",
            "deletable", "false",
            "numComments", "0"
            ), data.get(0));
    }
    @Test
    void getCommentsEmptyTest(){
        login("ge47son");
        List<Map<String, String>> data = commentController.getComments(90);
        assertEquals(0, data.size());
    }

    @Test
    void addCommentTest() {
        login("ge47son");
        commentController.addComment(89, "test comment 1.2");
        List<Map<String, String>> data = commentController.getComments(89);
        assertEquals(2, data.size());
        data = data.stream().sorted(Comparator.comparing(a -> a.get("commentId"))).toList();
        assertEquals(
                Map.of(
                        "commentId", "92",
                        "text", "test comment 1.2",
                        "authorTumId", "ge47son",
                        "authorFirstname", "Florian",
                        "authorLastname", "Adam",
                        "date", data.get(1).get("date"),
                        "likes", "0",
                        "liked", "false",
                        "deletable", "true",
                        "numComments", "0"
                ), data.get(1)
        );
    }

    @Test
    void deleteRootCommentTest() {
        login("ge47son");
        commentController.deleteComment(87);
        assertNull(getStorage().getComment(87));
        assertNull(getStorage().getComment(88));
        assertNull(getStorage().getComment(89));
        assertNull(getStorage().getComment(90));
        assertNull(getStorage().getComment(91));
    }

    @Test
    void deleteCommentTest() {
        login("ge95bit");
        commentController.deleteComment(89);
        List<Map<String, String>> data = commentController.getComments(87);
        assertEquals(2, data.size());
        assertNull(getStorage().getComment(89));
        assertNull(getStorage().getComment(91));
    }

    @Test
    void deleteCommentNotPermittedTest() {
        login("ge47son");
        commentController.deleteComment(89);
        List<Map<String, String>> data = commentController.getComments(87);
        assertEquals(3, data.size());
        assertNotNull(getStorage().getComment(89));
        assertNotNull(getStorage().getComment(91));
    }
    @Test
    void toggleLikeCommentLikeTest() {
        login("ge47son");
        Comment comment = getStorage().getComment(87);
        assertEquals(0, comment.getLikes().size());
        commentController.toggleLikeComment(87);
        comment.getLikeEntities().set(null);
        assertEquals(1, comment.getLikes().size());
    }

    @Test
    void toggleLikeCommentUnlikeTest() {
        login("ge47son");
        Comment comment = getStorage().getComment(86);
        assertEquals(3, comment.getLikes().size());
        commentController.toggleLikeComment(86);
        comment.getLikeEntities().set(null);
        assertEquals(2, comment.getLikes().size());
    }
}
