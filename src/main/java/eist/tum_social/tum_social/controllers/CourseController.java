package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.controllers.forms.CourseForm;
import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.datastorage.StorageFacade;
import eist.tum_social.tum_social.model.Announcement;
import eist.tum_social.tum_social.model.Comment;
import eist.tum_social.tum_social.model.Course;
import eist.tum_social.tum_social.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

@Controller
public class CourseController {
    private final Storage storage;
    public CourseController(@Autowired Storage storage) {
        this.storage = storage;
    }
    @GetMapping("/courses")
    public String coursesPage(Model model, @RequestParam(value = "searchText", required = false) String searchText) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Person person = getCurrentPerson();
        model.addAttribute(person);

        List<Course> myCourses = person.getCourses();
        List<Course> courses = storage.getCourses();

        courses.removeAll(myCourses);

        if (searchText != null) {
            myCourses = filterCourses(myCourses, searchText);
            courses = filterCourses(courses, searchText);
        }

        model.addAttribute("myCoursesList", myCourses);
        model.addAttribute("coursesList", courses);

        return "courses";
    }

    private List<Course> filterCourses(List<Course> courses, String query) {
        return courses.stream()
                .filter(it -> it.getName().toLowerCase().contains(query.toLowerCase())
                        || it.getAcronym().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    @GetMapping("/courses/{courseId}")
    public String coursePage(Model model, @PathVariable int courseId) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        addPersonToModel(model);

        Course course = storage.getCourse(courseId);
        model.addAttribute(course);

        List<Announcement> announcements = course.getAnnouncements();
        model.addAttribute("announcements", announcements);

        return "course_view";
    }

    @PostMapping("/createCourse")
    public String createCourse(CourseForm form) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Course course = new Course();
        form.apply(course);

        Person p = getCurrentPerson();
        course.setAdmin(p);

        storage.update(course);

        return "redirect:/courses";
    }

    @PostMapping("/joinCourse/{courseId}")
    public String subscribeCourse(@PathVariable int courseId) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Person p = getCurrentPerson();
        Course c = new Course();
        c.setId(courseId);
        p.getCourses().add(c);
        storage.update(p);

        return "redirect:/courses";
    }

    @PostMapping("/leaveCourse/{courseId}")
    public String unsubscribeCourse(@PathVariable int courseId) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Person p = getCurrentPerson();
        Course c = new Course();
        c.setId(courseId);
        p.getCourses().remove(c);
        storage.update(p);

        return "redirect:/courses";
    }

    @PostMapping("/deleteCourse/{courseId}")
    public String deleteCourse(@PathVariable int courseId) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Course course = storage.getCourse(courseId);

        if (course.getAdmin().getId() == getCurrentPerson().getId()) {
            storage.delete(course);
        }

        return "redirect:/courses";
    }

    @PostMapping("/updateCourse/{courseId}")
    public String updateCourse(@PathVariable int courseId, CourseForm form) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Course course = storage.getCourse(courseId);

        if (course.getAdmin().getId() == getCurrentPerson().getId()) {
            form.apply(course);
            storage.update(course);
        }

        return "redirect:/courses/" + courseId;
    }

    @PostMapping("/addAnnouncement/{courseId}")
    public String addAnnouncement(@PathVariable("courseId") int courseId,
                                  @RequestParam("title") String title,
                                  @RequestParam("text") String text) {
        Course course = storage.getCourse(courseId);

        if (course.getAdmin().equals(getCurrentPerson())) {
            Announcement announcement = new Announcement();
            announcement.setTitle(title);
            announcement.setDescription(text);
            announcement.setDate(LocalDate.now());
            announcement.setTime(LocalTime.now());

            storage.update(announcement);

            course.getAnnouncements().add(announcement);

            storage.update(course);
        }

        return "redirect:/courses/" + courseId;
    }

    @PostMapping("/deleteAnnouncement/{announcementId}")
    public String deleteAnnouncement(@PathVariable int announcementId, @RequestParam("courseId") int courseId) {

        Announcement announcement = storage.getAnnouncement(announcementId);
        Course course = storage.getCourse(courseId);

        if (course.getAdmin().equals(getCurrentPerson())) {
            deleteAnnouncement(announcement, storage);
        }

        return "redirect:/courses/" + courseId;
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
