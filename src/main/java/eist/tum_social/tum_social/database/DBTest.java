package eist.tum_social.tum_social.database;

import java.sql.*;

public class DBTest {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:tum_social.db";

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement statement = conn.createStatement();
            ResultSet res = statement.executeQuery("SELECT * FROM Persons");
            while (res.next()) {
                System.out.println(res.getString("vorname"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
