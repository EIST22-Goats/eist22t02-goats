package eist.tum_social.tum_social.LazyDataStorage;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class BridgingEntities<T> extends Entity {

    private List<T> values;

    public BridgingEntities(Database database, Field field, Map<String, Object> row) {
        super(database, field, row);
    }

    public List<T> get() {
        if (values == null) {
            values = database.loadBridgingTableObjects(field, row);
        }
        return values;
    }

    public void set(List<T> values) {
        this.values = values;
    }

}