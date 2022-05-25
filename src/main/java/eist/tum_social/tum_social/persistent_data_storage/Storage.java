package eist.tum_social.tum_social.persistent_data_storage;

import eist.tum_social.tum_social.model.Course;
import eist.tum_social.tum_social.model.DegreeProgram;
import eist.tum_social.tum_social.model.Person;

import java.text.SimpleDateFormat;
import java.util.List;

public class Storage implements StorageFacade {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final Database db;

    public Storage() {
        db = new SqliteDatabase();
    }

    @Override
    public void updatePerson(Person person) {
        db.update(person);
    }

    @Override
    public Person getPerson(String tumId) {
        return firstOrNull(db.select(Person.class, "tumId='"+tumId+"'", true));
    }

    @Override
    public List<Person> getPersons() {
        return db.select(Person.class, "1", false);
    }

    @Override
    public void deletePerson(String tumId) {
        db.delete("Persons", "tumId='" + tumId + "'");
    }

    @Override
    public DegreeProgram getDegreeProgram(String degreeProgramName) {
        return firstOrNull(db.select(DegreeProgram.class, "name='" + degreeProgramName + "'", true));
    }

    @Override
    public List<DegreeProgram> getDegreePrograms() {
        return db.select(DegreeProgram.class, "1", true);
    }

    @Override
    public Course getCourse(int courseId) {
        return firstOrNull(db.select(Course.class, "id=" + courseId, true));
    }

    @Override
    public List<Course> getCourses() {
        return db.select(Course.class, "1", false);
    }

    private <T> T firstOrNull(List<T> list) {
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }


}
