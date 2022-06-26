package eist.tum_social.tum_social.datastorage;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * The interface for database usage.
 */
public interface Database {

    /**
     * Selects all entries from a certain table that fit to the given condition.
     * @param clazz the class of the model. The table is derived from the @DatabaseEntity annotation on the model.
     * @param whereCondition the where condition. In order to get all elements set it to "1".
     * @return the list of all objects from the select command.
     * @param <T> the type of the model class. Is derived from the clazz parameter.
     */
    <T> List<T> select(Class<T> clazz, String whereCondition);

    /**
     * Loads the related object from the database.
     * @param field the field of the object whose foreign object should be loaded.
     * @param row the row as map where the needed ids can be read from.
     * @return the related object for the foreign field.
     * @param <T> the type of the foreign object.
     */
    <T> T loadForeignTableObject(Field field, Map<String, Object> row);

    /**
     * Loads all related object. Looks up the bridging table for all foreign ids.
     * @param field the field of the object whose bridging table objects should be loaded.
     * @param row the row as a map where the needed ids can be found.
     * @return the list with all related bridging objects.
     * @param <T> the type of the foreign model class.
     */
    <T> List<T> loadBridgingTableObjects(Field field, Map<String, Object> row);

    /**
     * Updates the given model object in the database.
     * @param bean the object that should be updated in the database.
     */
    void update(Object bean);

    /**
     * Deletes the model object in the database.
     * @param object the object that should be deleted.
     */
    void delete(Object object);

}
