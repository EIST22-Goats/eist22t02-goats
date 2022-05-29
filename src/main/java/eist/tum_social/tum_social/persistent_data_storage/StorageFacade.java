package eist.tum_social.tum_social.persistent_data_storage;


import eist.tum_social.tum_social.model.Course;
import eist.tum_social.tum_social.model.DegreeProgram;
import eist.tum_social.tum_social.model.Person;

import java.util.List;

public interface StorageFacade {

    void updatePerson(Person person);
    Person getPerson(String tumId);
    List<Person> getPersons();
    void deletePerson(Person person);

    DegreeProgram getDegreeProgram(String degreeProgramName);
    List<DegreeProgram> getDegreePrograms();

    void updateCourse(Course course);
    Course getCourse(int courseId);
    List<Course> getCourses();

    void deleteCourse(Course course);

}
