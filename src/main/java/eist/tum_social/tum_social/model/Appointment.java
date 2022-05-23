package eist.tum_social.tum_social.model;

import java.util.List;

public abstract class Appointment {

    protected Location location;

    public Appointment(Location location) {
        this.location = location;
    }

    public abstract List<Timeslot> getTimeslots();

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
