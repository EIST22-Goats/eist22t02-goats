package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.model.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static eist.tum_social.tum_social.controllers.util.DateUtils.formatTimestamp;
import static eist.tum_social.tum_social.controllers.util.Util.getCurrentPerson;

@RestController
public class CommentController {

    @PostMapping("getAnnouncements/{courseId}")
    public List<Map<String, String>> getAnnouncements(@PathVariable int courseId) {

        Storage storage = new Storage();
        Course course = storage.getCourse(courseId);
        List<Announcement> announcements = course.getAnnouncements();

        List<Map<String, String>> result = new ArrayList<>();

        for (Announcement announcement: announcements) {
            result.add(Map.of(
                    "title", announcement.getTitle(),
                    "description", announcement.getDescription(),
                    "date", formatTimestamp(announcement.getDate(), announcement.getTime())
            ));
        }
        return result;
    }

    @PostMapping("getRootComments/{announcementId}")
    public List<Map<String, String>> getRootComments(@PathVariable int announcementId){
        Storage storage = new Storage();
        Announcement announcement = storage.getAnnouncement(announcementId);
        List<Comment> comments = announcement.getComments();

        List<Map<String, String>> result = new ArrayList<>();

        Person person = getCurrentPerson(storage);

        for (Comment comment:comments) {
            result.add(Map.of(
                    "commentId", String.valueOf(comment.getId()),
                    "text", comment.getText(),
                    "authorTumId", comment.getAuthor().getTumId(),
                    "authorFirstname", comment.getAuthor().getFirstname(),
                    "authorLastname", comment.getAuthor().getLastname(),
                    "date", formatTimestamp(comment.getDate(), comment.getTime()),
                    "likes", String.valueOf(comment.getLikes().size()),
                    "liked", String.valueOf(comment.getLikes().contains(person)),
                    "deletable", String.valueOf(comment.getAuthor().equals(person)),
                    "numComments", String.valueOf(comment.getChildComments().size())
            ));
        }
        return result;
    }

    @PostMapping("getComments/{commentId}")
    public List<Map<String, String>> getComments(@PathVariable int commentId){
        Storage storage = new Storage();
        Comment parentComment = storage.getComment(commentId);
        List<Comment> comments = parentComment.getChildComments();

        List<Map<String, String>> result = new ArrayList<>();

        Person person = getCurrentPerson(storage);

        for (Comment comment:comments) {
            result.add(Map.of(
                    "commentId", String.valueOf(comment.getId()),
                    "text", comment.getText(),
                    "authorTumId", comment.getAuthor().getTumId(),
                    "authorFirstname", comment.getAuthor().getFirstname(),
                    "authorLastname", comment.getAuthor().getLastname(),
                    "date", formatTimestamp(comment.getDate(), comment.getTime()),
                    "likes", String.valueOf(comment.getLikes().size()),
                    "liked", String.valueOf(comment.getLikes().contains(person)),
                    "deletable", String.valueOf(comment.getAuthor().equals(person)),
                    "numComments", String.valueOf(comment.getChildComments().size())
            ));
        }
        return result;
    }

    @PostMapping("/NaddComment/{parentId}")
    public void addComment(@PathVariable int parentId, String text) {
        Storage storage = new Storage();

        Person person = getCurrentPerson(storage);

        Comment parentComment = storage.getComment(parentId);

        Comment comment = createComment(text);
        comment.setAuthor(person);

        storage.update(comment);

        parentComment.getChildComments().add(comment);

        storage.update(parentComment);

    }

    @PostMapping("/NaddRootComment/{announcementId}")
    public void addRootComment(@PathVariable int announcementId, String text) {
        Storage storage = new Storage();

        Person person = getCurrentPerson(storage);

        Announcement announcement = storage.getAnnouncement(announcementId);

        Comment comment = createComment(text);
        comment.setAuthor(person);

        storage.update(comment);

        announcement.getComments().add(comment);

        storage.update(announcement);
    }

    @PostMapping("/NdeleteComment/{commentId}")
    public void deleteComment(@PathVariable int commentId) {
        Storage storage = new Storage();
        Comment comment = storage.getComment(commentId);



        if (comment.getAuthor().equals(getCurrentPerson(storage))) {
            deleteCommentTree(comment, storage);
        }
    }

    @GetMapping("/NtoggleLikeComment/{commentId}/{courseId}")
    public void toggleLikeComment(@PathVariable int commentId, @PathVariable("courseId") int courseId) {

    }

    private Comment createComment(String text) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setDate(LocalDate.now());
        comment.setTime(LocalTime.now());
        return comment;
    }

    private void deleteAnnouncement(Announcement announcement, Storage storage) {
        for (Comment comment:announcement.getComments()) {
            deleteCommentTree(comment, storage);
        }
        storage.delete(announcement);
    }
    private void deleteCommentTree(Comment comment, Storage storage) {
        for (Comment childComment:comment.getChildComments()) {
            deleteCommentTree(childComment, storage);
        }
        storage.delete(comment);
    }
}
