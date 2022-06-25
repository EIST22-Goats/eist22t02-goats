package eist.tum_social;

import eist.tum_social.tum_social.datastorage.Database;
import eist.tum_social.tum_social.datastorage.SqliteDatabase;
import eist.tum_social.tum_social.model.Appointment;
import eist.tum_social.tum_social.model.Course;
import eist.tum_social.tum_social.model.Person;
import eist.tum_social.tum_social.model.UniquelyIdentifiable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static eist.tum_social.Util.getDatabase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SqliteDatabaseTest {

    @BeforeEach
    void copyTestDatabase() {
        Util.copyTestDatabase();
    }

    @AfterEach
    void removeTestDatabase() {
        Util.removeTestDatabase();
    }

    <T> void assertEqualLists(List<T> list1, List<T> list2) {
        assertEquals(list1.size(), list2.size());
        for (var elem : list1) {
            assertTrue(list2.contains(elem));
        }
        for (var elem : list2) {
            assertTrue(list1.contains(elem));
        }
    }

    private void assertEqualPersons(Person expected, Person actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getFirstname(), actual.getFirstname());
        assertEquals(expected.getBirthdate(), actual.getBirthdate());
        assertEquals(expected.getTumId(), actual.getTumId());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getPassword(), actual.getPassword());
    }

    private Person createTestPerson(
            int id,
            String firstname,
            String lastname,
            LocalDate birthdate,
            String tumId,
            String email,
            String password
    ) {
        Person person = new Person();
        person.setId(id);
        person.setFirstname(firstname);
        person.setLastname(lastname);
        person.setBirthdate(birthdate);
        person.setTumId(tumId);
        person.setEmail(email);
        person.setPassword(password);
        return person;
    }

    private void assertEqualsTestPerson1(Person person) {
        assertEqualPersons(createTestPerson(
                        3,
                        "Florian",
                        "Adam",
                        LocalDate.of(2022, 6, 28),
                        "ge47son",
                        "test@flo.de",
                        "$2a$10$eyY0nGcLudwhsUnH2W.d8.BguraenRP7hBN7cHt9ETkxSXcnLBVES"),
                person
        );
    }

    private void assertEqualsTestPerson2(Person person) {
        assertEqualPersons(createTestPerson(
                        14,
                        "Willi",
                        "Adam",
                        null,
                        "go47tum",
                        "some@mail.com",
                        "$2a$10$mrOLNTjznXWO1I10jLr5nO5NvOwMdiKhB50lGuH87OaLqdyC7xFIO"),
                person
        );
    }

    private void assertEqualsTestPerson3(Person person) {
        assertEqualPersons(createTestPerson(
                        17,
                        "Kilian",
                        "Nothoff",
                        LocalDate.of(2002, 12, 28),
                        "ge95bit",
                        "ge95bit@mytum.de",
                        "$2a$10$g5PTPC13e/Tw5naMo6TzaeuavARVAfyCQWvIeDQUYHEGk3h74eAC."),
                person
        );
    }

    private <T extends UniquelyIdentifiable> List<T> sortById(List<T> objects) {
        return objects.stream().sorted(Comparator.comparing(T::getId)).toList();
    }

    @Test
    void testSelectShallow() {
        SqliteDatabase db = (SqliteDatabase) getDatabase();

        List<Person> persons = db.select(Person.class, "1");
        persons = sortById(persons);
        assertEquals(4, persons.size());
        assertEqualsTestPerson1(persons.get(0));
        assertEqualsTestPerson2(persons.get(1));
        assertEqualsTestPerson3(persons.get(2));
    }

    @Test
    void testSelectEmpty() {
        SqliteDatabase db = (SqliteDatabase) getDatabase();

        List<Person> persons = db.select(Person.class, "id=232");
        assertTrue(persons.isEmpty());
    }

    @Test
    void testSelectShallowConditional() {
        SqliteDatabase db = (SqliteDatabase) getDatabase();

        List<Person> persons = db.select(Person.class, "id=17");
        assertEquals(1, persons.size());
        assertEqualsTestPerson3(persons.get(0));
        persons = db.select(Person.class, "lastname='Adam'");
        persons = sortById(persons);
        assertEquals(2, persons.size());
        assertEqualsTestPerson1(persons.get(0));
        assertEqualsTestPerson2(persons.get(1));
    }

    @Test
    void testSelectForeignTable() {
        SqliteDatabase db = (SqliteDatabase) getDatabase();

        List<Person> persons = db.select(Person.class, "tumId='ge95bit'");
        assertEquals(1, persons.size());
        Person person = persons.get(0);
        assertEquals(1, person.getDegreeProgram().getId());
        assertEquals("Informatik", person.getDegreeProgram().getName());
        assertEquals("Informatik ist cool", person.getDegreeProgram().getDescription());
    }

    @Test
    void testSelectForeignTableNull() {
        // TODO
    }

    @Test
    void testSelectBridgingTable() {
        SqliteDatabase db = (SqliteDatabase) getDatabase();

        List<Person> persons = db.select(Person.class, "tumId='ge95bit'");
        assertEquals(1, persons.size());
        Person person = persons.get(0);
        List<Course> courses = person.getCourses();
        courses = sortById(courses);
        assertEquals(2, courses.size());
        assertEquals(18, courses.get(0).getId());
        assertEquals("Fortgeschrittene Grundlagen der Programmierung", courses.get(0).getName());
        assertEquals("PGdP", courses.get(0).getAcronym());
        assertEquals("Geilomat!", courses.get(0).getDescription());
        assertEquals(20, courses.get(1).getId());
        assertEquals("Diskrete Strukturen", courses.get(1).getName());
        assertEquals("DS", courses.get(1).getAcronym());
        assertEquals("Moonaaaa", courses.get(1).getDescription());
    }

    @Test
    void testSelectBridgingTableBidirectional() {
        SqliteDatabase db = (SqliteDatabase) getDatabase();
        List<Person> persons = db.select(Person.class, "id='17'");
        assertEquals(1, persons.size());
        Person person = persons.get(0);
    }

    @Test
    void testSelectBridgingTableEmpty() {
        SqliteDatabase db = (SqliteDatabase) getDatabase();

        List<Person> persons = db.select(Person.class, "tumId='go47tum'");
        assertEquals(1, persons.size());
        List<Course> courses = persons.get(0).getCourses();
        assertTrue(courses.isEmpty());
    }

    @Disabled
    @Test
    void testSelectDeep() {
        SqliteDatabase db = (SqliteDatabase) getDatabase();

        List<Person> persons = db.select(Person.class, "id=17");
        assertEquals(1, persons.size());
        Person person = persons.get(0);

        for (int i = 0; i < 5; i++) {
            List<Course> courses = person.getCourses();
            assertEquals(2, courses.size());
            courses = sortById(courses);
            Course course = courses.get(0);
            assertEquals(3, course.getAdmin().getId());
            List<Appointment> appointments = course.getAppointments();

            assertEquals(2, appointments.size());
            appointments = sortById(appointments);
            Appointment appointment = appointments.get(0);
            Course appointmentCourse = appointment.getCourses().get(0);
            List<Person> participants = appointmentCourse.getParticipants();
            assertEquals(2, participants.size());
            participants = sortById(participants);
            assertEquals(person.getId(), participants.get(1).getId());
            person = participants.get(1);
        }
    }



}
