package eist.tum_social.tum_social.database;

import eist.tum_social.tum_social.model.Person;

public interface DatabaseFacade {

    Person getPerson(String tumId);
    void addPerson(Person person);
    void removePerson(String tumId);

}
