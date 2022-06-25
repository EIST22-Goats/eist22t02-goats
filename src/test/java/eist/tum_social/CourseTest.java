package eist.tum_social;

import eist.tum_social.tum_social.controllers.forms.CourseForm;
import eist.tum_social.tum_social.model.Announcement;
import eist.tum_social.tum_social.model.Course;
import eist.tum_social.tum_social.model.Person;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static eist.tum_social.tum_social.controllers.AuthenticationController.isLoggedIn;
import static eist.tum_social.tum_social.controllers.util.Util.addPersonToModel;
import static eist.tum_social.tum_social.controllers.util.Util.getCurrentPerson;

public class CourseTest extends SessionBasedTest {
    @Disabled
    @Test
    void coursesPageTest() {
        // TODO
    }
    @Disabled
    @Test
    void coursePageTest() {
        // TODO
    }
    @Disabled
    @Test
    void createCourseTest() {
        // TODO
    }
    @Disabled
    @Test
    void subscribeCourseTest() {
        // TODO
    }
    @Disabled
    @Test
    void unsubscribeCourseTest() {
        // TODO
    }
    @Disabled
    @Test
    void deleteCourseTest() {
        // TODO
    }
    @Disabled
    @Test
    void updateCourseTest() {
        // TODO
    }
    @Disabled
    @Test
    void addAnnouncementTest() {
        // TODO
    }
    @Disabled
    @Test
    void deleteAnnouncementTest() {
        // TODO
    }
}
