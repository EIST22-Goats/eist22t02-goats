package eist.tum_social.tum_social.model;

import eist.tum_social.tum_social.data_storage.util.DatabaseEntity;
import eist.tum_social.tum_social.data_storage.util.ColumnMapping;
import eist.tum_social.tum_social.data_storage.util.IgnoreInDatabase;
import eist.tum_social.tum_social.data_storage.util.PrimaryKey;

import java.util.Date;
import java.util.List;

@DatabaseEntity(tableName = "Persons")
public class Person {

    @PrimaryKey
    private int id;
    private String firstname;
    private String lastname;
    private Date birthdate;
    private String tumId;
    private String email;
    @IgnoreInDatabase
    private List<Person> friends;
    @IgnoreInDatabase
    private Timetable timetable;
    private int semesterNr;
    @ColumnMapping(columnName = "degreeProgramId", isForeignKey = true, foreignKey = "id")
    private DegreeProgram degreeProgram;
    private String password;

    public Person() {
    }

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

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
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

    public Timetable getTimetable() {
        return timetable;
    }

    public void setTimetable(Timetable timetable) {
        this.timetable = timetable;
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
}
