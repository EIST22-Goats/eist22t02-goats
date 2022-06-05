package eist.tum_social.tum_social.LazyDataStorage;

import eist.tum_social.tum_social.model.Appointment;
import eist.tum_social.tum_social.model.Person;
import eist.tum_social.tum_social.persistent_data_storage.Storage;
import eist.tum_social.tum_social.persistent_data_storage.util.*;
import org.sqlite.SQLiteDataSource;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


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
        Person p = db.select(Person.class, "tumId='ge47son'").get(0);
        System.out.println(p.getFirstname()+" "+p.getLastname());

        p.setFirstname("Florian");
        Appointment appointment = new Storage().getAppointment(31);
        p.getAppointments().add(appointment);

        db.update(p);

//        System.out.println(p.getFirstname());
//        for (var c : p.getCourses()) {
//            System.out.println(c.getName());
//            System.out.println(c.getParticipants().get(0).getCourses());
//        }
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

    // TODO
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

    // TODO
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

    // TODO
    private void updateBridgingTable(Field field, Object bean) {
        BridgingTable bridgingTable = field.getAnnotation(BridgingTable.class);
        String bridgingTableName = bridgingTable.bridgingTableName();

        System.out.println("updating bridging table "+bridgingTableName);

        String clearStatement = String.format("DELETE FROM %s WHERE %s=%s", bridgingTableName, bridgingTable.ownForeignColumnName(), getFieldValue(ID_COLUMN_NAME, bean));
        executeStatement(clearStatement);

        System.out.println(field.getName());

        Object others = getLazyFieldValue(field, bean);


        if (others instanceof List othersList) {
            for (Object other : othersList) {
                String sql = String.format("INSERT OR REPLACE INTO %s (%s, %s) VALUES (%s, %s)", bridgingTableName, bridgingTable.ownForeignColumnName(), bridgingTable.otherForeignColumnName(), getFieldValue(ID_COLUMN_NAME, bean), getFieldValue(ID_COLUMN_NAME, other));
                executeStatement(sql);
            }
        }
    }

    // TODO
    private void executeStatement(String sql) {
        try (Connection conn = DriverManager.getConnection(dataSource.getUrl())) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e + " SQL: " + sql);
        }
    }

    // TODO
    private Pair<String, String> createFieldSqlAssignment(Field field, Object bean) {
        String name = field.getName();

        Object value = getFieldValue(field, bean);
        if (value != null && !(name.equals(ID_COLUMN_NAME) && (int) value == -1)) {
            if (hasAnnotation(field, ForeignTable.class)) {
                name = field.getAnnotation(ForeignTable.class).ownColumnName();
                value = getLazyFieldValue(field, bean);
                try {
                    value = getFieldValue(value.getClass().getDeclaredField(ID_COLUMN_NAME), value);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }

            return new Pair<>(name, toSqlString(value));
        }
        return null;
    }

    private String getNameOfLazyGetter(Field field) {
        String field_name = field.getName();
        String actual_field_name = field_name;
        if (field_name.endsWith("Entity")) {
            actual_field_name = field_name.substring(0, field_name.length() - "Entity".length());
        } else if (field_name.endsWith("Entities")) {
            actual_field_name = field_name.substring(0, field_name.length() - "Entities".length())+"s";
        }
        return "get"+actual_field_name.substring(0, 1).toUpperCase() + actual_field_name.substring(1);
    }

    private Class<?> getTypeOfLazyField(Field field, Object bean) {
        try {
            if (field.getName().endsWith("Entity")) {
                String nameOfGetter = getNameOfLazyGetter(field);
                return bean.getClass().getMethod(nameOfGetter).getReturnType();
            } else {
                return field.getType();
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    private Object getLazyFieldValue(Field field, Object bean) {
        try {
            if (field.getName().endsWith("Entity") || field.getName().endsWith("Entities")) {
                String nameOfGetter = getNameOfLazyGetter(field);
                return bean.getClass().getMethod(nameOfGetter).invoke(bean);
            } else{
                Object value = getFieldValue(field, bean);
                return getFieldValue(field.getType().getDeclaredField(ID_COLUMN_NAME), value);
            }
        } catch (NoSuchMethodException | NoSuchFieldException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();


            throw new RuntimeException("Foreign key " + ID_COLUMN_NAME + " in object " + bean + " not found");
        }
    }
    // TODO
    private Object getFieldValue(String field, Object bean) {
        try {
            return getFieldValue(bean.getClass().getDeclaredField(field), bean);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO
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

    private Object getFieldValue(Field field, Object bean) {
        try {
            return new PropertyDescriptor(field.getName(), bean.getClass()).getReadMethod().invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            throw new BeanFieldException("Failed accessing Field " + field.getName() + " of Object " + bean);
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
