package eist.tum_social.tum_social.model;

import java.util.Date;
import java.util.List;

public class RepeatingAppointment extends Appointment {

    private Timeslot timeslot;

    private Date startDate;
    private Date endDate;

    public RepeatingAppointment(Location location, Timeslot timeslot, Date startDate, Date endDate) {
        super(location);
        this.timeslot = timeslot;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public List<Timeslot> getTimeslots() {
        return null;
    }
}
