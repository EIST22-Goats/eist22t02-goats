package eist.tum_social;

import eist.tum_social.TestClasses.TestModel;
import eist.tum_social.tum_social.controllers.CourseController;
import eist.tum_social.tum_social.controllers.forms.CourseForm;
import eist.tum_social.tum_social.model.Announcement;
import eist.tum_social.tum_social.model.Course;
import eist.tum_social.tum_social.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static eist.tum_social.Util.getStorage;
import static eist.tum_social.tum_social.controllers.AuthenticationController.login;
import static eist.tum_social.tum_social.controllers.util.Util.*;
import static org.junit.jupiter.api.Assertions.*;

public class CourseTest extends SessionBasedTest {

    private CourseController courseController;

    @BeforeEach
    void setupCourseController() {
        courseController = new CourseController(getStorage());
    }

    @Test
    void coursesPageTest() {
        login("ge47son");
        TestModel testModel = new TestModel();
        courseController.coursesPage(testModel, "");
        Person person = getCurrentPerson();
        assertTrue(testModel.asMap().containsValue(person));
        List<Course> myCourses = person.getCourses();
        List<Course> courses = storage.getCourses();
        courses.removeAll(myCourses);
        assertEquals(myCourses, testModel.getAttribute("myCoursesList"));
        assertEquals(courses, testModel.getAttribute("coursesList"));
    }

    @Test
    void coursePageTest() {
        login("ge47son");
        TestModel testModel = new TestModel();
        courseController.coursePage(testModel, 20);
        Person person = getCurrentPerson();
        assertTrue(testModel.asMap().containsValue(person));
        assertTrue(testModel.asMap().containsValue(storage.getCourse(20)));
        assertEquals(storage.getCourse(20).getAnnouncements(), testModel.getAttribute("announcements"));
    }

    @Test
    void createCourseTest() {
        login("ge47son");
        CourseForm courseForm = new CourseForm();
        courseForm.setName("test");
        courseForm.setDescription("test course");
        courseForm.setAcronym("t");
        assertEquals("redirect:/courses", courseController.createCourse(courseForm));
        Course newCourse = null;
        for (Course course:getStorage().getCourses()) {
            if (course.getName().equals("test")) {
                newCourse = course;
                break;
            }
        }
        assertNotNull(newCourse);
        assertEquals("test", newCourse.getName());
        assertEquals("test course", newCourse.getDescription());
        assertEquals("t", newCourse.getAcronym());
    }
    @Test
    void subscribeCourseTest() {
        login("ge47son");
        courseController.unsubscribeCourse(18);
        assertEquals("redirect:/courses", courseController.subscribeCourse(18));
        assertTrue(getCurrentPerson().getCourses().contains(getStorage().getCourse(18)));
    }

    @Test
    void unsubscribeCourseTest() {
        login("ge47son");
        assertEquals("redirect:/courses",  courseController.unsubscribeCourse(20));
        assertFalse(getCurrentPerson().getCourses().contains(getStorage().getCourse(20)));
    }

    @Test
    void deleteCourseTest() {
        login("ge47son");
        assertEquals("redirect:/courses", courseController.deleteCourse(20));
        assertNull(getStorage().getCourse(20));
    }

    @Test
    void updateCourseTest() {
        login("ge47son");
        CourseForm courseForm = new CourseForm();
        courseForm.setName("test");
        courseForm.setDescription("test course");
        courseForm.setAcronym("t");
        assertEquals("redirect:/courses/20", courseController.updateCourse(20, courseForm));
        Course course = getStorage().getCourse(20);
        assertEquals("test", course.getName());
        assertEquals("test course", course.getDescription());
        assertEquals("t", course.getAcronym());
    }

    @Test
    void addAnnouncementTest() {
        login("ge47son");
        assertEquals("redirect:/courses/20", courseController.addAnnouncement(20, "Test", "test"));
        for (Announcement announcement:getStorage ().getCourse(20).getAnnouncements()) {
            if (announcement.getTitle().equals("Test") && announcement.getDescription().equals("test")) {
                assertEquals(0, announcement.getComments().size());
                return;
            }
        }
        fail("announcement not added");
    }

    @Test
    void deleteAnnouncementTest() {
        login("ge47son");
        assertEquals("redirect:/courses/20", courseController.deleteAnnouncement(14, 20));
        assertNull(getStorage().getAnnouncement(14));
    }
}
