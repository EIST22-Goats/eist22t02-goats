package eist.tum_social.tum_social.model;

import eist.tum_social.tum_social.persistent_data_storage.util.BridgingTable;
import eist.tum_social.tum_social.persistent_data_storage.util.DatabaseEntity;
import eist.tum_social.tum_social.persistent_data_storage.util.ForeignTable;
import eist.tum_social.tum_social.persistent_data_storage.util.IgnoreInDatabase;

import java.util.List;

@DatabaseEntity(tableName = "Courses")
public class Course extends UniquelyIdentifiable {

    private int id = -1;
    private String name;
    private String acronym;
    private String description;
    @ForeignTable(ownColumnName = "adminId")
    private Person admin;
    @BridgingTable(
            bridgingTableName = "CourseParticipants",
            ownForeignColumnName = "courseId",
            otherForeignColumnName = "personId")
    private List<Person> participants;
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

    public Person getAdmin() {
        return admin;
    }

    public void setAdmin(Person admin) {
        this.admin = admin;
    }

    public List<Person> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Person> participants) {
        this.participants = participants;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }
}
