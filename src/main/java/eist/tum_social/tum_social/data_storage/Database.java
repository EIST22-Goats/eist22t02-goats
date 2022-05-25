package eist.tum_social.tum_social.data_storage;

import java.util.List;

public interface Database {

    <T> List<T> select(Class<T> clazz, String whereCondition, boolean recursive);

    void update(Object bean);

    void delete(String tableName, String whereCondition);

}
