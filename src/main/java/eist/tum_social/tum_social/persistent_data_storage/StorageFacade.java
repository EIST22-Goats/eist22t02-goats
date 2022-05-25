package eist.tum_social.tum_social.persistent_data_storage;


import eist.tum_social.tum_social.model.Course;
import eist.tum_social.tum_social.model.DegreeProgram;
import eist.tum_social.tum_social.model.Person;

import java.util.List;

public interface StorageFacade {

    void updatePerson(Person person);
    Person getPerson(String tumId);
    List<Person> getPersons();
    void deletePerson(String tumId);

    DegreeProgram getDegreeProgram(String degreeProgramName);
    List<DegreeProgram> getDegreePrograms();

    Course getCourse(int courseId);
    List<Course> getCourses();

}
