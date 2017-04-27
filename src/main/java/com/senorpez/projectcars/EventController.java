package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@Api(tags = {"events"})
@RequestMapping(method = {RequestMethod.GET})
class EventController {
    class EventList extends ResourceSupport {
        @JsonProperty("events")
        private final List<Event> eventList;

        public EventList(List<Event> eventList) {
            this.eventList = eventList;
        }
    }

    @RequestMapping(value = "/v1/events")
    @ApiOperation(
            value = "Lists all events available",
            notes = "Returns a list of all single player events available through the Project CARS Data API",
            response = Event.class,
            responseContainer = "List"
    )
    public EventList events() {
        try (Connection conn = Application.DatabaseConnection()) {
            EventList events = new EventList(eventQuery(conn));
            events.add(linkTo(methodOn(EventController.class).events()).withSelfRel());
            return events;
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

    private static List<Event> processEventResults(final Connection conn, final ResultSet eventResults) throws SQLException {
        final List<Event> events = new ArrayList<>();
        while (eventResults.next()) {
            Integer eventId = eventResults.getInt("id");
            String carFilter = eventResults.getString("carFilter");
            List<Car> cars = CarController.carQuery(conn, eventId, carFilter);
            List<Round> rounds = RoundController.roundQuery(conn, eventId);
            Event event = new Event(eventResults, cars, rounds);
            event.add(linkTo(methodOn(EventController.class).events(event.getEventId())).withSelfRel());
            event.add(linkTo(methodOn(CarController.class).eventCars(event.getEventId())).withRel("cars"));
            event.add(linkTo(methodOn(RoundController.class).rounds(event.getEventId())).withRel("rounds"));
            event.add(linkTo(methodOn(EventController.class).events()).withRel("parent"));
            events.add(event);
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

    static Event eventQuery(final Connection conn, final Integer eventId) throws SQLException {
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



}
