package eist.tum_social.tum_social.model;

import eist.tum_social.tum_social.persistent_data_storage.util.DatabaseEntity;
import eist.tum_social.tum_social.persistent_data_storage.util.IgnoreInDatabase;
import eist.tum_social.tum_social.persistent_data_storage.util.PrimaryKey;

import java.util.Date;
import java.util.List;

@DatabaseEntity(tableName = "Courses")
public class Course {

    @PrimaryKey
    private int id;
    private String name;
    private String acronym;
    private String description;
    @IgnoreInDatabase
    private List<Appointment> appointments;
    @IgnoreInDatabase
    private List<Comment> comments;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }
}
