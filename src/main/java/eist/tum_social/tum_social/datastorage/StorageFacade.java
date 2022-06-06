package eist.tum_social.tum_social.datastorage;


import eist.tum_social.tum_social.model.Appointment;
import eist.tum_social.tum_social.model.Course;
import eist.tum_social.tum_social.model.DegreeProgram;
import eist.tum_social.tum_social.model.Person;

import java.util.List;

public interface StorageFacade {

    Person getPerson(String tumId);
    List<Person> getPersons();

    DegreeProgram getDegreeProgram(String degreeProgramName);
    List<DegreeProgram> getDegreePrograms();

    Course getCourse(int courseId);
    List<Course> getCourses();

    Appointment getAppointment(int id);

    void update(Object object);

    void delete(Object object);

}
