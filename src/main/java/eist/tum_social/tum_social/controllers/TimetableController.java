package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.model.Appointment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eist.tum_social.tum_social.controllers.AuthenticationController.isLoggedIn;
import static eist.tum_social.tum_social.controllers.util.Util.addPersonToModel;

@Controller
public class TimetableController {

    @GetMapping("/timetable")
    public String timetablePage(Model model) {
        if (!isLoggedIn()) {
            return "redirect:/login";
        }
        addPersonToModel(model);

        Appointment ap1 = new Appointment();
        ap1.setName("EIST");
        ap1.setStartDate(LocalDate.of(2022, 5, 31));
        ap1.setStartTime(LocalTime.of(12, 0));
        ap1.setEndTime(LocalTime.of(14, 0));


        Appointment ap2 = new Appointment();
        ap2.setName("EIST");
        ap2.setStartDate(LocalDate.of(2022, 5, 30));
        ap2.setStartTime(LocalTime.of(16, 0));
        ap2.setEndTime(LocalTime.of(17, 0));

        List<String> daysOfWeek = new ArrayList<>(List.of("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"));
        List<Appointment> appointments = new ArrayList<>(List.of(ap1, ap2));

        // TODO settings or dynamic calculation ?
        int startTime = 8;
        int endTime = 20;

        appointments = appointments.stream().filter(
                appointment -> appointment.getStartTime().isAfter(LocalTime.of(startTime, 0)) &&
                               appointment.getStartTime().isBefore(LocalTime.of(endTime, 0))
        ).toList();

        Map<String, List<Appointment>> mappedAppointments = new HashMap<>();
        for (String day : daysOfWeek) {
            mappedAppointments.put(day, new ArrayList<>());
        }

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

}
