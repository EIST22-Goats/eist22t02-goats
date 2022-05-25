package eist.tum_social.tum_social.database;

import eist.tum_social.tum_social.model.DegreeProgram;
import eist.tum_social.tum_social.model.Person;

import java.util.List;

public interface DatabaseFacade {

    void update(Object bean);

    <T> List<T> select(Class<T> clazz);
    <T> List<T> select(Class<T> clazz, String whereCondition, boolean recursive);

}
