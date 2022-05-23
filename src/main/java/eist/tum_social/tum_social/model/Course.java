package eist.tum_social.tum_social.model;

import java.util.Date;
import java.util.List;

public class Course {

    private String name;
    private String description;
    private Date finalDate;
    private Date retakeDate;
    private List<Appointment> appointments;
    private List<Comment> comments;

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

    public Date getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(Date finalDate) {
        this.finalDate = finalDate;
    }

    public Date getRetakeDate() {
        return retakeDate;
    }

    public void setRetakeDate(Date retakeDate) {
        this.retakeDate = retakeDate;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }
}
