package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.model.Appointment;
import eist.tum_social.tum_social.model.Course;
import eist.tum_social.tum_social.model.Person;
import eist.tum_social.tum_social.persistent_data_storage.Storage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eist.tum_social.tum_social.controllers.AuthenticationController.isLoggedIn;
import static eist.tum_social.tum_social.controllers.util.Util.getCurrentPerson;

@Controller
public class TimetableController {

    @GetMapping("/timetable")
    public String timetablePage(Model model) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }
        Person person = getCurrentPerson();
        model.addAttribute(person);

        List<Appointment> appointments = new ArrayList<>(person.getAppointments());

        for (Course course : new Storage().reloadObjects(person.getCourses())) {
            appointments.addAll(course.getAppointments());
        }

        // TODO settings or dynamic calculation ?
        int startTime = 8;
        int endTime = 20;

        appointments = appointments.stream().filter(
                appointment -> appointment.getStartTime().isAfter(LocalTime.of(startTime, 0)) &&
                        appointment.getStartTime().isBefore(LocalTime.of(endTime, 0))
        ).toList();

        List<String> daysOfWeek = new ArrayList<>(List.of("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"));
        Map<String, List<Appointment>> mappedAppointments = new HashMap<>();
        for (String day : daysOfWeek) {
            mappedAppointments.put(day, new ArrayList<>());
        }

        // TODO: use thymeleaf language mapping instead
        for (Appointment appointment : appointments) {
            String nameOfDay = switch (appointment.getStartDate().getDayOfWeek()) {
                case MONDAY -> "Montag";
                case TUESDAY -> "Dienstag";
                case WEDNESDAY -> "Mittwoch";
                case THURSDAY -> "Donnerstag";
                case FRIDAY -> "Freitag";
                case SATURDAY -> "Samstag";
                case SUNDAY -> "Sonntag";
            };
            mappedAppointments.get(nameOfDay).add(appointment);
        }

        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);
        model.addAttribute("daysOfWeek", daysOfWeek);
        model.addAttribute("mappedAppointments", mappedAppointments);

        return "timetable";
    }

    @PostMapping("/createAppointment")
    public String createAppointment(Appointment appointment) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }

        Storage storage = new Storage();

        storage.updateAppointment(appointment);

        Person person = getCurrentPerson();
        person.getAppointments().add(appointment);

        storage.updatePerson(person);

        return "redirect:/timetable";
    }

}
