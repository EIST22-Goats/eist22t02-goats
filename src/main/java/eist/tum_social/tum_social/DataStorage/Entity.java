package eist.tum_social.tum_social.DataStorage;

import java.lang.reflect.Field;
import java.util.Map;

public abstract class Entity {

    protected Database database;
    protected Field field;
    protected Map<String, Object> row;

    public Entity(Database database, Field field, Map<String, Object> row) {
        this.database = database;
        this.field = field;
        this.row = row;
    }

    abstract boolean isSet();

}
