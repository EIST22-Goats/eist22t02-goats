package eist.tum_social.tum_social.datastorage;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public interface Database {

    <T> List<T> select(Class<T> clazz, String whereCondition);

    <T> T loadForeignTableObject(Field field, Map<String, Object> row);
    <T> List<T> loadBridgingTableObjects(Field field, Map<String, Object> row);

    void update(Object bean);

    void delete(Object object);

}
