package eist.tum_social.tum_social.datastorage;

import java.lang.reflect.Field;
import java.util.Map;

public class ForeignEntity<T> extends Entity {

    private T value;
    private boolean loaded = false;

    public ForeignEntity(Database database, Field field, Map<String, Object> row) {
        super(database, field, row);
    }

    @Override
    boolean isSet() {
        return value != null;
    }

    public T get() {
        if (!loaded) {
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
