package eist.tum_social.tum_social.datastorage;

import eist.tum_social.tum_social.datastorage.util.*;
import eist.tum_social.tum_social.model.DegreeProgram;
import eist.tum_social.tum_social.model.Person;
import org.springframework.stereotype.Component;
import org.sqlite.SQLiteDataSource;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eist.tum_social.tum_social.datastorage.util.BeanUtil.getValueOfField;
import static eist.tum_social.tum_social.datastorage.util.BeanUtil.setValueOfField;
import static eist.tum_social.tum_social.datastorage.util.BeanUtil.hasAnnotation;

/**
 * The SQL implementation for the database interface. For more details look into the interface documentation.
 */
@Component
public class SqliteDatabase implements Database {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH-mm");
    private static final String ID_COLUMN_NAME = "id";
    private static final String URL = "jdbc:sqlite:tum_social.db";
    private final SQLiteDataSource dataSource;

    public SqliteDatabase() {
        this(URL);
    }

    public SqliteDatabase(String url) {
        dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
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

        if (bridgingTable.bidirectional()) {
            sql = String.format(
                    "SELECT %s.* FROM %s INNER JOIN %s ON %s=%s WHERE %s=%s",
                    otherTableName,
                    bridgingTable.bridgingTableName(),
                    otherTableName,
                    row.get(ID_COLUMN_NAME),
                    bridgingTable.otherForeignColumnName(),
                    bridgingTable.ownForeignColumnName(),
                    getTableName(listClass) + "." + ID_COLUMN_NAME
            );

            for (var new_row : executeQuery(sql)) {
                ret.add((T) instantiateBean(listClass, new_row));
            }
        }

        return ret;
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

    public void update(Object bean) {
        String tableName = getTableName(bean.getClass());

        List<String> parameters = new ArrayList<>();
        List<String> values = new ArrayList<>();

        for (Field field : bean.getClass().getDeclaredFields()) {
            String name = field.getName();

            if (hasAnnotation(field, IgnoreInDatabase.class) || name.equals("class")) {
                continue;
            }

            if (hasAnnotation(field, BridgingTable.class)) {
                updateBridgingTable(field, bean);
                continue;
            }

            Pair<String, String> assignment = createFieldSqlAssignment(field, bean);
            if (assignment != null) {
                parameters.add(assignment.first());
                values.add(assignment.second());
            }
        }

        String sql = String.format(
                "INSERT OR REPLACE INTO %s (%s) VALUES (%s);",
                tableName,
                String.join(", ", parameters),
                String.join(", ", values)
        );

        int key = updateQuery(sql);
        setIdOfBean(bean, key);
    }

    @Override
    public void delete(Object bean) {
        String tableName = getTableName(bean.getClass());
        int id = getIdOfBean(bean);

        String deleteSql = String.format("DELETE FROM %s WHERE %s=%s", tableName, ID_COLUMN_NAME, id);
        executeStatement(deleteSql);

        for (Field field : bean.getClass().getDeclaredFields()) {
            if (hasAnnotation(field, BridgingTable.class)) {
                deleteFromBridgingTable(field, id);
            }
        }
    }

    private void deleteFromBridgingTable(Field field, int id) {
        BridgingTable bridgingTable = field.getAnnotation(BridgingTable.class);

        String deleteSql = String.format("DELETE FROM %s WHERE %s=%s",
                bridgingTable.bridgingTableName(),
                bridgingTable.ownForeignColumnName(),
                id
        );
        executeStatement(deleteSql);

        if (bridgingTable.bidirectional()) {
            deleteSql = String.format("DELETE FROM %s WHERE %s=%s",
                    bridgingTable.bridgingTableName(),
                    bridgingTable.otherForeignColumnName(),
                    id
            );
            executeStatement(deleteSql);
        }
    }

    private int updateQuery(String sql) {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("insert failed");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return (int) generatedKeys.getLong(1);
                } else {
                    throw new SQLException();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateBridgingTable(Field field, Object bean) {
        BridgingTable bridgingTable = field.getAnnotation(BridgingTable.class);
        String bridgingTableName = bridgingTable.bridgingTableName();
        BridgingEntities<?> bridgingEntities = ((BridgingEntities<?>) getValueOfField(field, bean));

        if (bridgingEntities != null && bridgingEntities.isSet()) {
            String clearStatement = String.format(
                    "DELETE FROM %s WHERE %s=%s",
                    bridgingTableName,
                    bridgingTable.ownForeignColumnName(),
                    getIdOfBean(bean));
            executeStatement(clearStatement);

            if (bridgingTable.bidirectional()) {
                clearStatement = String.format(
                        "DELETE FROM %s WHERE %s=%s",
                        bridgingTableName,
                        bridgingTable.otherForeignColumnName(),
                        getIdOfBean(bean));
                executeStatement(clearStatement);
            }

            Object others = bridgingEntities.get();
            if (others instanceof List othersList) {
                for (Object other : othersList) {
                    String sql = String.format(
                            "INSERT OR REPLACE INTO %s (%s, %s) VALUES (%s, %s)",
                            bridgingTableName,
                            bridgingTable.ownForeignColumnName(),
                            bridgingTable.otherForeignColumnName(),
                            getIdOfBean(bean),
                            getIdOfBean(other)
                    );
                    executeStatement(sql);
                }
            }
        }
    }

    private Pair<String, String> createFieldSqlAssignment(Field field, Object bean) {
        String name = field.getName();
        Object value = getValueOfField(field, bean);

        if (value != null && !(name.equals(ID_COLUMN_NAME) && (int) value == -1)) {
            if (hasAnnotation(field, ForeignTable.class)) {
                name = field.getAnnotation(ForeignTable.class).ownColumnName();
                value = ((ForeignEntity<?>) value).get();

                if (value != null) {
                    value = getIdOfBean(value);
                }
            }
            String stringValue = valueToRawString(value);

            return new Pair<>(name, stringValue);
        } else {
            return null;
        }
    }

    private int getIdOfBean(Object bean) {
        try {
            return (int) getValueOfField(bean.getClass().getDeclaredField(ID_COLUMN_NAME), bean);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private void setIdOfBean(Object bean, Object value) {
        try {
            setValueOfField(bean.getClass().getDeclaredField(ID_COLUMN_NAME), bean, value);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
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
            value = rawStringToValue(field, rawValue);
        }

        return value;
    }

    private void executeStatement(String sql) {
        try (Connection conn = DriverManager.getConnection(dataSource.getUrl())) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e + " SQL: " + sql);
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

    private String valueToRawString(Object object) {
        if (object == null) {
            return "NULL";
        } else if (object instanceof LocalDate localDate) {
            return "'" + localDate.format(DATE_FORMAT) + "'";
        } else if (object instanceof LocalTime localTime) {
            return "'" + localTime.format(TIME_FORMAT) + "'";
        } else if (object instanceof String) {
            return "'" + object + "'";
        } else {
            return object.toString();
        }
    }

    private Object rawStringToValue(Field field, Object rawValue) {
        if (rawValue == null) {
            return null;
        } else if (field.getType() == LocalDate.class) {
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

}