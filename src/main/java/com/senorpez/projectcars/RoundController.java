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
@Api(tags = {"rounds"})
@RequestMapping(method = {RequestMethod.GET})
public class RoundController {
    class RoundList extends ResourceSupport {
        @JsonProperty("rounds")
        private final List<Round> roundList;

        public RoundList(List<Round> roundList) {
            this.roundList = roundList;
        }
    }

    @RequestMapping(value = "/v1/events/{eventId}/rounds")
    @ApiOperation(
            value = "Lists all rounds available for an event",
            notes = "Returns a list of all rounds in a single player event",
            response = Round.class,
            responseContainer = "List"
    )
    public RoundList rounds(@PathVariable Integer eventId) {
        try (Connection conn = Application.DatabaseConnection()) {
            RoundList rounds = new RoundList(roundQuery(conn, eventId));
            rounds.add(linkTo(methodOn(RoundController.class).rounds(eventId)).withSelfRel());
            rounds.add(linkTo(methodOn(EventController.class).events(eventId)).withRel("parent"));
            return rounds;
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
    public Round rounds(
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

    private static List<Round> processRoundResults(final Connection conn, final ResultSet roundResults, final Integer eventId) throws SQLException {
        final List<Round> rounds = new ArrayList<>();
        while (roundResults.next()) {
            final Integer trackId = roundResults.getInt("trackID");
            final Integer roundId = roundResults.getInt("id");

            Track track = TrackController.trackQuery(conn, trackId);
            List<Race> races = RaceController.raceQuery(conn, eventId, roundId);
            Round round = new Round(roundResults, track, races);
            round.add(linkTo(methodOn(RoundController.class).rounds(eventId, round.getRoundId())).withSelfRel());
            round.add(linkTo(methodOn(RoundController.class).rounds(eventId)).withRel("parent"));
            rounds.add(round);
        }
        return rounds;
    }

    static List<Round> roundQuery(final Connection conn, final Integer eventId) throws SQLException {
        try (final PreparedStatement roundStmt = conn.prepareStatement(
                "SELECT " + Round.DB_COLUMNS.stream().collect(Collectors.joining(", ")) +
                        " FROM " + Round.DB_TABLE_NAME +
                        " WHERE eventId = ?;")
        ) {
            roundStmt.setInt(1, eventId);
            try (final ResultSet roundResults = roundStmt.executeQuery()) {
                return processRoundResults(conn, roundResults, eventId);
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
                List<Round> rounds = processRoundResults(conn, roundResults, eventId);
                return (rounds.size() == 1) ? rounds.get(0) : null;
            }
        }
    }
}
