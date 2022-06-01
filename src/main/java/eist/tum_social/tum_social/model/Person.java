package eist.tum_social.tum_social.model;

import eist.tum_social.tum_social.persistent_data_storage.util.BridgingTable;
import eist.tum_social.tum_social.persistent_data_storage.util.DatabaseEntity;
import eist.tum_social.tum_social.persistent_data_storage.util.ForeignTable;
import eist.tum_social.tum_social.persistent_data_storage.util.IgnoreInDatabase;

import java.time.LocalDate;
import java.util.List;

@DatabaseEntity(tableName = "Persons")
public class Person extends UniquelyIdentifiable {

    private int id = -1;
    private String firstname;
    private String lastname;
    private LocalDate birthdate;
    private String tumId;
    private String email;
    @BridgingTable(
            bridgingTableName = "CourseParticipants",
            ownForeignColumnName = "personId",
            otherForeignColumnName = "courseId")
    private List<Course> courses;
    @IgnoreInDatabase
    private List<Person> friends;
    @BridgingTable(
            bridgingTableName = "PersonAppointments",
            ownForeignColumnName = "personId",
            otherForeignColumnName = "appointmentId")
    private List<Appointment> appointments;
    private int semesterNr;
    @ForeignTable(ownColumnName = "degreeProgramId")
    private DegreeProgram degreeProgram;
    private String password;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getTumId() {
        return tumId;
    }

    public void setTumId(String tumId) {
        this.tumId = tumId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public List<Person> getFriends() {
        return friends;
    }

    public void setFriends(List<Person> friends) {
        this.friends = friends;
    }

    public List<Appointment> getTimetable() {
        return appointments;
    }

    public void setTimetable(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSemesterNr() {
        return semesterNr;
    }

    public void setSemesterNr(int semesterNr) {
        this.semesterNr = semesterNr;
    }

    public DegreeProgram getDegreeProgram() {
        return degreeProgram;
    }

    public void setDegreeProgram(DegreeProgram degreeProgram) {
        this.degreeProgram = degreeProgram;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }
}
