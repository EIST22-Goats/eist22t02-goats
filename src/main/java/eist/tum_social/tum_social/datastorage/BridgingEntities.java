package eist.tum_social.tum_social.datastorage;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * The lazy loading proxy for the list with all related objects of type T.
 * @param <T> the type of the bridging object.
 */
public class BridgingEntities<T> extends Entity {

    private List<T> values;

    public BridgingEntities() {
        super(null, null, null);
    }

    public BridgingEntities(Database database, Field field, Map<String, Object> row) {
        super(database, field, row);
    }

    @Override
    boolean isSet() {
        return values != null;
    }

    /**
     * Lazy loads the actual content.
     * @return the list with the bridging objects.
     */
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
