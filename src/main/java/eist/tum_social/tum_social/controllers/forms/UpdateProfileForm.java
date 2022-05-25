package eist.tum_social.tum_social.controllers.forms;

import eist.tum_social.tum_social.persistent_data_storage.StorageFacade;
import eist.tum_social.tum_social.persistent_data_storage.Storage;
import eist.tum_social.tum_social.model.DegreeProgram;
import eist.tum_social.tum_social.model.Person;

import java.text.ParseException;

import static eist.tum_social.tum_social.persistent_data_storage.Storage.DATE_FORMAT;

public class UpdateProfileForm {
    private String firstname;
    private String lastname;
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

    public void apply(Person person) {
        System.out.println(birthdate);
        person.setFirstname(firstname);
        person.setLastname(lastname);

        if (!birthdate.isBlank()) {
             try {
                 System.out.println("birthdate: "+birthdate);
                person.setBirthdate(DATE_FORMAT.parse(birthdate));
            } catch (ParseException ignored) {
            }
        }

        person.setEmail(email);
        person.setSemesterNr(semesterNr);

        StorageFacade db = new Storage();
        if (!degreeProgramName.isBlank()) {

            DegreeProgram degreeProgram = db.getDegreeProgram(degreeProgramName);
            if (degreeProgram != null) {
                person.setDegreeProgram(degreeProgram);
            }
        }
    }

}
