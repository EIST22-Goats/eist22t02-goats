package eist.tum_social.tum_social.LazyDataStorage;

import eist.tum_social.tum_social.model.Person;
import eist.tum_social.tum_social.persistent_data_storage.util.*;
import org.sqlite.SQLiteDataSource;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SqliteDatabase implements Database {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH-mm");
    private static final String ID_COLUMN_NAME = "id";
    private static final String URL = "jdbc:sqlite:tum_social.db";
    private final SQLiteDataSource dataSource;

    public SqliteDatabase() {
        dataSource = new SQLiteDataSource();
        dataSource.setUrl(URL);
    }

    public static void main(String[] args) {
        Database db = new SqliteDatabase();
        Person p = db.select(Person.class, "tumId='ge95bit'").get(0);
        System.out.println(p.getFirstname());
        for (var c : p.getCourses()) {
            System.out.println(c.getName());
            System.out.println(c.getParticipants().get(0).getCourses());
        }
    }

    public <T> List<T> select(Class<T> clazz, String whereCondition) {
        return select(getTableName(clazz), clazz, whereCondition);
    }

    private <T> List<T> select(String tableName, Class<T> clazz, String whereCondition) {
        String sql = "SELECT * FROM " + tableName + " WHERE " + whereCondition;

        List<T> ret = new ArrayList<>();

        for (var row : executeQuery(sql)) {
            ret.add(instantiateBean(clazz, row));
        }

        return ret;
    }

    private <T> T instantiateBean(Class<T> clazz, Map<String, Object> row) {
        try {
            T object = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                if (!hasAnnotation(field, IgnoreInDatabase.class)) {
                    Object value = getValueFromRow(field, row);
                    setValueOfField(field, object, value);
                }
            }

            return object;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getValueFromRow(Field field, Map<String, Object> row) {
        Object value;

        if (hasAnnotation(field, ForeignTable.class)) {
            value = new ForeignEntity<>(this, field, row);
        } else if (hasAnnotation(field, BridgingTable.class)) {
            value = new BridgingEntities<>(this, field, row);
        } else {
            Object rawValue = row.get(field.getName());
            value = parseRawValue(field, rawValue);
        }

        return value;
    }

    public <T> T loadForeignTableObject(Field field, Map<String, Object> row) {
        ForeignTable foreignTable = field.getAnnotation(ForeignTable.class);
        String name = foreignTable.ownColumnName();

        if (row.get(name) != null) {
            int key = (int) row.get(name);
            String whereCondition = ID_COLUMN_NAME + "=" + key;
            return (T) select(foreignTable.foreignTableName(), getGenericType(field), whereCondition).get(0);
        }

        return null;
    }

    public <T> List<T> loadBridgingTableObjects(Field field, Map<String, Object> row) {
        BridgingTable bridgingTable = field.getAnnotation(BridgingTable.class);
        ArrayList<T> ret = new ArrayList<>();

        Class<?> listClass = getGenericType(field);
        String otherTableName = listClass.getAnnotation(DatabaseEntity.class).tableName();
        String sql = String.format(
                "SELECT %s.* FROM %s INNER JOIN %s ON %s=%s WHERE %s=%s",
                otherTableName,
                bridgingTable.bridgingTableName(),
                otherTableName,
                getTableName(listClass) + "." + ID_COLUMN_NAME,
                bridgingTable.otherForeignColumnName(),
                bridgingTable.ownForeignColumnName(),
                row.get(ID_COLUMN_NAME)
        );

        for (var new_row : executeQuery(sql)) {
            ret.add((T) instantiateBean(listClass, new_row));
        }

        return ret;
    }

    private void setValueOfField(Field field, Object bean, Object value) {
        try {
            if (value != null) {
                new PropertyDescriptor(field.getName(), bean.getClass()).getWriteMethod().invoke(bean, value);
            }
        } catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            throw new BeanFieldException(e + " Failed accessing Field " + field.getName() + " of Object " + bean);
        }
    }

    private List<Map<String, Object>> executeQuery(String sql) {
        List<Map<String, Object>> data;

        try (Connection conn = dataSource.getConnection()) {
            ResultSet resultSet = conn.createStatement().executeQuery(sql);
            data = resultSetToMap(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return data;
    }

    private List<Map<String, Object>> resultSetToMap(ResultSet resultSet) {
        List<Map<String, Object>> data = new ArrayList<>();

        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(columnName);
                    row.put(columnName, value);
                }
                data.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return data;
    }

    private Object parseRawValue(Field field, Object rawValue) {
        if (rawValue == null) {
            return null;
        }

        if (field.getType() == LocalDate.class) {
            return LocalDate.parse(rawValue.toString(), DATE_FORMAT);
        } else if (field.getType() == LocalTime.class) {
            return LocalTime.parse(rawValue.toString(), TIME_FORMAT);
        } else {
            return rawValue;
        }
    }

    private String getTableName(Class<?> clazz) {
        DatabaseEntity databaseAnnotation = clazz.getAnnotation(DatabaseEntity.class);
        if (databaseAnnotation == null) {
            throw new RuntimeException("Add @DatabaseEntity Annotation to your Bean, but lul u stupid");
        }
        return databaseAnnotation.tableName();
    }

    private Class<?> getGenericType(Field field) {
        ParameterizedType listType = (ParameterizedType) field.getGenericType();
        return (Class<?>) listType.getActualTypeArguments()[0];
    }

    private <T extends Annotation> boolean hasAnnotation(Field field, Class<T> annotationClass) {
        return field.getAnnotation(annotationClass) != null;
    }
}
