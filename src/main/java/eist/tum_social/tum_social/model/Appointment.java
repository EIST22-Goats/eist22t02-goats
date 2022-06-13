package eist.tum_social.tum_social.model;

import eist.tum_social.tum_social.datastorage.BridgingEntities;
import eist.tum_social.tum_social.datastorage.ForeignEntity;
import eist.tum_social.tum_social.datastorage.util.BridgingTable;
import eist.tum_social.tum_social.datastorage.util.DatabaseEntity;
import eist.tum_social.tum_social.datastorage.util.ForeignTable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@DatabaseEntity(tableName = "Appointments")
public class Appointment extends UniquelyIdentifiable {

    private int id = -1;
    private String name;
    private String description;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate startDate;
    private int repetitions;
    private String address;
    private String roomName;
    @BridgingTable(
            bridgingTableName = "PersonAppointments",
            ownForeignColumnName = "appointmentId",
            otherForeignColumnName = "personId")
    private BridgingEntities<Person> subscriberEntities;
    @BridgingTable(
            bridgingTableName = "CourseAppointments",
            ownForeignColumnName = "appointmentId",
            otherForeignColumnName = "courseId")
    private BridgingEntities<Course> courseEntities;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public double getDurationInHours() {
        return startTime.until(endTime, ChronoUnit.MINUTES) / 60.0;
    }

    public BridgingEntities<Person> getSubscriberEntities() {
        return subscriberEntities;
    }

    public List<Person> getSubscribers() {
        return subscriberEntities.get();
    }

    public void setSubscriberEntities(BridgingEntities<Person> subscriberEntities) {
        this.subscriberEntities = subscriberEntities;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
