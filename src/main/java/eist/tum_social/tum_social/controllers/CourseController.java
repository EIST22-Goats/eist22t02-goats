package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.controllers.forms.CourseForm;
import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.datastorage.StorageFacade;
import eist.tum_social.tum_social.model.Announcement;
import eist.tum_social.tum_social.model.Comment;
import eist.tum_social.tum_social.model.Course;
import eist.tum_social.tum_social.model.Person;
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

    @PostMapping("/addAnnouncement/{courseId}")
    public String addAnnouncement(@PathVariable("courseId") int courseId,
                                  @RequestParam("title") String title,
                                  @RequestParam("text") String text) {
        Storage storage = new Storage();

        Course course = storage.getCourse(courseId);

        if (course.getAdmin().equals(getCurrentPerson(storage))) {
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

    @PostMapping("/addComment/{parentId}")
    public String addComment(@PathVariable int parentId, String text, @RequestParam("courseId") int courseId) {
        Storage storage = new Storage();

        Person person = getCurrentPerson(storage);

        Comment parentComment = storage.getComment(parentId);
        if (parentComment == null) {
            return "redirect:/courses/" + courseId;
        }

        Comment comment = createComment(text);
        comment.setAuthor(person);

        storage.update(comment);

        parentComment.getChildComments().add(comment);

        storage.update(parentComment);

        return "redirect:/courses/" + courseId;
    }

    @PostMapping("/addRootComment/{announcementId}")
    public String addRootComment(@PathVariable int announcementId, String text, @RequestParam("courseId") int courseId) {
        Storage storage = new Storage();

        Person person = getCurrentPerson(storage);

        Announcement announcement = storage.getAnnouncement(announcementId);

        Comment comment = createComment(text);
        comment.setAuthor(person);

        storage.update(comment);

        announcement.getComments().add(comment);

        storage.update(announcement);

        return "redirect:/courses/" +courseId;
    }

    @PostMapping("/deleteComment/{commentId}")
    public String deleteComment(@PathVariable int commentId, @RequestParam("courseId") int courseId) {
        Storage storage = new Storage();
        Comment comment = storage.getComment(commentId);

        if (comment.getAuthor().equals(getCurrentPerson(storage))) {
            deleteCommentTree(comment, storage);
        }

        return "redirect:/courses/" + courseId;
    }

    @PostMapping("/deleteAnnouncement/{announcementId}")
    public String deleteAnnouncement(@PathVariable int announcementId, @RequestParam("courseId") int courseId) {
        Storage storage = new Storage();

        Announcement announcement = storage.getAnnouncement(announcementId);
        Course course = storage.getCourse(courseId);

        if (course.getAdmin().equals(getCurrentPerson(storage))) {
            deleteAnnouncement(announcement, storage);
        }

        return "redirect:/courses/" + courseId;
    }

    // TODO: post + dynamic loading
    /*
     *    _______ ____  _____   ____
     *   |__   __/ __ \|  __ \ / __ \
     *      | | | |  | | |  | | |  | |
     *      | | | |  | | |  | | |  | |
     *      | | | |__| | |__| | |__| |
     *      |_|  \____/|_____/ \____/
     *
     */
    @GetMapping("/toggleLikeComment/{commentId}/{courseId}")
    public String toggleLikeComment(@PathVariable int commentId, @PathVariable("courseId") int courseId) {
        Storage storage = new Storage();

        Person person = getCurrentPerson(storage);

        Comment comment = storage.getComment(commentId);
        List<Person> likes = comment.getLikes();
        if (likes.contains(person)) {
            likes.remove(person);
        } else {
            likes.add(person);
        }
        storage.update(comment);

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

    private Comment createComment(String text) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setDate(LocalDate.now());
        comment.setTime(LocalTime.now());
        return comment;
    }
}
