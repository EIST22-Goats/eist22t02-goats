package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.model.Course;
import eist.tum_social.tum_social.model.Person;
import eist.tum_social.tum_social.persistent_data_storage.SqliteDatabase;
import eist.tum_social.tum_social.persistent_data_storage.Storage;
import eist.tum_social.tum_social.persistent_data_storage.StorageFacade;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import static eist.tum_social.tum_social.controllers.AuthenticationController.getCurrentUsersTumId;
import static eist.tum_social.tum_social.controllers.AuthenticationController.isLoggedIn;
import static eist.tum_social.tum_social.controllers.util.Util.addPersonToModel;

@Controller
public class CourseController {

    @GetMapping("/courses")
    public String coursesPage(Model model) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }
        addPersonToModel(model);

        StorageFacade db = new Storage();
        List<Course> courses = db.getCourses();
        model.addAttribute("coursesList", courses);

        return "courses";
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

        return "course_view";
    }

    @PostMapping("/createCourse")
    public String createPost(Course course) {
        new Storage().updateCourse(course);

        return "redirect:/courses";
    }

    @PostMapping("/joinCourse/{courseId}")
    public String subscribeCourse(@PathVariable int courseId) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Storage storage = new Storage();
        Person p = storage.getPerson(getCurrentUsersTumId());
        Course c = new Course();
        c.setId(courseId);
        p.getCourses().add(c);
        storage.updatePerson(p);

        return "redirect:/courses";
    }

    @PostMapping("/leaveCourse/{courseId}")
    public String unsubscribeCourse(@PathVariable int courseId) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Storage storage = new Storage();
        Person p = storage.getPerson(getCurrentUsersTumId());
        Course c = new Course();
        c.setId(courseId);
        p.getCourses().remove(c);
        storage.updatePerson(p);

        return "redirect:/courses";
    }
}
