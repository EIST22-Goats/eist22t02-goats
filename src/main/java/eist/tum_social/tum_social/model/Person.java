package eist.tum_social.tum_social.model;

import eist.tum_social.tum_social.datastorage.BridgingEntities;
import eist.tum_social.tum_social.datastorage.ForeignEntity;
import eist.tum_social.tum_social.datastorage.util.*;

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
    private String password;
    @BridgingTable(
            bridgingTableName = "CourseParticipants",
            ownForeignColumnName = "personId",
            otherForeignColumnName = "courseId")
    private BridgingEntities<Course> courseEntities;
    @BridgingTable(
            bridgingTableName = "PersonAppointments",
            ownForeignColumnName = "personId",
            otherForeignColumnName = "appointmentId")
    private BridgingEntities<Appointment> appointmentEntities;
    private int semesterNr;
    @ForeignTable(
            foreignTableName = "DegreePrograms",
            ownColumnName = "degreeProgramId")
    private ForeignEntity<DegreeProgram> degreeProgramEntity;
    @IgnoreInDatabase
    private List<Person> friends;

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

    public List<Person> getFriends() {
        return friends;
    }

    public void setFriends(List<Person> friends) {
        this.friends = friends;
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

    public ForeignEntity<DegreeProgram> getDegreeProgramEntity() {
        return degreeProgramEntity;
    }

    public DegreeProgram getDegreeProgram() {
        return degreeProgramEntity.get();
    }

    public void setDegreeProgram(DegreeProgram degreeProgram) {
        degreeProgramEntity.set(degreeProgram);
    }

    public void setDegreeProgramEntity(ForeignEntity<DegreeProgram> degreeProgramEntity) {
        this.degreeProgramEntity = degreeProgramEntity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BridgingEntities<Course> getCourseEntities() {
        return courseEntities;
    }

    public List<Course> getCourses() {
        return courseEntities.get();
    }

    public void setCourseEntities(BridgingEntities<Course> courseEntities) {
        this.courseEntities = courseEntities;
    }

    public BridgingEntities<Appointment> getAppointmentEntities() {
        return appointmentEntities;
    }

    public List<Appointment> getAppointments() {
        return appointmentEntities.get();
    }

    public void setAppointmentEntities(BridgingEntities<Appointment> appointmentEntities) {
        this.appointmentEntities = appointmentEntities;
    }
}
