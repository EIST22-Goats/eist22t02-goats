package eist.tum_social.tum_social.model;

import eist.tum_social.tum_social.persistent_data_storage.util.DatabaseEntity;
import eist.tum_social.tum_social.persistent_data_storage.util.ForeignTable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@DatabaseEntity(tableName = "Appointments")
public class Appointment extends UniquelyIdentifiable {

    private int id = -1;
    private String name;
    private String description;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate startDate;
    private int repetitions;
    @ForeignTable(ownColumnName = "locationId")
    private Location location;
    @ForeignTable(ownColumnName = "roomId")
    private Room room;

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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public double getDurationInHours() {
        return startTime.until(endTime, ChronoUnit.MINUTES) / 60.0;
    }
}
