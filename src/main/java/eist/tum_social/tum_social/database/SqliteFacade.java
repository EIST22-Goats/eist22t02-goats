package eist.tum_social.tum_social.database;

import eist.tum_social.tum_social.database.util.*;
import eist.tum_social.tum_social.model.DegreeLevel;
import eist.tum_social.tum_social.model.DegreeProgram;
import eist.tum_social.tum_social.model.Person;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.sqlite.SQLiteDataSource;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SqliteFacade implements DatabaseFacade {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static final String URL = "jdbc:sqlite:tum_social.db";
    private final SQLiteDataSource dataSource;

    public SqliteFacade() {
        dataSource = new SQLiteDataSource();
        dataSource.setUrl(URL);
    }

    public static void main(String[] args) {
        SqliteFacade db = new SqliteFacade();
        var res = db.select(Person.class);
        for (var it : res) {
            System.out.println(it.getFirstname() + " " + it.getLastname() + " " + it.getDegreeProgram().getDegreeLevel().getName());
        }
    }

    public Person getPerson(String tumId) {
        QueryRunner run = new QueryRunner(dataSource);

        ResultSetHandler<List<Person>> h = new BeanListHandler<>(Person.class);
        try {
            List<Person> persons = run.query("SELECT * FROM Persons WHERE tumId='" + tumId + "'", h);
            if (!persons.isEmpty()) {
                return persons.get(0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void addPerson(Person person) {
        try (Connection conn = DriverManager.getConnection(dataSource.getUrl())) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE Persons (firstname, lastname, birthdate, tumId, email, password) VALUES (" + toSqlString(person.getFirstname()) + ", " + toSqlString(person.getLastname()) + ", " + toSqlString(person.getBirthdate()) + ", " + toSqlString(person.getTumId()) + ", " + toSqlString(person.getEmail()) + ", " + toSqlString(person.getPassword()) + ")");
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removePerson(String tumId) {
        try {
            Connection conn = DriverManager.getConnection(dataSource.getUrl());
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Persons WHERE tumId='" + tumId + "'");
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updatePerson(Person person) {
        try (Connection conn = DriverManager.getConnection(dataSource.getUrl())) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Persons (firstname, lastname, birthdate, tumId, email, password) VALUES (" + toSqlString(person.getFirstname()) + ", " + toSqlString(person.getLastname()) + ", " + toSqlString(person.getBirthdate()) + ", " + toSqlString(person.getTumId()) + ", " + toSqlString(person.getEmail()) + ", " + toSqlString(person.getPassword()) + ")");
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DegreeProgram> getDegreePrograms() {
        QueryRunner run = new QueryRunner(dataSource);

        ResultSetHandler<List<DegreeProgram>> h = new BeanListHandler<>(DegreeProgram.class);
        try {
            return run.query("SELECT * FROM DegreePrograms", h);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> select(Class<T> clazz) {
        return select(clazz, "1");
    }

    public <T> List<T> select(Class<T> clazz, String whereCondition) {
        String tableName = getTableName(clazz);
        String sql = "SELECT * FROM " + tableName + " WHERE " + whereCondition;

        List<T> ret = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            ResultSet res = conn.createStatement().executeQuery(sql);
            while (res.next()) {
                ret.add(createObject(clazz, res));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return ret;
    }

    private void update(Object bean) {
        String tableName = getTableName(bean);

        String whereCondition = null;
        StringBuilder parameters = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (Field field : bean.getClass().getDeclaredFields()) {
            String name = field.getName();

            if (!name.equals("class") && field.getAnnotation(IgnoreInDatabase.class) == null) {
                String value = getFieldValue(field, bean);

                ColumnMapping foreignKey;
                if (field.getAnnotation(PrimaryKey.class) != null) {
                    whereCondition = name + "=" + value;
                } else if ((foreignKey = field.getAnnotation(ColumnMapping.class)) != null) {
                    name = foreignKey.columnName();
                }

                parameters.append(name).append(",");
                values.append(value).append(",");
            }
        }

        parameters.deleteCharAt(parameters.length() - 1);
        values.deleteCharAt(values.length() - 1);

        if (whereCondition == null) {
            throw new RuntimeException("You specified no Primary key in you bean, lul still dumb?");
        }

        executeSql(String.format("INSERT OR REPLACE INTO %s (%s) VALUES (%s)", tableName, parameters, values));
    }

    private <T> T createObject(Class<T> clazz, ResultSet row) {
        try {
            T obj = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                if (field.getAnnotation(IgnoreInDatabase.class) == null) {
                    Object value = sqlToObject(field, row);
                    setFieldValue(field, obj, value);
                }
            }

            return obj;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private Object sqlToObject(Field field, ResultSet row) {
        Object value = null;

        try {
            ColumnMapping columnMapping;
            if ((columnMapping = field.getAnnotation(ColumnMapping.class)) != null) {
                if (columnMapping.isForeignKey()) {
                    String name = columnMapping.columnName();
                    if (row.getObject(name) != null) {
                        int key = row.getInt(name);
                        value = select(field.getType(), columnMapping.foreignKey() + "=" + key).get(0);
                    }
                } else {
                    value = row.getObject(columnMapping.columnName());
                }
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

    private String getFieldValue(Field field, Object bean) {
        try {
            Object obj = new PropertyDescriptor(field.getName(), Person.class).getReadMethod().invoke(bean);
            return toSqlString(obj);
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
            throw new RuntimeException(e);
        }
    }

    public String toSqlString(Object object) {
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
