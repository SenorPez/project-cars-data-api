package com.senorpez.projectcars;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@SpringBootApplication
public class Application {
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/";

    private static final String H2_DRIVER = "org.h2.Driver";
    private static final String H2_URL = "jdbc:h2:~/projectcars;MODE=mysql";

    private static final String USER_NAME = "pcarsapi_user";
    private static final String USER_PASS = "F=R4tV}p:Jb2>VqJ";
    private static final String DB_NAME = "projectcarsapi";

    public static void main(String[] args) {
        DatabaseFactory.main(args);
        SpringApplication.run(Application.class, args);
    }

    static Connection DatabaseConnection() throws ClassNotFoundException, SQLException {
        try {
            return MySQLConnection();
        } catch (ClassNotFoundException | SQLException e) {
            return H2Connection();
        }
    }

    private static Connection MySQLConnection() throws ClassNotFoundException, SQLException {
        Class.forName(MYSQL_DRIVER);
        Connection conn = DriverManager.getConnection(MYSQL_URL, USER_NAME, USER_PASS);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("USE " + DB_NAME + ";");
        }
        return conn;
    }

    private static Connection H2Connection() throws ClassNotFoundException, SQLException {
        Class.forName(H2_DRIVER);
        return DriverManager.getConnection(H2_URL, USER_NAME, USER_PASS);
    }
}
