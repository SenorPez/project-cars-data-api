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
import java.util.stream.Collectors;

@RestController
@Api(tags = {"events"})
@RequestMapping(method = {RequestMethod.GET})
class EventController {
    @RequestMapping(value = "/v1/events")
    @ApiOperation(
            value = "Lists all events available",
            notes = "Returns a list of all single player events available through the Project CARS Data API",
            response = Event.class,
            responseContainer = "List"
    )
    public List<Event> events() {
        try (Connection conn = Application.DatabaseConnection()) {
            return eventQuery(conn);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/v1/events/{eventId}")
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
            @PathVariable Integer eventId) {
        try (Connection conn = Application.DatabaseConnection()) {
            return eventQuery(conn, eventId);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/v1/events/{eventId}/rounds")
    @ApiOperation(
            value = "Lists all rounds available for an event",
            notes = "Returns a list of all rounds in a single player event",
            response = Round.class,
            responseContainer = "List"
    )
    public List<Round> eventRounds(@PathVariable Integer eventId) {
        try (Connection conn = Application.DatabaseConnection()) {
            return roundQuery(conn, eventId);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
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
        try (Connection conn = Application.DatabaseConnection()) {
            return roundQuery(conn, eventID, roundID);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/v1/events/{eventId}/cars")
    @ApiOperation(
            value = "Lists all cars eligible for an event",
            notes = "Returns a list of all cars eligible for a single player event",
            response = Car.class,
            responseContainer = "List"
    )
    public List<Car> eventCars(@PathVariable Integer eventId) {
        try (Connection conn = Application.DatabaseConnection()) {
            Event event = eventQuery(conn, eventId);
            return (event == null) ? null : CarController.carQuery(conn, event.getCarFilter());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
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
        try (Connection conn = Application.DatabaseConnection()) {
            Event event = eventQuery(conn, eventId);
            return (event == null) ? null : CarController.carQuery(conn, event.getCarFilter(), carId);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Event> processEventResults(final Connection conn, final ResultSet eventResults) throws SQLException {
        final List<Event> events = new ArrayList<>();
        while (eventResults.next()) {
            Integer eventId = eventResults.getInt("id");
            String carFilter = eventResults.getString("carFilter");
            List<Car> cars = CarController.carQuery(conn, carFilter);
            List<Round> rounds = roundQuery(conn, eventId);
            events.add(new Event(eventResults, cars, rounds));
        }
        return events;
    }

    private static List<Event> eventQuery(final Connection conn) throws SQLException {
        try (
                final Statement eventStmt = conn.createStatement();
                final ResultSet eventResults = eventStmt.executeQuery(
                        "SELECT " + Event.DB_COLUMNS.stream().collect(Collectors.joining(", ")) +
                                " FROM " + Event.DB_TABLE_NAME + ";"
                )
        ) {
            return processEventResults(conn, eventResults);
        }
    }

    private static Event eventQuery(final Connection conn, final Integer eventId) throws SQLException {
        try (final PreparedStatement eventStmt = conn.prepareStatement(
                "SELECT " + Event.DB_COLUMNS.stream().collect(Collectors.joining(", ")) +
                        " FROM " + Event.DB_TABLE_NAME +
                        " WHERE id = ?;")
        ) {
            eventStmt.setInt(1, eventId);
            try (final ResultSet eventResults = eventStmt.executeQuery()) {
                List<Event> events = processEventResults(conn, eventResults);
                return (events.size() == 1) ? events.get(0) : null;
            }
        }
    }

    private static List<Round> processRoundResults(final Connection conn, final ResultSet roundResults) throws SQLException {
        final List<Round> rounds = new ArrayList<>();
        while (roundResults.next()) {
            Integer trackId = roundResults.getInt("trackID");
            Track track = TrackController.trackQuery(conn, trackId);
            rounds.add(new Round(roundResults, track));
        }
        return rounds;
    }

    private static List<Round> roundQuery(final Connection conn, final Integer eventId) throws SQLException {
        try (final PreparedStatement roundStmt = conn.prepareStatement(
                "SELECT " + Round.DB_COLUMNS.stream().collect(Collectors.joining(", ")) +
                        " FROM " + Round.DB_TABLE_NAME +
                        " WHERE eventId = ?;")
        ) {
            roundStmt.setInt(1, eventId);
            try (final ResultSet roundResults = roundStmt.executeQuery()) {
                return processRoundResults(conn, roundResults);
            }
        }
    }

    private static Round roundQuery(final Connection conn, final Integer eventId, final Integer roundId) throws SQLException {
        try (final PreparedStatement roundStmt = conn.prepareStatement(
                "SELECT " + Round.DB_COLUMNS.stream().collect(Collectors.joining(", ")) +
                        " FROM " + Round.DB_TABLE_NAME +
                        " WHERE id = ? AND eventID = ?;")
        ) {
            roundStmt.setInt(1, roundId);
            roundStmt.setInt(2, eventId);
            try (final ResultSet roundResults = roundStmt.executeQuery()) {
                List<Round> rounds = processRoundResults(conn, roundResults);
                return (rounds.size() == 1) ? rounds.get(0) : null;
            }
        }
    }
}
