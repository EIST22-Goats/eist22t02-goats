package eist.tum_social.tum_social.controllers.forms;

import eist.tum_social.tum_social.model.Person;

import java.time.LocalDate;

public class RegistrationForm extends Form<Person> {

    private String firstname;
    private String lastname;
    private LocalDate birthdate;
    private String tumId;
    private String email;
    private int semesterNr;
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

    public int getSemesterNr() {
        return semesterNr;
    }

    public void setSemesterNr(int semesterNr) {
        this.semesterNr = semesterNr;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
