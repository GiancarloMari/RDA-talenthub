package model;

import java.sql.Connection;
import java.sql.DriverManager;

public class DB {
    public static Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(
                "jdbc:mysql://ucka.veleri.hr/gmari?user=gmari&password=11"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}