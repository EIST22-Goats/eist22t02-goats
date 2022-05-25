package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.model.Course;
import eist.tum_social.tum_social.model.Person;
import eist.tum_social.tum_social.persistent_data_storage.Database;
import eist.tum_social.tum_social.persistent_data_storage.Storage;
import eist.tum_social.tum_social.persistent_data_storage.StorageFacade;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

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
        addPersonToModel(model);
        return "redirect:/";
    }
}
