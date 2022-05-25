package eist.tum_social.tum_social.controllers.forms;

import eist.tum_social.tum_social.controllers.Status;
import eist.tum_social.tum_social.database.DatabaseFacade;
import eist.tum_social.tum_social.database.SqliteFacade;
import eist.tum_social.tum_social.model.DegreeProgram;
import eist.tum_social.tum_social.model.Person;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static eist.tum_social.tum_social.controllers.Status.ERROR;
import static eist.tum_social.tum_social.controllers.Status.SUCCESS;
import static eist.tum_social.tum_social.database.SqliteFacade.DATE_FORMAT;

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

    public void createPerson(Person person) {
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

        DatabaseFacade db = new SqliteFacade();
        if (!degreeProgramName.isBlank()) {
            List<DegreeProgram> degreeProgram = db.select(DegreeProgram.class, "name='" + degreeProgramName + "'", false);
            if (!degreeProgram.isEmpty()) {
                person.setDegreeProgram(degreeProgram.get(0));
            }
        }
    }

}
