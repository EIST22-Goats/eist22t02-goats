package eist.tum_social.tum_social.model;

import eist.tum_social.tum_social.datastorage.BridgingEntities;
import eist.tum_social.tum_social.datastorage.ForeignEntity;
import eist.tum_social.tum_social.datastorage.util.BridgingTable;
import eist.tum_social.tum_social.datastorage.util.DatabaseEntity;
import eist.tum_social.tum_social.datastorage.util.ForeignTable;
import eist.tum_social.tum_social.datastorage.util.IgnoreInDatabase;

import java.util.List;

@DatabaseEntity(tableName = "Courses")
public class Course extends UniquelyIdentifiable {

    private int id = -1;
    private String name;
    private String acronym;
    private String description;
    @ForeignTable(
            foreignTableName = "Persons",
            ownColumnName = "adminId")
    private ForeignEntity<Person> adminEntity = new ForeignEntity<>();
    @BridgingTable(
            bridgingTableName = "CourseParticipants",
            ownForeignColumnName = "courseId",
            otherForeignColumnName = "personId")
    private BridgingEntities<Person> participantEntities = new BridgingEntities<>();
    @BridgingTable(
            bridgingTableName = "CourseAppointments",
            ownForeignColumnName = "courseId",
            otherForeignColumnName = "appointmentId")
    private BridgingEntities<Appointment> appointmentEntities = new BridgingEntities<>();
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

    public ForeignEntity<Person> getAdminEntity() {
        return adminEntity;
    }

    public Person getAdmin() {
        return adminEntity.get();
    }

    public void setAdmin(Person p) {
        adminEntity.set(p);
    }

    public void setAdminEntity(ForeignEntity<Person> adminEntity) {
        this.adminEntity = adminEntity;
    }

    public BridgingEntities<Person> getParticipantEntities() {
        return participantEntities;
    }

    public List<Person> getParticipants() {
        return participantEntities.get();
    }

    public void setParticipantEntities(BridgingEntities<Person> participantEntities) {
        this.participantEntities = participantEntities;
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
