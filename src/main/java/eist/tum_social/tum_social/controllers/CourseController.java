package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.controllers.forms.CourseForm;
import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.datastorage.StorageFacade;
import eist.tum_social.tum_social.model.Announcement;
import eist.tum_social.tum_social.model.Course;
import eist.tum_social.tum_social.model.Person;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

import static eist.tum_social.tum_social.controllers.AuthenticationController.isLoggedIn;
import static eist.tum_social.tum_social.controllers.util.Util.addPersonToModel;
import static eist.tum_social.tum_social.controllers.util.Util.getCurrentPerson;

@Controller
public class CourseController {

    @GetMapping("/courses")
    public String coursesPage(Model model, @RequestParam(value = "searchText", required = false) String searchText) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Storage db = new Storage();

        Person person = getCurrentPerson(db);
        model.addAttribute(person);

        List<Course> myCourses = person.getCourses();
        List<Course> courses = db.getCourses();

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

        StorageFacade db = new Storage();
        Course course = db.getCourse(courseId);
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

        Storage storage = new Storage();
        Person p = getCurrentPerson(storage);
        course.setAdmin(p);

        storage.update(course);

        return "redirect:/courses";
    }

    @PostMapping("/joinCourse/{courseId}")
    public String subscribeCourse(@PathVariable int courseId) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Storage storage = new Storage();
        Person p = getCurrentPerson(storage);
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

        Storage storage = new Storage();
        Person p = getCurrentPerson(storage);
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

        Storage storage = new Storage();
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

        Storage storage = new Storage();
        Course course = storage.getCourse(courseId);

        if (course.getAdmin().getId() == getCurrentPerson().getId()) {
            form.apply(course);
            storage.update(course);
        }

        return "redirect:/courses/" + courseId;
    }

}
