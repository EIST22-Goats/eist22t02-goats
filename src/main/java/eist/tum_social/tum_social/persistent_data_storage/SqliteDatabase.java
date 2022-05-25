package eist.tum_social.tum_social.persistent_data_storage;

import eist.tum_social.tum_social.model.Course;
import eist.tum_social.tum_social.model.Person;
import eist.tum_social.tum_social.persistent_data_storage.util.*;
import org.apache.commons.dbutils.DbUtils;
import org.sqlite.SQLiteDataSource;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static eist.tum_social.tum_social.persistent_data_storage.Storage.DATE_FORMAT;

public class SqliteDatabase implements Database {

    private static final String URL = "jdbc:sqlite:tum_social.db";
    private final SQLiteDataSource dataSource;

    public SqliteDatabase() {
        dataSource = new SQLiteDataSource();
        dataSource.setUrl(URL);
    }

    public void delete(String tableName, String whereCondition) {
        executeSql(String.format("DELETE FROM %s WHERE %s", tableName, whereCondition));
    }

    public <T> List<T> select(Class<T> clazz, String whereCondition, boolean recursive) {
        String tableName = getTableName(clazz);
        String sql = "SELECT * FROM " + tableName + " WHERE " + whereCondition;

        List<T> ret = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            ResultSet res = conn.createStatement().executeQuery(sql);
            while (res.next()) {
                ret.add(createObject(clazz, res, recursive, conn));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
                    ColumnMapping foreignKey;
                    if (field.getAnnotation(PrimaryKey.class) != null) {
                        whereCondition = name + "=" + toSqlString(value);
                    } else {
                        if ((foreignKey = field.getAnnotation(ColumnMapping.class)) != null) {
                            name = foreignKey.columnName();
                            try {
                                value = getFieldValue(field.getType().getDeclaredField(foreignKey.foreignKey()), value);
                            } catch (NoSuchFieldException e) {
                                throw new RuntimeException("Foreign key " + foreignKey.foreignKey() + " in object " + value
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

        executeSql(String.format("INSERT OR REPLACE INTO %s (%s) VALUES (%s)", tableName, parameters, values));
    }

    private <T> T createObject(Class<T> clazz, ResultSet row, boolean recursive, Connection conn) {
        try {
            T obj = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                if (field.getAnnotation(IgnoreInDatabase.class) == null) {
                    Object value = rowToObject(field, row, recursive, conn);
                    setFieldValue(field, obj, value);
                }
            }

            return obj;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SqliteDatabase db = new SqliteDatabase();
        var res = db.select(Person.class, "tumId='ge95bit'", false);
        for (var it : res) {
            System.out.println(it.getCourses());
        }
    }

    private Object rowToObject(Field field, ResultSet row, boolean recursive, Connection conn) {
        Object value = null;

        try {
            BridgingTable bridgingTable;
            ColumnMapping columnMapping;
            if ((columnMapping = field.getAnnotation(ColumnMapping.class)) != null) {
                if (columnMapping.isForeignKey()) {
                    String name = columnMapping.columnName();
                    if (recursive && row.getObject(name) != null) {
                        int key = row.getInt(name);
                        value = select(field.getType(), columnMapping.foreignKey() + "=" + key, recursive).get(0);
                    }
                } else {
                    value = row.getObject(columnMapping.columnName());
                }
            } else if ((bridgingTable = field.getAnnotation(BridgingTable.class)) != null) {
                Class<?> listClass = getListType(field);
                String otherTableName = listClass.getAnnotation(DatabaseEntity.class).tableName();
                String sql = String.format(
                        "SELECT * FROM %s INNER JOIN %s ON %s=%s WHERE %s=%s",
                        bridgingTable.tableName(),
                        otherTableName,
                        getTableName(listClass) + "." + bridgingTable.otherColumnName(),
                        bridgingTable.otherForeignColumnName(),
                        bridgingTable.ownForeignColumnName(),
                        row.getInt(bridgingTable.ownColumnName())
                );
                System.out.println(">>>>>"+sql);
                ResultSet res = conn.createStatement().executeQuery(sql);
                ArrayList<Object> ret = new ArrayList();
                while (res.next()) {
                    ret.add(createObject(listClass, res, false, conn));
                }
                value = ret;
            } else {
                value = row.getObject(field.getName());
            }

            if (value != null && field.getType() == Date.class) {
                value = DATE_FORMAT.parse(value.toString());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException("Date was not stored correctly in database");
        }
        return value;
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

    private void executeSql(String sql) {
        try (Connection conn = DriverManager.getConnection(dataSource.getUrl())) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e + " SQL: " + sql);
        }
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
}
