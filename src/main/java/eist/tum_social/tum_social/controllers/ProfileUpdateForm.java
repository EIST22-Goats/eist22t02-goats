package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.database.DatabaseFacade;
import eist.tum_social.tum_social.database.SqliteFacade;
import eist.tum_social.tum_social.database.util.ColumnMapping;
import eist.tum_social.tum_social.database.util.DatabaseEntity;
import eist.tum_social.tum_social.database.util.IgnoreInDatabase;
import eist.tum_social.tum_social.database.util.PrimaryKey;
import eist.tum_social.tum_social.model.DegreeProgram;
import eist.tum_social.tum_social.model.Person;
import eist.tum_social.tum_social.model.Timetable;

import java.util.Date;
import java.util.List;

public class ProfileUpdateForm {
    private String firstname;
    private String lastname;
    private Date birthdate;
    private String email;
    private int semesterNr;
    private String degreeProgramName;

    public ProfileUpdateForm() {
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

    public Date getRawBirthdate() {
        return birthdate;
    }

    public void setRawBirthdate(Date birthdate) {
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
         person.setFirstname(firstname);
         person.setLastname(lastname);
         person.setBirthdate(birthdate);
         person.setEmail(email);
         person.setSemesterNr(semesterNr);

         DatabaseFacade db = new SqliteFacade();
         DegreeProgram degreeProgram = db.select(DegreeProgram.class, "name'"+degreeProgramName+"'", false).get(0);
         person.setDegreeProgram(degreeProgram);
    }

}
