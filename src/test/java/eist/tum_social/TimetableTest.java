package eist.tum_social;

import eist.tum_social.TestClasses.TestDatabase;
import eist.tum_social.tum_social.controllers.TimetableController;
import eist.tum_social.tum_social.controllers.forms.AppointmentForm;
import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.model.Appointment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static eist.tum_social.Util.getStorage;
import static eist.tum_social.tum_social.controllers.AuthenticationController.login;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimetableTest extends SessionBasedTest {

    private TimetableController timetableController;

    private Storage storage;

    @BeforeEach
    void setupTimetableController() {
        login("ge47son");
        storage = getStorage();
        timetableController = new TimetableController(storage);
    }
    @Disabled
    @Test
    void timetablePage() {
        // TODO
    }

    @Disabled
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
        Appointment appointment = appointments.get(0);

        // TODO: finish test (appointment comparison)
    }
    @Disabled
    @Test
    void createCourseAppointment() {
        // TODO
    }
    @Disabled
    @Test
    void updateAppointment() {
        // TODO
    }
    @Disabled
    @Test
    void updateCourseAppointment() {
        // TODO
    }
    @Disabled
    @Test
    void deleteAppointment() {
        // TODO
    }
    @Disabled
    @Test
    void deleteCourseAppointment() {
        // TODO
    }
}
