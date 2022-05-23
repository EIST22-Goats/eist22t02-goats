package eist.tum_social.tum_social.model;

import java.util.Date;
import java.util.List;

public class OneTimeAppointment extends Appointment {

    Timeslot timeslot;

    public OneTimeAppointment(Location location, Timeslot timeslot) {
        super(location);
        this.timeslot = timeslot;
    }

    @Override
    public List<Timeslot> getTimeslots() {
        return null;
    }
}
