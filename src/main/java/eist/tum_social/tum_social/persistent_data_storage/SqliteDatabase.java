package eist.tum_social.tum_social.persistent_data_storage;

import eist.tum_social.tum_social.persistent_data_storage.util.*;
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

    public <T> List<T> select(Class<T> clazz, String whereCondition, boolean recursive) {
        String tableName = getTableName(clazz);
        String sql = "SELECT * FROM " + tableName + " WHERE " + whereCondition;

        List<T> ret = new ArrayList<>();

        for (var row : executeQuery(sql)) {
            ret.add(createObject(clazz, row, recursive));
        }

        return ret;
    }

    public Object reloadObject(Object bean) {
        return select(bean.getClass(), ID_COLUMN_NAME + "=" + getFieldValue(ID_COLUMN_NAME, bean), true).get(0);
    }

    public void update(Object bean) {
        String tableName = getTableName(bean);

        StringBuilder parameters = new StringBuilder();
        StringBuilder values = new StringBuilder();

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
                String sqlName = assignment.first();
                String sqlValue = assignment.second();

                parameters.append(sqlName).append(",");
                values.append(sqlValue).append(",");
            }
        }

        parameters.deleteCharAt(parameters.length() - 1);
        values.deleteCharAt(values.length() - 1);

        String sql = String.format("INSERT OR REPLACE INTO %s (%s) VALUES (%s);", tableName, parameters, values);
        int key = updateQuery(sql);
        setFieldValue(ID_COLUMN_NAME, bean, key);
    }

    public void delete(Object bean) {
        String tableName = getTableName(bean);
        int id = (int) getFieldValue(ID_COLUMN_NAME, bean);

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
    }

    private void updateBridgingTable(Field field, Object bean) {
        BridgingTable bridgingTable = field.getAnnotation(BridgingTable.class);
        String bridgingTableName = bridgingTable.bridgingTableName();

        String clearStatement = String.format("DELETE FROM %s WHERE %s=%s", bridgingTableName, bridgingTable.ownForeignColumnName(), getFieldValue(ID_COLUMN_NAME, bean));
        executeStatement(clearStatement);

        Object others = getFieldValue(field, bean);

        if (others instanceof List othersList) {
            for (Object other : othersList) {
                String sql = String.format("INSERT OR REPLACE INTO %s (%s, %s) VALUES (%s, %s)", bridgingTableName, bridgingTable.ownForeignColumnName(), bridgingTable.otherForeignColumnName(), getFieldValue(ID_COLUMN_NAME, bean), getFieldValue(ID_COLUMN_NAME, other));
                executeStatement(sql);
            }
        }

    }

    private Pair<String, String> createFieldSqlAssignment(Field field, Object bean) {
        String name = field.getName();

        Object value = getFieldValue(field, bean);
        if (value != null && !(name.equals(ID_COLUMN_NAME) && (int) value == -1)) {
            if (hasAnnotation(field, ForeignTable.class)) {
                name = field.getAnnotation(ForeignTable.class).ownColumnName();
                try {
                    value = getFieldValue(field.getType().getDeclaredField(ID_COLUMN_NAME), value);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException("Foreign key " + ID_COLUMN_NAME + " in object " + value + " not found");
                }
            }

            return new Pair<>(name, toSqlString(value));
        }
        return null;
    }

    private <T> T createObject(Class<T> clazz, Map<String, Object> row, boolean recursive) {
        try {
            T object = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                if (!hasAnnotation(field, IgnoreInDatabase.class)) {
                    Object value = readFieldValueFromRow(field, row, recursive);
                    setFieldValue(field, object, value);
                }
            }

            return object;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private Object readFieldValueFromRow(Field field, Map<String, Object> row, boolean recursive) {
        Object value;

        if (hasAnnotation(field, ForeignTable.class)) {
            value = readForeignTable(field, row, recursive);
        } else if (hasAnnotation(field, BridgingTable.class)) {
            value = readBridgingTable(field, row, recursive);
        } else {
            value = row.get(field.getName());
        }

        if (value != null) {
            if (field.getType() == LocalDate.class) {
                value = LocalDate.parse(value.toString(), DATE_FORMAT);
            } else if (field.getType() == LocalTime.class) {
                value = LocalTime.parse(value.toString(), TIME_FORMAT);
            }
        }

        return value;
    }

    private Object readForeignTable(Field field, Map<String, Object> row, boolean recursive) {
        ForeignTable foreignTable = field.getAnnotation(ForeignTable.class);
        String name = foreignTable.ownColumnName();

        if (row.get(name) != null) {
            int key = (int) row.get(name);
            String whereCondition = ID_COLUMN_NAME + "=" + key;
            return select(field.getType(), whereCondition, recursive).get(0);
        }

        return null;
    }

    private Object readBridgingTable(Field field, Map<String, Object> row, boolean recursive) {
        BridgingTable bridgingTable = field.getAnnotation(BridgingTable.class);
        ArrayList<Object> ret = new ArrayList<>();

        if (!recursive) {
            return ret;
        }

        Class<?> listClass = getListType(field);
        String otherTableName = listClass.getAnnotation(DatabaseEntity.class).tableName();
        String sql = String.format("SELECT %s.* FROM %s INNER JOIN %s ON %s=%s WHERE %s=%s", otherTableName, bridgingTable.bridgingTableName(), otherTableName, getTableName(listClass) + "." + ID_COLUMN_NAME, bridgingTable.otherForeignColumnName(), bridgingTable.ownForeignColumnName(), row.get(ID_COLUMN_NAME));

        for (var new_row : executeQuery(sql)) {
            ret.add(createObject(listClass, new_row, false));
        }

        return ret;
    }

    private Class<?> getListType(Field field) {
        ParameterizedType listType = (ParameterizedType) field.getGenericType();
        return (Class<?>) listType.getActualTypeArguments()[0];
    }

    private Object getFieldValue(String field, Object bean) {
        try {
            return getFieldValue(bean.getClass().getDeclaredField(field), bean);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getFieldValue(Field field, Object bean) {
        try {
            return new PropertyDescriptor(field.getName(), bean.getClass()).getReadMethod().invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            throw new BeanFieldException("Failed accessing Field " + field.getName() + " of Object " + bean);
        }
    }

    private void setFieldValue(String field, Object bean, Object value) {
        try {
            setFieldValue(bean.getClass().getDeclaredField(field), bean, value);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private void setFieldValue(Field field, Object bean, Object value) {
        try {
            if (value != null) {
                new PropertyDescriptor(field.getName(), bean.getClass()).getWriteMethod().invoke(bean, value);
            }
        } catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            throw new BeanFieldException(e + " Failed accessing Field " + field.getName() + " of Object " + bean);
        }
    }

    private String getTableName(Object bean) {
        return getTableName(bean.getClass());
    }

    private String getTableName(Class<?> clazz) {
        DatabaseEntity databaseAnnotation = clazz.getAnnotation(DatabaseEntity.class);
        if (databaseAnnotation == null) {
            throw new RuntimeException("Add @DatabaseEntity Annotation to your Bean, but lul u stupid");
        }
        return databaseAnnotation.tableName();
    }

    private void executeStatement(String sql) {
        try (Connection conn = DriverManager.getConnection(dataSource.getUrl())) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e + " SQL: " + sql);
        }
    }

    private int updateQuery(String sql) {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);

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

    private String toSqlString(Object object) {
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

    private <T extends Annotation> boolean hasAnnotation(Field field, Class<T> annotationClass) {
        return field.getAnnotation(annotationClass) != null;
    }
}
