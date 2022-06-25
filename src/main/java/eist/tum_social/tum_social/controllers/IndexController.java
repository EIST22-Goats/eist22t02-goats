package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.datastorage.util.Pair;
import eist.tum_social.tum_social.model.Announcement;
import eist.tum_social.tum_social.model.Course;
import eist.tum_social.tum_social.model.Person;
import net.bytebuddy.description.method.ParameterList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static eist.tum_social.tum_social.controllers.AuthenticationController.isLoggedIn;
import static eist.tum_social.tum_social.controllers.util.Util.addPersonToModel;
import static eist.tum_social.tum_social.controllers.util.Util.getCurrentPerson;

@Controller
public class IndexController {

    private final Storage storage;
    public IndexController(@Autowired Storage storage) {
        this.storage = storage;
    }
    @GetMapping("/")
    public String index(Model model) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Person person = getCurrentPerson();

        List<Course> myCourses = person.getCourses();

        model.addAttribute(person);

        model.addAttribute("myCoursesList", myCourses);

        List<Pair<String, Announcement>> announcements = new ArrayList<>();
        for (Course course : myCourses) {
            List<Announcement> courseAnnouncement = course.getAnnouncements();
            if (!courseAnnouncement.isEmpty()) {
                announcements.add(new Pair<>(course.getName(), courseAnnouncement.get(0)));
            }
        }

        model.addAttribute("myAnnouncements", announcements);

        return "index";
    }

}
