package com.senorpez.projectcars;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@Api(tags = {"events"})
@RequestMapping(method = {RequestMethod.GET})
class EventController {
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String MYSQL_URL = "jdbc:mysql://pcarsapi.cbwuidepjacv.us-west-2.rds.amazonaws.com:3306/";

    private static final String H2_DRIVER = "org.h2.Driver";
    private static final String H2_URL = "jdbc:h2:~/projectcars;MODE=mysql";

    private static final String USER_NAME = "pcarsapi_user";
    private static final String USER_PASS = "F=R4tV}p:Jb2>VqJ";

    @RequestMapping(value = "/v1/events")
    @ApiOperation(
            value = "Lists all events available",
            notes = "Returns a list of all single player events available through the Project CARS Data API",
            response = Event.class,
            responseContainer = "List"
    )
    public List<Event> events() {
        Connection conn = null;
        Statement stmt = null;
        final String sql = "SELECT id, name, carFilter, tier FROM events;";
        final List<Event> events = new ArrayList<>();

        try {
            try {
                Class.forName(MYSQL_DRIVER);
                conn = DriverManager.getConnection(MYSQL_URL, USER_NAME, USER_PASS);
                stmt = conn.createStatement();
                stmt.execute("USE projectcarsapi;");
            } catch (ClassNotFoundException | SQLException e) {
                Class.forName(H2_DRIVER);
                conn = DriverManager.getConnection(H2_URL, USER_NAME, USER_PASS);
                stmt = conn.createStatement();
            }

            ResultSet eventsResults = stmt.executeQuery(sql);
            while (eventsResults.next()) {
                Integer id = eventsResults.getInt("id");
                String name = eventsResults.getString("name");
                Integer tier = eventsResults.getInt("tier");
                if (eventsResults.wasNull()) tier = null;
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
                    final String trackSql = "SELECT name, location, variation, length, pitEntryX, pitEntryZ, pitExitX, pitExitZ, gridSize " +
                            "FROM tracks " +
                            "WHERE id = " + trackId + ";";
                    Track track = null;

                    ResultSet trackResults = trackStmt.executeQuery(trackSql);
                    while (trackResults.next()) {
                        String trackName = trackResults.getString("name");
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

                        Integer gridSize = trackResults.getInt("gridSize");

                        track = new Track(
                                trackId,
                                trackName,
                                location,
                                variation,
                                length,
                                pitEntryX,
                                pitEntryZ,
                                pitExitX,
                                pitExitZ,
                                gridSize);
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
                final String carsSql = "SELECT id, manufacturer, model, class, country FROM cars WHERE " + carFilter + ";";
                final List<Car> cars = new ArrayList<>();

                ResultSet carsResults = carsStmt.executeQuery(carsSql);
                while (carsResults.next()) {
                    Integer carID = carsResults.getInt("id");
                    String manufacturer = carsResults.getString("manufacturer");
                    String model = carsResults.getString("model");
                    String country = carsResults.getString("country");
                    String carClass = carsResults.getString("class");

                    cars.add(new Car(
                            carID,
                            manufacturer,
                            model,
                            country,
                            carClass
                    ));
                }

                carsResults.close();
                carsStmt.close();

                events.add(new Event(
                        id,
                        name,
                        tier,
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
    @ApiOperation(
            value = "Returns an event",
            notes = "Returns an event as specified by its ID number",
            response = Event.class
    )
    public Event events(
            @ApiParam(
                    value = "ID of event to return",
                    required = true
            )
            @PathVariable Integer id) {
        Connection conn = null;
        Statement stmt = null;
        final String sql = "SELECT id, name, carFilter, tier FROM events WHERE id = " + id + ";";
        Event event = null;

        try {
            try {
                Class.forName(MYSQL_DRIVER);
                conn = DriverManager.getConnection(MYSQL_URL, USER_NAME, USER_PASS);
                stmt = conn.createStatement();
                stmt.execute("USE projectcarsapi;");
            } catch (ClassNotFoundException | SQLException e) {
                Class.forName(H2_DRIVER);
                conn = DriverManager.getConnection(H2_URL, USER_NAME, USER_PASS);
                stmt = conn.createStatement();
            }

            ResultSet eventsResults = stmt.executeQuery(sql);
            while (eventsResults.next()) {
                String name = eventsResults.getString("name");
                String carFilter = eventsResults.getString("carFilter");
                Integer tier = eventsResults.getInt("tier");
                if (eventsResults.wasNull()) tier = null;

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
                    final String trackSql = "SELECT name, location, variation, length, pitEntryX, pitEntryZ, pitExitX, pitExitZ, gridSize " +
                            "FROM tracks " +
                            "WHERE id = " + trackId + ";";
                    Track track = null;

                    ResultSet trackResults = trackStmt.executeQuery(trackSql);
                    while (trackResults.next()) {
                        String trackName = trackResults.getString("name");
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

                        Integer gridSize = trackResults.getInt("gridSize");

                        track = new Track(
                                trackId,
                                trackName,
                                location,
                                variation,
                                length,
                                pitEntryX,
                                pitEntryZ,
                                pitExitX,
                                pitExitZ,
                                gridSize);
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
                final String carsSql = "SELECT id, manufacturer, model, class, country FROM cars WHERE " + carFilter + ";";
                final List<Car> cars = new ArrayList<>();

                ResultSet carsResults = carsStmt.executeQuery(carsSql);
                while (carsResults.next()) {
                    Integer carID = carsResults.getInt("id");
                    String manufacturer = carsResults.getString("manufacturer");
                    String model = carsResults.getString("model");
                    String country = carsResults.getString("country");
                    String carClass = carsResults.getString("class");

                    cars.add(new Car(
                            carID,
                            manufacturer,
                            model,
                            country,
                            carClass
                    ));
                }

                carsResults.close();
                carsStmt.close();

                event = new Event(id, name, tier, cars, rounds, carFilter);
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
    @ApiOperation(
            value = "Lists all rounds available for an event",
            notes = "Returns a list of all rounds in a single player event",
            response = Round.class,
            responseContainer = "List"
    )
    public List<Round> eventRounds(@PathVariable Integer id) {
        Connection conn = null;
        Statement stmt = null;
        final String sql = "SELECT id, name, carFilter, tier FROM events WHERE id = " + id + ";";
        final List<Round> rounds = new ArrayList<>();

        try {
            try {
                Class.forName(MYSQL_DRIVER);
                conn = DriverManager.getConnection(MYSQL_URL, USER_NAME, USER_PASS);
                stmt = conn.createStatement();
                stmt.execute("USE projectcarsapi;");
            } catch (ClassNotFoundException | SQLException e) {
                Class.forName(H2_DRIVER);
                conn = DriverManager.getConnection(H2_URL, USER_NAME, USER_PASS);
                stmt = conn.createStatement();
            }

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
                    final String trackSql = "SELECT name, location, variation, length, pitEntryX, pitEntryZ, pitExitX, pitExitZ, gridSize " +
                            "FROM tracks " +
                            "WHERE id = " + trackId + ";";
                    Track track = null;

                    ResultSet trackResults = trackStmt.executeQuery(trackSql);
                    while (trackResults.next()) {
                        String trackName = trackResults.getString("name");
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

                        Integer gridSize = trackResults.getInt("gridSize");

                        track = new Track(
                                trackId,
                                trackName,
                                location,
                                variation,
                                length,
                                pitEntryX,
                                pitEntryZ,
                                pitExitX,
                                pitExitZ,
                                gridSize);
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
    @ApiOperation(
            value = "Returns a round for an event",
            notes = "Returns a specific round for an event.",
            response = Round.class
    )
    public Round eventRounds(
            @ApiParam(
                    value = "ID of event",
                    required = true
            )
            @PathVariable Integer eventID,
            @ApiParam(
                    value = "Round number within event.",
                    required = true
            )
            @PathVariable Integer roundID) {
        Connection conn = null;
        Statement stmt = null;
        final String sql = "SELECT id, name, carFilter, tier FROM events WHERE id = " + eventID + ";";
        Round round = null;

        try {
            try {
                Class.forName(MYSQL_DRIVER);
                conn = DriverManager.getConnection(MYSQL_URL, USER_NAME, USER_PASS);
                stmt = conn.createStatement();
                stmt.execute("USE projectcarsapi;");
            } catch (ClassNotFoundException | SQLException e) {
                Class.forName(H2_DRIVER);
                conn = DriverManager.getConnection(H2_URL, USER_NAME, USER_PASS);
                stmt = conn.createStatement();
            }

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
                    final String trackSql = "SELECT name, location, variation, length, pitEntryX, pitEntryZ, pitExitX, pitExitZ, gridSize " +
                            "FROM tracks " +
                            "WHERE id = " + trackId + ";";
                    Track track = null;

                    ResultSet trackResults = trackStmt.executeQuery(trackSql);
                    while (trackResults.next()) {
                        String trackName = trackResults.getString("name");
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

                        Integer gridSize = trackResults.getInt("gridSize");

                        track = new Track(
                                trackId,
                                trackName,
                                location,
                                variation,
                                length,
                                pitEntryX,
                                pitEntryZ,
                                pitExitX,
                                pitExitZ,
                                gridSize);
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
    @ApiOperation(
            value = "Lists all cars eligible for an event",
            notes = "Returns a list of all cars eligible for a single player event",
            response = Car.class,
            responseContainer = "List"
    )
    public List<Car> eventCars(@PathVariable Integer id) {
        Connection conn = null;
        Statement stmt = null;
        final String sql = "SELECT carFilter, tier FROM events WHERE id = " + id + ";";
        final List<Car> cars = new ArrayList<>();

        try {
            try {
                Class.forName(MYSQL_DRIVER);
                conn = DriverManager.getConnection(MYSQL_URL, USER_NAME, USER_PASS);
                stmt = conn.createStatement();
                stmt.execute("USE projectcarsapi;");
            } catch (ClassNotFoundException | SQLException e) {
                Class.forName(H2_DRIVER);
                conn = DriverManager.getConnection(H2_URL, USER_NAME, USER_PASS);
                stmt = conn.createStatement();
            }

            ResultSet eventsResults = stmt.executeQuery(sql);
            while (eventsResults.next()) {
                String carFilter = eventsResults.getString("carFilter");
                Statement carsStmt = conn.createStatement();
                final String carsSql = "SELECT id, manufacturer, model, class, country FROM cars WHERE " + carFilter + ";";

                ResultSet carsResults = carsStmt.executeQuery(carsSql);
                while (carsResults.next()) {
                    Integer carID = carsResults.getInt("id");
                    String manufacturer = carsResults.getString("manufacturer");
                    String model = carsResults.getString("model");
                    String country = carsResults.getString("country");
                    String carClass = carsResults.getString("class");

                    cars.add(new Car(
                            carID,
                            manufacturer,
                            model,
                            country,
                            carClass
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
    @ApiOperation(
            value = "Returns an eligible car for an event",
            notes = "Returns an eligible car for an event",
            response = Car.class
    )
    public Car eventCars(
            @ApiParam(
                    value = "ID of event",
                    required = true
            )
            @PathVariable Integer eventId,
            @ApiParam(
                    value = "ID of car",
                    required = true
            )
            @PathVariable Integer carId) {
        Connection conn = null;
        Statement stmt = null;
        final String sql = "SELECT carFilter, tier FROM events WHERE id = " + eventId + ";";
        Car car = null;

        try {
            try {
                Class.forName(MYSQL_DRIVER);
                conn = DriverManager.getConnection(MYSQL_URL, USER_NAME, USER_PASS);
                stmt = conn.createStatement();
                stmt.execute("USE projectcarsapi;");
            } catch (ClassNotFoundException | SQLException e) {
                Class.forName(H2_DRIVER);
                conn = DriverManager.getConnection(H2_URL, USER_NAME, USER_PASS);
                stmt = conn.createStatement();
            }

            ResultSet eventsResults = stmt.executeQuery(sql);
            while (eventsResults.next()) {
                String carFilter = eventsResults.getString("carFilter");
                Statement carsStmt = conn.createStatement();
                final String carsSql = "SELECT manufacturer, model, class, country FROM cars " +
                        "WHERE " + carFilter + " AND id = " + carId + ";";

                ResultSet carsResults = carsStmt.executeQuery(carsSql);
                while (carsResults.next()) {
                    String manufacturer = carsResults.getString("manufacturer");
                    String model = carsResults.getString("model");
                    String country = carsResults.getString("country");
                    String carClass = carsResults.getString("class");

                    car = new Car(carId, manufacturer, model, country, carClass);
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
