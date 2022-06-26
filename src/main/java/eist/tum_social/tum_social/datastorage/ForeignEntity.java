package eist.tum_social.tum_social.datastorage;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * The lazy loading proxy for the foreign object.
 * @param <T> the type of the foreign object.
 */
public class ForeignEntity<T> extends Entity {

    private T value;
    private boolean loaded = false;

    public ForeignEntity() {
        super(null, null, null);
    }

    public ForeignEntity(Database database, Field field, Map<String, Object> row) {
        super(database, field, row);
    }

    @Override
    boolean isSet() {
        return value != null;
    }

    /**
     * Lazy loads the object.
     * @return the actual object.
     */
    public T get() {
        if (!loaded && database != null) {
            loaded = true;
            value = database.loadForeignTableObject(field, row);
        }
        return value;
    }

    public void set(T value) {
        loaded = true;
        this.value = value;
    }

}
