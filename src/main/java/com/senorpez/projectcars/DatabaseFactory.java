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
import java.util.stream.Stream;

public class DatabaseFactory {
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/";

    private static final String H2_DRIVER = "org.h2.Driver";
    private static final String H2_URL = "jdbc:h2:~/projectcars;MODE=mysql";

    private static final String ADMIN_USER = "root";
    private static final String ADMIN_PASS = "7,U-~N^EQhau8MAH";

    private static final String USER_NAME = "pcarsapi_user";
    private static final String USER_PASS = "F=R4tV}p:Jb2>VqJ";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        String databaseType;

        try {
            try {
                Class.forName(MYSQL_DRIVER);
                System.out.println("Connecting to MySQL database.");
                conn = DriverManager.getConnection(MYSQL_URL, ADMIN_USER, ADMIN_PASS);
                databaseType = "mysql";
            } catch (ClassNotFoundException | SQLException e) {
                Class.forName(H2_DRIVER);
                System.out.println("Connection to MySQL Failed.");
                System.out.println("Connecting to H2 database.");
                conn = DriverManager.getConnection(H2_URL, ADMIN_USER, ADMIN_PASS);
                databaseType = "h2";
            }

            stmt = conn.createStatement();
            System.out.println("Nuking existing database from orbit. It's the only way to be sure.");
            if (databaseType.equals("mysql")) {
                stmt.executeUpdate("DROP DATABASE IF EXISTS projectcarsapi;");
                stmt.executeUpdate("CREATE DATABASE projectcarsapi;");
                stmt.execute("USE projectcarsapi;");

                System.out.println("Creating access user.");
                try {
                    stmt.executeUpdate("DROP USER " + USER_NAME + ";");
                } catch (SQLException e) {
                    // No need to do anything. Later MySQL adds support for IF EXISTS
                }
                stmt.execute("CREATE USER " + USER_NAME + " IDENTIFIED BY  '" + USER_PASS + "';");
            } else if (databaseType.equals("h2")) {
                stmt.executeUpdate("DROP ALL OBJECTS;");
                stmt.execute("CREATE USER " + USER_NAME + " PASSWORD '" + USER_PASS + "';");
            }

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

        System.out.println("Creating Cars table.");
        String sql = "CREATE TABLE cars " +
                "(id INTEGER NOT NULL, " +
                " manufacturer VARCHAR(13) NOT NULL, " +
                " model VARCHAR(36) NOT NULL, " +
                " class VARCHAR(18) NOT NULL, " +
                " year SMALLINT UNSIGNED NOT NULL, " +
                " country VARCHAR(20) NOT NULL, " +
                " drivetrain CHAR(3) NOT NULL, " +
                " enginePosition VARCHAR(5) NOT NULL, " +
                " engineType VARCHAR(15) NOT NULL, " +
                " topSpeed SMALLINT UNSIGNED NOT NULL, " +
                " horsepower SMALLINT UNSIGNED NOT NULL, " +
                " acceleration DECIMAL(4, 2) UNSIGNED NOT NULL, " +
                " braking DECIMAL(3, 2) UNSIGNED NOT NULL, " +
                " weight SMALLINT UNSIGNED NOT NULL, " +
                " torque SMALLINT UNSIGNED NOT NULL, " +
                " weightBalance TINYINT UNSIGNED NOT NULL, " +
                " wheelbase DECIMAL(3, 2) UNSIGNED NOT NULL, " +
                " shiftPattern VARCHAR(10) NOT NULL, " +
                " shifter CHAR(7) NOT NULL, " +
                " gears TINYINT UNSIGNED NOT NULL, " +
                " dlc VARCHAR(43) NULL, " +
                " PRIMARY KEY (id), " +
                " UNIQUE (manufacturer, model));";
        stmt.executeUpdate(sql);

        System.out.println("Granting Cars privileges.");
        sql = "GRANT SELECT ON cars TO " + USER_NAME + ";";
        stmt.executeUpdate(sql);

        ObjectMapper mapper = new ObjectMapper();
        ClassLoader classLoader = DatabaseFactory.class.getClassLoader();

        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};
        List<Map<String, Object>> cars = null;
        String sqlKeys;
        String sqlValues;

        try {
            InputStream inputStream = classLoader.getResourceAsStream("data.json");
            Map<String, Object> jsonMap = mapper.readValue(inputStream, typeReference);
            cars = (List<Map<String, Object>>) jsonMap.get("cars");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (cars != null) {
            sqlKeys = cars
                    .stream()
                    .limit(1)
                    .flatMap(o -> o.keySet().stream())
                    .collect(Collectors.joining(", ", "(", ")"));

            sqlValues = cars
                    .stream()
                    .map(stringObjectMap -> stringObjectMap
                            .values()
                            .stream()
                            .map(o -> o == null ? "NULL" : "'" + o.toString() + "'")
                            .collect(Collectors.joining(", ", "(", ")")))
                    .collect(Collectors.joining(", "));

            System.out.println("Adding Cars data.");
            sql = "INSERT INTO cars " + sqlKeys + " VALUES " + sqlValues + ";";
            stmt.executeUpdate(sql);
        }

        stmt.close();
    }

    private static void CreateTracksTable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql;

        System.out.println("Creating Tracks table.");
        sql = "CREATE TABLE tracks " +
                "(id INTEGER NOT NULL, " +
                " name VARCHAR(39) NOT NULL, " +
                " location VARCHAR(30) NOT NULL, " +
                " variation VARCHAR(29) NOT NULL, " +
                " length DECIMAL(15,4) UNSIGNED NOT NULL, " +
                " pitEntryX DECIMAL(15,4) NULL, " +
                " pitEntryZ DECIMAL(15,4) NULL, " +
                " pitExitX DECIMAL(15,4) NULL, " +
                " pitExitZ DECIMAL(15,4) NULL, " +
                " gridSize TINYINT UNSIGNED NOT NULL, " +
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
        String sqlKeys;
        String sqlValues;

        try {
            InputStream inputStream = classLoader.getResourceAsStream("data.json");
            Map<String, Object> jsonMap = mapper.readValue(inputStream, typeReference);
            tracks = (List<Map<String, Object>>) jsonMap.get("tracks");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (tracks != null) {
            sqlKeys = tracks
                    .stream()
                    .limit(1)
                    .flatMap(o -> o.keySet().stream())
                    .collect(Collectors.joining(", ", "(", ")"));

            sqlValues = tracks
                    .stream()
                    .map(stringObjectMap -> stringObjectMap
                            .values()
                            .stream()
                            .map(o -> o == null ? "NULL" : "'" + o.toString() + "'")
                            .collect(Collectors.joining(", ", "(", ")")))
                    .collect(Collectors.joining(", "));
            System.out.println("Adding Tracks data.");
            sql = "INSERT INTO tracks " + sqlKeys + " VALUES " + sqlValues + ";";
            stmt.executeUpdate(sql);
        }

        stmt.close();
    }

    private static void CreateEventsTable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql;

        System.out.println("Creating Events table.");
        sql = "CREATE TABLE events " +
                "(id TINYINT UNSIGNED NOT NULL AUTO_INCREMENT, " +
                " name VARCHAR(41) NOT NULL, " +
                " carFilter VARCHAR(78) NOT NULL, " +
                " tier TINYINT UNSIGNED, " +
                " verified TINYINT UNSIGNED NOT NULL, " +
                " PRIMARY KEY (id));";
        stmt.executeUpdate(sql);

        System.out.println("Granting Events privileges.");
        sql = "GRANT SELECT ON events TO " + USER_NAME + ";";
        stmt.executeUpdate(sql);

        System.out.println("Creating Rounds table.");
        sql = "CREATE TABLE rounds " +
                "(id TINYINT UNSIGNED NOT NULL, " +
                " eventID TINYINT UNSIGNED NOT NULL, " +
                " trackID INTEGER NOT NULL, " +
                " PRIMARY KEY (id, eventID), " +
                " FOREIGN KEY (eventID) REFERENCES events(id) ON DELETE CASCADE);";
        stmt.executeUpdate(sql);

        System.out.println("Granting Rounds privileges.");
        sql = "GRANT SELECT ON rounds TO " + USER_NAME + ";";
        stmt.executeUpdate(sql);

        System.out.println("Creating Races table.");
        sql = "CREATE TABLE races " +
                "(id TINYINT UNSIGNED NOT NULL, " +
                " roundID TINYINT UNSIGNED NOT NULL, " +
                " eventID TINYINT UNSIGNED NOT NULL, " +
                " laps SMALLINT UNSIGNED, " +
                " time SMALLINT UNSIGNED, " +
                " type VARCHAR(50), " +
                " CHECK ((laps IS NOT NULL AND time IS NULL) OR (laps IS NULL AND time IS NOT NULL)), " +
                " PRIMARY KEY (id, roundID, eventID), " +
                " FOREIGN KEY (roundID, eventID) REFERENCES rounds(id, eventID) ON DELETE CASCADE);";
        stmt.executeUpdate(sql);

        System.out.println("Granting Races privileges.");
        sql = "GRANT SELECT ON races TO " + USER_NAME + ";";
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

                        String eventValues = Stream
                                .of(
                                        "'" + event.get("name") + "'",
                                        "'" + ((String) event.get("carFilter")).replace("'", "''") + "'",
                                        (event.get("tier") == null) ? "NULL" : event.get("tier").toString(),
                                        event.get("verified").toString())
                                .collect(Collectors.joining(", ", "(", ")"));

                        String eventSql = "INSERT INTO events (name, carFilter, tier, verified) " +
                                "VALUES " + eventValues + ";";
                        ResultSet eventKeys;
                        Integer eventID = 0;

                        try {
                            stmt.executeUpdate(eventSql, Statement.RETURN_GENERATED_KEYS);
                            eventKeys = stmt.getGeneratedKeys();
                            if (eventKeys.next()) {
                                eventID = eventKeys.getInt(1);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        final Integer finalEventID = eventID;
                        List<Map<String, Object>> rounds = (List<Map<String, Object>>) event.get("rounds");
                        AtomicInteger roundNumber = new AtomicInteger(0);

                        rounds
                                .forEach(round -> {
                                    System.out.println("Adding Round data.");

                                    String roundValues = Stream
                                            .of(
                                                    String.valueOf(roundNumber.incrementAndGet()),
                                                    finalEventID.toString(),
                                                    "(SELECT id FROM tracks WHERE location LIKE '" + round.get("location") + "' AND variation LIKE '" + round.get("variation") + "')")
                                            .collect(Collectors.joining(", ", "(", ")"));

                                    String roundSql = "INSERT INTO rounds (id, eventID, trackID) " +
                                            "VALUES " + roundValues + ";";

                                    try {
                                        stmt.executeUpdate(roundSql);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                    List<Map<String, Object>> races = (List<Map<String, Object>>) round.get("races");
                                    AtomicInteger raceNumber = new AtomicInteger(0);

                                    String raceValues = races
                                            .stream()
                                            .map(race -> Stream
                                                    .of(
                                                            String.valueOf(raceNumber.incrementAndGet()),
                                                            String.valueOf(roundNumber.get()),
                                                            finalEventID.toString(),
                                                            (race.get("laps") == null) ? "NULL" : race.get("laps").toString(),
                                                            (race.get("time") == null) ? "NULL" : race.get("time").toString(),
                                                            (race.get("type") == null) ? "NULL" : "'" + race.get("type").toString() + "'")
                                                    .collect(Collectors.joining(", ", "(", ")")))
                                            .collect(Collectors.joining(","));
                                    System.out.println("Adding Race data.");
                                    String raceSql = "INSERT INTO races (id, roundID, eventID, laps, time, type) " +
                                            "VALUES " + raceValues + ";";

                                    try {
                                        stmt.executeUpdate(raceSql);
                                    } catch (SQLException e){
                                        e.printStackTrace();
                                    }
                                });
                    });
        }
    }
}
