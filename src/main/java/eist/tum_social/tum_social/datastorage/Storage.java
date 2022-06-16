package eist.tum_social.tum_social.datastorage;

import eist.tum_social.tum_social.model.*;

import java.util.List;

public class Storage implements StorageFacade {

    private final Database db;

    public Storage() {
        db = new SqliteDatabase();
    }

    @Override
    public Person getPerson(String tumId) {
        return firstOrNull(db.select(Person.class, "tumId='" + tumId + "'"));
    }

    @Override
    public List<Person> getPersons() {
        return db.select(Person.class, "1");
    }

    @Override
    public DegreeProgram getDegreeProgram(String degreeProgramName) {
        return firstOrNull(db.select(DegreeProgram.class, "name='" + degreeProgramName + "'"));
    }

    @Override
    public List<DegreeProgram> getDegreePrograms() {
        return db.select(DegreeProgram.class, "1");
    }

    @Override
    public Course getCourse(int courseId) {
        return firstOrNull(db.select(Course.class, "id=" + courseId));
    }

    @Override
    public List<Course> getCourses() {
        return db.select(Course.class, "1");
    }

    @Override
    public Appointment getAppointment(int id) {
        return firstOrNull(db.select(Appointment.class, "id=" + id));
    }

    public Comment getComment(int id) {
        return firstOrNull(db.select(Comment.class, "id="+id));
    }

    public Announcement getAnnouncement(int id) {
        return firstOrNull(db.select(Announcement.class, "id="+id));
    }


    @Override
    public void update(Object object) {
        db.update(object);
    }

    @Override
    public void delete(Object object) {
        db.delete(object);
    }

    private <T> T firstOrNull(List<T> list) {
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

}
