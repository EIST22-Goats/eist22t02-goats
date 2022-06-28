package eist.tum_social;

import eist.tum_social.TestClasses.TestDatabase;
import eist.tum_social.TestClasses.TestModel;
import eist.tum_social.tum_social.controllers.TimetableController;
import eist.tum_social.tum_social.controllers.forms.AppointmentForm;
import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.model.Appointment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static eist.tum_social.Util.getDatabase;
import static eist.tum_social.Util.getStorage;
import static eist.tum_social.tum_social.controllers.AuthenticationController.login;
import static org.junit.jupiter.api.Assertions.*;

public class TimetableTest extends SessionBasedTest {

    private TimetableController timetableController;

    private Storage storage;

    @BeforeEach
    void setupTimetableController() {
        login("ge47son");
        storage = getStorage();
        timetableController = new TimetableController(storage);
    }

    @SuppressWarnings("unchecked")
    @Test
    void timetablePage() {
        login("ge47son");
        TestModel model = new TestModel();
        assertEquals("timetable", timetableController.timetablePage(model, LocalDate.of(2022, 6, 14)));
        assertEquals(LocalDate.of(2022, 6, 13), model.getAttribute("startDate"));
        assertEquals(LocalDate.of(2022, 6, 19), model.getAttribute("endDate"));
        assertEquals(8, model.getAttribute("startTime"));
        assertEquals(20, model.getAttribute("endTime"));
        assertEquals(List.of(
                "Montag",
                "Dienstag",
                "Mittwoch",
                "Donnerstag",
                "Freitag",
                "Samstag",
                "Sonntag"), model.getAttribute("daysOfWeek"));
        assertEquals(LocalDate.of(2022, 6, 6), model.getAttribute("previousDate"));
        assertEquals(LocalDate.of(2022, 6, 20), model.getAttribute("nextDate"));
        Map<String, List<Appointment>> mappedAppointments = (Map<String, List<Appointment>>) Objects.requireNonNull(model.getAttribute("mappedAppointments"));
        assertEquals(0, mappedAppointments.get("Montag").size());
        assertEquals(1, mappedAppointments.get("Dienstag").size());
        assertEquals(storage.getAppointment(50), mappedAppointments.get("Dienstag").get(0));
        assertEquals(0, mappedAppointments.get("Mittwoch").size());
        assertEquals(2, mappedAppointments.get("Donnerstag").size());
        assertEquals(storage.getAppointment(51), mappedAppointments.get("Donnerstag").get(0));
        assertEquals(storage.getAppointment(55), mappedAppointments.get("Donnerstag").get(1));
        assertEquals(1, mappedAppointments.get("Freitag").size());
        assertEquals(storage.getAppointment(52), mappedAppointments.get("Freitag").get(0));
        assertEquals(0, mappedAppointments.get("Samstag").size());
        assertEquals(0, mappedAppointments.get("Sonntag").size());
    }

    @Test
    void createAppointment() {
        AppointmentForm appointmentForm = new AppointmentForm();
        List<Appointment> oldAppointments = storage.getAppointments();
        assertEquals(9, oldAppointments.size());
        assertEquals("redirect:/timetable", timetableController.createAppointment(appointmentForm));
        List<Appointment> appointments = storage.getAppointments();
        assertEquals(10, appointments.size());
        appointments = appointments.stream().filter(appointment -> !oldAppointments.contains(appointment)).toList();
        assertEquals(1, appointments.size());
    }

    @Test
    void createCourseAppointment() {
        AppointmentForm appointmentForm = new AppointmentForm();
        List<Appointment> oldAppointments = storage.getCourse(18).getAppointments();
        assertEquals(0, oldAppointments.size());
        assertEquals("redirect:/courses/18", timetableController.createCourseAppointment(appointmentForm, 18));
        List<Appointment> appointments = storage.getCourse(18).getAppointments();
        assertEquals(1, appointments.size());
        appointments = appointments.stream().filter(appointment -> !oldAppointments.contains(appointment)).toList();
        assertEquals(1, appointments.size());
    }
    @Test
    void updateAppointment() {
        login("ge47son");
        AppointmentForm appointmentForm = new AppointmentForm();
        appointmentForm.setName("Test");
        assertEquals("redirect:/timetable", timetableController.updateAppointment(49, appointmentForm));
        Appointment appointment = storage.getAppointment(49);
        assertEquals("Test", appointment.getName());
    }

    @Test
    void updateCourseAppointment() {
        login("ge47son");
        AppointmentForm appointmentForm = new AppointmentForm();
        appointmentForm.setName("Test");
        assertEquals("redirect:/courses/20", timetableController.updateCourseAppointment(52, appointmentForm));
        Appointment appointment = storage.getAppointment(52);
        assertEquals("Test", appointment.getName());
    }

    @Test
    void deleteAppointment() {
        login("ge47son");
        timetableController.deleteAppointment(49);
        assertNull(storage.getAppointment(49));
    }

    @Test
    void deleteCourseAppointment() {
        login("ge47son");
        timetableController.deleteCourseAppointment(52);
        assertNull(storage.getAppointment(52));
    }
}
