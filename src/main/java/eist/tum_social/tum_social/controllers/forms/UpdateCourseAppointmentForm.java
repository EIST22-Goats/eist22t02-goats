package eist.tum_social.tum_social.controllers.forms;

import eist.tum_social.tum_social.model.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class UpdateCourseAppointmentForm {

    private int id;
    private String name;
    private String description;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate startDate;
    private int repetitions;

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

    public void apply(Appointment appointment) {
        appointment.setName(name);
        appointment.setDescription(description);
        appointment.setEndTime(endTime);
        appointment.setStartDate(startDate);
        appointment.setEndTime(endTime);
        appointment.setRepetitions(repetitions);
    }

}
