package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.controllers.forms.AppointmentForm;
import eist.tum_social.tum_social.model.Appointment;
import eist.tum_social.tum_social.model.Course;
import eist.tum_social.tum_social.model.Person;
import eist.tum_social.tum_social.datastorage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import static eist.tum_social.tum_social.controllers.AuthenticationController.isLoggedIn;
import static eist.tum_social.tum_social.controllers.util.Util.getCurrentPerson;

@Controller
public class TimetableController {

    private final Storage storage;
    public TimetableController(@Autowired Storage storage) {
        this.storage = storage;
    }

    @GetMapping("/timetable")
    public String timetablePage(Model model, @RequestParam(value = "startDate", required = false) LocalDate startDate) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Person person = getCurrentPerson();
        model.addAttribute(person);

        List<Appointment> appointments = findAllAppointmentsForPerson(person);

        if (startDate == null) {
            startDate = LocalDate.now();
        }
        startDate = startDate.minusDays(startDate.getDayOfWeek().getValue() - 1);

        appointments = filterAppointments(appointments, startDate);

        Map<DayOfWeek, List<Appointment>> map = mapAppointmentsToDays(appointments);

        // TODO: use Thymeleaf language files instead
        List<String> daysOfWeek = new ArrayList<>(List.of("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"));
        Map<String, List<Appointment>> map2 = tempAppointmentMapToDays(map);

        int startHour = Math.min(earliestTimeslot(appointments) - 1, 8);
        int endHour = Math.max(latestTimeslot(appointments) + 1, 20);

        model.addAttribute("startTime", startHour);
        model.addAttribute("endTime", endHour);
        model.addAttribute("daysOfWeek", daysOfWeek);
        model.addAttribute("mappedAppointments", map2);

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", startDate.plusDays(6));

        model.addAttribute("previousDate", startDate.minusWeeks(1));
        model.addAttribute("nextDate", startDate.plusWeeks(1));

        return "timetable";
    }

    private int earliestTimeslot(List<Appointment> appointments) {
       int earliestTime = Integer.MAX_VALUE;
       for (Appointment appointment:appointments) {
           if (appointment.getStartTime().getHour() < earliestTime) {
               earliestTime = appointment.getStartTime().getHour();
           }
       }
       return earliestTime;
    }

    private int latestTimeslot(List<Appointment> appointments) {
        int latestTime = Integer.MIN_VALUE;
        for (Appointment appointment:appointments) {
            if (appointment.getStartTime().getHour() > latestTime) {
                latestTime = appointment.getStartTime().getHour() + (int) appointment.getDurationInHours();
            }
        }
        return latestTime;
    }

    private List<Appointment> findAllAppointmentsForPerson(Person person) {
        List<Appointment> appointments = new ArrayList<>(person.getAppointments());
        for (Course course : person.getCourses()) {
            appointments.addAll(course.getAppointments());
        }
        return appointments;
    }

    private Map<String, List<Appointment>> tempAppointmentMapToDays(Map<DayOfWeek, List<Appointment>> mappedAppointments) {
        Map<String, List<Appointment>> res = new HashMap<>();
        for (DayOfWeek dayOfWeek: mappedAppointments.keySet()) {
            String nameOfDay = switch (dayOfWeek) {
                case MONDAY -> "Montag";
                case TUESDAY -> "Dienstag";
                case WEDNESDAY -> "Mittwoch";
                case THURSDAY -> "Donnerstag";
                case FRIDAY -> "Freitag";
                case SATURDAY -> "Samstag";
                case SUNDAY -> "Sonntag";
            };
            res.put(nameOfDay, mappedAppointments.get(dayOfWeek));
        }
        return res;
    }
    private Map<DayOfWeek, List<Appointment>> mapAppointmentsToDays(List<Appointment> appointments) {
        Map<DayOfWeek, List<Appointment>> mappedAppointments = new HashMap<>();
        for (DayOfWeek dayOfWeek: DayOfWeek.values()) {
            mappedAppointments.put(dayOfWeek, new ArrayList<>());
        }
        for (Appointment appointment : appointments) {
            mappedAppointments.get(appointment.getStartDate().getDayOfWeek()).add(appointment);
        }
        return mappedAppointments;
    }

    private List<Appointment> filterAppointments(List<Appointment> appointments, LocalDate startDate) {
        LocalDate endDate = startDate.plusDays(6);
        appointments = appointments.stream().filter(appointment -> (
                appointment.getStartDate().plusWeeks(appointment.getRepetitions()-1).plusDays(1).isAfter(startDate))
                && appointment.getStartDate().minusDays(1).isBefore(endDate)
        ).toList();

        return appointments;
    }

    @PostMapping("/createAppointment")
    public String createAppointment(AppointmentForm form) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Appointment appointment = new Appointment();

        form.apply(appointment);

        storage.update(appointment);

        Person person = getCurrentPerson();
        person.getAppointments().add(appointment);

        storage.update(person);

        return "redirect:/timetable";
    }

    @PostMapping("/createCourseAppointment/{courseId}")
    public String createCourseAppointment(AppointmentForm form, @PathVariable int courseId) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Appointment appointment = new Appointment();
        form.apply(appointment);

        storage.update(appointment);
        Course course = storage.getCourse(courseId);
        course.getAppointments().add(appointment);
        storage.update(course);

        return "redirect:/courses/"+course.getId();
    }

    @PostMapping("/updateAppointment/{appointmentId}")
    public String updateAppointment(@PathVariable int appointmentId, AppointmentForm form) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Appointment appointment = storage.getAppointment(appointmentId);
        form.apply(appointment);
        boolean hasAccessRights = appointment.getSubscribers().contains(getCurrentPerson());

        if (hasAccessRights) {
            storage.update(appointment);
        }

        return "redirect:/timetable";
    }

    @PostMapping("/updateCourseAppointment/{appointmentId}")
    public String updateCourseAppointment(@PathVariable int appointmentId, AppointmentForm form) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Appointment appointment = storage.getAppointment(appointmentId);
        form.apply(appointment);
        Person person = getCurrentPerson();
        Course course = appointment.getCourses().get(0);
        if (course.getAdmin().equals(person)) {
            storage.update(appointment);
        }

        return "redirect:/courses/" + course.getId();
    }

    @PostMapping("/deleteAppointment/{appointmentId}")
    public String deleteAppointment(@PathVariable int appointmentId) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Appointment appointment = storage.getAppointment(appointmentId);

        boolean hasAccessRights = appointment.getSubscribers().contains(getCurrentPerson());

        if (hasAccessRights) {
            storage.delete(appointment);
        }

        return "redirect:/timetable";
    }

    @PostMapping("/deleteCourseAppointment/{appointmentId}")
    public String deleteCourseAppointment(@PathVariable int appointmentId) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Appointment appointment = storage.getAppointment(appointmentId);
        Person person = getCurrentPerson();

        Course course = appointment.getCourses().get(0);
        if (course.getAdmin().equals(person)) {
            storage.delete(appointment);
        }

        return "redirect:/courses/" + course.getId();

    }

}
