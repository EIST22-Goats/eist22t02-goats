package eist.tum_social.tum_social.DataStorage;

import java.lang.reflect.Field;
import java.util.Map;

public class ForeignEntity<T> extends Entity {

    private T value;

    public ForeignEntity(Database database, Field field, Map<String, Object> row) {
        super(database, field, row);
    }

    @Override
    boolean isSet() {
        return value != null;
    }

    public T get() {
        if (value == null) {
            value = database.loadForeignTableObject(field, row);
        }
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

}
