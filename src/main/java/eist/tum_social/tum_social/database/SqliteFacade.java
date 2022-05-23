package eist.tum_social.tum_social.database;

import eist.tum_social.tum_social.model.Person;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SqliteFacade implements DatabaseFacade {

    // TODO make static?

    private static final String URL = "jdbc:sqlite:tum_social.db";
    private final SQLiteDataSource dataSource;

    public SqliteFacade() {
        dataSource = new SQLiteDataSource();
        dataSource.setUrl(URL);
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

    public String toSqlString(Object object) {
        return object == null ? "NULL" : "'"+ object +"'";
    }

    @Override
    public void addPerson(Person person) {
        try {
            Connection conn = DriverManager.getConnection(dataSource.getUrl());

            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO Persons (firstname, lastname, birthdate, tumId, email, password) VALUES ("+
                            toSqlString(person.getFirstname())+ ", "+
                            toSqlString(person.getLastname())+ ", "+
                            toSqlString(person.getBirthdate()) + ", "+
                            toSqlString(person.getTumId())+", "+
                            toSqlString(person.getEmail())+", "+
                            toSqlString(person.getPassword())+")"
            );
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

    public static void main(String[] args) {
        Person p = new Person();
        p.setFirstname("Some");
        p.setLastname("One");
        p.setTumId("ge42non");
        p.setEmail("some@mail.com");
        p.setPassword("test");
        p.setBirthdate(new java.util.Date());

        DatabaseFacade db = new SqliteFacade();
        db.addPerson(p);
    }
}
