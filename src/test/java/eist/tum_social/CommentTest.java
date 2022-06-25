package eist.tum_social;

import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.model.Announcement;
import eist.tum_social.tum_social.model.Comment;
import eist.tum_social.tum_social.model.Course;
import eist.tum_social.tum_social.model.Person;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static eist.tum_social.tum_social.controllers.util.DateUtils.formatTimestamp;
import static eist.tum_social.tum_social.controllers.util.Util.getCurrentPerson;

public class CommentTest extends SessionBasedTest {
    @Disabled
    @Test
    void getRootComments(){
        // TODO
    }
    @Disabled
    @Test
    void getCommentsTest(){
        // TODO
    }
    @Disabled
    @Test
    void addCommentTest() {
        // TODO
    }
    @Disabled
    @Test
    void addRootCommentTest() {
        // TODO
    }
    @Disabled
    @Test
    void deleteCommentTest() {
        // TODO
    }
    @Disabled
    @Test
    void toggleLikeCommentTest() {
        // TODO
    }
    @Disabled
    @Test
    void createCommentTest() {
        // TODO
    }
}
