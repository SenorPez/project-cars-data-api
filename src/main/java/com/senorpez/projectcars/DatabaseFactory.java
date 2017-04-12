package com.senorpez.projectcars;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DatabaseFactory {
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String MYSQL_URL = "jdbc:mysql://pcarsapi.cbwuidepjacv.us-west-2.rds.amazonaws.com:3306/";

    private static final String H2_DRIVER = "org.h2.Driver";
    private static final String H2_URL = "jdbc:h2:~/projectcars;MODE=mysql";

    private static final String ADMIN_USER = "root";
    private static final String ADMIN_PASS = "7,U-~N^EQhau8MAH";

    private static final String USER_NAME = "project_cars_api_user";
    private static final String USER_PASS = "F=R4tV}p:Jb2>VqJ";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        String sql;

        try {
            try {
                Class.forName(MYSQL_DRIVER);
                System.out.println("Connecting to MySQL database.");
                conn = DriverManager.getConnection(MYSQL_URL, ADMIN_USER, ADMIN_PASS);
            } catch (ClassNotFoundException | SQLException e) {
                Class.forName(H2_DRIVER);
                System.out.println("Connection to MySQL Failed.");
                System.out.println("Connecting to H2 database.");
                conn = DriverManager.getConnection(H2_URL, ADMIN_USER, ADMIN_PASS);
            }

            stmt = conn.createStatement();
            System.out.println("Nuking existing database from orbit. It's the only way to be sure.");
            sql = "DROP ALL OBJECTS;";
            stmt.execute(sql);

            System.out.println("Creating access user.");
            sql = "CREATE USER " + USER_NAME + " PASSWORD '" + USER_PASS + "';";
            stmt.execute(sql);

            CreateCarsTable(conn);
            CreateTracksTable(conn);
            CreateEventsTable(conn);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void CreateCarsTable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql;

        System.out.println("Creating Cars table.");
        sql = "CREATE TABLE cars " +
                "(id INTEGER NOT NULL AUTO_INCREMENT, " +
                " manufacturer VARCHAR(255) NOT NULL, " +
                " model VARCHAR(255) NOT NULL, " +
                " class VARCHAR(255) NOT NULL, " +
                " country VARCHAR(255) NOT NULL, " +
                " PRIMARY KEY (id), " +
                " UNIQUE (manufacturer, model));";
        stmt.executeUpdate(sql);

        System.out.println("Granting Cars privileges.");
        sql = "GRANT SELECT ON cars to " + USER_NAME + ";";
        stmt.executeUpdate(sql);

        ObjectMapper mapper = new ObjectMapper();
        ClassLoader classLoader = DatabaseFactory.class.getClassLoader();

        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};
        List<Map<String, Object>> cars = null;
        String sqlValues;

        try {
            InputStream inputStream = classLoader.getResourceAsStream("data.json");
            Map<String, Object> jsonMap = mapper.readValue(inputStream, typeReference);
            cars = (List<Map<String, Object>>) jsonMap.get("cars");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (cars != null) {
            sqlValues = cars
                    .stream()
                    .sorted((o1, o2) -> {
                        int compareResult;
                        String mfg1 = (String) o1.get("manufacturer");
                        String mfg2 = (String) o2.get("manufacturer");

                        compareResult = mfg1.compareTo(mfg2);
                        if (compareResult == 0) {
                            String model1 = (String) o1.get("model");
                            String model2 = (String) o2.get("model");
                            compareResult = model1.compareTo(model2);
                        }
                        return compareResult;
                    })
                    .map(stringObjectMap -> stringObjectMap
                            .values()
                            .stream()
                            .map(o -> o == null ? "NULL" : "'" + o.toString() + "'")
                            .collect(Collectors.joining(", ", "(", ")")))
                    .collect(Collectors.joining(", "));

            System.out.println("Adding Cars data.");
            sql = "INSERT INTO cars " +
                    "(manufacturer, model, class, country) " +
                    "VALUES " + sqlValues + ";";
            stmt.executeUpdate(sql);
        }

        stmt.close();
    }

    private static void CreateTracksTable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql;

        System.out.println("Creating Tracks table.");
        sql = "CREATE TABLE tracks " +
                "(id INTEGER NOT NULL AUTO_INCREMENT, " +
                " location VARCHAR(255) NOT NULL, " +
                " variation VARCHAR(255) NOT NULL, " +
                " length DECIMAL(8,4) NOT NULL, " +
                " pitEntryX DECIMAL(8,4) NULL, " +
                " pitEntryZ DECIMAL(8,4) NULL, " +
                " pitExitX DECIMAL(8,4) NULL, " +
                " pitExitZ DECIMAL(8,4) NULL, " +
                " PRIMARY KEY (id), " +
                " UNIQUE (location, variation));";
        stmt.executeUpdate(sql);

        System.out.println("Granting Tracks privileges.");
        sql = "GRANT SELECT ON tracks TO " + USER_NAME + ";";
        stmt.executeUpdate(sql);

        ObjectMapper mapper = new ObjectMapper();
        ClassLoader classLoader = DatabaseFactory.class.getClassLoader();

        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};
        List<Map<String, Object>> tracks = null;
        String sqlValues;

        try {
            InputStream inputStream = classLoader.getResourceAsStream("data.json");
            Map<String, Object> jsonMap = mapper.readValue(inputStream, typeReference);
            tracks = (List<Map<String, Object>>) jsonMap.get("tracks");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (tracks != null) {
            sqlValues = tracks
                    .stream()
                    .sorted((o1, o2) -> {
                        int compareResult;
                        String location1 = (String) o1.get("location");
                        String location2 = (String) o2.get("location");

                        compareResult = location1.compareTo(location2);
                        if (compareResult == 0) {
                            String variation1 = (String) o1.get("variation");
                            String variation2 = (String) o2.get("variation");
                            compareResult = variation1.compareTo(variation2);
                        }
                        return compareResult;
                    })
                    .map(stringObjectMap -> stringObjectMap
                            .values()
                            .stream()
                            .map(o -> o == null ? "NULL" : "'" + o.toString() + "'")
                            .collect(Collectors.joining(", ", "(", ")")))
                    .collect(Collectors.joining(", "));
            System.out.println("Adding Tracks data.");
            sql = "INSERT INTO tracks " +
                    "(location, variation, length, pitEntryX, pitEntryZ, pitExitX, pitExitZ) " +
                    "VALUES " + sqlValues + ";";
            stmt.executeUpdate(sql);
        }

        stmt.close();
    }

    private static void CreateEventsTable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql;

        System.out.println("Creating Events table.");
        sql = "CREATE TABLE events " +
                "(id INTEGER NOT NULL AUTO_INCREMENT, " +
                " name VARCHAR(255) NOT NULL, " +
                " carFilter VARCHAR(255) NOT NULL, " +
                " tier INTEGER, " +
                " PRIMARY KEY (id));";
        stmt.executeUpdate(sql);

        System.out.println("Granting Events privileges.");
        sql = "GRANT SELECT ON events TO " + USER_NAME + ";";
        stmt.executeUpdate(sql);

        System.out.println("Creating Rounds table.");
        sql = "CREATE TABLE rounds " +
                "(id INTEGER NOT NULL, " +
                " eventID INTEGER NOT NULL, " +
                " trackID INTEGER NOT NULL, " +
                " laps INTEGER, " +
                " time INTEGER, " +
                " CHECK (laps IS NOT NULL AND time IS NULL) OR (laps IS NULL AND time IS NOT NULL), " +
                " PRIMARY KEY (id, eventID), " +
                " FOREIGN KEY (eventID) REFERENCES events(id) ON DELETE CASCADE);";
        stmt.executeUpdate(sql);

        System.out.println("Granting Rounds privileges.");
        sql = "GRANT SELECT ON rounds TO " + USER_NAME + ";";
        stmt.executeUpdate(sql);

        ObjectMapper mapper = new ObjectMapper();
        ClassLoader classLoader = DatabaseFactory.class.getClassLoader();

        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};
        List<Map<String, Object>> events = null;

        try {
            InputStream inputStream = classLoader.getResourceAsStream("data.json");
            Map<String, Object> jsonMap = mapper.readValue(inputStream, typeReference);
            events = (List<Map<String, Object>>) jsonMap.get("events");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (events != null) {
            events
                    .forEach(event -> {
                        System.out.println("Adding Event data.");
                        String eventName = "'" + event.get("name") + "'";
                        String carFilter = (String) event.get("carFilter");
                        Integer tier = (event.get("tier") == null) ? null : (Integer) event.get("tier");
                        carFilter = "'" + carFilter.replace("'", "''") + "'";
                        String eventSql = "INSERT INTO events (name, carFilter, tier) VALUES (" + eventName + ", " + carFilter + ", " + tier +");";
                        ResultSet keys;
                        Integer eventID = null;
                        AtomicInteger counter = new AtomicInteger(0);

                        try {
                            stmt.executeUpdate(eventSql, Statement.RETURN_GENERATED_KEYS);
                            keys = stmt.getGeneratedKeys();
                            while (keys.next()) {
                                eventID = keys.getInt(1);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        List<Map<String, Object>> rounds = (List<Map<String, Object>>) event.get("rounds");

                        final Integer eventIDfinal = eventID;
                        String roundsSQLValues = rounds
                                .stream()
                                .map(round -> {
                                    String location = "'" + round.get("location") + "'";
                                    String variation = "'" + round.get("variation") + "'";
                                    Integer laps = (round.get("laps") == null) ? null : (Integer) round.get("laps");
                                    Integer time = (round.get("time") == null) ? null : (Integer) round.get("time");


                                    return ("(" + counter.incrementAndGet() + ", " + eventIDfinal +
                                            ", (SELECT id FROM tracks WHERE location LIKE " + location + " AND variation LIKE " + variation + "), " + laps + ", " + time +")");
                                })
                                .collect(Collectors.joining(","));

                        System.out.println("Adding Rounds data.");
                        String roundSql = "INSERT INTO rounds " +
                                "(id, eventID, trackID, laps, time) " +
                                "VALUES " + roundsSQLValues + ";";
                        try {
                            stmt.executeUpdate(roundSql);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });

            ResultSet roundCount = stmt.executeQuery("SELECT COUNT(*) as roundCount FROM rounds;");
            if (roundCount.next()) {
                System.out.println("Rounds: " + roundCount.getInt("roundCount"));
            }
            roundCount.close();

        }
    }
}
