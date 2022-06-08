package eist.tum_social.tum_social.controllers.forms;

import eist.tum_social.tum_social.controllers.forms.Form;
import eist.tum_social.tum_social.controllers.util.FormIgnore;
import eist.tum_social.tum_social.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class AppointmentForm extends Form<Appointment> {

    private String name;
    private String description;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate startDate;
    private int repetitions;
    @FormIgnore
    private String roomName;
    @FormIgnore
    private String address;

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

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public void apply(Appointment object) {
        super.apply(object);
        object.setAddress(Objects.requireNonNullElse(address, ""));
        object.setRoomName(Objects.requireNonNullElse(roomName, ""));
    }
}
