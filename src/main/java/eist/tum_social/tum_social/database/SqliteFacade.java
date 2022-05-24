package eist.tum_social.tum_social.database;

import eist.tum_social.tum_social.database.util.ColumnMapping;
import eist.tum_social.tum_social.database.util.DatabaseEntity;
import eist.tum_social.tum_social.database.util.IgnoreInDatabase;
import eist.tum_social.tum_social.database.util.PrimaryKey;
import eist.tum_social.tum_social.model.DegreeProgram;
import eist.tum_social.tum_social.model.Person;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.validation.ValidationUtils;
import org.sqlite.SQLiteDataSource;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        Person p = new Person();
        p.setId(4);
        p.setFirstname("Kilian");
        p.setLastname("Northoff");
        p.setTumId("ge95bit");
        p.setEmail("some@mail.com");
        p.setPassword("test");
        try {
            p.setBirthdate(DATE_FORMAT.parse("28-12-2002"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        SqliteFacade db = new SqliteFacade();
        db.updateBean(p);
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

    private void updateBean(Object bean) {
        try {
            DatabaseEntity databaseAnnotation = bean.getClass().getAnnotation(DatabaseEntity.class);
            if (databaseAnnotation == null) {
                throw new RuntimeException("wtf this should never happen lul");
            }
            String tableName = databaseAnnotation.tableName();

            String whereCondition = null;
            StringBuilder parameters = new StringBuilder();

            for (Field field : bean.getClass().getDeclaredFields()) {
                String name = field.getName();

                if (!name.equals("class")) {
                    if (field.getAnnotation(IgnoreInDatabase.class) == null) {
                        Object obj = new PropertyDescriptor(field.getName(), Person.class).getReadMethod().invoke(bean);
                        String value = toSqlString(obj);

                        ColumnMapping foreignKey;
                        if (field.getAnnotation(PrimaryKey.class) != null) {
                            whereCondition = name + "=" + value;
                        } else if ((foreignKey = field.getAnnotation(ColumnMapping.class)) != null) {
                            name = foreignKey.columnName();
                        }

                        parameters.append(name).append("=").append(value).append(",");
                    }
                }
            }

            parameters.deleteCharAt(parameters.length() - 1);

            if (whereCondition != null) {
                String sqlQuery = "UPDATE " + tableName + " SET " + parameters + " WHERE " + whereCondition;

                System.out.println(sqlQuery);
                try (Connection conn = DriverManager.getConnection(dataSource.getUrl())) {
                    PreparedStatement stmt = conn.prepareStatement(sqlQuery);
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
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
