package eist.tum_social.tum_social.database;

import eist.tum_social.tum_social.model.DatabaseEntity;
import eist.tum_social.tum_social.model.DegreeProgram;
import eist.tum_social.tum_social.model.IgnoreInDatabase;
import eist.tum_social.tum_social.model.Person;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.sqlite.SQLiteDataSource;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class SqliteFacade implements DatabaseFacade {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String URL = "jdbc:sqlite:tum_social.db";
    private final SQLiteDataSource dataSource;

    public SqliteFacade() {
        dataSource = new SQLiteDataSource();
        dataSource.setUrl(URL);
    }

    public static void main(String[] args) {
        Person p = new Person();
        p.setFirstname("Peter");
        p.setLastname("One");
        p.setTumId("ge95bit");
        p.setEmail("some@mail.com");
        p.setPassword("test");
        p.setBirthdate(new java.util.Date());

        SqliteFacade db = new SqliteFacade();
        db.updateBean("Persons", p, "tumId='" + p.getTumId() + "'");
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

    private void updateBean(String database, Object bean, String whereCondition) {
        try {
            BeanInfo info = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] pds = info.getPropertyDescriptors();

            StringBuilder parameters = new StringBuilder();

            for (var it : pds) {
                String name = it.getName();
                if (!Objects.equals(name, "class")) {
                    if (!hasAnnotation(bean, name, IgnoreInDatabase.class)) {
                        Method readMethod = it.getReadMethod();
                        Object value = readMethod.invoke(bean);
                        if (DatabaseEntity.class.isAssignableFrom(readMethod.getReturnType())) {
                            name += "Id";
                        }
                        parameters.append(name).append("=").append(toSqlString(value)).append(",");
                    }
                }
            }
            parameters.deleteCharAt(parameters.length() - 1);

            String sqlQuery = "UPDATE " + database + " SET " + parameters + " WHERE " + whereCondition;

            System.out.println(sqlQuery);
            try (Connection conn = DriverManager.getConnection(dataSource.getUrl())) {
                PreparedStatement stmt = conn.prepareStatement(sqlQuery);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean hasAnnotation(Object bean, String attribute, Class<?> annotation) throws NoSuchFieldException {
        Field classMemberField = bean.getClass().getDeclaredField(attribute);

        Annotation[] annotations = classMemberField.getAnnotations();

        for (Annotation ann : annotations) {
            if (ann.annotationType() == annotation) {
                return true;
            }
        }
        return false;
    }

    public String toSqlString(Object object) {
        if (object == null) {
            return "NULL";
        } else if (object instanceof Date) {
            return "'" + DATE_FORMAT.format(object) + "'";
        } else if (object instanceof String) {
            return "'" + object + "'";
        } else if (object instanceof DatabaseEntity) {
            return ((DatabaseEntity) object).getId() + "";
        } else {
            return object.toString();
        }
    }
}
