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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@Api(tags = {"races"})
@RequestMapping(method = {RequestMethod.GET})
public class RaceController {
    class RaceList extends ResourceSupport {
        @JsonProperty("races")
        private final List<Race> raceList;

        public RaceList(List<Race> raceList) {
            this.raceList = raceList;
        }
    }


    @RequestMapping(value = "/v1/events/{eventID}/rounds/{roundID}/races")
    @ApiOperation(
            value = "Returns all races available for a round",
            notes = "Returns a list of all races in a round of a single player event.",
            response = Round.class
    )
    public RaceList races(
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
            RaceList races = new RaceList(raceQuery(conn, eventID, roundID));
            races.add(linkTo(methodOn(RaceController.class).races(eventID, roundID)).withSelfRel());
            races.add(linkTo(methodOn(RoundController.class).rounds(eventID, roundID)).withRel("parent"));
            return races;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/v1/events/{eventID}/rounds/{roundID}/races/{raceID}")
    @ApiOperation(
            value = "Returns all races available for a round",
            notes = "Returns a list of all races in a round of a single player event.",
            response = Round.class
    )
    public Race races(
            @ApiParam(
                    value = "ID of event",
                    required = true
            )
            @PathVariable Integer eventID,
            @ApiParam(
                    value = "Round number within event.",
                    required = true
            )
            @PathVariable Integer roundID,
            @ApiParam(
                    value = "Race number within round.",
                    required = true
            )
            @PathVariable Integer raceID) {
        try (Connection conn = Application.DatabaseConnection()) {
            return raceQuery(conn, eventID, roundID, raceID);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Race> processRaceResults(final ResultSet raceResults, final Integer eventId, final Integer roundId) throws SQLException {
        final List<Race> races = new ArrayList<>();
        while (raceResults.next()) {
            Race race = new Race(raceResults);
            race.add(linkTo(methodOn(RaceController.class).races(eventId, roundId, race.getRaceId())).withSelfRel());
            race.add(linkTo(methodOn(RaceController.class).races(eventId, roundId)).withRel("parent"));
            races.add(race);
        }
        return races;
    }

    static List<Race> raceQuery(final Connection conn, final Integer eventId, final Integer roundId) throws SQLException {
        try (final PreparedStatement raceStmt = conn.prepareStatement(
                "SELECT " + Race.DB_COLUMNS.stream().collect(Collectors.joining(", ")) +
                        " FROM " + Race.DB_TABLE_NAME +
                        " WHERE eventId = ? AND roundId = ?;")
        ) {
            raceStmt.setInt(1, eventId);
            raceStmt.setInt(2, roundId);
            try (final ResultSet raceResults = raceStmt.executeQuery()) {
                return processRaceResults(raceResults, eventId, roundId);
            }
        }
    }

    private static Race raceQuery(final Connection conn, final Integer eventId, final Integer roundId, final Integer raceId) throws  SQLException {
        try (final PreparedStatement raceStmt = conn.prepareStatement(
                "SELECT " + Race.DB_COLUMNS.stream().collect(Collectors.joining(", ")) +
                        " FROM " + Race.DB_TABLE_NAME +
                        " WHERE id = ? AND eventID = ? and roundID = ?;")
        ) {
            raceStmt.setInt(1, raceId);
            raceStmt.setInt(2, eventId);
            raceStmt.setInt(3, roundId);
            try (final ResultSet raceResults = raceStmt.executeQuery()) {
                List<Race> races = processRaceResults(raceResults, eventId, roundId);
                return (races.size() == 1) ? races.get(0) : null;
            }
        }
    }
}
