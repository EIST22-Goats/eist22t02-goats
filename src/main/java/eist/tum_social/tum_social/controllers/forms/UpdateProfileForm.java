package eist.tum_social.tum_social.controllers.forms;

import eist.tum_social.tum_social.controllers.ProfileController;
import eist.tum_social.tum_social.controllers.util.Util;
import eist.tum_social.tum_social.datastorage.StorageFacade;
import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.model.DegreeProgram;
import eist.tum_social.tum_social.model.Person;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UpdateProfileForm extends Form<Person> {
    private String firstname;
    private String lastname;

    private String biography;
    private String birthdate;
    private String email;
    private int semesterNr;
    private String degreeProgramName;

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

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
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

    public String getDegreeProgramName() {
        return degreeProgramName;
    }

    public void setDegreeProgramName(String degreeProgramName) {
        this.degreeProgramName = degreeProgramName;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
    public void apply(Person person) {
        person.setFirstname(firstname);
        person.setLastname(lastname);
        person.setBiography(biography);

        if (!birthdate.isBlank()) {
            person.setBirthdate(LocalDate.parse(birthdate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        person.setEmail(email);
        person.setSemesterNr(semesterNr);

        if (!degreeProgramName.isBlank()) {

            DegreeProgram degreeProgram = Util.storage.getDegreeProgram(degreeProgramName);
            if (degreeProgram != null) {
                person.setDegreeProgram(degreeProgram);
            }
        }
    }
}
