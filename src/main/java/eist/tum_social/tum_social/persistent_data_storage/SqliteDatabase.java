package eist.tum_social.tum_social.persistent_data_storage;

import eist.tum_social.tum_social.model.Course;
import eist.tum_social.tum_social.model.DegreeProgram;
import eist.tum_social.tum_social.model.Person;
import eist.tum_social.tum_social.persistent_data_storage.util.*;
import org.sqlite.SQLiteDataSource;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.text.ParseException;
import java.util.*;
import java.util.Date;

import static eist.tum_social.tum_social.persistent_data_storage.Storage.DATE_FORMAT;

public class SqliteDatabase implements Database {

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

        for (var row: executeQuery(sql)) {
            ret.add(createObject(clazz, row, recursive));
        }

        return ret;
    }

    public void update(Object bean) {
        String tableName = getTableName(bean);

        String whereCondition = null;
        StringBuilder parameters = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (Field field : bean.getClass().getDeclaredFields()) {
            String name = field.getName();

            if (!name.equals("class") && field.getAnnotation(IgnoreInDatabase.class) == null) {
                Object value = getFieldValue(field, bean);

                if (value != null) {
                    ForeignTable foreignKey;
                    if (field.getAnnotation(PrimaryKey.class) != null) {
                        whereCondition = name + "=" + toSqlString(value);
                    } else {
                        if ((foreignKey = field.getAnnotation(ForeignTable.class)) != null) {
                            name = foreignKey.ownColumnName();
                            try {
                                value = getFieldValue(field.getType().getDeclaredField(foreignKey.foreignColumnName()), value);
                            } catch (NoSuchFieldException e) {
                                throw new RuntimeException("Foreign key " + foreignKey.foreignColumnName() + " in object " + value
                                        + " not found");
                            }
                        }

                        parameters.append(name).append(",");
                        values.append(toSqlString(value)).append(",");
                    }
                }
            }
        }

        parameters.deleteCharAt(parameters.length() - 1);
        values.deleteCharAt(values.length() - 1);

        if (whereCondition == null) {
            throw new RuntimeException("You specified no Primary key in you bean, lul still dumb?");
        }

        executeStatement(String.format("INSERT OR REPLACE INTO %s (%s) VALUES (%s)", tableName, parameters, values));
    }

    public void delete(String tableName, String whereCondition) {
        executeStatement(String.format("DELETE FROM %s WHERE %s", tableName, whereCondition));
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

    public static void main(String[] args) {
        SqliteDatabase db = new SqliteDatabase();
        var res = db.select(Person.class, "tumId='ge95bit'", true);
        for (var it : res) {
            System.out.println("Person: "+it.getFirstname());
            List<Course> courses = it.getCourses();
            System.out.println("   Kurse:");
            for (var course: courses) {
                System.out.println("       "+course.getName());
            }
            DegreeProgram degreeProgram = it.getDegreeProgram();
            System.out.println("    DegreeProgram:"+degreeProgram.getName());
        }
    }

    private Object readFieldValueFromRow(Field field, Map<String, Object> row, boolean recursive) {
        Object value;

        if (hasAnnotation(field, ForeignTable.class)) {
            value = readForeignTable(field.getAnnotation(ForeignTable.class), field, row, recursive);
        } else if (hasAnnotation(field, BridgingTable.class)) {
            value = readBridgingTable(field.getAnnotation(BridgingTable.class), field, row, recursive);
        } else {
            value = row.get(field.getName());
        }

        try {
            if (value != null && field.getType() == Date.class) {
                value = DATE_FORMAT.parse(value.toString());
            }
        } catch (ParseException e) {
            throw new RuntimeException("Date was not stored correctly in database");
        }

        return value;
    }

    private Object readForeignTable(ForeignTable foreignTable, Field field, Map<String, Object> row, boolean recursive) {
        String name = foreignTable.ownColumnName();

        if (recursive && row.get(name) != null) {
            int key = (int) row.get(name);
            String whereCondition = foreignTable.foreignColumnName() + "=" + key;
            return select(field.getType(), whereCondition, recursive).get(0);
        }

        return null;
    }

    private Object readBridgingTable(BridgingTable bridgingTable, Field field, Map<String, Object> row, boolean recursive) {
        ArrayList<Object> ret = new ArrayList<>();
        if (!recursive) {
            return ret;
        }

        Class<?> listClass = getListType(field);
        String otherTableName = listClass.getAnnotation(DatabaseEntity.class).tableName();
        String sql = String.format(
                "SELECT * FROM %s INNER JOIN %s ON %s=%s WHERE %s=%s",
                bridgingTable.bridgingTableName(),
                otherTableName,
                getTableName(listClass) + "." + bridgingTable.otherColumnName(),
                bridgingTable.otherForeignColumnName(),
                bridgingTable.ownForeignColumnName(),
                row.get(bridgingTable.ownColumnName())
        );

        for (var new_row: executeQuery(sql)) {
            ret.add(createObject(listClass, new_row, false));
        }
        return ret;
    }

    private Class<?> getListType(Field field) {
        ParameterizedType listType = (ParameterizedType) field.getGenericType();
        return (Class<?>) listType.getActualTypeArguments()[0];
    }

    private Object getFieldValue(Field field, Object bean) {
        try {
            return new PropertyDescriptor(field.getName(), bean.getClass()).getReadMethod().invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            throw new BeanFieldException("Failed accessing Field " + field.getName() + " of Object " + bean);
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
        } else if (object instanceof Date) {
            return "'" + DATE_FORMAT.format(object) + "'";
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
