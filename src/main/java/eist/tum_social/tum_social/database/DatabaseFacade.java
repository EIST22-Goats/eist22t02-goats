package eist.tum_social.tum_social.database;

import eist.tum_social.tum_social.model.DegreeProgram;
import eist.tum_social.tum_social.model.Person;

import java.util.List;

public interface DatabaseFacade {

    Person getPerson(String tumId);
    void addPerson(Person person);
    void updatePerson(Person person);
    void removePerson(String tumId);

    List<DegreeProgram> getDegreePrograms();

}
