package com.senorpez.projectcars;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RestController
class EventController {
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:~/projectcars";
    private static final String USER_NAME = "project_cars_api_user";
    private static final String USER_PASS = "o5RXD}XL!-K2";

    @RequestMapping(value = "/v1/events")
    public List<Event> events() {
        Connection conn = null;
        Statement stmt = null;
        final String sql = "SELECT id, name, carFilter FROM events;";
        final List<Event> events = new ArrayList<>();

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER_NAME, USER_PASS);
            stmt = conn.createStatement();

            ResultSet eventsResults = stmt.executeQuery(sql);
            while (eventsResults.next()) {
                Integer id = eventsResults.getInt("id");
                String name = eventsResults.getString("name");
                String carFilter = eventsResults.getString("carFilter");

                Statement roundsStmt = conn.createStatement();
                final String roundsSql = "SELECT id, trackID, laps, time FROM rounds WHERE eventID = " + id + ";";
                final List<Round> rounds = new ArrayList<>();

                ResultSet roundsResults = roundsStmt.executeQuery(roundsSql);
                while (roundsResults.next()) {
                    Integer roundId = roundsResults.getInt("id");
                    Integer trackId = roundsResults.getInt("trackID");
                    Integer laps = roundsResults.getInt("laps");
                    if (roundsResults.wasNull()) laps = null;
                    Integer time = roundsResults.getInt("time");
                    if (roundsResults.wasNull()) time = null;

                    Statement trackStmt = conn.createStatement();
                    final String trackSql = "SELECT id, location, variation, length, pitEntryX, pitEntryZ, pitExitX, pitExitZ " +
                            "FROM tracks " +
                            "WHERE id = " + trackId + ";";
                    Track track = null;

                    ResultSet trackResults = trackStmt.executeQuery(trackSql);
                    while (trackResults.next()) {
                        String location = trackResults.getString("location");
                        String variation = trackResults.getString("variation");
                        Float length = trackResults.getFloat("length");

                        Float pitEntryX = trackResults.getFloat("pitEntryX");
                        if (trackResults.wasNull()) pitEntryX = null;
                        Float pitEntryZ = trackResults.getFloat("pitEntryZ");
                        if (trackResults.wasNull()) pitEntryZ = null;

                        Float pitExitX = trackResults.getFloat("pitExitX");
                        if (trackResults.wasNull()) pitExitX = null;
                        Float pitExitZ = trackResults.getFloat("pitExitZ");
                        if (trackResults.wasNull()) pitExitZ = null;

                        track = new Track(
                                trackId,
                                location,
                                variation,
                                length,
                                pitEntryX,
                                pitEntryZ,
                                pitExitX,
                                pitExitZ);
                    }

                    trackResults.close();
                    trackStmt.close();

                    rounds.add(new Round(
                            roundId,
                            track,
                            laps,
                            time
                    ));
                }

                roundsResults.close();
                roundsStmt.close();

                Statement carsStmt = conn.createStatement();
                final String carsSql = "SELECT id, manufacturer, model FROM cars WHERE " + carFilter + ";";
                final List<Car> cars = new ArrayList<>();

                ResultSet carsResults = carsStmt.executeQuery(carsSql);
                while (carsResults.next()) {
                    Integer carID = carsResults.getInt("id");
                    String manufacturer = carsResults.getString("manufacturer");
                    String model = carsResults.getString("model");

                    cars.add(new Car(
                            carID,
                            manufacturer,
                            model
                    ));
                }

                carsResults.close();
                carsStmt.close();

                events.add(new Event(
                        id,
                        name,
                        cars,
                        rounds,
                        carFilter
                ));
            }

            eventsResults.close();
            stmt.close();
            conn.close();
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
        return events;
    }

    @RequestMapping(value = "/v1/events/{id}")
    public Event events(@PathVariable Integer id) {
        Connection conn = null;
        Statement stmt = null;
        final String sql = "SELECT id, name, carFilter FROM events WHERE id = " + id +";";
        Event event = null;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER_NAME, USER_PASS);
            stmt = conn.createStatement();

            ResultSet eventsResults = stmt.executeQuery(sql);
            while (eventsResults.next()) {
                String name = eventsResults.getString("name");
                String carFilter = eventsResults.getString("carFilter");

                Statement roundsStmt = conn.createStatement();
                final String roundsSql = "SELECT id, trackID, laps, time FROM rounds WHERE eventID = " + id + ";";
                final List<Round> rounds = new ArrayList<>();

                ResultSet roundsResults = roundsStmt.executeQuery(roundsSql);
                while (roundsResults.next()) {
                    Integer roundId = roundsResults.getInt("id");
                    Integer trackId = roundsResults.getInt("trackID");
                    Integer laps = roundsResults.getInt("laps");
                    if (roundsResults.wasNull()) laps = null;
                    Integer time = roundsResults.getInt("time");
                    if (roundsResults.wasNull()) time = null;

                    Statement trackStmt = conn.createStatement();
                    final String trackSql = "SELECT id, location, variation, length, pitEntryX, pitEntryZ, pitExitX, pitExitZ " +
                            "FROM tracks " +
                            "WHERE id = " + trackId + ";";
                    Track track = null;

                    ResultSet trackResults = trackStmt.executeQuery(trackSql);
                    while (trackResults.next()) {
                        String location = trackResults.getString("location");
                        String variation = trackResults.getString("variation");
                        Float length = trackResults.getFloat("length");

                        Float pitEntryX = trackResults.getFloat("pitEntryX");
                        if (trackResults.wasNull()) pitEntryX = null;
                        Float pitEntryZ = trackResults.getFloat("pitEntryZ");
                        if (trackResults.wasNull()) pitEntryZ = null;

                        Float pitExitX = trackResults.getFloat("pitExitX");
                        if (trackResults.wasNull()) pitExitX = null;
                        Float pitExitZ = trackResults.getFloat("pitExitZ");
                        if (trackResults.wasNull()) pitExitZ = null;

                        track = new Track(
                                trackId,
                                location,
                                variation,
                                length,
                                pitEntryX,
                                pitEntryZ,
                                pitExitX,
                                pitExitZ);
                    }

                    trackResults.close();
                    trackStmt.close();

                    rounds.add(new Round(
                            roundId,
                            track,
                            laps,
                            time
                    ));
                }

                roundsResults.close();
                roundsStmt.close();

                Statement carsStmt = conn.createStatement();
                final String carsSql = "SELECT id, manufacturer, model FROM cars WHERE " + carFilter + ";";
                final List<Car> cars = new ArrayList<>();

                ResultSet carsResults = carsStmt.executeQuery(carsSql);
                while (carsResults.next()) {
                    Integer carID = carsResults.getInt("id");
                    String manufacturer = carsResults.getString("manufacturer");
                    String model = carsResults.getString("model");

                    cars.add(new Car(
                            carID,
                            manufacturer,
                            model
                    ));
                }

                carsResults.close();
                carsStmt.close();

                event = new Event(id, name, cars, rounds, carFilter);
            }

            eventsResults.close();
            stmt.close();
            conn.close();
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
        return event;
    }

    @RequestMapping(value = "/v1/events/{id}/rounds")
    public List<Round> eventRounds(@PathVariable Integer id) {
        Connection conn = null;
        Statement stmt = null;
        final String sql = "SELECT id, name, carFilter FROM events WHERE id = " + id +";";
        final List<Round> rounds = new ArrayList<>();

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER_NAME, USER_PASS);
            stmt = conn.createStatement();

            ResultSet eventsResults = stmt.executeQuery(sql);
            while (eventsResults.next()) {
                Statement roundsStmt = conn.createStatement();
                final String roundsSql = "SELECT id, trackID, laps, time FROM rounds WHERE eventID = " + id + ";";

                ResultSet roundsResults = roundsStmt.executeQuery(roundsSql);
                while (roundsResults.next()) {
                    Integer roundId = roundsResults.getInt("id");
                    Integer trackId = roundsResults.getInt("trackID");
                    Integer laps = roundsResults.getInt("laps");
                    if (roundsResults.wasNull()) laps = null;
                    Integer time = roundsResults.getInt("time");
                    if (roundsResults.wasNull()) time = null;

                    Statement trackStmt = conn.createStatement();
                    final String trackSql = "SELECT id, location, variation, length, pitEntryX, pitEntryZ, pitExitX, pitExitZ " +
                            "FROM tracks " +
                            "WHERE id = " + trackId + ";";
                    Track track = null;

                    ResultSet trackResults = trackStmt.executeQuery(trackSql);
                    while (trackResults.next()) {
                        String location = trackResults.getString("location");
                        String variation = trackResults.getString("variation");
                        Float length = trackResults.getFloat("length");

                        Float pitEntryX = trackResults.getFloat("pitEntryX");
                        if (trackResults.wasNull()) pitEntryX = null;
                        Float pitEntryZ = trackResults.getFloat("pitEntryZ");
                        if (trackResults.wasNull()) pitEntryZ = null;

                        Float pitExitX = trackResults.getFloat("pitExitX");
                        if (trackResults.wasNull()) pitExitX = null;
                        Float pitExitZ = trackResults.getFloat("pitExitZ");
                        if (trackResults.wasNull()) pitExitZ = null;

                        track = new Track(
                                trackId,
                                location,
                                variation,
                                length,
                                pitEntryX,
                                pitEntryZ,
                                pitExitX,
                                pitExitZ);
                    }

                    trackResults.close();
                    trackStmt.close();

                    rounds.add(new Round(
                            roundId,
                            track,
                            laps,
                            time
                    ));
                }

                roundsResults.close();
                roundsStmt.close();
            }

            eventsResults.close();
            stmt.close();
            conn.close();
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
        return rounds;
    }

    @RequestMapping(value = "/v1/events/{eventID}/rounds/{roundID}")
    public Round eventRounds(@PathVariable Integer eventID, @PathVariable Integer roundID) {
        Connection conn = null;
        Statement stmt = null;
        final String sql = "SELECT id, name, carFilter FROM events WHERE id = " + eventID +";";
        Round round = null;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER_NAME, USER_PASS);
            stmt = conn.createStatement();

            ResultSet eventsResults = stmt.executeQuery(sql);
            while (eventsResults.next()) {
                Statement roundsStmt = conn.createStatement();
                final String roundsSql = "SELECT id, trackID, laps, time FROM rounds " +
                        "WHERE eventID = " + eventID + " AND id = " + roundID + ";";

                ResultSet roundsResults = roundsStmt.executeQuery(roundsSql);
                while (roundsResults.next()) {
                    Integer roundId = roundsResults.getInt("id");
                    Integer trackId = roundsResults.getInt("trackID");
                    Integer laps = roundsResults.getInt("laps");
                    if (roundsResults.wasNull()) laps = null;
                    Integer time = roundsResults.getInt("time");
                    if (roundsResults.wasNull()) time = null;

                    Statement trackStmt = conn.createStatement();
                    final String trackSql = "SELECT id, location, variation, length, pitEntryX, pitEntryZ, pitExitX, pitExitZ " +
                            "FROM tracks " +
                            "WHERE id = " + trackId + ";";
                    Track track = null;

                    ResultSet trackResults = trackStmt.executeQuery(trackSql);
                    while (trackResults.next()) {
                        String location = trackResults.getString("location");
                        String variation = trackResults.getString("variation");
                        Float length = trackResults.getFloat("length");

                        Float pitEntryX = trackResults.getFloat("pitEntryX");
                        if (trackResults.wasNull()) pitEntryX = null;
                        Float pitEntryZ = trackResults.getFloat("pitEntryZ");
                        if (trackResults.wasNull()) pitEntryZ = null;

                        Float pitExitX = trackResults.getFloat("pitExitX");
                        if (trackResults.wasNull()) pitExitX = null;
                        Float pitExitZ = trackResults.getFloat("pitExitZ");
                        if (trackResults.wasNull()) pitExitZ = null;

                        track = new Track(
                                trackId,
                                location,
                                variation,
                                length,
                                pitEntryX,
                                pitEntryZ,
                                pitExitX,
                                pitExitZ);
                    }

                    trackResults.close();
                    trackStmt.close();

                    round = new Round(roundID, track, laps, time);
                }

                roundsResults.close();
                roundsStmt.close();
            }

            eventsResults.close();
            stmt.close();
            conn.close();
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
        return round;
    }

    @RequestMapping(value = "/v1/events/{id}/cars")
    public List<Car> eventCars(@PathVariable Integer id) {
        Connection conn = null;
        Statement stmt = null;
        final String sql = "SELECT carFilter FROM events WHERE id = " + id +";";
        final List<Car> cars = new ArrayList<>();

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER_NAME, USER_PASS);
            stmt = conn.createStatement();

            ResultSet eventsResults = stmt.executeQuery(sql);
            while (eventsResults.next()) {
                String carFilter = eventsResults.getString("carFilter");
                Statement carsStmt = conn.createStatement();
                final String carsSql = "SELECT id, manufacturer, model FROM cars WHERE " + carFilter + ";";

                ResultSet carsResults = carsStmt.executeQuery(carsSql);
                while (carsResults.next()) {
                    Integer carID = carsResults.getInt("id");
                    String manufacturer = carsResults.getString("manufacturer");
                    String model = carsResults.getString("model");

                    cars.add(new Car(
                            carID,
                            manufacturer,
                            model
                    ));
                }

                carsResults.close();
                carsStmt.close();
            }

            eventsResults.close();
            stmt.close();
            conn.close();
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
        return cars;
    }
    @RequestMapping(value = "/v1/events/{eventId}/cars/{carId}")
    public Car eventCars(@PathVariable Integer eventId, @PathVariable Integer carId) {
        Connection conn = null;
        Statement stmt = null;
        final String sql = "SELECT carFilter FROM events WHERE id = " + eventId +";";
        Car car = null;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER_NAME, USER_PASS);
            stmt = conn.createStatement();

            ResultSet eventsResults = stmt.executeQuery(sql);
            while (eventsResults.next()) {
                String carFilter = eventsResults.getString("carFilter");
                Statement carsStmt = conn.createStatement();
                final String carsSql = "SELECT manufacturer, model FROM cars " +
                        "WHERE " + carFilter + " AND id = " + carId + ";";

                ResultSet carsResults = carsStmt.executeQuery(carsSql);
                while (carsResults.next()) {
                    String manufacturer = carsResults.getString("manufacturer");
                    String model = carsResults.getString("model");

                    car = new Car(carId, manufacturer, model);
                }

                carsResults.close();
                carsStmt.close();
            }

            eventsResults.close();
            stmt.close();
            conn.close();
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
        return car;
    }
}
